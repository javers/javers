package org.javers.repository.jql;

class ShadowScopeDefinition {
    private final ShadowScope shadowScope;
    private final int maxGapsToFill;

    ShadowScopeDefinition(ShadowScope shadowScope, int maxGapsToFill) {
        this.shadowScope = shadowScope;
        this.maxGapsToFill = maxGapsToFill;
    }

    ShadowScope getShadowScope() {
        return shadowScope;
    }

    int getMaxGapsToFill() {
        return maxGapsToFill;
    }
}
