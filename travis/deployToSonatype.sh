# commands to decode keys from base64 on CI:
export JRELEASER_GPG_SECRET_KEY="$(echo "$JRELEASER_GPG_SECRET_KEY_B64" | base64 --decode)"
export JRELEASER_GPG_PUBLIC_KEY="$(echo "$JRELEASER_GPG_PUBLIC_KEY_B64" | base64 --decode)"

echo "publish..."
./gradlew publish

echo "jreleaserDeploy..."
./gradlew jreleaserDeploy
