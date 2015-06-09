package org.javers.common.string;

import java.util.List;

/**
 * @author bartosz.walacik
 */
public class PrettyPrintBuilder {

    private StringBuilder out = new StringBuilder();

    public PrettyPrintBuilder(Object instance) {
        println(instance.getClass().getSimpleName()+"{");
    }

    public PrettyPrintBuilder addField(String fieldName, Object value) {
        println("  " + fieldName + ": " + value);
        return this;
    }

    public PrettyPrintBuilder addMultiField(String fieldName, List<?> values) {
        println("  " + fieldName + ":");
        for (Object v : values) {
            println("    " + v);
        }
        return this;
    }


    private void println(String text) {
        out.append(text + "\n");
    }

    public String build() {
        println("}");
        return out.toString();
    }
}
