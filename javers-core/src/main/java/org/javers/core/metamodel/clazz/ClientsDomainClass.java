package org.javers.core.metamodel.clazz;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Reflects one class in a client's domain model.
 *
 * @author bartosz walacik
 */
public abstract class ClientsDomainClass {
    private final Class clientsClass;

    ClientsDomainClass(Class clientsClass) {
        argumentIsNotNull(clientsClass);
        this.clientsClass = clientsClass;
    }

    public Class getClientsClass() {
        return clientsClass;
    }

    public boolean isInstance(Object cdo) {
        argumentIsNotNull(cdo);
        return (clientsClass.isAssignableFrom(cdo.getClass()));
    }

    /**
     * clientsClass.name
     */
    public String getName() {
        return clientsClass.getName();
    }

    /**
     * 'Entity', 'ValueObject' or 'Value'
     */
    public String getSimpleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ClientsDomainClass)) {
            return false;
        }

        ClientsDomainClass that = (ClientsDomainClass) o;
        return clientsClass.equals(that.clientsClass);
    }

    @Override
    public int hashCode() {
        return clientsClass.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"("+getClientsClass().getSimpleName()+")";
    }
}
