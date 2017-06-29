package org.javers.api;

import org.javers.common.exception.JaversException;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author pawel szymczyk
 */
@Component
public class JaversQueryService {

    private final Javers javers;

    @Autowired
    public JaversQueryService(Javers javers) {
        this.javers = javers;
    }

    public List<CdoSnapshot> findSnapshots(String instanceId, String className) {
        JqlQuery jqlQuery = QueryBuilder.byInstanceId(instanceId, quietlyClassForName(className)).build();

        return javers.findSnapshots(jqlQuery);
    }

    private Class<?> quietlyClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new JaversException(e);
        }
    }


}
