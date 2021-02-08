package org.javers.spring.boot

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes=[TestApplication])
@ActiveProfiles("jproxy")
class SpringBootAuditableIntegrationJavaProxiesTest extends SpringBootAuditableIntegrationBaseTest {
}
