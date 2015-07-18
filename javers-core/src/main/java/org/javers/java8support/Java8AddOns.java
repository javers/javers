package org.javers.java8support;

import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.slf4j.LoggerFactory;

public class Java8AddOns implements JaversBuilderPlugin {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JaversBuilder.class);

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        logger.info("loading Java8 add-ons ...");
        javersBuilder.registerValueTypeAdapter(new LocalDateTypeAdapter());
    }
}