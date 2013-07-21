package pl.edu.icm.crmanager.logic;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.model.CrmSession;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject;

/**
 * Metody wołane z klas generowanych przez Javassist
 * 
 * @author bart
 */
public class CrmProxyUtil {
    private static final Logger logger = LoggerFactory.getLogger(CrmProxyUtil.class);
    
    public static CrmCollectionWrapper buildColWrapper(Revision revision, DataObject node, String getterName, Collection instance, Class proxyClass) {
        if (instance instanceof Set) {
            return new CrmSetWrapper(revision, node, getterName, instance, proxyClass);
        } else if (instance instanceof List) {
            return new CrmListWrapper(revision, node, getterName, instance, proxyClass);
        } else {
            throw new CrmRuntimeException("This type of persistence Collection ("+instance.getClass().getSimpleName()+" is not supported");
        }
    }
    
	public static boolean isProxyInSync(CrmProxy proxy, DataObject superInstance) {
		if (superInstance == null) return true;
		
		if (proxy == null) return false;
		
		CrmSession session = CrmSessionFactoryImpl.getCurrentSessionS();
		
		return (proxy.getInstance() == superInstance && proxy.getRevision() == session.getRevision());
	}

	public static void checkAccessInSetter(CrmProxy proxy) {
		if (proxy.isDetached()) {
		    throw new CrmRuntimeException("Trying to change detached CrmProxy ["+((DataObject)proxy).getGlobalId()+"]");  
		}
	}
	
	/**
	 * Tworzy lub odzyskuje obiekt proxy i dowiązuje go do bieżącej sesji <br/> 
 	 *
	 * Wykonuje operacje {@link CrmSession#registerProxy(CrmProxy)}
	 *  
	 * @param superInstance moze byc null, moze juz byc CrmProxy - wtedy reattach
	 * @param previousProxy proxy, które zostało nadpisane w setterze, do odpięcia
	 */
	public static CrmProxy getBindedProxy (DataObject superInstance, Class proxyClass, CrmProxy previousProxy) {
	    CrmSession session = CrmSessionFactoryImpl.getCurrentSessionS();
	    
		CrmProxy proxy;
		
		if (previousProxy != null) {
			detachProxy(previousProxy);
		}
		
		if (superInstance == null) {
			proxy = null;
		}
		else if (session.getRegisteredProxy(superInstance) != null ) {
	        //reuse
//		    logger.debug("bindProxy(): reusing registered proxy for superInstance ["+superInstance.getGlobalId()+"]");
			proxy = session.getRegisteredProxy(superInstance);
	/*	} else if (CrmReflectionUtil.isCrmProxy(superInstance)) { 
            //reattach
		    logger.debug("bindProxy(): reattaching unregistered proxy for superInstance ["+superInstance.getGlobalId()+"]");         
			proxy = (CrmProxy)superInstance;
			session.registerProxy(proxy);*/
		} else {
		    //create
		    if (proxyClass == null) {
		        throw new IllegalArgumentException("getBindedProxy() : proxyClass == null, superInstance:"+ superInstance.getGlobalId());
		    }
//		    logger.debug("bindProxy(): creating proxy for superInstance ["+superInstance.getGlobalId()+"]");        
			try {
				proxy = (CrmProxy)proxyClass.newInstance();
				if (superInstance.isTransient() || session.isForceNew(superInstance)) {
				    superInstance.accept(new NewObjectVisitor());
				}
			} catch (Exception e) {
				throw new CrmRuntimeException("error at instantiating CrmProxy for superInstance ["+superInstance.getGlobalId()+"]", e);
			}
			
			proxy.setInstance(superInstance);
			session.registerProxy(proxy);
		}		
		return proxy;
	}
	
	public static void detachProxy(CrmProxy proxy) {
	    CrmSessionFactoryImpl.getCurrentSessionS().deregisterProxy(proxy);
	}
	
	/*
	public static Object invokePrivateGetter(String getterName, Object onObject) {
		Method getter = ReflectionUtil.findGetter(getterName, onObject.getClass());
		return ReflectionUtil.invokeGetterEvenIfPrivate(getter, onObject);
	}*/
}
