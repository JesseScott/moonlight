# Releasing

The version lives in one file, `version.properties`, and covers both the phone and the Wear OS app. A merge to `main`
that changes it tags the commit and creates a GitHub release; the signed bundles are built locally and attached.

## Cutting a release

1. Branch from `main` and change `version.properties`:
   - `versionCode` goes up by at least one for every upload to Google Play (Play rejects a repeat). The Wear OS app uses
     `1000 + versionCode`, so it follows automatically and is always the higher of the two.
   - `versionName` is what people see, in the form `1.2.3`.
2. Update `docs/play-store/release-notes-en-US.txt` (and `-es.txt`), the Play "What's new" (500 characters at most), if it
   should change.
3. Open a pull request into `main`. CI runs the tests and builds a debug APK, and the **Version check** fails the PR
   if `versionCode` is not higher than on `main`.
4. Merge it. The **Release** workflow creates the tag `v<versionName>-<versionCode>` (for example `v0.7.0-14`) on the
   merge commit and a GitHub release for it: the release notes file first, then GitHub's list of merged pull requests.
   Versions below 1.0 are marked as pre-releases.
5. **Smoke-test the release builds** on the merge commit, with the emulators running (see
   [Smoke-testing the release builds](#smoke-testing-the-release-builds)):
   ```bash
   scripts/smoke-test.sh --phone emulator-5554 --wear emulator-5562
   ```
6. On your machine, with the upload key available (`keystore.properties`, vault unlocked):
   ```bash
   git checkout main && git pull
   scripts/release.sh
   ```
   It refuses to run unless the key is there, HEAD is the tagged commit and the tree is clean, builds both signed
   bundles, and attaches `moonlight-<name>-<code>-release.aab`, `moonlight-wear-<name>-<code>-release.aab` and the two
   R8 mapping files to the release.
7. Upload the two bundles to the Play Console (the phone bundle and the Wear OS bundle go to the same app).

The upload key never leaves your machine, which is why the bundles are not built in CI.

## Smoke-testing the release builds

CI builds the release variants but never runs them, and debug builds skip R8, so a crash that only exists in a release
build (a stripped constructor, mismatched library versions) gets through both. Before uploading, run the release builds:

```bash
scripts/smoke-test.sh --phone <adb serial> --wear <adb serial>     # adb devices lists the serials
```

It builds both release variants, signs them with the **debug** key so they can be installed, installs and launches
them, and fails (exit code 1, with the start of the crash) if either app crashed or is not in the foreground. Leave a
device out to only build and sign.

- **It never uses the upload key.** `keystore.properties` is moved aside for the build and put back afterwards, even on
  failure. With the key present a release build signs with it and uploads the R8 mapping to the production Crashlytics
  project, so do not run `assembleRelease` by hand while the key file is in place.
- **Use a phone emulator or device and a Wear OS emulator.** The phone build uses the same application id as the Play
  version, so the script uninstalls any existing copy first; do not point it at a phone that has the Play version you
  care about.
- **Then look at the apps by hand** (the script only catches crashes at launch): swipe through Moon, Data and About,
  open each About section, About > acknowledgements > Open Source licenses (the real list should show), Add widget (it
  must leave its loading spinner), Set as wallpaper, and the Wear gradient. Watch `adb logcat` for errors that did not
  crash the app (WorkManager, Hilt, Firebase). The first launch shows the Analytics consent dialog.

This found the Wear start-up crash in 0.7.0 (the shared `LocalLifecycleOwner` mismatch, #180) and the widget/WorkManager
problem on the AGP 9 branch (#163), neither of which any debug build showed.

## Release notes

The GitHub release lists the merged pull requests since the last release, grouped by label (`.github/release.yml`):
**New** (`enhancement`), **Fixes** (`bug`) and **Everything else** (no label). Label the release pull request `release`
so it is left out, and anything that should not appear in the notes `skip-changelog`.

## The upload key

`keystore.properties` sits in the repo root and is gitignored, like `*.jks`, `*.keystore` and `*.aab`. It points at the key
in the encrypted vault:

```properties
storeFile=C:/path/to/the/unlocked/vault/moonlight-upload.jks
storePassword=...
keyAlias=upload
keyPassword=...
```

A relative `storeFile` is read from the repo root. Without `keystore.properties`, release builds are unsigned: fine for
checking that they build, but Play rejects them, and `scripts/release.sh` stops before building.

## Crashlytics and the R8 mapping

A signed release build uploads the R8 mapping to the production Crashlytics project, which is what lets crash reports
for that version be read. Builds without the key (CI, or a local build without `keystore.properties`) do not upload, so
checking that a release build works can not overwrite the real mapping.

## One-time repository settings

Require a pull request and the **Build and test** and **Version check** checks before merging to `main`, squash-merge,
delete branches on merge, and protect `v*` tags.
