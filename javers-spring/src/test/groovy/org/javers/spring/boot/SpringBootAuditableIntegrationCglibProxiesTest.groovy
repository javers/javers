package org.javers.spring.boot

import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.ActiveProfiles

/**
 * @author bartosz.walacik
 */
@IntegrationTest
@SpringApplicationConfiguration(classes = SpringBootAuditableApp)
@ActiveProfiles("cglib")
class SpringBootAuditableIntegrationCglibProxiesTest extends SpringBootAuditableIntegrationBaseTest{
}
