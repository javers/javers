package org.javers.repository.jql;

import org.javers.common.string.ToStringBuilder;

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

    @Override
    public String toString() {
        return "ShadowScope{"+ToStringBuilder.toStringSimple(
                "shadowScope", shadowScope,
                "maxGapsToFill", maxGapsToFill) +"}";
    }
}
