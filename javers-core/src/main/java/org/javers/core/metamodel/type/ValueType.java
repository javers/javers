package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.common.collections.WellKnownValueTypes;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.custom.CustomValueComparator;
import org.javers.core.json.JsonTypeAdapter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

/**
 * Value class in client's domain model. Simple value holder.
 * <br/>
 *
 * JaVers doesn't interact with internal properties of this type but treats its similarly to primitives.
 * <br><br>
 *
 * Two Value instances are compared using equals() so
 * it's highly important to implement it properly by comparing underlying field (or fields).
 * <br><br>
 *
 * It's highly advisable to implement Values as immutable objects, for example:
 * {@link BigDecimal}, {@link LocalDateTime}
 * <br><br>
 *
 * Values are serialized to JSON using Gson defaults,
 * if it's not what you need, implement {@link JsonTypeAdapter} for custom serialization
 * and register it with {@link JaversBuilder#registerValueTypeAdapter(JsonTypeAdapter)}
 *
 * @author bartosz walacik
 */
public class ValueType extends PrimitiveOrValueType {
    private final Optional<Function<Object, String>> toStringFunction;

    public ValueType(Type baseJavaType) {
        super(baseJavaType);
        toStringFunction = Optional.empty();
    }

    ValueType(Type baseJavaType, CustomValueComparator customValueComparator, Function<Object, String> toStringFunction) {
        super(baseJavaType, customValueComparator);
        this.toStringFunction = Optional.ofNullable(toStringFunction);
    }

    @Override
    public String smartToString(Object cdo) {
        if (cdo == null){
            return "";
        }

        if (WellKnownValueTypes.isValueType(cdo)){
            return cdo.toString();
        }

        return toStringFunction
                .map(f -> f.apply(cdo))
                .orElse(ReflectionUtil.reflectiveToString(cdo));
    }
}
