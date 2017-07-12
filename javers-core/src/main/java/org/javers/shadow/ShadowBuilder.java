package org.javers.shadow;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ShadowBuilder {
    private final CdoSnapshot cdoSnapshot;
    private Object shadow;
    private Set<Wiring> wirings = new HashSet<>();

    ShadowBuilder(CdoSnapshot cdoSnapshot, Object shadow) {
        this.cdoSnapshot = cdoSnapshot;
        this.shadow = shadow;
    }

    void withStub(Object shadowStub) {
        this.shadow = shadowStub;
    }

    Object getShadow() {
        return shadow;
    }

    /**
     * nullable
     */
    CdoSnapshot getCdoSnapshot() {
        return cdoSnapshot;
    }

    void addReferenceWiring(JaversProperty property, ShadowBuilder targetShadow) {
        this.wirings.add(new ReferenceWiring(property, targetShadow));
    }

    void addEnumerableWiring(JaversProperty property, Object targetWithShadows) {
        this.wirings.add(new EnumerableWiring(property, targetWithShadows));
    }

    void wire() {
        wirings.forEach(Wiring::wire);
    }

    private abstract class Wiring {
        final JaversProperty property;

        Wiring(JaversProperty property) {
            this.property = property;
        }

        abstract void wire();
    }

    private class ReferenceWiring extends Wiring {
        final ShadowBuilder target;

        ReferenceWiring(JaversProperty property, ShadowBuilder targetShadow) {
            super(property);
            this.target = targetShadow;
        }

        @Override
        void wire() {
            property.set(shadow, target.shadow);
        }
    }

    private class EnumerableWiring extends Wiring {
        final Object targetWithShadows;

        EnumerableWiring(JaversProperty property, Object targetWithShadows) {
            super(property);
            this.targetWithShadows = targetWithShadows;
        }

        @Override
        void wire() {
            EnumerableType propertyType = property.getType();

            Object targetContainer = propertyType.map(targetWithShadows, (valueOrShadow) -> {
                if (valueOrShadow instanceof ShadowBuilder) {
                    //injecting reference to shadow
                    return ((ShadowBuilder) valueOrShadow).shadow;
                }
                return valueOrShadow; //vale is passed as is
            });

            property.set(shadow, targetContainer);
        }
    }
}
