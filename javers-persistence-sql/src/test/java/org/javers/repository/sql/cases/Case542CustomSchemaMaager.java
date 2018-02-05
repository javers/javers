package org.javers.repository.sql.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.sql.H2RepositoryFactory;
import org.junit.Before;
import org.junit.Test;
import org.polyjdbc.core.exception.QueryExecutionException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * see https://github.com/javers/javers/issues/249
 * @author bartosz.walacik
 */
public class Case542CustomSchemaMaager {

    public static abstract class AbstractEntity<T extends Serializable> {

        @Id
        protected T id;
        protected T value;

        public AbstractEntity(T id, T value) {
            this.id = id;
            this.value = value;
        }
    }

    @Entity
    public class Account extends AbstractEntity<String> {
        public Account(String id, String value) {
            super(id, value);
        }
    }

    @Test(expected = QueryExecutionException.class)
    public void shouldThrowQueryExecutionExceptionOnEmptySchema() {

        //given
        Javers javers = JaversBuilder.javers()
                .registerJaversRepository(H2RepositoryFactory.emptySchema(false)).build();

        //when
        Account acc = new Account("1", "2");
        javers.commit("author", acc);
    }

}