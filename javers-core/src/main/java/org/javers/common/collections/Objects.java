package org.javers.common.collections;

/**
 * @author bartosz walacik
 */
public class Objects {

    /**
     * like Guava Objects.equal(Object a, Object b)
     */
    public static boolean nullSafeEquals(Object one, Object other) {
        if (one == null && other == null) {
            return true;
        }

        if (one != null && other != null) {
            return one.equals(other);
        }

        return false;
    }
}
