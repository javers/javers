package org.javers.mongosupport;

import org.javers.common.collections.Predicate;
import org.javers.repository.api.JaversRepository;

public class RequiredMongoSupportPredicate implements Predicate<JaversRepository> {

    private static final String JAVERS_MONGO_REPOSITORY_CLASS_NAME = "org.javers.repository.mongo.MongoRepository";

    @Override
    public boolean apply(JaversRepository repository) {
        return repository != null && repository.getClass().getName().equals(JAVERS_MONGO_REPOSITORY_CLASS_NAME);
    }
}
