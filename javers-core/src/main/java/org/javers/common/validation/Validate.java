package org.javers.common.validation;

/**
 * Set of utils for methods preconditions.
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Validate {

    /**
     * Checks if given argument is not null.
     *
     * @throws  IllegalArgumentException
     */
    public static void argumentIsNotNull(Object argument, String message) {
        if(argument == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @throws  IllegalArgumentException
     */
    public static void argumentIsNotNull(Object argument) {
        argumentIsNotNull(argument,"argument should not be null");
    }

    /**
     * @deprecated every time when an argument should be null a fairy dies ...
     */
    public static void argumentShouldBeNull(Object argument, String message) {
        if(argument != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * @throws  IllegalArgumentException
     */
    public static void argumentCheck(boolean argumentCondition, String message) {
        if (!argumentCondition) {
            throw new IllegalArgumentException(message);
        }

    }

    /**
     * @throws  IllegalStateException
     */
    public static void conditionFulfilled(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
