echo "uploadArchives..."
./gradlew uploadArchives -PnexusUsername=$sonatypeUser -PnexusPassword=$sonatypePassword

echo "closeAndPromoteRepository..."
./gradlew closeAndPromoteRepository -PnexusUsername=$sonatypeUser -PnexusPassword=$sonatypePassword
