package org.javers.spring.boot.sql;

import org.javers.repository.sql.ConnectionProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.javers.spring.boot.mongo")
@Import(JaversSqlAutoConfiguration.class)
public class TestApplication {

    @Bean
    ConnectionProvider connectionProvider() {
        return new ConnectionProvider() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection( "jdbc:h2:mem:test" );
            }
        };
    }
}
