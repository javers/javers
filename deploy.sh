./gradlew -v

./gradlew clean build -x test publish closeAndReleaseSonatypeStagingRepository

echo "published"