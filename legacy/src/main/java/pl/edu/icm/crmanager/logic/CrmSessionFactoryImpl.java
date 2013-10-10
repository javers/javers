package pl.edu.icm.crmanager.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmSession;

/**
 * 
 * @author bart
 */
@Service("crmSessionFactory")
public class CrmSessionFactoryImpl implements CrmSessionFactory {
    public static ThreadLocal<CrmSession> sessions = new ThreadLocal<CrmSession>();
    Logger logger = LoggerFactory.getLogger(CrmSessionFactoryImpl.class);
    
    @Autowired
    public RevisionService revisionService;
    
    /**
     * static equiv of getCurrentSession()
     */
    public static CrmSession getCurrentSessionS() {
        if (sessions.get() == null || !sessions.get().isOpened())
            throw new CrmRuntimeException("No active crmSession binded to your thread");
        
        return sessions.get();
    }
    
    public boolean isActiveSession() {
    	if (sessions.get() == null)
    		return false;
    	
    	return sessions.get().isOpened();
    }
   
    public CrmSession getCurrentSession() {
        return getCurrentSessionS();
    }
    
    //@Override
    //public CrmSession startNewSession() {
   //    return startNewSession(null);
    //}
    
    @Override
    public CrmSession startNewSession(String login) {
        if (sessions.get() != null) {
            logger.error("Crm runtime error : Session already binded to your thread, calling closeCurrentSessionPanic()");
            closeCurrentSessionPanic();
        }
        
        CrmSession session = new CrmSession(login);       
        sessions.set(session);
        revisionService.persistNew(session.getRevision());
        return session;
    }
    
    @Override
    public void closeCurrentSessionIfFound() {
        if (sessions.get() != null) {
            closeCurrentSession();
        }
    }
    
    @Override
    public void closeCurrentSession() {
        CrmSession session = getCurrentSession();
        
        session.close();
        sessions.set(null);
        sessions.remove();
    }
    
    @Override
    public void closeCurrentSessionPanic() {
    	try {
	        CrmSession session = getCurrentSession();
	        
	        session.panicClose();
	        sessions.set(null);
                sessions.remove();
    	} catch (RuntimeException e) {
    		logger.warn("closeCurrentSessionPanic() didn't completed due to : "+ e.getClass()+" - "+e.getMessage());
		}
    }
    
    public void setRevisionService(RevisionService revisionService) {
        this.revisionService = revisionService;
    }
    
}
