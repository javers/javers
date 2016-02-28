package org.javers.spring.boot.sql;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author pawelszymczyk
 */
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan("org.javers.spring.boot.sql")
public class TestApplication {

}
