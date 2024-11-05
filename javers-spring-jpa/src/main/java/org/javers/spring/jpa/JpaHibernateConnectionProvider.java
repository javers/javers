package org.javers.spring.jpa;

import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.javers.repository.sql.ConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author bartosz walacik
 */
@Component
public class JpaHibernateConnectionProvider implements ConnectionProvider{

    @Autowired
    private EntityManager entityManager;

    @Override
    public Connection getConnection() {
        Session session = entityManager.unwrap(Session.class);
        GetConnectionWork connectionWork = new GetConnectionWork();
        session.doWork(connectionWork);

        //TODO that's a dirty hack forced by upgrading to Hibernate 6
        //this method should accept a connection consumer
        return connectionWork.theConnection;
    }

    private class GetConnectionWork implements Work{
        private Connection theConnection;
        @Override
        public void execute(Connection connection) throws SQLException {
            theConnection = connection;
        }

    }
}
