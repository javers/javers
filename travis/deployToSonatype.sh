echo "uploadArchives..."
./gradlew uploadArchives -PnexusUsername=$sonatypeUser -PnexusPassword=$sonatypePassword -Psigning.keyId=$signingKeyId -Psigning.password=$signingPassword -Psigning.secretKeyRingFile=travis/codesigning.asc

echo "closeAndPromoteRepository..."
./gradlew closeAndPromoteRepository -PnexusUsername=$sonatypeUser -PnexusPassword=$sonatypePassword
