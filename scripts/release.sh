#!/usr/bin/env bash
# Builds the signed release bundles (phone and Wear OS) for the version in version.properties and attaches them,
# with the R8 mapping files, to the GitHub release for that version.
#
#   1. Merge the version bump to main. The Release workflow tags it and creates the release.
#   2. Unlock the vault that holds the upload key.
#   3. git checkout main && git pull
#   4. scripts/release.sh
#
# Needs: the upload key (keystore.properties, with the vault unlocked) and the GitHub CLI (gh), signed in.
# The release build also uploads the R8 mapping to the production Crashlytics project, which is what lets
# crash reports from this version be read.
set -euo pipefail
cd "$(dirname "$0")/.."

read_prop() { grep -E "^$1=" version.properties | head -n1 | cut -d= -f2- | tr -d '[:space:]'; }
name=$(read_prop versionName)
code=$(read_prop versionCode)
tag="v${name}-${code}"

if [ ! -f keystore.properties ]; then
  echo "keystore.properties is missing. Without it the bundles are unsigned and Google Play rejects them." >&2
  exit 1
fi
store=$(grep -E '^storeFile=' keystore.properties | head -n1 | cut -d= -f2-)
if [ ! -f "$store" ] && [ ! -f "./$store" ]; then
  echo "The upload key ($store) is not there. Is the vault unlocked?" >&2
  exit 1
fi

if ! gh release view "$tag" >/dev/null 2>&1; then
  echo "There is no GitHub release for $tag yet. Merge the version bump to main first and let the Release workflow run." >&2
  exit 1
fi

# The bundles must be built from the commit the tag points at, or the release lies about what is in the files.
git fetch --tags --quiet
if [ "$(git rev-parse "$tag^{commit}")" != "$(git rev-parse HEAD)" ]; then
  echo "HEAD is not the commit $tag points at. Check out the tag (git checkout $tag) and run this again." >&2
  exit 1
fi
if [ -n "$(git status --porcelain --untracked-files=no)" ]; then
  echo "The working tree has uncommitted changes. Commit or stash them first." >&2
  exit 1
fi

(cd app && ./gradlew :androidApp:bundleRelease :wearApp:bundleRelease --console=plain)

phone_aab="app/androidApp/build/outputs/bundle/release/moonlight-${name}-${code}-release.aab"
wear_aab="app/wearApp/build/outputs/bundle/release/moonlight-wear-${name}-${code}-release.aab"
phone_map="app/androidApp/build/outputs/mapping/release/mapping.txt"
wear_map="app/wearApp/build/outputs/mapping/release/mapping.txt"
for f in "$phone_aab" "$wear_aab" "$phone_map" "$wear_map"; do
  [ -f "$f" ] || { echo "Expected $f but it was not built." >&2; exit 1; }
done

# Named copies, so downloads from the release say which version they belong to.
tmp=$(mktemp -d)
cp "$phone_map" "$tmp/moonlight-${name}-${code}-mapping.txt"
cp "$wear_map" "$tmp/moonlight-wear-${name}-${code}-mapping.txt"

gh release upload "$tag" "$phone_aab" "$wear_aab" "$tmp"/*.txt --clobber
echo "Attached both bundles and their mapping files to $tag. Upload the two .aab files to the Play Console."
