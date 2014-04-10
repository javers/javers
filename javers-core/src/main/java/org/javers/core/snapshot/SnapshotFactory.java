package org.javers.core.snapshot;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.core.graph.AbstractMapFunction;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import static org.javers.common.exception.exceptions.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {
    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public SnapshotFactory(TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    /**
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    public CdoSnapshot create (Object liveCdo, GlobalCdoId id) {
        CdoSnapshot snapshot =  new CdoSnapshot(id);

        for (Property property : id.getCdoClass().getProperties()){
            Object propertyVal = property.get(liveCdo);
            if (propertyVal == null){
                continue;
            }

            JaversType propertyType = typeMapper.getPropertyType(property);
            OwnerContext owner = new OwnerContext(id, property.getName());

            Object filteredPropertyVal;
            if (propertyType instanceof EnumerableType) {
                filteredPropertyVal = extractAndDehydrateEnumerable(propertyVal, (EnumerableType) propertyType, owner);
            } else {
                filteredPropertyVal = dehydrate(propertyVal, propertyType, owner);
            }

            snapshot.addPropertyValue(property, filteredPropertyVal);
        }

        return snapshot;
    }

    public CdoSnapshot create (ObjectNode objectNode) {
        return create(objectNode.wrappedCdo().get(),objectNode.getGlobalCdoId());
    }

    private Object extractAndDehydrateEnumerable(Object propertyVal, EnumerableType propertyType, OwnerContext owner) {
        if (!propertyType.isFullyParametrized()){
            throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, propertyType.getBaseJavaType().toString());
        }

        EnumerableFunction dehydratorMapFunction;
        if (propertyType instanceof ContainerType) {
            dehydratorMapFunction = new DehydrateContainerFunction((ContainerType) propertyType);
        }
        else if (propertyType instanceof MapType) {
            dehydratorMapFunction = new DehydrateMapFunction((MapType) propertyType, typeMapper);
        }
        else {
            throw new JaversException(NOT_IMPLEMENTED);
        }

        return  propertyType.map(propertyVal, dehydratorMapFunction, owner);
    }

    /**
     * If item is Primitive or Value - returns it,
     * if item is Entity or ValueObject - returns its globalId
     */
    private Object dehydrate(Object item, JaversType targetType, OwnerContext context){
        if (targetType instanceof ManagedType){
            return globalIdFactory.createId(item, context);
        }  else {
            return item;
        }
    }

    private class DehydrateContainerFunction implements EnumerableFunction{
        JaversType itemType;

        DehydrateContainerFunction(ContainerType containerType) {
            this.itemType = typeMapper.getJaversType(containerType.getItemClass());
        }

        @Override
        public Object apply(Object input, OwnerContext enumerationAwareOwnerContext) {
            return dehydrate(input, itemType, enumerationAwareOwnerContext);
        }
    }

    private class DehydrateMapFunction extends AbstractMapFunction {

        DehydrateMapFunction(MapType mapType, TypeMapper typeMapper) {
            super(mapType,typeMapper);
        }

        @Override
        public Object apply(Object input, OwnerContext enumerationAwareOwnerContext) {
            MapEnumeratorContext mapContext =  enumerationAwareOwnerContext.getEnumeratorContext();
            if (mapContext.isKey()){
                return dehydrate(input, getKeyType(), enumerationAwareOwnerContext);
            }
            else {
                return dehydrate(input, getValueType(), enumerationAwareOwnerContext);
            }
        }
    }
}
