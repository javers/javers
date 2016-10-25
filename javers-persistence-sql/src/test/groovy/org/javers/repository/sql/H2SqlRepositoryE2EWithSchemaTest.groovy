package org.javers.repository.sql

/**
 * @author Ian Agius
 */
class H2SqlRepositoryE2EWithSchemaTest extends H2SqlRepositoryE2ETest {

    String getSchema() {
        return "public"
    }
}
