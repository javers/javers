package org.javers.repository.sql.session;

import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;

public class SessionFactory {
    private final DialectName dialectName;
    private final ConnectionProvider connectionProvider;

    public SessionFactory(DialectName dialectName, ConnectionProvider connectionProvider) {
        this.dialectName = dialectName;
        this.connectionProvider = connectionProvider;
    }

    public Session create(String sessionName) {
        return new Session(dialectName, connectionProvider, sessionName);
    }
}
