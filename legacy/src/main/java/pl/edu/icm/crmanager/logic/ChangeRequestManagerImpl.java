package pl.edu.icm.crmanager.logic;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.edu.icm.crmanager.diff.ExtractPersistentComponents;
import pl.edu.icm.crmanager.model.ChangeAction;
import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.CrmSession;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.model.Indexable;

import com.google.common.base.Preconditions;

/**
 * 
 * @author bart
 */
@Service("changeRequestManager")
public class ChangeRequestManagerImpl implements ChangeRequestManager {
    private static final Logger logger = LoggerFactory.getLogger(ChangeRequestManagerImpl.class);
    
    private static final String REVISION_CLASS = Revision.class.getName();
    
    @Autowired
    private CrmProxyFactory crmProxyFactory;
    
    @Autowired 
    private CrmSessionFactory crmSessionFactory;
    
    @Autowired
    private RevisionService revisionService;
 
    @Autowired
    private DataObjectDAO dataObjectDAO;
                
    @Override
    public Revision loadRevisionWithReferences(int revisionId) {
    	Revision rev = loadRevision(revisionId);
    	revisionService.initializeTransientReferences(rev);
    	return rev;
    }
    
    @Override
    public Revision addObjectAndAccept(DataObject domainObject, String login) {
    	
    	if (!domainObject.isTransient()) {
    		throw new IllegalArgumentException("addObjectAndAccept(): domainObject ["+domainObject.getGlobalId()+"] is not transient");
    	}
    	
        openCrmSession(login);
        Revision rev = getCurrentRevision();
        
        //attach(domainObject);            
        
	    getCurrentRevision().setRootModPointCombo(domainObject);
	    domainObject.accept(new NewObjectVisitor());
        
        closeCrmSessionWithAutoAccept();
        return rev;
    }
    
    @Override
    public Revision loadRevision(int revisionId) {
        Revision rev = dataObjectDAO.get(Revision.class, revisionId);
        Hibernate.initialize(rev.getChangeRequests());      
        return rev;
    }
    
	@Override
	public void acceptRevision(int revisionId, String acceptedBy) {
		revisionService.accept(getPersistedRevision(revisionId), acceptedBy);
	}

    @Override
    public void cancelRevision(int revisionId, String cancelledBy) {
        revisionService.cancel(getPersistedRevision(revisionId), cancelledBy);
    }
	
	@Override
	public <T extends DataObject> T attach(T domainObject) {
	    logger.debug("attaching rootModPoint :" + domainObject.getGlobalId() +" to crm session");
	    
	    getCurrentRevision().setRootModPointCombo(domainObject);
	    
	    resetModDate(domainObject);
	    return (T)crmProxyFactory.createRedoLogProxy(domainObject); 
	}

    @Override
    public <T extends DataObject> T attachForceNew(T domainObject) {
        CrmSession session = crmSessionFactory.getCurrentSession();
        session.setForceNew(domainObject);
        return attach(domainObject);
    }
    
    public <T extends DataObject> T attachComponent(T domainObject) {
    	//logger.debug("attaching component :" + domainObject.getGlobalId() +" to crm session");	    
	    
    	resetModDate(domainObject);
	    return (T)crmProxyFactory.createRedoLogProxy(domainObject); 
    }
    
    private void resetModDate(DataObject obj) {
        if (obj instanceof Indexable)
            ((Indexable) obj).resetModDate();    
    }
	
	@Override
	public void closeCrmSessionWithNoAccept() {
        revisionService.flushWithNoAccept(getCurrentRevision());	    
	    crmSessionFactory.closeCurrentSession();
	}
	
    @Override
    public void closeCrmSessionWithAutoAccept() {
        revisionService.flushWithAutoAccept(getCurrentRevision());       
        crmSessionFactory.closeCurrentSession();
    }
    
    @Override
    public <T extends DataObject> Revision doInCrmWithNoAccept(T domainObject, ChangeAction<T> action, String login) {
    	return doInCrm(domainObject, action, login, false);
    }
    
    @Override
    public <T extends DataObject> Revision doInCrmWithAutoAccept(T domainObject, ChangeAction<T> action, String login) {
    	return doInCrm(domainObject, action, login, true);
    }
      
    private <T extends DataObject> Revision doInCrm(T domainObject, ChangeAction<T> action, String login, boolean autoAccept) {
    	Preconditions.checkNotNull(domainObject);
    	Preconditions.checkNotNull(action);
    	
    	openCrmSession(login);
    	Revision rev = getCurrentRevision();
    	
    	T proxy = attach(domainObject);
    	
    	action.execute(proxy);
    	
    	if (autoAccept) {
    		closeCrmSessionWithAutoAccept();
    	} else {
    		closeCrmSessionWithNoAccept();
    	}
    	return rev;    	
    }
   
    @Override
    public <T extends DataObject> Revision deleteTree(T domainObject, String login) {
     	Preconditions.checkNotNull(domainObject);
    	Preconditions.checkNotNull(login);
    	
    	openCrmSession(login);
    	Revision rev = getCurrentRevision();
    	
        //--extractPersistentComponents
        ExtractPersistentComponents eVisitor = new ExtractPersistentComponents();        
        domainObject.accept(eVisitor);
        for(DataObject node : eVisitor.getResult()) {
    		logger.debug("marking node :" + node.getGlobalId() +" as DELETED");
    		getCurrentRevision().registerDelete(node);	
        }
    	
    	closeCrmSessionWithAutoAccept();
    	return rev;    	
    }
    
    @Override
    public int openCrmSession(String login) {
             
        CrmSession session = crmSessionFactory.startNewSession(login);
        return session.getRevisionId();
    }
	
    private Revision getPersistedRevision(int revisionId) {
        return dataObjectDAO.get(Revision.class, revisionId);
    }
    
    @Override
    public Revision getCurrentRevision() {
        return crmSessionFactory.getCurrentSession().getRevision();
    }
    
    @Override
    public boolean isActiveSession() {
    	return crmSessionFactory.isActiveSession();
    }
    
	@Override
	public List<Revision> getRootRevisions(DataObject domainObject) {
		
	   String ql = "from "+Revision.class.getName()+" r where r.rootModPointClass = ?1 and r.rootModPointId = ?2 order by r.idRevision asc";
	   
	   List<Revision> list =  dataObjectDAO.findByHQL(ql, domainObject.getWrappedClass().getName(), domainObject.getId());
	  
	   for (Revision r : list)
	       r.initialize();
	   
	   return list;	    	   	 
	}
	
	@Override
	public Revision getLastRootRevision(DataObject domainObject) {
		if (domainObject.isTransient()) {
			return null;
		}
		
		String ql = "from  "+REVISION_CLASS+" r where r.rootModPointClass = ?1 and r.rootModPointId = ?2 " +
				    "and   r.idRevision = (select max(r1.idRevision) from "+REVISION_CLASS+" r1 " +
				    "                      where  r1.rootModPointClass = r.rootModPointClass " +
				    "                      and    r1.rootModPointId = r.rootModPointId) ";
		
		List<Revision> list =  dataObjectDAO.findByHQL(ql, 
													   domainObject.getWrappedClass().getName(),
													   domainObject.getId());
		if (list.size() == 0) {
			return null;
		}
		
		
		if (list.size() > 1) {
			throw new RuntimeException("query ["+ql+"] should return zero or one row"); 
		}
		
		return list.get(0);
	}

	@Override
	public List<ChangeRequest> getPendingRequests(DataObject domainObject) {
	    throw new NotImplementedException();
	}

	@Override
	public <T extends DataObject> T previewChanges(T domainObject) {
	    throw new NotImplementedException();
	}
	
	public void setCrmProxyFactory(CrmProxyFactory crmProxyFactory) {
        this.crmProxyFactory = crmProxyFactory;
    }
	
	public void setCrmSessionFactory(CrmSessionFactory crmSessionFactory) {
        this.crmSessionFactory = crmSessionFactory;
    }

    public void setRevisionService(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    public void setDataObjectDAO(DataObjectDAO dataObjectDAO) {
        this.dataObjectDAO = dataObjectDAO;
    }

}
