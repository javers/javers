package org.javers.model.mapping;

import org.javers.core.json.JsonTypeAdapter;
import org.joda.time.LocalDateTime;

import java.math.BigDecimal;

/**
 * Simple, immutable value holder.
 * Javers do not interact with internal properties of this type but treats its similarly to primitives.
 * <p/>
 *
 * Two ImmutableValues are compared 'by value' using equals() so
 * its highly important to implement it properly by comparing underlying value(s).
 * <p/>
 *
 * Examples: {@link BigDecimal}, {@link LocalDateTime}
 * <p/>
 *
 * See {@link JsonTypeAdapter} for JSON serialization tuning
 *
 * @author bartosz walacik
 */
public class ImmutableValue extends ManagedClass {
    public ImmutableValue(Class sourceClass) {
        super(sourceClass);
    }
}
