package org.javers.core.snapshot;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

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

            if (propertyType instanceof EnumerableType){
                EnumerableType enumerablePropertyType = (EnumerableType)propertyType;
                JaversType elementType = typeMapper.getJaversType(enumerablePropertyType.getElementType());
                EnumerableFunction dehydrate = new DehydrateFunction(owner, elementType);
                snapshot.addPropertyValue(property,  enumerablePropertyType.map(propertyVal, dehydrate));
            } else{
                snapshot.addPropertyValue(property,  dehydrate(propertyVal, propertyType, owner));
            }
        }

        return snapshot;
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
        public Object apply(Object input, int index) {
            owner.setListIndex(index);
            return dehydrate(input, contentType, owner);
        }
    }
}
