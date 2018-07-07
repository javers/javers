package org.javers.repository.sql.session;

import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;

public class SessionFactory {
    private final DialectName dialectName;
    private final ConnectionProvider connectionProvider;
    private final String schemaName;

    public SessionFactory(DialectName dialectName, ConnectionProvider connectionProvider, String schemaName) {
        this.dialectName = dialectName;
        this.connectionProvider = connectionProvider;
        this.schemaName = schemaName;
    }

    public Session create() {
        return new Session(dialectName);
    }
}
