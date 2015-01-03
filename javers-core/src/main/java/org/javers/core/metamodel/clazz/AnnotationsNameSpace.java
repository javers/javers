package org.javers.core.metamodel.clazz;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public interface AnnotationsNameSpace {

    Set<String> getEntityAliases();

    Set<String> getValueObjectAliases();

    Set<String> getValueAliases();

    Set<String> getTransientPropertyAliases();
}
