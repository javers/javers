package org.javers.java8support;

import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Java8AddOns implements JaversBuilderPlugin {
    private static final Logger logger = LoggerFactory.getLogger(Java8AddOns.class);

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        logger.info("loading Java8 add-ons ...");
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