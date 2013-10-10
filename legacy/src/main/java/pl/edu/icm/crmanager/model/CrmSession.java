package pl.edu.icm.crmanager.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.sedno.common.model.DataObject;

/**
 * @author bart
 */
public class CrmSession {
    private static final Logger logger = LoggerFactory.getLogger(CrmSession.class);
    
	private Revision revision;
	private boolean  isOpened;
	private Set<String> forceNewObjectIds = new  HashSet<String>();
	
	/** klucz dataObject.globalId */
    private HashMap<String, CrmProxy>  proxyRegister;   
	
	public CrmSession(String login) {
	    isOpened = true;
	    revision = new Revision(this, login);

	    proxyRegister = new HashMap<String, CrmProxy>(10);
	}
	
	public void panicClose() {
	    isOpened = false;
	}
	
	public void close() {
	    revision.resolveTransientReferences();
	    
	    revision.checkTransientReferences();
        isOpened = false;
	}
	
	public void registerProxy(CrmProxy p) {
	    p.setRevision(revision);
	    proxyRegister.put( getProxyRegisterKey(p.getInstance()), p);
	    //logger.debug(".. registering proxy:            " + ((DataObject)p).getGlobalId());
	}
	
    public void deregisterProxy(CrmProxy p) {
        p.setRevision(null);
        p.setDetached(true);
	    proxyRegister.remove( getProxyRegisterKey(p.getInstance()) );
	    //logger.debug("####.. deregistering proxy " + ((DataObject)p).getGlobalId());
    }
    
    public CrmProxy getRegisteredProxy(DataObject forInstance) {
        if (proxyRegister == null) {
            return null;
        }
        return proxyRegister.get(getProxyRegisterKey(forInstance));
    }
    
    public Collection<CrmProxy> getRegisteredProxies() {
    	return proxyRegister.values();
    }
    
    public String getProxyRegisterKey(DataObject obj) {
        return obj.getGlobalId();
    }
    
    
    public void setForceNew(DataObject o) {
        forceNewObjectIds.add(o.getGlobalId());
    }
    
    //-- getters
    
    public boolean isForceNew(DataObject o) {
        return forceNewObjectIds.contains(o.getGlobalId());
    } 
    
    public int getRevisionId() {
        return revision.getIdRevision();
    }
    
    public Revision getRevision() {
        return revision;
    }   
    
    public boolean isOpened() {
        return isOpened;
    }
}
