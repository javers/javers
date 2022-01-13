package org.javers.repository.mongo.pico;

import java.util.Arrays;
import java.util.Collection;
import org.javers.core.pico.JaversModule;
import org.javers.repository.mongo.CollectionNameProvider;
import org.javers.repository.mongo.MongoRepository;
import org.javers.repository.mongo.MongoSchemaManager;

/**
 * Provides Pico beans setup for Mongo repositories
 *
 */
public class JaversMongoModule implements JaversModule {
    private static Class[] moduleComponents = new Class[]{
            MongoRepository.class,
            MongoSchemaManager.class,
            CollectionNameProvider.class
    };

    @Override
    public Collection<Class> getComponents() {
        return Arrays.asList(moduleComponents);
    }
}
