package org.javers.repository.jql;

/**
 * @author bartosz.walacik
 */
class InstanceFilter extends Filter{
    private final Object instance;

    InstanceFilter(Object instance) {
        this.instance = instance;
    }

    Object getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "instance=" + instance;
    }
}
