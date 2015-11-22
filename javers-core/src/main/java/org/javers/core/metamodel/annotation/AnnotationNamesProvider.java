package org.javers.core.metamodel.annotation;

import org.javers.common.collections.Lists;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class AnnotationNamesProvider {
    private final Set<String> entityAliases = new HashSet<>();
    private final Set<String> valueObjectAliases = new HashSet<>();
    private final Set<String> valueAliases = new HashSet<>();
    private final Set<String> transientPropertyAliases = new HashSet<>();
    private final Set<String> shallowReferenceAliases = new HashSet<>();
    private final List<AnnotationsNameSpace> namesProviders = Lists.immutableListOf(
            new JaversAnnotationsNamesSpace(),
            new JPAAnnotationsNameSpace());


    public AnnotationNamesProvider() {

        for (AnnotationsNameSpace provider : namesProviders){
            entityAliases.addAll(provider.getEntityAliases());
            valueObjectAliases.addAll(provider.getValueObjectAliases());
            valueAliases.addAll(provider.getValueAliases());
            transientPropertyAliases.addAll(provider.getTransientPropertyAliases());
            shallowReferenceAliases.addAll(provider.getShallowReferenceAliases());
        }
    }

    boolean isEntityAlias(Annotation ann) {
        return entityAliases.contains(ann.annotationType().getSimpleName());
    }

    boolean isValueObjectAlias(Annotation ann){
        return valueObjectAliases.contains(ann.annotationType().getSimpleName());
    }

    boolean isValueAlias(Annotation ann){
        return valueAliases.contains(ann.annotationType().getSimpleName());
    }

    public Set<String> getTransientAliases() {
        return Collections.unmodifiableSet(transientPropertyAliases);
    }

    boolean isShallowReference(Annotation ann){return  shallowReferenceAliases.contains(ann.annotationType().getSimpleName());}
}
