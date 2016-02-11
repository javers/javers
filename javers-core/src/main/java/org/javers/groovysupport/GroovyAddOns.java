package org.javers.groovysupport;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bartosz.walacik
 */
public class GroovyAddOns implements JaversBuilderPlugin {
    private static final Logger logger = LoggerFactory.getLogger(GroovyAddOns.class);
    public static final String GROOVY_META_CLASS = "groovy.lang.MetaClass";

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        if (!ReflectionUtil.isClassPresent(GROOVY_META_CLASS)){
            return;
        }

        logger.info("loading Groovy add-ons ...");

        Class<?> metaClass = ReflectionUtil.classForName(GROOVY_META_CLASS);

        javersBuilder.registerIgnoredClass(metaClass);
    }
}
