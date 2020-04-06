package org.javers.core.metamodel.type;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

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

    public boolean equals(Object left, Object right) {
        return mathFriendlyEquals(left, right);
    }

    /**
     * Delegates to Objects.equals(a, b) in most cases.
     * Compares Long and Integer mathematically.
     */
    static boolean mathFriendlyEquals(Object a, Object b) {
        if (a instanceof Long && b instanceof Integer) {
            return ((Long) a).intValue() == ((Integer) b).intValue();
        }

        if (b instanceof Long && a instanceof Integer) {
            return ((Long) b).intValue() == ((Integer) a).intValue();
        }

        return Objects.equals(a, b);
    }
}
