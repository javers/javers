package org.javers.repository.mongo

class EmbeddedMongoE2EWithRandomGeneratorTest extends EmbeddedMongoE2ETest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
