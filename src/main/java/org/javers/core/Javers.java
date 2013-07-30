package org.javers.core;

import java.util.HashMap;
import org.javers.core.exceptions.ExceptionCode;
import org.javers.core.exceptions.JaversException;import org.javers.model.Entity;

import java.util.Map;

/**
 * Facade to JaVers instance.
 * Should be constructed by {@link JaversFactory} provided with your domain model metadata and configuration.
 *
 * @author bartosz walacik
 */
public class Javers {
    private Map<Class<?>,Entity> models = new HashMap<>();

    public Entity getByClass(Class<?> forClass) {
        if(!isManaged(forClass)) {
          throw new JaversException(ExceptionCode.CLASS_NOT_MANAGED.getErrorCode(forClass),
                  ExceptionCode.CLASS_NOT_MANAGED.getMessage());
        }

        return models.get(forClass);
    }

    public boolean isManaged(Class<?> forClass) {
        return models.get(forClass) != null;
    }

}
