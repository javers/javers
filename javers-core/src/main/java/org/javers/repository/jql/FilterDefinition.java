package org.javers.repository.jql;

import org.javers.common.collections.Sets;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Set;

abstract class FilterDefinition {

    abstract Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper);

    static class IdFilterDefinition extends FilterDefinition {
        private final GlobalIdDTO globalIdDTO;

        IdFilterDefinition(GlobalIdDTO globalIdDTO) {
            this.globalIdDTO = globalIdDTO;
        }

        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new IdFilter(globalIdFactory.createFromDto(globalIdDTO));
        }
    }

    static class IdAndTypeNameFilterDefinition extends FilterDefinition {
        private final Object localId;
        private final String typeName;

        IdAndTypeNameFilterDefinition(Object localId, String typeName) {
            Validate.argumentsAreNotNull(localId, typeName);
            this.localId = localId;
            this.typeName = typeName;
        }

        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new IdFilter(globalIdFactory.createInstanceId(localId, typeName));
        }
    }

    static class ClassFilterDefinition extends FilterDefinition {
        private final Set<Class> requiredClasses;

        ClassFilterDefinition(Set<Class> requiredClasses) {
            this.requiredClasses = requiredClasses;
        }

        @Override
        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new ClassFilter(Sets.transform(requiredClasses, javaClass -> typeMapper.getJaversManagedType(javaClass)));
        }
    }

    static class InstanceFilterDefinition extends  FilterDefinition {
        private final Object instance;

        InstanceFilterDefinition(Object instance) {
            this.instance = instance;
        }

        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            try {
                return new IdFilter(globalIdFactory.createId(instance));
            } catch (JaversException e) {
                if (e.getCode() == JaversExceptionCode.MANAGED_CLASS_MAPPING_ERROR) {
                    throw new JaversException(JaversExceptionCode.MALFORMED_JQL,
                        "object passed to byInstance(Object) query should be instance of Entity or ValueObject, got "+typeMapper.getJaversType(instance.getClass()) + " - " +ToStringBuilder.format(instance)+".\nDid you mean byInstanceId(Object localId, Class entityClass)?");
                }
                else {
                    throw e;
                }
            }
        }
    }

    static class VoOwnerFilterDefinition extends FilterDefinition {
        private final Class ownerEntityClass;
        private final String path;

        VoOwnerFilterDefinition(Class ownerEntityClass, String path) {
            this.ownerEntityClass = ownerEntityClass;
            this.path = path;
        }

        @Override
        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            ManagedType mType = typeMapper.getJaversManagedType(ownerEntityClass);

            if (! (mType instanceof EntityType)) {
                throw new JaversException(
                        JaversExceptionCode.MALFORMED_JQL, "queryForChanges: ownerEntityClass {'"+ownerEntityClass.getName()+"'} should be an Entity");
            }

            return new VoOwnerFilter((EntityType)mType, path);
        }
    }

    static class AnyDomainObjectFilterDefinition extends FilterDefinition {
        @Override
        Filter compile(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
            return new AnyDomainObjectFilter();
        }
    }
}
