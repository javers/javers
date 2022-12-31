package org.javers.repository.mongo

class MongoE2EWithRandomGeneratorTest extends MongoE2ETest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
