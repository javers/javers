package org.javers.guava;

import org.javers.common.collections.Sets;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;
import java.util.Set;

/**
 * @author akrystian
 */
public class GuavaAddOns extends ConditionalTypesPlugin {

    public static final String GUAVA_COLLECTION_CLASS = "com.google.common.collect.Multimap";

    @Override
    public boolean shouldBeActivated() {
        return ReflectionUtil.isClassPresent(GUAVA_COLLECTION_CLASS);
    }

    @Override
    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders(){
        return (Set)Sets.asSet(MultisetChangeAppender.class, MultimapChangeAppender.class);
    }

    @Override
    public Collection<JaversType> getNewTypes() {
        return (Set)Sets.asSet(MultimapType.getInstance(),
                               MultisetType.getInstance());
    }
}
