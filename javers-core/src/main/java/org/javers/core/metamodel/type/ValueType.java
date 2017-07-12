package org.javers.core.metamodel.type;

import org.javers.core.JaversBuilder;
import org.javers.core.diff.custom.CustomValueComparator;
import org.javers.core.json.JsonTypeAdapter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    public ValueType(Type baseJavaType) {
        super(baseJavaType);
    }

    ValueType(Type baseJavaType, CustomValueComparator customValueComparator) {
        super(baseJavaType, customValueComparator);
    }
}
