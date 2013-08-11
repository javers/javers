package org.javers.core.validation;

/**
 * Set of methods validate preconditions.
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class Validate {


    /**
     * Check if object is not null.
     */
    public static void isNotNull(Object objectToValidate, String message) {
        if(objectToValidate == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
