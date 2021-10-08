package org.javers.guava;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.javers.common.collections.Sets;
import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapperLazy;

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
    public Collection<JaversType> getNewTypes(TypeMapperLazy typeMapperLazy) {
        return (Set)Sets.asSet(new MultimapType(Multimap.class, typeMapperLazy),
                               new MultisetType(Multiset.class, typeMapperLazy));
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        javersBuilder.registerJsonAdvancedTypeAdapter(new MultimapTypeAdapter());
        javersBuilder.registerJsonAdvancedTypeAdapter(new MultisetTypeAdapter());
    }
}
