package org.javers.core.metamodel.type;

import org.javers.common.collections.WellKnownValueTypes;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.custom.CustomValueComparator;
import org.javers.core.json.JsonTypeAdapter;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Value class in a client's domain model is a simple value holder.
 * <br/>
 *
 * JaVers doesn't interact with internal properties of Values and treats them similarly to primitives.
 * <br/><br/>
 *
 * Two Values are compared using {@link Object#equals(Object)} so
 * it's highly important to implement it properly by comparing underlying fields.
 * <br/><br/>
 *
 * If you don't control the <code>equals()</code> implementation in a Value class you can still
 * provide a {@link CustomValueComparator}
 * and register it with {@link JaversBuilder#registerValue(Class, CustomValueComparator)}.
 * <br/><br/>
 *
 * It's highly advisable to implement Values as immutable objects,
 * like {@link BigDecimal} or {@link LocalDateTime}.
 * <br/><br/>
 *
 * Values are serialized to JSON using Gson defaults,
 * if it's not what you need, implement {@link JsonTypeAdapter} for custom serialization
 * and register it with {@link JaversBuilder#registerValueTypeAdapter(JsonTypeAdapter)}.
 *
 * @see <a href="http://javers.org/documentation/domain-configuration/#ValueType">http://javers.org/documentation/domain-configuration/#ValueType</a>
 * @see <a href="https://javers.org/documentation/diff-configuration/#custom-comparators">https://javers.org/documentation/diff-configuration/#custom-comparators</a>
 */
public class ValueType extends PrimitiveOrValueType {

    public ValueType(Type baseJavaType) {
        super(baseJavaType);
    }

    ValueType(Type baseJavaType, CustomValueComparator customValueComparator) {
        super(baseJavaType, customValueComparator);
    }

    @Override
    public String valueToString(Object value) {
        if (value == null){
            return "";
        }

        if (hasCustomValueComparator()) {
            return getValueComparator().toString(value);
        }

        if (WellKnownValueTypes.isValueType(value)){
            return value.toString();
        }

        return ReflectionUtil.reflectiveToString(value);
    }
}
