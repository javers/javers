package org.javers.core.metamodel.object;

import org.javers.common.validation.Validate;
import org.javers.core.graph.AbstractMapFunction;
import org.javers.core.metamodel.type.MapEnumeratorContext;
import org.javers.core.metamodel.type.MapType;
import org.javers.core.metamodel.type.TypeMapper;

/**
* @author bartosz walacik
*/
public class DehydrateMapFunction extends AbstractMapFunction {
    private final GlobalIdFactory globalIdFactory;

    public DehydrateMapFunction(MapType mapType, TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        super(mapType,typeMapper);
        Validate.argumentIsNotNull(globalIdFactory);
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public Object apply(Object input, OwnerContext enumerationAwareOwnerContext) {
        MapEnumeratorContext mapContext =  enumerationAwareOwnerContext.getEnumeratorContext();
        if (mapContext.isKey()){
            return globalIdFactory.dehydrate(input, getKeyType(), enumerationAwareOwnerContext);
        }
        else {
            return globalIdFactory.dehydrate(input, getValueType(), enumerationAwareOwnerContext);
        }
    }
}
