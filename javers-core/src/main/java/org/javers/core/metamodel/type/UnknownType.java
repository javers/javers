package org.javers.core.metamodel.type;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;

import java.util.Optional;

public class UnknownType extends ManagedType {

    public UnknownType(String typeName) {
        super(ManagedClass.unknown(), Optional.of(typeName));
    }

    @Override
    ManagedType spawn(ManagedClass managedClass, Optional<String> typeName) {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

}
