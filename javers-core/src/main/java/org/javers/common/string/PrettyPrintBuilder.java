package org.javers.common.string;

import java.util.List;

/**
 * @author bartosz.walacik
 */
public class PrettyPrintBuilder {

    private final StringBuilder out = new StringBuilder();

    public PrettyPrintBuilder(Object instance) {
        println(instance.getClass().getSimpleName() + "{");
    }

    public PrettyPrintBuilder addField(String fieldName, Object value) {
        println(String.format("  %s: %s", fieldName, value));
        return this;
    }

    public PrettyPrintBuilder addMultiField(String fieldName, List<?> values) {
        println(String.format("  %s:", fieldName));
        for (Object v : values) {
            println(String.format("    %s", v));
        }
        return this;
    }


    private void println(String text) {
        out.append(text).append("\n");
    }

    private void print(String text) {
        out.append(text);
    }

    public String build() {
        print("}");
        return out.toString();
    }
}
