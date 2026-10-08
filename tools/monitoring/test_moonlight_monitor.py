"""Offline tests: python3 -m unittest discover tools/monitoring"""

import base64
import datetime as dt
import json
import os
import subprocess
import tempfile
import unittest
from pathlib import Path
from unittest import mock

import moonlight_monitor as mm


def metric(name, value):
    return {"metric": name, "decimalValue": {"value": str(value)}}


def day(d):
    return {"year": d.year, "month": d.month, "day": d.day}


END = dt.date(2026, 10, 7)


class FakeApi:
    """Answers the URLs the script calls with canned payloads."""

    def __init__(self, crash7d=0.002, new_version_rate=0.001, anomalies=(), issues=(), reviews=(), csvs=None):
        self.crash7d, self.new_version_rate = crash7d, new_version_rate
        self.anomalies, self.issues, self.reviews, self.csvs = anomalies, issues, reviews, csvs or {}

    def call(self, url, body=None, raw=False):
        if raw:
            for key, data in self.csvs.items():
                if key in url:
                    return data
            raise mm.ApiError("404 from storage: not found")
        if url.endswith("MetricSet"):
            return {"freshnessInfo": {"freshnesses": [{"aggregationPeriod": "DAILY", "latestEndTime": day(END)}]}}
        if ":query" in url:
            dims = body["dimensions"]
            rows = []
            for i in range(7):
                d = END - dt.timedelta(days=7 - i)
                if dims == ["versionCode"]:
                    for code, rate in [(15, 0.001), (16, self.new_version_rate)]:
                        rows.append({"startTime": day(d), "dimensions": [{"dimension": "versionCode",
                                                                           "int64Value": str(code)}],
                                     "metrics": [metric(m, rate) for m in body["metrics"] if m != "distinctUsers"]
                                     + [metric("distinctUsers", 100)]})
                else:
                    for device in ["PHONE", "WATCH"]:
                        ms = [metric(m, self.crash7d if "7d" in m else 0.001) for m in body["metrics"]
                              if m != "distinctUsers"] + [metric("distinctUsers", 120 if device == "PHONE" else 30)]
                        rows.append({"startTime": day(d), "dimensions": ([{"dimension": dims[0],
                                                                           "stringValue": device}] if dims else []),
                                     "metrics": ms})
            return {"rows": rows}
        if "/anomalies" in url:
            return {"anomalies": list(self.anomalies)}
        if "errorIssues:search" in url:
            return {"errorIssues": list(self.issues)}
        if "/reviews" in url:
            return {"reviews": list(self.reviews)}
        raise AssertionError(url)


def review(rid, stars, text="Nice"):
    return {"reviewId": rid, "comments": [{"userComment": {
        "text": text, "starRating": stars, "lastModified": {"seconds": str(int(__import__("time").time()))},
        "appVersionCode": 16, "device": "panther"}}]}


class HealthTest(unittest.TestCase):
    def test_healthy_app_is_ok(self):
        report = mm.run_health(FakeApi(), {})
        self.assertTrue(report.startswith("STATUS: OK"), report)
        self.assertIn("watch", report)

    def test_crash_rate_over_threshold_warns(self):
        report = mm.run_health(FakeApi(crash7d=0.02), {})
        self.assertTrue(report.startswith("STATUS: WARN"))
        self.assertIn("2.00% over 7 days", report)

    def test_version_regression_warns(self):
        report = mm.run_health(FakeApi(new_version_rate=0.01), {})
        self.assertIn("versionCode 16", report)

    def test_anomalies_and_low_reviews_reported_once(self):
        api = FakeApi(anomalies=[{"name": "a1", "metricSet": "apps/x/crashRateMetricSet",
                                  "metric": metric("crashRate", 0.05)}],
                      reviews=[review("r1", 1, "Crashes"), review("r2", 5)])
        state = {}
        first = mm.run_health(api, state)
        self.assertIn("anomaly", first)
        self.assertIn("Crashes", first)
        self.assertNotIn("Nice", first)
        second = mm.run_health(api, state)
        self.assertTrue(second.startswith("STATUS: OK"), second)

    def test_new_error_cluster_after_first_run(self):
        state = {}
        mm.run_health(FakeApi(), state)
        issue = {"name": "i1", "type": "CRASH", "cause": "NullPointerException", "location": "Foo.kt",
                 "errorReportCount": "3"}
        report = mm.run_health(FakeApi(issues=[issue]), state)
        self.assertIn("New error cluster: CRASH NullPointerException", report)


class GrowthTest(unittest.TestCase):
    def test_growth_report_reads_utf16_csvs_and_writes_history(self):
        today = dt.date.today()
        header = "Date,Package Name,Daily Device Installs,Daily User Installs,Daily User Uninstalls,Active Device Installs\n"
        body = "".join(f"{(today - dt.timedelta(days=i)).isoformat()},{mm.PACKAGE},2,3,1,50\n" for i in range(10, 0, -1))
        installs = (header + body).encode("utf-16")
        ratings = ("Date,Package Name,Daily Average Rating,Total Average Rating\n"
                   f"{today.isoformat()},{mm.PACKAGE},4.5,4.62\n").encode("utf-16")
        month = f"{today.year}{today.month:02d}"
        api = FakeApi(reviews=[review("r1", 5)], csvs={f"installs_{mm.PACKAGE}_{month}": installs,
                                                       f"ratings_{mm.PACKAGE}_{month}": ratings})
        with tempfile.TemporaryDirectory() as tmp, \
                mock.patch.dict(os.environ, {"MOONLIGHT_PLAY_REPORTS_BUCKET": "gs://pubsite_prod_rev_1/stats/"}):
            history = os.path.join(tmp, "history.csv")
            report = mm.run_growth(api, {"health_runs": [today.isoformat()]}, history)
            self.assertIn("New user installs: 21", report)
            self.assertIn("Active devices", report)
            self.assertIn("4.62", report)
            self.assertIn("Daily active users, watch", report)
            self.assertIn("1 of 7", report)
            with open(history) as f:
                self.assertIn("New user installs", f.read())


class AuthTest(unittest.TestCase):
    def test_jwt_is_signed_with_the_key(self):
        with tempfile.TemporaryDirectory() as tmp:
            pem = os.path.join(tmp, "k.pem")
            subprocess.run(["openssl", "genpkey", "-algorithm", "RSA", "-out", pem], check=True, capture_output=True)
            key = {"client_email": "sa@example.iam.gserviceaccount.com", "private_key": Path(pem).read_text(),
                   "token_uri": "https://oauth2.googleapis.com/token"}
            env = {"MOONLIGHT_PLAY_SA_KEY_B64": base64.b64encode(json.dumps(key).encode()).decode()}
            with mock.patch.dict(os.environ, env):
                self.assertEqual(mm.load_key()["client_email"], key["client_email"])
            seen = {}

            class Resp:
                def __enter__(self):
                    return self

                def __exit__(self, *a):
                    return False

                def read(self):
                    return b'{"access_token": "t"}'

            def fake_open(req, timeout):
                seen["body"] = req.data.decode()
                return Resp()

            with mock.patch("urllib.request.urlopen", fake_open):
                self.assertEqual(mm.access_token(key), "t")
            assertion = dict(p.split("=", 1) for p in seen["body"].split("&"))["assertion"]
            header, claims, sig = assertion.split(".")
            pad = lambda s: s + "=" * (-len(s) % 4)
            self.assertEqual(json.loads(base64.urlsafe_b64decode(pad(claims)))["iss"], key["client_email"])
            pub = os.path.join(tmp, "p.pem")
            subprocess.run(["openssl", "pkey", "-in", pem, "-pubout", "-out", pub], check=True, capture_output=True)
            sigf = os.path.join(tmp, "s")
            Path(sigf).write_bytes(base64.urlsafe_b64decode(pad(sig)))
            ok = subprocess.run(["openssl", "dgst", "-sha256", "-verify", pub, "-signature", sigf],
                                input=f"{header}.{claims}".encode(), capture_output=True)
            self.assertEqual(ok.returncode, 0, ok.stdout + ok.stderr)


if __name__ == "__main__":
    unittest.main()
