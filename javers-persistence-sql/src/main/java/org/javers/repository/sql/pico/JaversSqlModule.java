package org.javers.repository.sql.pico;

import org.javers.core.pico.JaversModule;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.domain.GlobalIdRepository;
import org.javers.repository.sql.poly.JaversPolyJDBC;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.javers.repository.sql.poly.ProvidedConnectionTransactionManager;
import org.polyjdbc.core.query.QueryRunnerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for sql repositories
 *
 * @author bartosz walacik
 */
public class JaversSqlModule implements JaversModule{
    private static Class[] moduleComponents = new Class[] {JaversPolyJDBC.class,
                                                           JaversSqlRepository.class,
                                                           FixedSchemaFactory.class,
                                                           JaversSchemaManager.class,
                                                           ProvidedConnectionTransactionManager.class,
                                                           QueryRunnerFactory.class,
                                                           GlobalIdRepository.class};

    @Override
    public Collection<Class> getComponents() {
        return Arrays.asList(moduleComponents);
    }
}
