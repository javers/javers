package org.javers.core.metamodel.scanner;

import org.javers.core.metamodel.type.ContainerType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ValueObjectType;


public abstract class CascadeFallbackEvaluator {

    public final boolean shouldCascade(final JaversType member) {
        return evaluateFallbackConfiguration(member);
    }

    protected abstract boolean evaluateFallbackConfiguration(final JaversType member);

    static class NoneFallbackEvaluator extends CascadeFallbackEvaluator {

        @Override
        protected boolean evaluateFallbackConfiguration(final JaversType member) {
            return false;
        }
    }

    static class AllFallbackEvaluator extends CascadeFallbackEvaluator {

        @Override
        protected boolean evaluateFallbackConfiguration(final JaversType member) {
            return true;
        }
    }

    static class OnlyValueObjectFallbackEvaluator extends CascadeFallbackEvaluator {

        @Override
        protected boolean evaluateFallbackConfiguration(final JaversType type) {
            if (type instanceof ContainerType) {
                return evaluateFallbackConfiguration(((ContainerType) type).getItemJaversType());
            }
            return type instanceof ValueObjectType;
        }
    }
}
