package org.javers.common.string;

public class Strings {
    public static boolean isNonEmpty(String val) {
        return val != null && !val.isEmpty();
    }

    public static String emptyIfNull(String val) {
        if (val == null) {
            return "";
        }
        return val;
    }
}
