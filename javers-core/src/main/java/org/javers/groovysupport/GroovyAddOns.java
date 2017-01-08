package org.javers.groovysupport;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;

/**
 * @author bartosz.walacik
 */
public class GroovyAddOns extends ConditionalTypesPlugin {
    public static final String GROOVY_META_CLASS = "groovy.lang.MetaClass";

    @Override
    public boolean shouldBeActivated() {
        return ReflectionUtil.isClassPresent(GROOVY_META_CLASS);
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        Class<?> metaClass = ReflectionUtil.classForName(GROOVY_META_CLASS);
        javersBuilder.registerIgnoredClass(metaClass);
    }
}
