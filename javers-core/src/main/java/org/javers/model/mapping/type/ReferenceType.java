package org.javers.model.mapping.type;

public abstract class ReferenceType extends JaversType {
    protected ReferenceType(Class baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public boolean isReferencedType() {
        return true;
    }
}
