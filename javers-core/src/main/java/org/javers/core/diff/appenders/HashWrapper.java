package org.javers.core.diff.appenders;

import org.javers.common.validation.Validate;

import java.util.function.BiFunction;
import java.util.function.Function;

public class HashWrapper {
    private final Object target;
    private final BiFunction<Object,Object, Boolean> equalsFunction;
    private final Function<Object, String> toStringFunction;

    public HashWrapper(Object target, BiFunction<Object, Object, Boolean> equalsFunction, Function<Object, String> toStringFunction) {
        Validate.argumentIsNotNull(equalsFunction);
        Validate.argumentIsNotNull(toStringFunction);
        this.target = target;
        this.equalsFunction = equalsFunction;
        this.toStringFunction = toStringFunction;
    }

    @Override
    public boolean equals(Object that) {
        return equalsFunction.apply(target, ((HashWrapper)that).target);
    }

    @Override
    public int hashCode() {
        return toStringFunction.apply(target).hashCode();
    }

    public Object unwrap() {
        return target;
    }
}
