package org.javers.common.collections;

import org.javers.common.validation.Validate;

/**
 * @author bartosz walacik
 */
public class Optional<T> {
    private static Optional EMPTY = new Optional();

    private T reference;

    private Optional() {
    }

    private Optional(T reference) {
        Validate.argumentIsNotNull(reference);
        this.reference = reference;
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }

    public static <T> Optional<T> of(T reference) {
        return new Optional(reference);
    }

    public static <T> Optional<T> fromNullable(T nullOrReference) {
        if (nullOrReference == null) {
            return empty();
        }

        return of(nullOrReference);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public boolean isPresent(){
        return !isEmpty();
    }

    /**
     *
     * @throws IllegalStateException if the instance is empty
     */
    public T get(){
        if (isEmpty()) {
            throw new IllegalStateException("can't get() from empty optional");
        }
        return reference;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (reference != null) consumer.consume(reference);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Optional other = (Optional) o;

        if (this.isEmpty() && other.isEmpty()){
            return true;
        }

        if (this.isPresent() && other.isPresent()){
            return reference.equals(other.reference);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return reference != null ? reference.hashCode() : 0;
    }
}
