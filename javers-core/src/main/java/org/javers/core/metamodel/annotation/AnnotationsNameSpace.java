package org.javers.core.metamodel.annotation;

import java.util.Set;

/**
 * @author bartosz walacik
 */
interface AnnotationsNameSpace {

    Set<String> getEntityAliases();

    Set<String> getValueObjectAliases();

    Set<String> getValueAliases();

    Set<String> getTransientPropertyAliases();

    Set<String> getTransientTypeAliases();

    Set<String> getShallowReferenceAliases();

    Set<String> getTypeNameAliases();
}
