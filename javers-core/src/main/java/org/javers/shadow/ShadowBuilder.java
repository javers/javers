package org.javers.shadow;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.property.Property;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class ShadowBuilder {
    private final CdoSnapshot cdoSnapshot;
    private Object shadow;
    private Set<Wiring> wirings = new HashSet<>();

    ShadowBuilder(CdoSnapshot cdoSnapshot) {
        this.cdoSnapshot = cdoSnapshot;
    }

    void withStub(Object shadowStub) {
        this.shadow = shadowStub;
    }

    Object getShadow() {
        return shadow;
    }

    CdoSnapshot getCdoSnapshot() {
        return cdoSnapshot;
    }

    void addReferenceWiring(Property property, ShadowBuilder targetShadow) {
        this.wirings.add(new ReferenceWiring(property, targetShadow));
    }

    void wire() {
        wirings.forEach(Wiring::wire);
    }

    private abstract class Wiring {
        final Property property;

        Wiring(Property property) {
            this.property = property;
        }

        abstract void wire();
    }

    private class ReferenceWiring extends Wiring {
        final ShadowBuilder target;

        ReferenceWiring(Property property, ShadowBuilder targetShadow) {
            super(property);
            this.target = targetShadow;
        }

        @Override
        void wire() {
            property.set(shadow, target.shadow);
        }
    }
}
