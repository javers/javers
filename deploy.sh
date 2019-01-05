export JAVA_HOME=`/usr/libexec/java_home -v 1.8`
./gradlew -v
./gradlew build
./gradlew uploadArchives --info
./gradlew closeAndReleaseRepository --info
