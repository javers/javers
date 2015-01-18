package org.javers.repository.sql.pico;

import org.javers.core.pico.JaversModule;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for sql repositories
 *
 * @author bartosz walacik
 */
public class JaversSqlModule implements JaversModule{
    private static Class[] moduleComponents = new Class[] {JaversSqlRepository.class,
                                                           FixedSchemaFactory.class,
                                                           JaversSchemaManager.class,
                                                           DataSourceTransactionManager.class,
                                                           QueryRunnerFactory.class};

    @Override
    public Collection<Class> getComponents() {
        return Arrays.asList(moduleComponents);
    }
}
