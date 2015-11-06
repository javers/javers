package org.javers.core.metamodel.type;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * typeName to Type index
 *
 * @author bartosz.walacik
 */
class TypeMapperNames {
    private final Map<String, Type> typeNames = new ConcurrentHashMap<>();

    void scanClasspath(List<String> packageNames){

    }

    Type getByName(String typeName){
        return null;
    }

    void registerTypeName(String typeName, Type JavaType){

    }
}
