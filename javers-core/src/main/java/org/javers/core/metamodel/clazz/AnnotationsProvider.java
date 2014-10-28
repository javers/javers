package org.javers.core.metamodel.clazz;

import java.util.List;

public interface AnnotationsProvider {

    List<String> entityAliases();

    List<String> valueObjectAliases();

    List<String> valueAliases();
}
