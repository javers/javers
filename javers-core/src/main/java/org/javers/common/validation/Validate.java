package org.javers.common.validation;

/**
 * Set of utils for methods preconditions.
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Validate {
    /**
     * Checks if given argument is not null.
     */
    public static void argumentIsNotNull(Object argument, String message) {
        if(argument == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
