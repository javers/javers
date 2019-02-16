./gradlew -v
./gradlew clean build
./gradlew publish --info
./gradlew closeAndReleaseRepository --info

echo "published"
