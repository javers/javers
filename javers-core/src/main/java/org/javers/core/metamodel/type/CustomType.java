package org.javers.core.metamodel.type;

import java.lang.reflect.Type;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.JaversBuilder;

/**
 * Custom type in client's domain model.
 * <br/><br/>
 *
 * JaVers treats a custom type as a black box
 * and doesn't take any assumptions about its content or behavior.
 * It's a 'not modeled' type, somehow similar to unbounded wildcard type {@code <?>}.
 * <br/><br/>
 *
 * Values of custom type are compared by a {@link CustomPropertyComparator}
 * and registering it (see {@link JaversBuilder#registerCustomComparator(CustomPropertyComparator, Class)}
 * is the only way to map a custom type.
 * <br/><br/>
 *
 * Custom types are serialized to JSON using Gson defaults,
 * if it's not what you need, implement {@link JsonTypeAdapter} for custom serialization
 * and register it with {@link JaversBuilder#registerValueTypeAdapter(JsonTypeAdapter)}
 *
 * @author bartosz walacik
 */
public class CustomType extends JaversType {
    public CustomType(Type baseJavaType) {
        super(baseJavaType);
    }
}