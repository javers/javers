package org.javers.common.reflection;

/**
 * @author bartosz walacik
 */
public interface ArgumentResolver {
    Object resolve(Class argType);
}
