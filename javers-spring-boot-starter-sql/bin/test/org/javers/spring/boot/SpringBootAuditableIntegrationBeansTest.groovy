package org.javers.spring.boot

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes=[TestApplication])
@ActiveProfiles("beans")
class SpringBootAuditableIntegrationBeansTest extends SpringBootAuditableIntegrationBaseTest {
}
