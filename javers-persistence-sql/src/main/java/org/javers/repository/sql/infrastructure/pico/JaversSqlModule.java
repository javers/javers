package org.javers.repository.sql.infrastructure.pico;

import org.javers.core.pico.JaversModule;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.domain.CdoSnapshotRepository;
import org.javers.repository.sql.domain.CommitMetadataRepository;
import org.javers.repository.sql.domain.GlobalIdRepository;
import org.javers.repository.sql.finders.CdoSnapshotFinder;
import org.javers.repository.sql.finders.PropertiesFinder;
import org.javers.repository.sql.infrastructure.poly.JaversPolyJDBC;
import org.javers.repository.sql.domain.FixedSchemaFactory;
import org.javers.repository.sql.domain.JaversSchemaManager;
import org.javers.repository.sql.infrastructure.poly.ProvidedConnectionTransactionManager;
import org.polyjdbc.core.query.QueryRunnerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for sql repositories
 *
 * @author bartosz walacik
 */
public class JaversSqlModule implements JaversModule {
    private static Class[] moduleComponents = new Class[]{JaversPolyJDBC.class,
                                                          JaversSqlRepository.class,
                                                          FixedSchemaFactory.class,
                                                          JaversSchemaManager.class,
                                                          ProvidedConnectionTransactionManager.class,
                                                          QueryRunnerFactory.class,
                                                          GlobalIdRepository.class,
                                                          CommitMetadataRepository.class,
                                                          CdoSnapshotRepository.class,
                                                          CdoSnapshotFinder.class,
                                                          PropertiesFinder.class};

    @Override
    public Collection<Class> getComponents() {
        return Arrays.asList(moduleComponents);
    }
}
