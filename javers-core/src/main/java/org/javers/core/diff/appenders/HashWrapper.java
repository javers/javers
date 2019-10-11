package org.javers.core.diff.appenders;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.CustomComparableType;
import org.javers.core.metamodel.type.JaversType;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public static Set wrapIfNeeded(Set set, JaversType itemType) {
        if (hasCustomValueComparator(itemType)) {
            CustomComparableType customType = (CustomComparableType) itemType;
            return (Set)set.stream()
                    .map(it -> new HashWrapper(it, itemType::equals, customType::valueToString))
                    .collect(Collectors.toSet());
        }
        return set;
    }

    private static boolean hasCustomValueComparator(JaversType javersType) {
        return (javersType instanceof CustomComparableType &&
                ((CustomComparableType) javersType).hasCustomValueComparator());
    }
}
