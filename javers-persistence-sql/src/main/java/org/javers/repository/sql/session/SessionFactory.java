package org.javers.repository.sql.session;

import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.KeyGenerator;

public class SessionFactory {
    private final Dialect dialect;
    private final ConnectionProvider connectionProvider;
    private final KeyGenerator keyGenerator;
    private final KeyGenerator fallbackKeyGenerator;

    public SessionFactory(DialectName dialectName,
                          ConnectionProvider connectionProvider,
                          KeyGenerator keyGenerator) {
        this.dialect = Dialects.fromName(dialectName);
        this.connectionProvider = connectionProvider;
        this.keyGenerator = keyGenerator;
        this.fallbackKeyGenerator = dialect.getKeyGeneratorDefinition().createKeyGenerator();
    }

    public Session create(String sessionName) {
        return new Session(dialect,
                           keyGenerator,
                           fallbackKeyGenerator,
                           connectionProvider,
                           sessionName);
    }

    public void resetKeyGeneratorCache() {
        if (keyGenerator != null) {
            keyGenerator.reset();
        }
        fallbackKeyGenerator.reset();
    }
}
