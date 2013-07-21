package pl.edu.icm.crmanager.logic;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmSession;

/**
 * @author bart
 */
public interface CrmSessionFactory {

	/**
	 * @throws CrmRuntimeException("No active crmSession binded to your thread")
	 */
    public CrmSession getCurrentSession();
    
    //public CrmSession startNewSession();
    
    public CrmSession startNewSession(String login);

    public void closeCurrentSessionIfFound();

    public void closeCurrentSession();
    
    /**
     * używana w sytuacji awaryjnej
     */
    public void closeCurrentSessionPanic();
    
    /**
     * true jeśli jest aktywna sesja CRM
     */
    public boolean isActiveSession();

}