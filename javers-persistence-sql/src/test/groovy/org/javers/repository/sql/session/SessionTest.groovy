package org.javers.repository.sql.session


import org.javers.core.FakeDateProvider
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.KeyGenerator
import org.javers.repository.sql.SqlRepositoryBuilder
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class SessionTest extends Specification{

    def customIdGenerator = new KeyGenerator() {

        @Override
        long generateKey(String sequenceName, Session session) {
            return 42
        }

        @Override
        long getKeyFromLastInsert(Session session) {
            return 42
        }

        @Override
        void reset() {
        }
    }

    @Entity
    static class Account {

        @Id
        protected long id
        protected String value

        Account(long id, String value) {
            this.id = id
            this.value = value
        }
    }

    def "should use custom key generator if provided"() {
        given:
        def repo = SqlRepositoryBuilder.sqlRepository()
                .withConnectionProvider(new ConnectionProvider() {
                    Connection getConnection() throws SQLException {
                        return getConnectionFromDriver()
                    }
                })
                .withDialect(DialectName.H2)
                .withKeyGenerator(customIdGenerator)
                .build()
        def  javers = JaversBuilder
                .javers()
                .withDateTimeProvider(new FakeDateProvider())
                .withInitialChanges(false)
                .registerJaversRepository(repo)
                .build()

        when:
        javers.commit("author", new Account(4711, "value"))

        then: "should find snapshot by id 42"
        getConnectionFromDriver().createStatement()
                .executeQuery("SELECT * FROM JV_SNAPSHOT WHERE SNAPSHOT_PK = 42")
                .next()
    }

    Connection getConnectionFromDriver(){
        return DriverManager.getConnection("jdbc:h2:mem:test;")
    }
}
