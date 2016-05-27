package org.javers.spring.auditable;

import java.util.Collections;
import java.util.Map;

public class EmptyPropertiesProvider implements CommitPropertiesProvider {
    @Override
    public Map<String, String> provide() {
        return Collections.emptyMap();
    }
}
