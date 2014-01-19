package org.javers.model.mapping.type;

import java.lang.reflect.Type;

/**
 * Map where both keys and values
 * should be of {@link PrimitiveType} or {@link ValueType}.
 * <p/>
 *
 * Javers doesn't support complex maps with ValueObjects or Entities
 *
 * @author bartosz walacik
 */
public class MapType extends JaversType {
    private EntryClass entryClass;

    public MapType(Type baseJavaType) {
        super(baseJavaType);

        if (getActualClassTypeArguments().size() == 2) {
            entryClass = new EntryClass(getActualClassTypeArguments().get(0), getActualClassTypeArguments().get(1));
        }
    }

    /**
     * not null only if both Key and Value type arguments are actual Classes
     */
    public EntryClass getEntryClass() {
        return entryClass;
    }
}
