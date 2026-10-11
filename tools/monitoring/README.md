# Moonlight monitoring

`moonlight_monitor.py` writes the daily HEALTH and weekly GROWTH reports that the project's cloud routines post. It
only reads from Google Play: Android vitals (Play Developer Reporting API), the bulk report CSVs in Cloud Storage, and
recent reviews. Standard library Python plus `openssl`; nothing to install. It is not part of the app build.

```bash
python3 tools/monitoring/moonlight_monitor.py health --state monitor-state.json
python3 tools/monitoring/moonlight_monitor.py growth --state monitor-state.json --history growth-history.csv \
    --experiments experiments.md
python3 -m unittest discover tools/monitoring        # offline tests, no credentials needed
```

The first line of the output is `STATUS: OK`, `WARN` or `ERROR`. Health stays quiet unless it is `WARN` or `ERROR`;
growth always posts. The state file remembers which anomalies, error clusters and low reviews were already reported.

Growth also reads the store performance reports (`stats/store_performance/..._country.csv` and `..._traffic_source.csv`):
store listing visitors, installs from the listing and the conversion rate, with the top traffic and UTM sources, so
listing changes and promotion pushes can be judged. Columns are matched by name (`visitors`, `acquisitions`,
`source`), so small renames by Play don't break it. The experiments log is a Markdown file with one line per change,
`- 2026-10-12 New title "Moonlight: Moon Phase Glow"`; lines from the last two weeks are printed under the numbers.

Credentials come from two environment variables on the `moonlight-monitor` cloud environment, never from the repo:

- `MOONLIGHT_PLAY_SA_KEY_B64`: the `moonlight-monitor` service account's JSON key, base64 encoded. The account has no
  Google Cloud roles; in Play Console it has only "View app information and download bulk reports (read-only)" and
  "Reply to reviews" (Google requires that one to read reviews; the script never replies).
- `MOONLIGHT_PLAY_REPORTS_BUCKET`: `gs://pubsite_prod_rev_...` from Play Console, Download reports.

Thresholds: user-perceived crash rate 1.09% and ANR rate 0.47% over 7 days (Play's bad behaviour thresholds), checked
for phone and watch separately; a warning also fires when the 28-day rate reaches 80% of the threshold, or when the
newest versionCode has at least twice the previous version's rate (and at least 0.5%).
