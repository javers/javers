./gradlew -v
./gradlew clean build
./gradlew publishToNexus closeAndReleaseRepository

echo "published"
