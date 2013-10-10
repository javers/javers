package pl.edu.icm.crmanager.logic;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.model.DataObject;

import java.util.*;


/**
 * @author bart
 */
public class CrmSetWrapper extends CrmCollectionWrapper implements Set {
    private static final Logger logger = LoggerFactory.getLogger(CrmSetWrapper.class);
	
    protected  Map<String, CrmProxy> proxyMirror; 
    
    public CrmSetWrapper (Revision revision, DataObject node, String getterName, Collection instance, Class proxyClass) {
        super(revision, node, getterName, instance, proxyClass);
    }
        
    
	@Override
	protected void checkType(Collection instance) {
	    if (!Set.class.isAssignableFrom(instance.getClass())) {
	        throw new CrmRuntimeException("CrmSetWrapper - instance collection is not a Set: "+ instance.getClass());
	    }
	}
	
    @Override
    protected Collection getProxyMirrorValues() {        
        return proxyMirror.values();
    }
	
	@Override
    protected void initProxyMirror() {      
        proxyMirror = new HashMap(instance.size());
        
        for (Object o : instance) {
            proxyMirror.put(getProxyMirrorMapKey((DataObject)o), createProxy((DataObject)o) );
        }
    }
	
	protected String getProxyMirrorMapKey(DataObject dataObject) {
	    if (dataObject == null) {
            return null;
        }
	    return dataObject.getGlobalId();
	}

	/*
	private static String getProxiedObjectKey(DataObject o) {

		if (CrmReflectionUtil.isCrmProxy(o)) {
			return ((CrmProxy)o).getInstance().getGlobalId(); 
		}
		else {
			return o.getGlobalId(); 
		}
	}*/
	
    protected void registerSetAdd(DataObject dataObject) {        
        if (dataObject != null) {
            registerAdd(dataObject);
            proxyMirror.put(getProxyMirrorMapKey(dataObject), createProxy((DataObject)dataObject));
        }
    }
    
    protected void registerSetRemove(DataObject o) {
        if (o != null) {             
            CrmProxy proxyToRemove = proxyMirror.get(  getProxyMirrorMapKey(o) ); 
            registerRemove(o, proxyToRemove);
            proxyMirror.remove( getProxyMirrorMapKey(o) );
        }
    }
	
	//--	
	
    private void registerAddAll(Collection c) {
        if (c != null) {
            for (Object o : c) {
                registerSetAdd((DataObject)o);
            }
        }
    }
	
	@Override
	public boolean add(Object o) {
		registerSetAdd((DataObject)o);
		return instance.add(CrmReflectionUtil.unproxyC((DataObject)o));
	}

	@Override
	public boolean addAll(Collection c) {
		registerAddAll(c);
		return instance.addAll(c);
	}

	@Override
	public void clear() {
		for (Object o : instance) {
			registerSetRemove((DataObject)o);
		}
		instance.clear();
	}

	@Override
	public boolean contains(Object o) {
		return instance.contains(CrmReflectionUtil.unproxyC((DataObject)o));
	}

	@Override
	public boolean containsAll(Collection c) {
		return instance.containsAll(c);
	}

	@Override
	public boolean remove(Object o) {
		registerSetRemove((DataObject)o);
		return instance.remove(CrmReflectionUtil.unproxyC((DataObject)o));
	}

	@Override
	public boolean removeAll(Collection c) {
		boolean ret = false;
		
		if (c != null) {
			for (Object o : c) {
				boolean sRet = remove(o);
				if (sRet) ret = true;
			}
			return ret;
		}
		
		return false;
	}

	@Override
    public Iterator iterator() {
        return proxyMirror.values().iterator();
    }
	
	@Override
	public boolean retainAll(Collection c) {
	    throw new NotImplementedException("retainAll() is not implemented!");
	}

	//-- setters

	//public void setNode(DataObject owner) {
	//	this.node = owner;
	//}
}
