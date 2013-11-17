package org.javers.repository.jdbc.pico;

import org.javers.common.pico.JaversModule;
import org.javers.core.Javers;
import org.javers.repository.jdbc.JdbcDiffRepository;
import org.javers.repository.jdbc.schema.FixedSchemaFactory;
import org.javers.repository.jdbc.schema.JaversSchemaManager;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for jdbc repositories
 *
 * @author bartosz walacik
 */
public class JdbcJaversModule implements JaversModule{
    private static Class[] moduleComponents = new Class[] {JdbcDiffRepository.class,
                                                           FixedSchemaFactory.class,
                                                           JaversSchemaManager.class,
                                                           DataSourceTransactionManager.class,
                                                           QueryRunnerFactory.class};

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }
}
