package org.javers.core.metamodel.clazz;

import java.util.Set;

/**
 * @author bartosz walacik
 */
public interface AnnotationNamesProvider {

    Set<String> getEntityAlias();

    Set<String> getValueObjectAlias();

    Set<String> getValueAlias();

}
