package org.javers.core.metamodel.type;

import org.javers.common.reflection.JaversMember;
import org.javers.core.metamodel.property.Property;

import java.util.function.Supplier;

import static org.javers.common.string.ToStringBuilder.typeName;

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

    private final boolean isListedAsShallowReference;

    public JaversProperty(Supplier<JaversType> propertyType, Property property) {
        this(propertyType, property.getMember(), property.hasTransientAnn(), property.hasShallowReferenceAnn(), property.getName(), property.isHasIncludedAnn(), false);
    }

    private JaversProperty(Supplier<JaversType> propertyType,
                           JaversMember member,
                           boolean hasTransientAnn,
                           boolean hasShallowReferenceAnn,
                           String name,
                           boolean hasIncludedAnn,
                           boolean isListedAsShallowReference) {
      super(member, hasTransientAnn, hasShallowReferenceAnn, name, hasIncludedAnn);
      this.propertyType = propertyType;
      this.isListedAsShallowReference = isListedAsShallowReference;
    }

    public <T extends JaversType> T getType() {
        return (T) propertyType.get();
    }

    public boolean isEntityType() {
        return getType() instanceof EntityType;
    }

    public boolean isValueObjectType() {
        return getType() instanceof ValueObjectType;
    }

    public boolean isPrimitiveOrValueType() {
        return getType() instanceof PrimitiveOrValueType;
    }

    public boolean isCustomType() {
        return getType() instanceof CustomType;
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
        return (hasShallowReferenceAnn()
                || getType() instanceof ShallowReferenceType)
                || isListedAsShallowReference;
    }

    public JaversProperty copyAsShallowReference() {
      return new JaversProperty(
              propertyType,
              getMember(),
              hasTransientAnn(),
              hasShallowReferenceAnn(),
              getName(),
              isHasIncludedAnn(),
              true);
    }

    @Override
    public String toString() {
        return getMember().memberType() + " " +
                getName() + (getMember().memberType().equals("Getter") ? "()" : "") + " " +
                typeName(getMember().getGenericResolvedType()) + ", " +
                "javersType: " + getType().getClass().getSimpleName() + ", " +
                (isShallowReference() ? "@ShallowReference, " : "")+
                "declared in: " + getDeclaringClass().getSimpleName();
    }
}
