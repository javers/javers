package org.javers.core.metamodel.scanner;

import java.util.Set;

/**
 * @author bartosz walacik
 */
interface AnnotationsNameSpace {

    Set<String> getEntityAliases();

    Set<String> getValueObjectAliases();

    Set<String> getValueAliases();

    Set<String> getTransientPropertyAliases();

    Set<String> getIgnoredTypeAliases();

    Set<String> getShallowReferenceAliases();

    Set<String> getTypeNameAliases();

    Set<String> getPropertyNameAliases();
}
