package org.javers.core.metamodel.type;

import org.javers.core.json.JsonTypeAdapter;
import org.joda.time.LocalDateTime;
import org.javers.core.json.JsonTypeAdapter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import org.javers.core.JaversBuilder;

/**
 * Value class in client's domain model. Simple value holder.
 * <br/>
 *
 * JaVers do not interact with internal properties of this type but treats its similarly to primitives.
 * <br><br>
 *
 * Two Value instances are compared using equals() so
 * its highly important to implement it properly by comparing underlying field (or fields).
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
}
