package org.javers.mongosupport;

import org.javers.repository.api.JaversRepository;
import java.util.function.Predicate;

public class RequiredMongoSupportPredicate implements Predicate<JaversRepository> {

    private static final String JAVERS_MONGO_REPOSITORY_CLASS_NAME = "org.javers.repository.mongo.MongoRepository";

    @Override
    public boolean test(JaversRepository repository) {
        return repository != null && repository.getClass().getName().equals(JAVERS_MONGO_REPOSITORY_CLASS_NAME);
    }
}
