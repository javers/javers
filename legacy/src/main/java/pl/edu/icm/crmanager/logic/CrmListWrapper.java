package pl.edu.icm.crmanager.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.NotImplementedException;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * 
 * @author bart
 */
public class CrmListWrapper extends CrmCollectionWrapper implements List{

    protected  List<CrmProxy> proxyMirror; 
    
    public CrmListWrapper (Revision revision, DataObject node, String getterName, Collection instance, Class proxyClass) {
        super(revision, node, getterName, instance, proxyClass);
    }
    
    @Override
    protected void checkType(Collection instance) {
        if (!List.class.isAssignableFrom(instance.getClass())) {
            throw new CrmRuntimeException("CrmSetWrapper - instance collection is not a List: "+ instance.getClass());
        }
    }
    
    @Override
    protected void initProxyMirror() {      
        proxyMirror = new ArrayList<CrmProxy>(instance.size());
        
        for (int i=0; i<instance.size(); i++) {
            proxyMirror.add(createProxy( getInstanceAt(i)) );
        }
    }
    
    @Override
    protected Collection getProxyMirrorValues() {
        return proxyMirror;
    }

    private DataObject getInstanceAt(int index) {
        return ((List<DataObject>)instance).get(index);
    }
       
    private CrmProxy getProxyAt(int index) {
        return proxyMirror.get(index);
    }
    
    //--
    
    @Override
    public boolean addAll(int index, Collection c) {
         throw new NotImplementedException();
    }
    
    @Override
    public Object get(int index) {
        return proxyMirror.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        if (getInstanceAt(index) != null) {
            registerRemove(getInstanceAt(index), getProxyAt(index));
        }
        
        registerAdd((DataObject)element);  
        ((List)instance).set(index, CrmReflectionUtil.unproxyC((DataObject)element));
        proxyMirror.set(index, createProxy((DataObject)element));
        
        return null;
    }

    @Override
    public void add(int index, Object element) {
        registerAdd((DataObject)element);  
        ((List)instance).add(index, CrmReflectionUtil.unproxyC((DataObject)element)); 
        proxyMirror.add(index, createProxy((DataObject)element));
    }

    @Override
    public Object remove(int index) { 
        if (getInstanceAt(index) != null) {
            registerRemove(getInstanceAt(index), getProxyAt(index));
        }
        
        
        proxyMirror.remove(index);
        return ((List)instance).remove(index);      
    }

    @Override
    public boolean remove(Object o) {
        int idx = indexOf(o);
        
        if (idx == -1)
            return false;
        
        remove(idx);
        
        return true;
    }
    
    @Override
    public int indexOf(Object o) {
        return ((List)instance).indexOf(o) ;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new NotImplementedException();        
    }

    @Override
    public ListIterator listIterator() {
        return proxyMirror.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return proxyMirror.listIterator(index);
    }

    @Override
    public Iterator iterator() {
        return proxyMirror.iterator();
    }
    
    @Override
    public List subList(int fromIndex, int toIndex) {
        return proxyMirror.subList(fromIndex, toIndex);
    }

    @Override
    public boolean contains(Object o) {
        throw new NotImplementedException();  
    }

    @Override
    public boolean add(Object e) {
        add(size(),e);
        return true;
    }


    @Override
    public boolean containsAll(Collection c) {
        return instance.contains(c);
    }

    @Override
    public boolean addAll(Collection c) {
        for (Object element : c) {
            add(element);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new NotImplementedException();  
    }

    @Override
    public boolean retainAll(Collection c) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clear() {
        for (int i=0; i<size(); i++) {
            registerRemove(getInstanceAt(i), getProxyAt(i));
        }
        instance.clear();
        proxyMirror.clear();
    }

}
