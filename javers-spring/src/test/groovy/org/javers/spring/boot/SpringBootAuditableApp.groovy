package org.javers.spring.boot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * @author bartosz.walacik
 */
@SpringBootApplication
class SpringBootAuditableApp {
    static void main(String[] args) {
        SpringApplication.run(SpringBootAuditableApp.class, args)
    }
}
