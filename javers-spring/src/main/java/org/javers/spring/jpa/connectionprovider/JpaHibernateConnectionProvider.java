package org.javers.spring.jpa.connectionprovider;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.javers.repository.sql.ConnectionProvider;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Connection;

/**
 * @author bartosz walacik
 */
public class JpaHibernateConnectionProvider implements ConnectionProvider{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Connection getConnection() {

        SessionImpl session =  (SessionImpl)entityManager.unwrap(Session.class);

        return session.connection();
    }

}
