echo "decrypting secring.gpg..."
openssl aes-256-cbc -K $encrypted_750aaef1260c_key -iv $encrypted_750aaef1260c_iv -in $TRAVIS_BUILD_DIR/travis/secring.gpg.enc -out $TRAVIS_BUILD_DIR/travis/secring.gpg -d

echo "publish..."
./gradlew publishToNexus closeAndReleaseRepository -PnexusUsername=$sonatypeUser -PnexusPassword=$sonatypePassword -Psigning.keyId=$signingKeyId -Psigning.password=$signingPassword -Psigning.secretKeyRingFile=$TRAVIS_BUILD_DIR/travis/secring.gpg
