package org.javers.guava;


import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.javers.common.collections.Lists;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.pico.ContainerArgumentResolver;
import org.javers.core.pico.LateInstantiatingModule;
import org.javers.guava.multimap.MultimapChangeAppender;
import org.javers.guava.multiset.MultisetChangeAppender;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * @author akrystian
 */
public class GuavaAddOns extends LateInstantiatingModule{
    private static final Logger logger = LoggerFactory.getLogger(GuavaAddOns.class);

    public static final String GUAVA_COLLECTION_CLASS = "com.google.common.collect.Multimap";
    private final JaversBuilder javersBuilder;
    private final ContainerArgumentResolver containerArgumentResolver;

    public GuavaAddOns(JaversCoreConfiguration javersCoreConfiguration, MutablePicoContainer container, JaversBuilder javersBuilder) {
        super(javersCoreConfiguration, container);
        this.javersBuilder =  javersBuilder;
        containerArgumentResolver = new ContainerArgumentResolver(container);
    }

    @Override
    protected Collection<Class> getImplementations(){
        if (!ReflectionUtil.isClassPresent(GUAVA_COLLECTION_CLASS)){
            return Collections.EMPTY_LIST;
        }
        logger.info("loading Guava add-ons ...");
        final TypeMapper typeMapper = (TypeMapper)containerArgumentResolver.resolve(TypeMapper.class);
        final GlobalIdFactory globalIdFactory = (GlobalIdFactory)containerArgumentResolver.resolve(GlobalIdFactory.class);
        javersBuilder.registerCustomComparator(new MultisetChangeAppender(typeMapper, globalIdFactory), Multiset.class);
        javersBuilder.registerCustomComparator(new MultimapChangeAppender(typeMapper, globalIdFactory), Multimap.class);
        return (Collection) Lists.asList(MultisetChangeAppender.class, MultimapChangeAppender.class);
    }
}
