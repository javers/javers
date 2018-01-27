package org.javers.core.metamodel.type;

import org.javers.core.metamodel.property.Property;
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

    public JaversProperty(Supplier<JaversType> propertyType, Property property) {
        super(property.getMember(),  property.hasTransientAnn(), property.hasShallowReferenceAnn(), property.getName(), property.isHasIncludedAnn());
        this.propertyType = propertyType;
    }

    public <T extends JaversType> T getType() {
        return (T) propertyType.get();
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
