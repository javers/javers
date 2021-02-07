package org.javers.repository.sql.pico;

import org.javers.core.pico.JaversModule;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.finders.CdoSnapshotFinder;
import org.javers.repository.sql.finders.CommitPropertyFinder;
import org.javers.repository.sql.repositories.CdoSnapshotRepository;
import org.javers.repository.sql.repositories.CommitMetadataRepository;
import org.javers.repository.sql.repositories.GlobalIdRepository;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.query.QueryRunnerFactory;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides Pico beans setup for sql repositories
 *
 * @author bartosz walacik
 */
public class JaversSqlModule implements JaversModule {
    private static Class[] moduleComponents = new Class[]{
            JaversSqlRepository.class,
            FixedSchemaFactory.class,
            JaversSchemaManager.class,
            QueryRunnerFactory.class,
            GlobalIdRepository.class,
            CommitMetadataRepository.class,
            CdoSnapshotRepository.class,
            CdoSnapshotFinder.class,
            CommitPropertyFinder.class,
            TableNameProvider.class
    };

    @Override
    public Collection<Class> getComponents() {
        return Arrays.asList(moduleComponents);
    }
}
