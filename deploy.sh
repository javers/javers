./gradlew -v

./gradlew clean build -x test publishToSonatype closeAndReleaseSonatypeStagingRepository

echo "published"