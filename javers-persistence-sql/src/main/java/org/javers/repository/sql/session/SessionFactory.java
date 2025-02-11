package org.javers.repository.sql.session;

import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;

public class SessionFactory {
    private final Dialect dialect;
    private final ConnectionProvider connectionProvider;
    private final KeyGenerator keyGenerator;
    private final boolean useNativeJSONType;

    public SessionFactory(DialectName dialectName, ConnectionProvider connectionProvider, boolean jsonTypeSupportEnabled) {
        this.dialect = Dialects.fromName(dialectName);
        this.connectionProvider = connectionProvider;
        this.keyGenerator = dialect.getKeyGeneratorDefinition().createKeyGenerator();
        this.useNativeJSONType = jsonTypeSupportEnabled;
    }

    public Session create(String sessionName) {
        return new Session(dialect, keyGenerator, connectionProvider, sessionName, useNativeJSONType);
    }

    public void resetKeyGeneratorCache() {
        keyGenerator.reset();
    }
}
