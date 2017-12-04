package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.Property;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class property with JaversType
 *
 * @author bartosz.walacik
 */
public class JaversProperty extends Property {
    /**
     * Supplier prevents stack overflow exception when building JaversType
     */
    private final Supplier<JaversType> propertyType;

    private final Optional<Function<?, String>> toString;

    public JaversProperty(Supplier<JaversType> propertyType, Optional<Function<?, String>> toString, Property property) {
        super(property.getMember(),  property.hasTransientAnn(), property.hasShallowReferenceAnn(), property.getName());
        this.propertyType = propertyType;
        this.toString = toString;
    }

    public <T extends JaversType> T getType() {
        return (T) propertyType.get();
    }

    public Function<?, String> getToString() {
        return toString.orElse(Object::toString);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JaversProperty that = (JaversProperty) o;
        return super.equals(that) && this.getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isShallowReference(){
        return (hasShallowReferenceAnn() ||
                getType() instanceof ShallowReferenceType);
    }
}
