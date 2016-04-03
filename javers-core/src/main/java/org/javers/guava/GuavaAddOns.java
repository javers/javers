package org.javers.guava;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.javers.common.collections.Lists;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.guava.multimap.MultimapComparator;
import org.javers.guava.multiset.MultisetComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author akrystian
 */
public class GuavaAddOns{
    private static final Logger logger = LoggerFactory.getLogger(GuavaAddOns.class);

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public static final List<String> GUAVA_COLLECTION_CLASSES = Lists.asList(
            "com.google.common.collect.Multiset",
            "com.google.common.collect.Multisets",
            "com.google.common.collect.Multimap",
            "com.google.common.collect.Multimaps"
    );

    public GuavaAddOns(TypeMapper typeMapper, GlobalIdFactory globalIdFactory){
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    public void afterAssemble(JaversBuilder javersBuilder){
        for (String className : GUAVA_COLLECTION_CLASSES){
            if (!ReflectionUtil.isClassPresent(className)){
                return;
            }
        }
        logger.info("loading Guava add-ons ...");
        javersBuilder.registerCustomComparator(new MultisetComparator(typeMapper, globalIdFactory), Multiset.class);
        javersBuilder.registerCustomComparator(new MultimapComparator(typeMapper, globalIdFactory), Multimap.class);

    }
}
