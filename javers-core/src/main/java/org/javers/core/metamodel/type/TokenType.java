package org.javers.core.metamodel.type;

import java.lang.reflect.TypeVariable;

public class TokenType extends JaversType {
    TokenType(TypeVariable baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean canBePrototype() {
        return false;
    }

    @Override
    public boolean isInstance(Object cdo) {
        return false;
    }
}
