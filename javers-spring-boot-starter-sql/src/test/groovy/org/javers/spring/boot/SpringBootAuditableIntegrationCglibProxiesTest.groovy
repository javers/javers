package org.javers.spring.boot

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes=[TestApplication])
@ActiveProfiles("cglib")
class SpringBootAuditableIntegrationCglibProxiesTest extends SpringBootAuditableIntegrationBaseTest{
}
