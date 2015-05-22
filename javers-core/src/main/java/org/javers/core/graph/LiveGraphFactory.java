package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.type.TypeMapper;

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


}
