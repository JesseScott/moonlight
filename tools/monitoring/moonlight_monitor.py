#!/usr/bin/env python3
"""Moonlight monitoring: daily HEALTH and weekly GROWTH reports from Google Play.

Standard library only (plus the `openssl` command for signing the service account token), so it runs in a cloud
routine without installing anything.

    python3 tools/monitoring/moonlight_monitor.py health --state STATE.json
    python3 tools/monitoring/moonlight_monitor.py growth --state STATE.json --history HISTORY.csv

Credentials come from the environment, never from arguments:
    MOONLIGHT_PLAY_SA_KEY_B64       the service account JSON key, base64 encoded
    MOONLIGHT_PLAY_REPORTS_BUCKET   gs://pubsite_prod_rev_... (Play Console bulk reports)

The report is Markdown on stdout. Its first line is `STATUS: OK`, `STATUS: WARN` or `STATUS: ERROR`, so the routine
can decide whether to post. Nothing secret is ever printed.
"""

import argparse
import base64
import csv
import datetime as dt
import io
import json
import os
import subprocess
import sys
import tempfile
import time
import urllib.error
import urllib.parse
import urllib.request

PACKAGE = "tt.co.jesses.moonlight.android"
REPORTING = "https://playdeveloperreporting.googleapis.com/v1beta1"
PUBLISHER = "https://androidpublisher.googleapis.com/androidpublisher/v3"
STORAGE = "https://storage.googleapis.com/storage/v1"
SCOPES = [
    "https://www.googleapis.com/auth/playdeveloperreporting",
    "https://www.googleapis.com/auth/androidpublisher",
    "https://www.googleapis.com/auth/devstorage.read_only",
]
CONSOLE = "https://play.google.com/console"

# Play's bad behaviour thresholds for the user-perceived rates (fractions, not percent).
CRASH_THRESHOLD = 0.0109
ANR_THRESHOLD = 0.0047
# A newer version counts as worse when its rate is this many times the previous one's and above this floor.
REGRESSION_FACTOR = 2.0
REGRESSION_FLOOR = 0.005


# --- Auth -------------------------------------------------------------------------------------------------------

def _b64url(data):
    return base64.urlsafe_b64encode(data).rstrip(b"=").decode()


def load_key():
    raw = os.environ.get("MOONLIGHT_PLAY_SA_KEY_B64", "").strip()
    if not raw:
        raise SystemExit("MOONLIGHT_PLAY_SA_KEY_B64 is not set in this environment")
    try:
        return json.loads(base64.b64decode(raw))
    except ValueError as e:
        raise SystemExit(f"MOONLIGHT_PLAY_SA_KEY_B64 is not base64 of a JSON key ({type(e).__name__})")


def access_token(key):
    """Exchange the service account key for an OAuth token (JWT bearer grant, signed with openssl)."""
    now = int(time.time())
    header = _b64url(json.dumps({"alg": "RS256", "typ": "JWT"}).encode())
    claims = _b64url(json.dumps({
        "iss": key["client_email"],
        "scope": " ".join(SCOPES),
        "aud": key.get("token_uri", "https://oauth2.googleapis.com/token"),
        "iat": now,
        "exp": now + 3600,
    }).encode())
    signing_input = f"{header}.{claims}".encode()
    fd, pem = tempfile.mkstemp(suffix=".pem")
    try:
        with os.fdopen(fd, "w") as f:
            f.write(key["private_key"])
        sig = subprocess.run(["openssl", "dgst", "-sha256", "-sign", pem], input=signing_input,
                             capture_output=True, check=True).stdout
    finally:
        os.remove(pem)
    assertion = f"{header}.{claims}.{_b64url(sig)}"
    body = urllib.parse.urlencode({
        "grant_type": "urn:ietf:params:oauth:grant-type:jwt-bearer",
        "assertion": assertion,
    }).encode()
    req = urllib.request.Request(key.get("token_uri", "https://oauth2.googleapis.com/token"), data=body)
    with urllib.request.urlopen(req, timeout=30) as r:
        return json.load(r)["access_token"]


class Api:
    def __init__(self, token):
        self.token = token

    def call(self, url, body=None, raw=False):
        data = json.dumps(body).encode() if body is not None else None
        req = urllib.request.Request(url, data=data, headers={"Authorization": f"Bearer {self.token}"})
        if data is not None:
            req.add_header("Content-Type", "application/json")
        try:
            with urllib.request.urlopen(req, timeout=60) as r:
                payload = r.read()
        except urllib.error.HTTPError as e:
            detail = e.read().decode(errors="replace")[:300]
            raise ApiError(f"{e.code} from {url.split('?')[0]}: {detail}") from None
        return payload if raw else json.loads(payload or b"{}")


class ApiError(Exception):
    pass


# --- Play Developer Reporting API (Android vitals) -------------------------------------------------------------

def _date(d):
    return {"year": d.year, "month": d.month, "day": d.day, "timeZone": {"id": "America/Los_Angeles"}}


def latest_day(api, metric_set):
    """The exclusive end date of the newest complete DAILY data for a metric set."""
    info = api.call(f"{REPORTING}/apps/{PACKAGE}/{metric_set}")
    for f in info.get("freshnessInfo", {}).get("freshnesses", []):
        if f.get("aggregationPeriod") == "DAILY":
            e = f["latestEndTime"]
            return dt.date(e["year"], e["month"], e["day"])
    raise ApiError(f"no DAILY freshness for {metric_set}")


def query_daily(api, metric_set, metrics, dimensions, days, end=None):
    end = end or latest_day(api, metric_set)
    body = {
        "timelineSpec": {"aggregationPeriod": "DAILY", "startTime": _date(end - dt.timedelta(days=days)),
                         "endTime": _date(end)},
        "metrics": metrics,
        "dimensions": dimensions,
        "pageSize": 10000,
    }
    rows = []
    while True:
        out = api.call(f"{REPORTING}/apps/{PACKAGE}/{metric_set}:query", body)
        rows.extend(flatten_rows(out.get("rows", [])))
        if not out.get("nextPageToken"):
            return rows, end
        body["pageToken"] = out["nextPageToken"]


def flatten_rows(rows):
    """Turn API rows into {"date": date, <dimension>: value, <metric>: float} dicts."""
    flat = []
    for row in rows:
        s = row.get("startTime", {})
        item = {"date": dt.date(s["year"], s["month"], s["day"]) if s else None}
        for d in row.get("dimensions", []):
            item[d["dimension"]] = d.get("stringValue", d.get("int64Value"))
        for m in row.get("metrics", []):
            v = m.get("decimalValue", {}).get("value")
            if v is not None:
                item[m["metric"]] = float(v)
        flat.append(item)
    return flat


def latest_by(rows, key, metric):
    """For each value of `key`, the newest row that carries `metric`."""
    out = {}
    for r in sorted(rows, key=lambda r: r["date"]):
        if metric in r:
            out[r.get(key) or "ALL"] = r
    return out


def weighted_rate(rows, metric):
    users = sum(r.get("distinctUsers", 0) for r in rows if metric in r)
    if not users:
        return None, 0
    return sum(r[metric] * r.get("distinctUsers", 0) for r in rows if metric in r) / users, users


def pct(x):
    return "n/a" if x is None else f"{x * 100:.2f}%"


# --- Checks -----------------------------------------------------------------------------------------------------

def rate_checks(api, metric_set, rate7d, rate28d, daily, threshold, label):
    """Warnings and summary lines for one rate (crash or ANR), per device type."""
    warns, lines = [], []
    rows, end = query_daily(api, metric_set, [rate7d, rate28d, daily, "distinctUsers"], ["deviceType"], 7)
    newest = latest_by(rows, "deviceType", rate7d)
    if not newest:
        lines.append(f"- {label}: no data for the 7 days to {end} (Play withholds vitals when too few users)")
    for device, r in sorted(newest.items()):
        r7, r28 = r.get(rate7d), r.get(rate28d)
        lines.append(f"- {label}, {device.lower()}: 7-day {pct(r7)}, 28-day {pct(r28)} "
                     f"(threshold {pct(threshold)}, {int(r.get('distinctUsers', 0))} users on {r['date']})")
        if r7 is not None and r7 >= threshold:
            warns.append(f"{label} on {device.lower()} is {pct(r7)} over 7 days, at or above Play's "
                         f"{pct(threshold)} threshold.")
        elif r28 is not None and r28 >= threshold * 0.8:
            warns.append(f"{label} on {device.lower()} is {pct(r28)} over 28 days, close to Play's "
                         f"{pct(threshold)} threshold.")
    return warns, lines, end


def version_check(api, metric_set, metric, label, end):
    rows, _ = query_daily(api, metric_set, [metric, "distinctUsers"], ["versionCode"], 7, end)
    by_version = {}
    for r in rows:
        by_version.setdefault(int(r["versionCode"]), []).append(r)
    versions = sorted(by_version)
    if len(versions) < 2:
        return [], []
    new, old = versions[-1], versions[-2]
    new_rate, new_users = weighted_rate(by_version[new], metric)
    old_rate, old_users = weighted_rate(by_version[old], metric)
    line = (f"- {label} by version (7 days): {new} {pct(new_rate)} ({new_users} user-days), "
            f"{old} {pct(old_rate)} ({old_users} user-days)")
    if new_rate is not None and old_rate is not None and new_rate >= REGRESSION_FLOOR \
            and new_rate >= old_rate * REGRESSION_FACTOR:
        return [f"{label} for versionCode {new} is {pct(new_rate)}, against {pct(old_rate)} for {old}."], [line]
    return [], [line]


def anomaly_check(api, state):
    out = api.call(f"{REPORTING}/apps/{PACKAGE}/anomalies?pageSize=50")
    seen = set(state.get("anomalies", []))
    warns = []
    for a in out.get("anomalies", []):
        name = a.get("name", "")
        if name in seen:
            continue
        seen.add(name)
        metric = a.get("metric", {})
        value = metric.get("decimalValue", {}).get("value")
        dims = ", ".join(f"{d['dimension']}={d.get('stringValue', d.get('int64Value'))}"
                         for d in a.get("dimensions", []))
        warns.append(f"Play flagged an anomaly in {a.get('metricSet', '').split('/')[-1]}: "
                     f"{metric.get('metric', '?')} = {value}" + (f" ({dims})" if dims else "") + ".")
    state["anomalies"] = sorted(seen)
    return warns


def error_issue_check(api, state):
    out = api.call(f"{REPORTING}/apps/{PACKAGE}/errorIssues:search?pageSize=50")
    known = state.get("issues", {})
    warns, lines = [], []
    for i in out.get("errorIssues", []):
        name = i.get("name", "")
        count = int(i.get("errorReportCount", 0) or 0)
        desc = (f"{i.get('type', 'ERROR')} {i.get('cause', '?')} at {i.get('location', '?')}, "
                f"{count} reports, {i.get('distinctUsers', '?')} users, "
                f"versions {i.get('firstAppVersion', {}).get('versionCode', '?')}-"
                f"{i.get('lastAppVersion', {}).get('versionCode', '?')}")
        link = i.get("issueUri")
        if link:
            desc += f" ([open]({link}))"
        if name not in known:
            if state.get("issues_initialised"):
                warns.append(f"New error cluster: {desc}.")
            else:
                lines.append(f"- Existing error cluster: {desc}")
        elif count >= max(2 * known[name], known[name] + 5):
            warns.append(f"Error cluster jumped from {known[name]} to {count} reports: {desc}.")
        known[name] = count
    state["issues"] = known
    state["issues_initialised"] = True
    return warns, lines


def other_vitals(api):
    """Slow start, slow rendering and excessive wakeups: summary lines only (thresholds differ by metric)."""
    lines = []
    for metric_set, metric, label in [
        ("slowStartRateMetricSet", "slowStartRate7dUserWeighted", "Slow cold start"),
        ("slowRenderingRateMetricSet", "slowRenderingRate20Fps7dUserWeighted", "Slow rendering"),
        ("excessiveWakeupRateMetricSet", "excessiveWakeupRate7dUserWeighted", "Excessive wakeups"),
    ]:
        try:
            dims = ["startType"] if metric_set == "slowStartRateMetricSet" else []
            rows, _ = query_daily(api, metric_set, [metric], dims, 7)
            newest = latest_by(rows, "startType", metric)
            for k, r in sorted(newest.items()):
                suffix = f" ({k.lower()})" if dims else ""
                lines.append(f"- {label}{suffix}: 7-day {pct(r[metric])}")
        except ApiError as e:
            lines.append(f"- {label}: unavailable ({str(e)[:80]})")
    return lines


def recent_reviews(api, days):
    reviews, token = [], None
    cutoff = time.time() - days * 86400
    while True:
        url = f"{PUBLISHER}/applications/{PACKAGE}/reviews?maxResults=100&translationLanguage=en"
        if token:
            url += "&token=" + urllib.parse.quote(token)
        out = api.call(url)
        for r in out.get("reviews", []):
            c = next((c["userComment"] for c in r.get("comments", []) if "userComment" in c), None)
            if c and int(c.get("lastModified", {}).get("seconds", 0)) >= cutoff:
                reviews.append({"id": r["reviewId"], "stars": c.get("starRating"),
                                "text": " ".join(c.get("text", "").split()),
                                "version": c.get("appVersionCode"), "lang": c.get("reviewerLanguage"),
                                "device": c.get("device")})
        token = out.get("tokenPagination", {}).get("nextPageToken")
        if not token:
            return reviews


def quote(r):
    text = r["text"] if len(r["text"]) <= 200 else r["text"][:197] + "..."
    return f"{r['stars']} stars, v{r['version'] or '?'}, {r['device'] or 'unknown device'}: \"{text}\""


# --- Play Console bulk reports (Cloud Storage) ------------------------------------------------------------------

def bucket_name():
    raw = os.environ.get("MOONLIGHT_PLAY_REPORTS_BUCKET", "").strip()
    if not raw:
        raise SystemExit("MOONLIGHT_PLAY_REPORTS_BUCKET is not set in this environment")
    return raw.removeprefix("gs://").split("/")[0]


def read_play_csv(data):
    """Play's report CSVs are UTF-16 with a BOM; tolerate UTF-8 too."""
    if data[:2] in (b"\xff\xfe", b"\xfe\xff"):
        text = data.decode("utf-16")
    else:
        text = data.decode("utf-8-sig")
    return list(csv.DictReader(io.StringIO(text)))


def monthly_overview(api, bucket, kind, months):
    """Rows of the `overview` CSV for each month, keyed by date (ISO string)."""
    rows = {}
    for month in months:
        obj = f"stats/{kind}/{kind}_{PACKAGE}_{month}_overview.csv"
        url = f"{STORAGE}/b/{bucket}/o/{urllib.parse.quote(obj, safe='')}?alt=media"
        try:
            data = api.call(url, raw=True)
        except ApiError as e:
            if str(e).startswith("404"):
                continue
            raise
        for r in read_play_csv(data):
            if r.get("Date"):
                rows[r["Date"]] = r
    return rows


def column(row, *words):
    """The value of the first column whose name contains all the given words (case-insensitive)."""
    for k, v in row.items():
        if k and all(w in k.lower() for w in words):
            try:
                return float(v)
            except (TypeError, ValueError):
                return None
    return None


def sum_col(rows, *words):
    vals = [column(r, *words) for r in rows]
    vals = [v for v in vals if v is not None]
    return sum(vals) if vals else None


def months_back(today, n):
    out, y, m = [], today.year, today.month
    for _ in range(n):
        out.append(f"{y}{m:02d}")
        y, m = (y, m - 1) if m > 1 else (y - 1, 12)
    return sorted(out)


# --- Reports ----------------------------------------------------------------------------------------------------

def run_health(api, state):
    warns, lines, notes = [], [], []
    for metric_set, r7, r28, daily, threshold, label in [
        ("crashRateMetricSet", "userPerceivedCrashRate7dUserWeighted", "userPerceivedCrashRate28dUserWeighted",
         "userPerceivedCrashRate", CRASH_THRESHOLD, "User-perceived crash rate"),
        ("anrRateMetricSet", "userPerceivedAnrRate7dUserWeighted", "userPerceivedAnrRate28dUserWeighted",
         "userPerceivedAnrRate", ANR_THRESHOLD, "User-perceived ANR rate"),
    ]:
        try:
            w, l, end = rate_checks(api, metric_set, r7, r28, daily, threshold, label)
            warns += w
            lines += l
            w, l = version_check(api, metric_set, daily, label, end)
            warns += w
            lines += l
        except ApiError as e:
            notes.append(f"{label} check failed: {e}")
    try:
        warns += anomaly_check(api, state)
    except ApiError as e:
        notes.append(f"Anomaly check failed: {e}")
    try:
        w, l = error_issue_check(api, state)
        warns += w
        lines += l
    except ApiError as e:
        notes.append(f"Error cluster check failed: {e}")
    lines += other_vitals(api)
    try:
        seen = set(state.get("reviews_warned", []))
        for r in recent_reviews(api, 2):
            if r["stars"] and r["stars"] <= 2 and r["id"] not in seen:
                warns.append(f"New low review: {quote(r)}")
                seen.add(r["id"])
        state["reviews_warned"] = sorted(seen)[-500:]
    except ApiError as e:
        notes.append(f"Review check failed: {e}")

    state.setdefault("health_runs", []).append(dt.date.today().isoformat())
    state["health_runs"] = state["health_runs"][-60:]
    status = "ERROR" if notes and not lines else ("WARN" if warns or notes else "OK")
    out = [f"STATUS: {status}", "", f"## Moonlight health, {dt.date.today()}", ""]
    if warns:
        out += ["**Needs a look**", ""] + [f"- {w}" for w in warns] + [""]
    if notes:
        out += ["**Checks that did not run**", ""] + [f"- {n}" for n in notes] + [""]
    out += ["**Vitals**", ""] + lines + ["", f"[Android vitals in Play Console]({CONSOLE})"]
    return "\n".join(out)


def run_growth(api, state, history_path):
    today = dt.date.today()
    lines, notes, record = [], [], {"week_ending": None}

    # Installs and ratings from the bulk report CSVs (posted 3 to 7 days late).
    try:
        bucket = bucket_name()
        installs = monthly_overview(api, bucket, "installs", months_back(today, 3))
        ratings = monthly_overview(api, bucket, "ratings", months_back(today, 3))
        dates = sorted(installs)
        if dates:
            last = dt.date.fromisoformat(dates[-1])
            week = [installs[d] for d in dates if dt.date.fromisoformat(d) > last - dt.timedelta(days=7)]
            prev = [installs[d] for d in dates
                    if last - dt.timedelta(days=14) < dt.date.fromisoformat(d) <= last - dt.timedelta(days=7)]
            record["week_ending"] = last.isoformat()
            for label, words in [("New user installs", ("daily", "user", "installs")),
                                 ("User uninstalls", ("daily", "user", "uninstalls")),
                                 ("New device installs", ("daily", "device", "installs")),
                                 ("Device uninstalls", ("daily", "device", "uninstalls"))]:
                now_v, prev_v = sum_col(week, *words), sum_col(prev, *words)
                if now_v is not None:
                    record[label] = int(now_v)
                    lines.append(f"- {label}: {int(now_v)} (week before: "
                                 f"{'n/a' if prev_v is None else int(prev_v)})")
            active = column(installs[dates[-1]], "active", "device")
            if active is not None:
                record["Active devices"] = int(active)
                lines.append(f"- Active devices on {last}: {int(active)}")
            lines.insert(0, f"Installs cover the 7 days to {last} (Play posts these 3 to 7 days late).")
        else:
            notes.append("No install reports found in the bucket for the last three months.")
        rdates = sorted(ratings)
        if rdates:
            total = column(ratings[rdates[-1]], "total", "average")
            week = [ratings[d] for d in rdates[-7:]]
            daily = [column(r, "daily", "average") for r in week]
            daily = [d for d in daily if d]
            record["Average rating"] = total
            lines.append(f"- Average rating: {total if total is not None else 'n/a'} overall"
                         + (f", {sum(daily) / len(daily):.2f} from ratings given this week" if daily else ""))
    except ApiError as e:
        notes.append(f"Bulk reports unavailable: {e}")

    # Rough daily active users from vitals, split by phone and watch.
    try:
        rows, end = query_daily(api, "crashRateMetricSet", ["distinctUsers"], ["deviceType"], 14)
        for device in sorted({r.get("deviceType") for r in rows}):
            this = [r["distinctUsers"] for r in rows if r.get("deviceType") == device
                    and r["date"] >= end - dt.timedelta(days=7) and "distinctUsers" in r]
            last = [r["distinctUsers"] for r in rows if r.get("deviceType") == device
                    and r["date"] < end - dt.timedelta(days=7) and "distinctUsers" in r]
            if this:
                avg, prev = sum(this) / len(this), (sum(last) / len(last) if last else None)
                record[f"DAU {device.lower()}"] = round(avg)
                lines.append(f"- Daily active users, {device.lower()} (rough, from vitals): {avg:.0f}"
                             f" (week before: {'n/a' if prev is None else f'{prev:.0f}'})")
    except ApiError as e:
        notes.append(f"Vitals user counts unavailable: {e}")

    # Reviews written or edited this week.
    try:
        reviews = recent_reviews(api, 7)
        stars = {s: sum(1 for r in reviews if r["stars"] == s) for s in range(5, 0, -1)}
        record["Reviews"] = len(reviews)
        lines.append(f"- Reviews this week: {len(reviews)}" + (
            " (" + ", ".join(f"{n}x {s} stars" for s, n in stars.items() if n) + ")" if reviews else ""))
        for r in sorted(reviews, key=lambda r: (r["stars"] or 0))[:3]:
            lines.append(f"    - {quote(r)}")
    except ApiError as e:
        notes.append(f"Reviews unavailable: {e}")

    runs = [d for d in state.get("health_runs", []) if dt.date.fromisoformat(d) > today - dt.timedelta(days=7)]
    lines.append(f"- Daily health checks run in the last 7 days: {len(set(runs))} of 7")

    if history_path:
        record["run_date"] = today.isoformat()
        append_history(history_path, record)

    out = [f"STATUS: {'WARN' if notes else 'OK'}", "", f"## Moonlight growth, week to {today}", ""] + lines
    if notes:
        out += ["", "**Not available this week**", ""] + [f"- {n}" for n in notes]
    return "\n".join(out)


def append_history(path, record):
    rows = []
    if os.path.exists(path):
        with open(path, newline="") as f:
            rows = list(csv.DictReader(f))
    rows.append({k: ("" if v is None else v) for k, v in record.items()})
    fields = []
    for r in rows:
        fields += [k for k in r if k not in fields]
    os.makedirs(os.path.dirname(os.path.abspath(path)), exist_ok=True)
    with open(path, "w", newline="") as f:
        w = csv.DictWriter(f, fieldnames=fields)
        w.writeheader()
        w.writerows(rows)


def main(argv=None):
    p = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    p.add_argument("report", choices=["health", "growth"])
    p.add_argument("--state", help="JSON file remembering what was already reported")
    p.add_argument("--history", help="CSV file the growth report appends a row to")
    args = p.parse_args(argv)

    state = {}
    if args.state and os.path.exists(args.state):
        with open(args.state) as f:
            state = json.load(f)
    try:
        api = Api(access_token(load_key()))
    except (urllib.error.URLError, subprocess.CalledProcessError, KeyError) as e:
        print(f"STATUS: ERROR\n\nCould not get a Google access token: {type(e).__name__}: {str(e)[:200]}")
        return 1
    report = run_health(api, state) if args.report == "health" else run_growth(api, state, args.history)
    print(report)
    if args.state:
        os.makedirs(os.path.dirname(os.path.abspath(args.state)), exist_ok=True)
        with open(args.state, "w") as f:
            json.dump(state, f, indent=1, sort_keys=True, default=str)
    return 0


if __name__ == "__main__":
    sys.exit(main())
