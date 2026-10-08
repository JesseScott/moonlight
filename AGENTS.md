# AGENTS.md

Notes for AI coding agents and new contributors. See `README.md` for what the app does and `docs/RELEASING.md` for
releases.

Moonlight is a free, open source (Apache 2.0) Android app and Wear OS app that washes the screen in a glow that follows
the moon, as an app, a live wallpaper and a home screen widget. Kotlin, Jetpack Compose, Hilt, Firebase, SunCalc.
No accounts, no ads.

## Working agreement
- Every change goes through a pull request into `main` (squash merge; required checks **android-build** and
  **Version check**; rulesets on `main` and on `v*` tags). Agents: commit locally in small, logical steps, but do not push
  or open a PR unless asked. Never merge a PR; the owner merges.
- Label pull requests: `enhancement` or `bug` decides the section in the release notes (`.github/release.yml`); the
  version bump PR gets `release`; anything that should not appear in the notes gets `skip-changelog`.
- Never commit or print secrets: `keystore.properties` (release signing) is git-ignored and stays local, and so are
  `*.jks`, `*.keystore` and `*.aab`. The upload key itself lives in an encrypted vault, outside the repo. The Firebase
  client config (`google-services.json`) is committed; it is not a secret, but do not add other credentials.
- Work is tracked in GitHub issues; the release checklist for a version is an issue in its milestone.
- Text files are LF; Windows checkouts show harmless "LF will be replaced by CRLF" warnings.

## Build and test
```bash
cd app
./gradlew assembleDebug testDebugUnitTest      # what CI runs on pull requests
./gradlew :androidApp:lintDebug                # lint
```
JDK 17. Debug builds install as `tt.co.jesses.moonlight.android.debug` (label "moonlight (debug)", About shows
"DEBUG") so they sit beside the Play version, and they never collect telemetry. The APK is named
`moonlight-<versionName>-<versionCode>-debug.apk`.

**Do not run a release build casually.** With `keystore.properties` present, `assembleRelease` and `bundleRelease` sign
with the real upload key and upload that version's R8 mapping to the production Crashlytics project. Releases are made
with `scripts/release.sh` (see `docs/RELEASING.md`). To look at the merged release manifest without building, use
`./gradlew :androidApp:processReleaseMainManifest`.

## Modules (`app/`)
- `:common` is the shared, testable core (JUnit 5 tests in `common/src/test`): the moon data source and repository,
  `GradientUtil` (moon to colour, and the contrast-safe text area), `LocationDataSource`, `MoonColorSource`,
  `TelemetryPolicy`, `MoonDescription`, `LifecycleRefresh`, `parseLicenses`. Put new logic here when it can be tested
  without Android.
- `:androidApp` is the phone app (Compose, Hilt, Firebase). `view/` has the Moon, Data and About screens in a pager,
  `TextOnGradient` (the text pages) and our own `LicensesActivity`.
- `:wearApp` is the Wear OS app: no Firebase and no internet permission. It shares the application id with the phone
  app and its versionCode is `1000 + versionCode`.
- `:widget` is the Glance home screen widget and the live wallpaper service.

## The gradient and text
- `GradientUtil.moonHsl` maps the moon to a colour: hue from the phase (blue at new moon to gold at full, tilted by
  waxing or waning), saturation from altitude, lightness and alpha from the lit fraction, a darker muted wash below the
  horizon. Without a location the altitude is assumed to be 45 degrees.
- The gradient is moon colour (bottom), silver (middle), light blue (top). The top half is the same in every moon
  state, so dark text is readable there; the bottom half darkens. Text pages (Data, About) keep their text in the top
  part of the screen (`GradientUtil.textAreaFraction`) and the text colour is `GradientUtil.TextColor`.
  `GradientContrastTest` checks 4.5:1 across 144 moon states; if you change colours, run it and expect it to catch you.
- Screen readers get the moon described (`moonDescription`) because the moon screen has no text.

## Privacy
Location is coarse only, used on the device to work out where the moon is, and never stored or sent. There is no
advertising ID (`AD_ID` and the ad permissions are removed in the manifest). Usage data and crash reports are one opt-in
switch in About, off by default (`TelemetryPolicy`, `Logger`, `EventNames`).
Changing any of this means updating together: the About screen text, the hosted privacy page
(https://www.jesses.co.tt/privacy-moonlight.html), the Play Data safety form, and the store text in `docs/play-store`.

## Store listing
`docs/play-store` has the text (short description 80 characters, full description 4,000, "What's new" 500; English and
Spanish) and `docs/screenshots` the images and how they were made. Keep them true to the release being published.
User-facing strings are in English and Spanish (`values` and `values-es`); add both.

## Testing notes
- Emulators and phones need the `.debug` build for installs beside the Play version. `adb shell cmd alarm set-time <ms>`
  changes an emulator's date (useful for seeing other moon phases), and a shell mock location
  (`cmd location providers set-test-provider-location`) gives it a position. Restore both afterwards.
- Before every release, run `scripts/smoke-test.sh --phone <serial> --wear <serial>`: it builds the release variants, signs
  them with the debug key (never the upload key), installs and launches them. Release-only crashes (R8) do not show in debug
  builds or CI. Risky changes (R8, signing, manifest, dependency versions) deserve the same check before merging.
