package org.javers.spring.boot

import org.javers.spring.boot.sql.TestApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes=[TestApplication])
@ActiveProfiles("jproxy")
class SpringBootAuditableIntegrationJavaProxiesTest extends SpringBootAuditableIntegrationBaseTest {
}
