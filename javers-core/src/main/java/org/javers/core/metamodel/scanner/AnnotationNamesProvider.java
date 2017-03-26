package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.annotation.IgnoreDeclaredProperties;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
class AnnotationNamesProvider {
    private final Set<String> entityAliases = new HashSet<>();
    private final Set<String> typeNameAliases = new HashSet<>();
    private final Set<String> valueObjectAliases = new HashSet<>();
    private final Set<String> valueAliases = new HashSet<>();
    private final Set<String> transientPropertyAliases = new HashSet<>();
    private final Set<String> shallowReferenceAliases = new HashSet<>();
    private final Set<String> ignoredTypeAliases = new HashSet<>();
    private final Set<String> propertyNameAliases = new HashSet<>();

    private final List<AnnotationsNameSpace> namesProviders = Lists.immutableListOf(
            new JaversAnnotationsNamesSpace(),
            new JPAAnnotationsNameSpace());


    AnnotationNamesProvider() {

        for (AnnotationsNameSpace provider : namesProviders){
            entityAliases.addAll(provider.getEntityAliases());
            valueObjectAliases.addAll(provider.getValueObjectAliases());
            valueAliases.addAll(provider.getValueAliases());
            transientPropertyAliases.addAll(provider.getTransientPropertyAliases());
            shallowReferenceAliases.addAll(provider.getShallowReferenceAliases());
            typeNameAliases.addAll(provider.getTypeNameAliases());
            ignoredTypeAliases.addAll(provider.getIgnoredTypeAliases());
            propertyNameAliases.addAll(provider.getPropertyNameAliases());
        }
    }

    boolean isTypeName(Annotation ann) {
        return typeNameAliases.contains(ann.annotationType().getSimpleName());
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

    boolean isIgnoredTypeAlias(Annotation ann) {
        return ignoredTypeAliases.contains(ann.annotationType().getSimpleName());
    }

    Set<String> getTransientAliases() {
        return Collections.unmodifiableSet(transientPropertyAliases);
    }

    boolean isShallowReferenceAlias(Annotation ann){
        return shallowReferenceAliases.contains(ann.annotationType().getSimpleName());
    }

    public boolean isIgnoreDeclaredPropertiesAlias(Annotation ann) {
        return IgnoreDeclaredProperties.class.getSimpleName().equals( ann.annotationType().getSimpleName() );
    }

    Set<String> getShallowReferenceAliases() {
        return Collections.unmodifiableSet(shallowReferenceAliases);
    }

    public Set<String> getPropertyNameAliases() {
        return Collections.unmodifiableSet(propertyNameAliases);
    }

    public Set<String> getTypeNameAliases() {
        return Collections.unmodifiableSet(typeNameAliases);
    }
}
