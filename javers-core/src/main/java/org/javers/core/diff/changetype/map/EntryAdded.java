package org.javers.core.diff.changetype.map;

import org.javers.common.string.PrettyValuePrinter;

import static org.javers.common.string.ToStringBuilder.format;

/**
 * @author bartosz walacik
 */
public class EntryAdded extends EntryAddOrRemove {

    public EntryAdded(Object key, Object value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return prettyPrint(PrettyValuePrinter.getDefault());
    }

    @Override
    protected String prettyPrint(PrettyValuePrinter valuePrinter) {
        return valuePrinter.formatWithQuotes(getKey()) + " -> " +
               valuePrinter.formatWithQuotes(getValue()) + " added";
    }
}
