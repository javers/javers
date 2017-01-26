package org.javers.groovysupport;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;

/**
 * @author bartosz.walacik
 */
public class GroovyAddOns extends ConditionalTypesPlugin {
    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        Class<?> metaClass = ReflectionUtil.classForName("groovy.lang.MetaClass");
        javersBuilder.registerIgnoredClass(metaClass);
    }
}
