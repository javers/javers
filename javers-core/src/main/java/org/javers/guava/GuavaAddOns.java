package org.javers.guava;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.guava.multimap.MultimapChangeAppender;
import org.javers.guava.multiset.MultisetChangeAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author akrystian
 */
public class GuavaAddOns{
    private static final Logger logger = LoggerFactory.getLogger(GuavaAddOns.class);

    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public static final String GUAVA_COLLECTION_CLASS = "com.google.common.collect.Multimap";

    public GuavaAddOns(TypeMapper typeMapper, GlobalIdFactory globalIdFactory){
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;

    }

    public void afterAssemble(JaversBuilder javersBuilder){
        if (!ReflectionUtil.isClassPresent(GUAVA_COLLECTION_CLASS)){
            return;
        }
        logger.info("loading Guava add-ons ...");
        javersBuilder.registerCustomComparator(new MultisetChangeAppender(typeMapper, globalIdFactory), Multiset.class);
        javersBuilder.registerCustomComparator(new MultimapChangeAppender(typeMapper, globalIdFactory), Multimap.class);
    }
}
