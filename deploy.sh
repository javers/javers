./gradlew -v

./gradlew clean build -x test publish closeAndReleaseRepository

echo "published"