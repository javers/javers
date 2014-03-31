package org.javers.core.snapshot;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import static org.javers.common.exception.exceptions.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;
import static org.javers.common.exception.exceptions.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {
    private final TypeMapper typeMapper;

    public SnapshotFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
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
            SimpleOwnerContext owner = new SimpleOwnerContext(id, property.getName());

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
        return create(objectNode.wrappedCdo(),objectNode.getGlobalCdoId());
    }

    private Object extractAndDehydrateEnumerable(Object propertyVal, EnumerableType propertyType, SimpleOwnerContext owner) {
        if (!propertyType.isFullyParametrized()){
            throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, propertyType.getBaseJavaType().toString());
        }

        if (propertyType instanceof ContainerType) {
            return extractAndDehydrateContainer(propertyVal, (ContainerType) propertyType, owner);
        }
        if (propertyType instanceof MapType) {
            return extractAndDehydrateMap(propertyVal, (MapType) propertyType, owner);
        }
        throw new JaversException(NOT_IMPLEMENTED);
    }

    private Object extractAndDehydrateMap(Object propertyVal, MapType propertyType, SimpleOwnerContext owner) {
        JaversType keyType =   typeMapper.getJaversType(propertyType.getKeyClass());
        JaversType valueType = typeMapper.getJaversType(propertyType.getValueClass());

        //corner case for Map<ValueObject,?>
        if (keyType instanceof ValueObjectType) {
            throw new JaversException(VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY,
                                      propertyType.getKeyClass().getName(),
                                      propertyType.getBaseJavaType().toString());
        }

        EnumerableFunction dehydrate = new DehydrateMapFunction(keyType, valueType);
        return  propertyType.map(propertyVal, dehydrate, owner);
    }

    private Object extractAndDehydrateContainer(Object propertyVal, ContainerType propertyType, SimpleOwnerContext owner) {
        JaversType itemType = typeMapper.getJaversType(propertyType.getItemClass());

        //corner case for Set<ValueObject>
        if (propertyType instanceof  SetType && itemType instanceof  ValueObjectType){
            return GlobalIdFactory.create(propertyVal, ((ValueObjectType)itemType).getManagedClass(),
                                          new SetOwnerContext(owner));
        }

        EnumerableFunction dehydrate = new DehydrateContainerFunction(itemType);
        return  propertyType.map(propertyVal, dehydrate, owner);
    }

    /**
     * If item is Primitive or Value - returns it,
     * if item is Entity or ValueObject - returns its globalId
     */
    private Object dehydrate(Object item, JaversType targetType, OwnerContext context){
        if (targetType instanceof ManagedType){
            ManagedType targetManagedType = (ManagedType)targetType;
            return GlobalIdFactory.create(item,
                                          targetManagedType.getManagedClass(),
                                          context);
        }  else {
            return item;
        }
    }

    private class DehydrateContainerFunction implements EnumerableFunction{
        private JaversType itemType;

        DehydrateContainerFunction(JaversType itemType) {
            this.itemType = itemType;
        }

        @Override
        public Object apply(Object input, OwnerContext iterationAwareOwnerContext) {
            return dehydrate(input, itemType, iterationAwareOwnerContext);
        }
    }

    private class DehydrateMapFunction implements EnumerableFunction{
        private JaversType keyType;
        private JaversType valueType;

        DehydrateMapFunction(JaversType keyType, JaversType valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        @Override
        public Object apply(Object input, OwnerContext iterationAwareOwnerContext) {
            MapOwnerContext mapOwnerContext = (MapOwnerContext)iterationAwareOwnerContext;

            if (mapOwnerContext.isKey()){
                return dehydrate(input, keyType, mapOwnerContext);
            }
            else {
                return dehydrate(input, valueType, mapOwnerContext);
            }

            //corner case for Map<?,ValueObject>
            //if (valueType instanceof ValueObjectType && key != null){
            //    return dehydrate(input, valueType, owner);
            //}

        }
    }
}
