package org.javers.spring.boot.sql;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author pawelszymczyk
 */
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan("org.javers.spring.boot.sql")
@Import(JaversSqlAutoConfiguration.class)
public class TestApplication {

}
