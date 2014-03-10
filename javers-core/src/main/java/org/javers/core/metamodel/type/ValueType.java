package org.javers.core.metamodel.type;

import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Simple value holder.
 * Javers do not interact with internal properties of this type but treats its similarly to primitives.
 * <p/>
 *
 * Two Values are compared using equals() so
 * its highly important to implement it properly by comparing underlying fields.
 * <p/>
 *
 * It's highly advisable to implement Values as immutable objects, for example:
 * {@link BigDecimal}, {@link LocalDateTime}
 * <p/>
 *
 * Values are serialized to JSON using Gson defaults,
 * if it's not what you need, implement {@link org.javers.core.json.JsonTypeAdapter} for custom serialization
 *
 * @see org.javers.core.json.JsonConverter
 *
 * @author bartosz walacik
 */
public class ValueType extends PrimitiveOrValueType {
    public ValueType(Type baseJavaType) {
        super(baseJavaType);
    }
}
