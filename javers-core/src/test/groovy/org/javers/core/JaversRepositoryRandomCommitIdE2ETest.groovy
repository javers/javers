package org.javers.core

class JaversRepositoryRandomCommitIdE2ETest extends JaversRepositoryShadowE2ETest {

    @Override
    boolean useRandomCommitIdGenerator() {
        true
    }
}
