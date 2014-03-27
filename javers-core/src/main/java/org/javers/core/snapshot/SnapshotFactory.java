package org.javers.core.snapshot;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.property.Property;
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

            if (typeMapper.isPrimitiveOrValue(property)){
                snapshot.addPropertyValue(property,  propertyVal);
                continue;
            }

            OwnerContext owner = new OwnerContext(id, property.getName());
            if (typeMapper.isEntityReferenceOrValueObject(property)){
                GlobalCdoId reference = GlobalIdFactory.create(propertyVal,
                                                               typeMapper.getManagedClass(propertyVal.getClass()),
                                                               owner);
                snapshot.addPropertyValue(property,  reference);
                continue;
            }

        }

        return snapshot;
    }
}
