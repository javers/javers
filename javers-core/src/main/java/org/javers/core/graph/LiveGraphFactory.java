package org.javers.core.graph;

import org.javers.core.diff.ObjectGraph;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.type.TypeMapper;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class LiveGraphFactory {
    private final TypeMapper typeMapper;
    private final LiveCdoFactory liveCdoFactory;
    private ObjectAccessHook objectAccessHook;

    public LiveGraphFactory(TypeMapper typeMapper, LiveCdoFactory liveCdoFactory, ObjectAccessHook objectAccessHook) {
        this.typeMapper = typeMapper;
        this.liveCdoFactory = liveCdoFactory;
        this.objectAccessHook = objectAccessHook;
    }

    /**
     * delegates to {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public LiveGraph createLiveGraph(Object handle) {
        Object wrappedHandle = wrapTopLevelContainer(handle);

        return new ObjectGraphBuilder(typeMapper, liveCdoFactory).buildGraph(wrappedHandle);
    }

    public <T> ObjectGraph createLiveGraph(Collection<T> handle, Class<T> clazz) {
        Object wrappedHandle = wrapTopLevelContainer(handle);

        return new CollectionsGraphBuilder(typeMapper, liveCdoFactory).buildGraph(wrappedHandle, clazz);
    }

    public Cdo createCdo(Object cdo){
        return liveCdoFactory.create(cdo, null);
    }

    private Object wrapTopLevelContainer(Object handle){
        if (handle instanceof  Map){
            return new MapWrapper((Map)handle);
        }
        if (handle instanceof  List){
            return new ListWrapper((List)handle);
        }
        if (handle instanceof  Set){
            return new SetWrapper((Set)handle);
        }
        if (handle.getClass().isArray()){
            return new ArrayWrapper(convertToObjectArray(handle));
        }
        return handle;
    }

    public static Class getMapWrapperType(){
        return MapWrapper.class;
    }

    public static Class getSetWrapperType(){
        return SetWrapper.class;
    }

    public static Class getListWrapperType(){
        return ListWrapper.class;
    }

    public static Class getArrayWrapperType() {
        return ArrayWrapper.class;
    }

    private class MapWrapper{
        private final Map<Object,Object> map;

        private MapWrapper(Map map) {
            this.map = map;
        }
    }

    private class SetWrapper{
        private final Set<Object> set;

        private SetWrapper(Set set) {
            this.set = set;
        }
    }

    private class ListWrapper{
        private final List<Object> list;

        public ListWrapper(List list) {
            this.list = list;
        }
    }

    private class ArrayWrapper {
        private final Object[] array;

        public ArrayWrapper(Object[] objects) {
            this.array = objects;
        }
    }

    //this is primarily used for copying array of primitives to array of objects
    //as there seems be no legal way for casting
    private static Object[] convertToObjectArray(Object obj) {
        if (obj instanceof Object[]) {
            return (Object[]) obj;
        }
        int arrayLength = Array.getLength(obj);
        Object[] retArray = new Object[arrayLength];
        for (int i = 0; i < arrayLength; ++i){
            retArray[i] = Array.get(obj, i);
        }
        return retArray;
    }

}
