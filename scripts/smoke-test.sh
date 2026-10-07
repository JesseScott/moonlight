#!/usr/bin/env bash
# Smoke-tests the RELEASE builds of the phone and Wear OS apps on emulators or devices, before a release is uploaded.
#
#   scripts/smoke-test.sh --phone emulator-5554 --wear emulator-5562
#
# Why: debug builds do not run R8, so crashes that only exist in release builds (a stripped constructor, a mismatched
# library) pass CI and a debug install. This builds both release variants, signs them with the debug key so they
# can be installed, launches them, and fails if the app crashed.
#
# It never touches the real upload key: keystore.properties is moved aside while building, and put back afterwards
# even on failure. (With the key present a release build would sign with it and upload the R8 mapping to the
# production Crashlytics project.) Nothing is uploaded and nothing is published.
#
# Options (leave a device out to only build and sign):
#   --phone <adb serial>   install and launch the phone app on this device
#   --wear  <adb serial>   install and launch the Wear OS app on this device
#
# After it passes, look at the app by hand: see "Check by hand" at the end of the output and docs/RELEASING.md.
set -euo pipefail
cd "$(dirname "$0")/.."

PHONE=""
WEAR=""
while [ $# -gt 0 ]; do
  case "$1" in
    --phone) PHONE="${2:?--phone needs an adb serial}"; shift 2 ;;
    --wear) WEAR="${2:?--wear needs an adb serial}"; shift 2 ;;
    -h|--help) sed -n '2,20p' "$0"; exit 0 ;;
    *) echo "Unknown option: $1 (see --help)" >&2; exit 2 ;;
  esac
done

PHONE_PKG="tt.co.jesses.moonlight.android"
PHONE_ACTIVITY="$PHONE_PKG/tt.co.jesses.moonlight.android.app.MainActivity"
WEAR_PKG="tt.co.jesses.moonlight.android" # the Wear app shares the application id
WEAR_ACTIVITY="$WEAR_PKG/tt.co.jesses.moonlight.wear.WearActivity"

# --- find the Android SDK tools -------------------------------------------------------------------------------------
sdk="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [ -z "$sdk" ] && [ -f app/local.properties ]; then
  sdk=$(grep -E '^sdk.dir=' app/local.properties | head -n1 | cut -d= -f2- | sed 's/\\\\/\//g; s/\\:/:/g; s/\\/\//g')
fi
[ -n "$sdk" ] && [ -d "$sdk" ] || { echo "Can not find the Android SDK. Set ANDROID_HOME." >&2; exit 1; }

adb="$sdk/platform-tools/adb"
[ -x "$adb" ] || adb="$sdk/platform-tools/adb.exe"
[ -x "$adb" ] || adb=$(command -v adb) || { echo "adb not found in the SDK or on PATH." >&2; exit 1; }

build_tools=$(ls -d "$sdk"/build-tools/* 2>/dev/null | sort -V | tail -n1)
apksigner="$build_tools/apksigner"
[ -e "$apksigner" ] || apksigner="$build_tools/apksigner.bat"
[ -e "$apksigner" ] || { echo "apksigner not found in $sdk/build-tools." >&2; exit 1; }

debug_keystore="$HOME/.android/debug.keystore"
[ -f "$debug_keystore" ] || { echo "No debug keystore at $debug_keystore (it is created by any debug build)." >&2; exit 1; }

# --- build the release variants without the real upload key ----------------------------------------------------------
parked=""
restore_key() {
  if [ -n "$parked" ] && [ -f "$parked" ]; then
    mv "$parked" keystore.properties
    parked=""
  fi
}
trap restore_key EXIT

if [ -f keystore.properties ]; then
  parked="$(mktemp -d)/keystore.properties"
  mv keystore.properties "$parked"
  echo "Moved keystore.properties aside for the build (it is put back when this finishes)."
fi

echo "Building the phone and Wear release variants (unsigned)..."
(cd app && ./gradlew :androidApp:assembleRelease :wearApp:assembleRelease --console=plain -q)
restore_key

out="$(mktemp -d)"
sign() { # <module> <prefix> <result name>
  local apk
  # $2 is a glob, so it must stay unquoted
  apk=$(ls -t app/"$1"/build/outputs/apk/release/$2 2>/dev/null | head -n1)
  [ -n "$apk" ] || { echo "No unsigned release APK found for $1." >&2; exit 1; }
  "$apksigner" sign --ks "$debug_keystore" --ks-pass pass:android --key-pass pass:android --out "$out/$3" "$apk"
  echo "Signed $(basename "$apk") with the debug key."
}
sign androidApp "moonlight-[0-9]*-unsigned.apk" phone.apk
sign wearApp "moonlight-wear-*-unsigned.apk" wear.apk

# --- install, launch, look for a crash -------------------------------------------------------------------------------
failed=0
smoke() { # <label> <serial> <apk> <package> <activity>
  local label="$1" serial="$2" apk="$3" pkg="$4" activity="$5"
  echo
  echo "== $label on $serial"
  "$adb" -s "$serial" get-state >/dev/null || { echo "Device $serial is not available." >&2; failed=1; return; }
  "$adb" -s "$serial" uninstall "$pkg" >/dev/null 2>&1 || true
  "$adb" -s "$serial" install -r "$apk" | tail -n1
  "$adb" -s "$serial" shell pm grant "$pkg" android.permission.ACCESS_COARSE_LOCATION >/dev/null 2>&1 || true
  "$adb" -s "$serial" shell input keyevent KEYCODE_WAKEUP >/dev/null 2>&1 || true
  "$adb" -s "$serial" logcat -c
  "$adb" -s "$serial" shell am start -n "$activity" >/dev/null
  sleep 12
  if "$adb" -s "$serial" logcat -d | grep -q "FATAL EXCEPTION"; then
    echo "FAIL: the app crashed. The first lines of the crash:" >&2
    "$adb" -s "$serial" logcat -d | grep -A6 "FATAL EXCEPTION" | head -n 12 >&2
    failed=1
  elif ! "$adb" -s "$serial" shell dumpsys window | grep -m1 mCurrentFocus | grep -q "$pkg"; then
    echo "FAIL: the app is not in the foreground after launch (it may have closed)." >&2
    failed=1
  else
    echo "OK: launched and still running, no crash in the log."
  fi
}

[ -n "$PHONE" ] && smoke "Phone release" "$PHONE" "$out/phone.apk" "$PHONE_PKG" "$PHONE_ACTIVITY"
[ -n "$WEAR" ] && smoke "Wear release" "$WEAR" "$out/wear.apk" "$WEAR_PKG" "$WEAR_ACTIVITY"
if [ -z "$PHONE" ] && [ -z "$WEAR" ]; then
  echo "No device given: built and signed only. The APKs are in $out"
fi

cat <<'TXT'

Check by hand (the script only catches crashes at launch):
  Phone: swipe through Moon, Data and About; open each About section; About > acknowledgements > Open Source licenses
         should list the libraries; About > wallpaper and widget: Add widget (it must leave its spinner and draw a
         gradient) and Set as wallpaper; the first launch shows the Analytics consent dialog (Enable or Disable).
  Wear:  the gradient draws.
  Then look at `adb logcat` for errors that did not crash the app (WorkManager, Hilt, Firebase).
TXT

exit "$failed"
