package org.javers.core.diff.changetype;

/**
 * When two objects being compared have different classes,
 * they can have different sets of properties.
 * <br/>
 * When both objects have the same class, all changes have PROPERTY_VALUE_CHANGED type.
 *
 * @since 5.5.0
 */
public enum PropertyChangeType {

    /**
     * When a property of the right object is absent in the left object.
     */
    PROPERTY_ADDED,

    /**
     * When a property of the left object is absent in the right object.
     */
    PROPERTY_REMOVED,

    /**
     * Regular value change &mdash; when a property is present in both objects.
     */
    PROPERTY_VALUE_CHANGED
}
