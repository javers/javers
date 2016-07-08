package org.javers.common.string;

/**
 * @author bartosz.walacik
 */
public class RegexEscape {
    private static final String TO_ESCAPE = "\\.[]{}()*+-?^$|";

    public static String escape(String literal) {
        String escaped = literal;
        for (int i=0; i<TO_ESCAPE.length(); i++) {
            char c = TO_ESCAPE.charAt(i);
            escaped = escaped.replace(c+"", "\\"+c);
        }
        return escaped;
    }
}
