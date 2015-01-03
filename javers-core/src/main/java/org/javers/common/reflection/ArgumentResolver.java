package org.javers.common.reflection;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public interface ArgumentResolver {
    Object resolve(Class argType);
}
