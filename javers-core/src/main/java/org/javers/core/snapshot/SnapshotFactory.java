package org.javers.core.snapshot;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {
    private final TypeMapper typeMapper;

    public SnapshotFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

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
                filteredPropertyVal = extractAndDehydrateEnumarable(propertyVal, (EnumerableType) propertyType, owner);
            } else {
                filteredPropertyVal = dehydrate(propertyVal, propertyType, owner);
            }

            snapshot.addPropertyValue(property, filteredPropertyVal);
        }

        return snapshot;
    }

    private Object extractAndDehydrateEnumarable(Object propertyVal, EnumerableType propertyType, OwnerContext owner) {
        JaversType elementType = typeMapper.getJaversType(propertyType.getElementType());

        //corner case for Set<ValueObject>
        if (propertyType instanceof  SetType && elementType instanceof  ValueObjectType){
            return createSetId((ValueObjectType)elementType,owner);
        }

        EnumerableFunction dehydrate = new DehydrateFunction(owner, elementType);
        return  propertyType.map(propertyVal, dehydrate);
    }

    private ValueObjectSetId createSetId(ValueObjectType targetType, OwnerContext context) {
        return new ValueObjectSetId(targetType.getManagedClass(), context);
    }

    private Object dehydrate(Object target, JaversType targetType, OwnerContext context){
        if (targetType instanceof ManagedType){
            ManagedType targetManagedType = (ManagedType)targetType;
            return GlobalIdFactory.create(target,
                                          targetManagedType.getManagedClass(),
                                          context);
        }  else {
            return target;
        }
    }

    /**
     * If item is Primitive or Value - returns it,
     * if item is Entity or ValueObject - returns its globalId
     */
    private class DehydrateFunction implements EnumerableFunction{

        private OwnerContext owner;
        private JaversType contentType;

        private DehydrateFunction(OwnerContext owner, JaversType contentType) {
            this.owner = owner;
            this.contentType = contentType;
        }

        @Override
        public Object apply(Object input, Integer index) {
            owner.setListIndex(index);
            return dehydrate(input, contentType, owner);
        }
    }
}
