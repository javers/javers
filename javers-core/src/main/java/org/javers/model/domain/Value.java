package org.javers.model.domain;

/**
 * Wrapper for client's primitives and Value Objects,
 * holds original value and its JSON serialized form.
 * <br/><br/>
 *
 * Value needs to be dehydrated before persisting.
 * Dehydration is simply serialization to JSON.
 *
 * @author bartosz walacik
 */
public class Value {
    private boolean isHydrated;
    private Object value;
    private String json;

    public Value(Object value) {
        isHydrated = true;
        this.value = value;
    }

    public void hydrate(Object value) {
        isHydrated = true;
        this.value = value;
    }

    public void dehydrate(String json) {
        isHydrated = false;
        this.json = json;
    }

    /**
     * True if Value is not serialized to JSON yet.
     */
    public boolean isHydrated() {
        return isHydrated;
    }

    /**
     * original Value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Value serialized to JSON
     */
    public String getJson() {
        return json;
    }
}
