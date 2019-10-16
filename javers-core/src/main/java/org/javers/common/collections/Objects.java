package org.javers.common.collections;

import org.javers.common.validation.Validate;

import java.util.function.BiFunction;

public class Objects {

    private static class NullSafetyEqualsWrapper implements BiFunction<Object, Object, Boolean> {
        private BiFunction<Object, Object, Boolean> delegate;

        private NullSafetyEqualsWrapper(BiFunction<Object, Object, Boolean> delegate) {
            Validate.argumentIsNotNull(delegate);
            this.delegate = delegate;
        }

        @Override
        public Boolean apply(Object o1, Object o2) {
            if (o1 == null && o2 == null) {
                return true;
            }

            if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
                return false;
            }

            return delegate.apply(o1, o2);
        }
    }

    public static BiFunction<Object, Object, Boolean> nullSafetyWrapper(BiFunction<Object, Object, Boolean> unsafeEquals) {
        return new NullSafetyEqualsWrapper(unsafeEquals);
    }
}
