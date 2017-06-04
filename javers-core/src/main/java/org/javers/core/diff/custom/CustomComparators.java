package org.javers.core.diff.custom;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author pszymczyk
 */
public class CustomComparators {

    private final List<CustomToNativeAppenderAdapter<?, ? extends PropertyChange>> customComparators;
    private final List<CustomToNativeAppenderAdapter<?, ValueChange>> valueChangeCustomComparators;

    public CustomComparators() {
        this.customComparators = new ArrayList<>();
        this.valueChangeCustomComparators = new ArrayList<>();
    }

    public <T> void registerCustomComparator(CustomPropertyComparator<T, ? extends PropertyChange> comparator, Class<T> customType) {
        customComparators.add(new CustomToNativeAppenderAdapter(comparator, customType));
    }

    public <T> void registerValueChangeComparator(CustomPropertyComparator<T, ValueChange> comparator, Class<T> customType) {
        valueChangeCustomComparators.add(new CustomToNativeAppenderAdapter(comparator, customType));
    }

    public Optional<CustomToNativeAppenderAdapter<?, ValueChange>> valueChangeCustomComparatorForClass(Class<?> clazz) {
        return valueChangeCustomComparators.stream()
                .filter(c -> c.supports(clazz))
                .findFirst();
    }

    public Collection<? extends PropertyChangeAppender> getAllComparators() {
        List<PropertyChangeAppender> customComparators = new ArrayList<>(this.customComparators);
        customComparators.addAll(valueChangeCustomComparators);
        return customComparators;
    }
}
