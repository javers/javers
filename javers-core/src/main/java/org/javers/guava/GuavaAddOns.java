package org.javers.guava;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.pico.CustomComparatorModule;
import org.javers.guava.multimap.MultimapChangeAppender;
import org.javers.guava.multiset.MultisetChangeAppender;
import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author akrystian
 */
public class GuavaAddOns extends CustomComparatorModule{
    private static final Logger logger = LoggerFactory.getLogger(GuavaAddOns.class);

    public static final String GUAVA_COLLECTION_CLASS = "com.google.common.collect.Multimap";
    private HashMap<Class, Class> customComparatorMappings = new HashMap<>();

    public GuavaAddOns(JaversCoreConfiguration javersCoreConfiguration, MutablePicoContainer container) {
        super(javersCoreConfiguration, container);
    }

    @Override
    protected Collection<Class> getImplementations(){
        if (!ReflectionUtil.isClassPresent(GUAVA_COLLECTION_CLASS)){
            return Collections.EMPTY_LIST;
        }
        logger.info("loading Guava add-ons ...");
        customComparatorMappings.put(MultisetChangeAppender.class, Multiset.class);
        customComparatorMappings.put(MultimapChangeAppender.class, Multimap.class);
        return Collections.unmodifiableSet(customComparatorMappings.keySet());
    }

    @Override
    public Map<Class, Class> getCustomComparatorMappings(){
        return ImmutableMap.copyOf(customComparatorMappings);
    }
}
