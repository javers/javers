package org.javers.spring.auditable;

import java.util.Collections;
import java.util.Map;

public class EmptyPropertiesProvider implements CommitPropertiesProvider {
    @Override
    public Map<String, String> provide(CommitPropertiesProviderContext context, Object domainObject) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId) {
        return Collections.emptyMap();
    }
}
