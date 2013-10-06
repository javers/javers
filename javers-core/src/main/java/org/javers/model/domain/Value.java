package org.javers.model.domain;

/**
 * Wrapper for client's primitives and Value Objects,
 * holds original value and its JSON serialized form.
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
     * Hydrated (fresh) Value needs to be dehydrated before persisting.
     * When in dehydrated state - Value is serialized to JSON
     */
    public boolean isHydrated() {
        return isHydrated;
    }

    /**
     * original value
     */
    public Object getValue() {
        return value;
    }

    /**
     * value serialized to JSON
     */
    public String getJson() {
        return json;
    }
}
