package org.javers.core.metamodel.object;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.MapContentType;
import org.javers.core.metamodel.type.MapEnumeratorContext;

/**
* @author bartosz walacik
*/
public class DehydrateMapFunction implements EnumerableFunction {
    private final GlobalIdFactory globalIdFactory;
    private final MapContentType mapContentType;

    public DehydrateMapFunction(GlobalIdFactory globalIdFactory, MapContentType mapContentType) {
        Validate.argumentsAreNotNull(globalIdFactory, mapContentType);
        this.globalIdFactory = globalIdFactory;
        this.mapContentType = mapContentType;
    }

    @Override
    public Object apply(Object input, OwnerContext enumerationAwareOwnerContext) {
        MapEnumeratorContext mapContext =  enumerationAwareOwnerContext.getEnumeratorContext();
        if (mapContext.isKey()){
            return globalIdFactory.dehydrate(input, mapContentType.getKeyType(), enumerationAwareOwnerContext);
        }
        else {
            return globalIdFactory.dehydrate(input, mapContentType.getValueType(), enumerationAwareOwnerContext);
        }
    }
}
