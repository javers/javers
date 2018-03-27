package org.javers.common.string;

import org.javers.core.JaversCoreProperties.PrettyPrintDateFormats;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofPattern;

public class PrettyValuePrinter {
    private final Map<Class<? extends Temporal>, DateTimeFormatter> dateFormatters = new HashMap<>();

    private static final PrettyValuePrinter defaultInstance = new PrettyValuePrinter(new PrettyPrintDateFormats());

    public static PrettyValuePrinter getDefault() {
        return defaultInstance;
    }

    public PrettyValuePrinter(PrettyPrintDateFormats prettyPrintDateFormats) {
        for (Class<? extends Temporal> classKey : prettyPrintDateFormats.getFormats().keySet()) {
            dateFormatters.put(classKey, ofPattern(prettyPrintDateFormats.getFormats()
                    .get(classKey)));
        }
    }

    public String formatWithQuotes(Object value) {
        return "'" + format(value) + "'";
    }

    public String format(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof TemporalAccessor) {
            DateTimeFormatter formatter = dateFormatters.get(value.getClass());
            if (formatter != null) {
                return formatter.format((TemporalAccessor) value);
            }
        }

        if ( value instanceof Set) {
            return ToStringBuilder.setToString((Set)value);
        }

        if ( value instanceof List) {
            return ToStringBuilder.listToString((List)value);
        }

        if ( value instanceof Optional) {
            if ( ((Optional)value).isPresent()) {
                return format(((Optional) value).get());
            } else {
                return "empty";
            }
        }
        return value.toString();
    }
}
