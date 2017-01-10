package org.javers.core.metamodel.object;

/**
 * @author bartosz walacik
 */
public interface OwnerContext {

    GlobalId getOwnerId();

    String getPath();

    boolean requiresObjectHasher();
}
