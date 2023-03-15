package org.javers.repository.sql;

import org.javers.repository.sql.session.Session;

/**
 * forked from org.polyjdbc.core.key.KeyGenerator
 *
 * @author Adam Dubiel
 */
public interface KeyGenerator {

    long generateKey(String sequenceName, Session session);

    long getKeyFromLastInsert(Session session);

    void reset();
}
