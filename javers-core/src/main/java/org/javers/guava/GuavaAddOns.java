package org.javers.guava;

import org.javers.common.collections.Sets;
import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;
import java.util.Set;

/**
 * @author akrystian
 */
public class GuavaAddOns extends ConditionalTypesPlugin {
    @Override
    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders(){
        return (Set)Sets.asSet(MultisetChangeAppender.class, MultimapChangeAppender.class);
    }

    @Override
    public Collection<JaversType> getNewTypes() {
        return (Set)Sets.asSet(MultimapType.getInstance(),
                               MultisetType.getInstance());
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        javersBuilder.registerJsonAdvancedTypeAdapter(new MultimapTypeAdapter());
        javersBuilder.registerJsonAdvancedTypeAdapter(new MultisetTypeAdapter());
    }
}
