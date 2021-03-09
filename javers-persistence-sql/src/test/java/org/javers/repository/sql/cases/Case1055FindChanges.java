package org.javers.repository.sql.cases;

import org.fest.assertions.api.Assertions;
import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.junit.Test;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * see https://github.com/javers/javers/issues/1055
 * @author https://github.com/WojtekPWas
 */

public class Case1055FindChanges {
    static final String TEST_DB_URL = "0.0.0.0";
    static final String TEST_DB_PORT = "1521";
    static final String TEST_DB_SID = "xe";
    static final String TEST_DB_USR = "user";
    static final String TEST_DB_PWD = "password";
    static final String TEST_DB_SCHEMA = "SCHEMANAME";
    static final String TEST_ENTITY_TYPE_NAME = "EntityTypeName";
    static final Long TEST_ENTITY_ID = 149698L;


    final Connection dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@"+TEST_DB_URL+":"+TEST_DB_PORT+":"+TEST_DB_SID, TEST_DB_USR, TEST_DB_PWD);

    ConnectionProvider connectionProvider = new ConnectionProvider() {
        @Override
        public Connection getConnection() {
            //suitable only for testing!
            return dbConnection;
        }
    };

    public Case1055FindChanges() throws SQLException {
    }

    public static abstract class AbstractEntity<T extends Serializable> {

        @Id
        protected T id;

        public AbstractEntity(T id) {
            this.id = id;
        }
    }

    @Entity
    @TypeName(TEST_ENTITY_TYPE_NAME)
    public class TestEntity extends AbstractEntity<Long> {
        public TestEntity(Long id) {
            super(id);
        }
    }

    @Test
    public void shouldFindChanges() {
        //given

        JaversSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withSchema(TEST_DB_SCHEMA) //optionally, provide the schame name
                .withConnectionProvider(connectionProvider)
                .withDialect(DialectName.ORACLE).build();
        Javers javers = JaversBuilder.javers().
                registerJaversRepository(sqlRepository).build();

        //when

        //then
        Changes changes = javers.findChanges(QueryBuilder
                .byInstanceId(TEST_ENTITY_ID, TestEntity.class)
                .withNewObjectChanges(true)
                .withChildValueObjects(true)
                .build());
        Assertions.assertThat(changes.size() > 0).isEqualTo(true);
    }
}
