package org.javers.java8support;

import org.javers.common.collections.Lists;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.OptionalType;

import java.util.Collection;
import java.util.List;

public class Java8AddOns extends ConditionalTypesPlugin {

    @Override
    public boolean shouldBeActivated() {
        return ReflectionUtil.isJava8runtime();
    }

    @Override
    public Collection<JaversType> getNewTypes() {
        return (List)Lists.asList(new OptionalType());
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        javersBuilder.registerValueTypeAdapter(new LocalDateTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new LocalDateTimeTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new LocalTimeTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new YearTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new ZonedDateTimeTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new ZoneOffsetTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new OffsetDateTimeTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new InstantTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new PeriodTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new DurationTypeAdapter());
    }
}