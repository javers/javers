package org.javers.spring.mongodb.example

import org.javers.spring.auditable.integration.JaversSpringDataAspectIntegrationTest
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [JaversSpringMongoApplicationConfigExample])
class ExampleTest extends JaversSpringDataAspectIntegrationTest {
}
