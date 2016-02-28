package org.javers.guava;

import com.google.common.collect.Multiset;
import org.javers.common.collections.Lists;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author akrystian
 */
public class GuavaAddOns implements JaversBuilderPlugin{
    Logger logger = LoggerFactory.getLogger(GuavaAddOns.class);
    public static final List<String> GUAVA_MULTISET_CLASSES = Lists.asList(
            "com.google.common.collect.Multiset",
            "com.google.common.collect.Multisets"
    );

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder){
        for (String className : GUAVA_MULTISET_CLASSES){
            if (!ReflectionUtil.isClassPresent(className)){
                return;
            }
        }

        logger.info("loading Guava add-ons ...");
        javersBuilder.registerCustomComparator(new CustomMultisetComparator(), Multiset.class);
    }
}
