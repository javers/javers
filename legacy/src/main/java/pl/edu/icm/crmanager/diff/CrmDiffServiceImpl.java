package pl.edu.icm.crmanager.diff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.logic.*;
import pl.edu.icm.crmanager.model.RecType;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.model.ADataObjectUtil;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.BeanUtil;
import pl.edu.icm.sedno.common.util.PropertyChange;
import pl.edu.icm.sedno.common.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Nowa implementacja działająca bez proxy
 * 
 * @author bart
 */
@Service("crmDiffService")
public class CrmDiffServiceImpl implements CrmDiffService {
    private static final Logger logger = LoggerFactory.getLogger(CrmDiffServiceImpl.class);
    
    @Autowired
    private DataObjectDAO dataObjectDAO;
    
    @Autowired
    private ChangeRequestManager changeRequestManager;   
    
    @Autowired
    private CrmSessionFactory crmSessionFactory;
    
    @Override
    public Revision addObjectAndAccept(DataObject domainObject, String login) {
    	return changeRequestManager.addObjectAndAccept(domainObject, login);
    }
    
    @Override
    public Revision generateRevisionAndAccept(DataObject newObjectState, String login) {
        Revision revision = generateRevision(newObjectState, login, true);
        //logger.info("rev: "+ revision.getShortDesc());      
        
        return revision;
    }
    
    @Override
    public Revision generateRevision(DataObject newObjectState, String login) {
        Revision revision = generateRevision(newObjectState, login, false);
        //logger.info("rev: "+ revision.getShortDesc());      
        
        return revision;    
    }
    
    private Revision generateRevision(DataObject newObjectState, String login, boolean autoAccept) {
    	logger.debug("doing CRM diff on "+newObjectState.getGlobalId() +", autoAccept:"+autoAccept+" ...");

    	//logger.info("newObjectState:");
    	//newObjectState.accept(new PrintVisitor(dataObjectDAO));
    	
        if (!dataObjectDAO.isDetached(newObjectState)) {
            throw new CrmRuntimeException("generateRevision(): object ["+newObjectState.getGlobalId()+"] should be Detached");
        }
        
        newObjectState = ADataObjectUtil.unproxyH(newObjectState);
              
        changeRequestManager.openCrmSession(login);
        Revision revision = getCurrentRevision();
        
        DataObject dbObjectState = dataObjectDAO.get(newObjectState.getWrappedClass(), newObjectState.getId());

        if (dbObjectState == newObjectState)
            throw new CrmRuntimeException("dbObjectState == newObjectState, so newObjectState is not Detached");
              
        if (!dbObjectState.equals(newObjectState))
            throw new CrmRuntimeException("bad equals() impl in "+dbObjectState.getWrappedClass().getSimpleName()+".class");                
  
        try {
	        //-- 1. initialize & detach dbObjectState
        	dataObjectDAO.initializeAndEvict(dbObjectState);
	        /*Visitor i = new InitializeVisitor();
	        dbObjectState.accept(i);
	        
	        Visitor e = new EvictVisitor(dataObjectDAO);
	        dbObjectState.accept(e);   //and evict*/    
	                
	        //instead of: dbObjectState = changeRequestManager.attach(dbObjectState);
	        revision.setRootModPointCombo(newObjectState);
        
            //-- 2. extractPersistentComponents
            ExtractPersistentComponents eVisitor = new ExtractPersistentComponents();
            
            dbObjectState.accept(eVisitor);
            List<DataObject> dbObjectsList = eVisitor.getResult();
            
            eVisitor = new ExtractPersistentComponents();
            newObjectState.accept(eVisitor);
            Map<String, DataObject> newObjectsMap = toMap(eVisitor.getResult());
                        
            //-- 3. register new objects
            newObjectState.accept(new NewObjectVisitor(dbObjectsList, dataObjectDAO));
            
            
            /*{
            	PrintVisitor pv1 = new PrintVisitor(dataObjectDAO);
                logger.info(".. dbObjectState");
                dbObjectState.accept(pv1);
                
            	PrintVisitor pv4 = new PrintVisitor(dataObjectDAO);
                logger.info("!! .. 4 newObjectState");
                newObjectState.accept(pv4);
            }*/
            
            //-- 4. do diff
            for (DataObject dbObject: dbObjectsList)
            {
            	//find new version of dbObject
                DataObject newObject = newObjectsMap.get(dbObject.getGlobalId());
                
                if (newObject == null) continue;
                
                if (newObject == dbObject)
                    throw new RuntimeException("newObject == dbObject ["+dataObjectDAO.getObjectShortDesc(newObject)+"], check if your Visitor impl, method accept() should be final, otherwise Hibernate overrides it in Proxy");

                
                //logger.debug(".. start doing diff on pair : " +dbObject.getGlobalId());
                detectChangesOnOneModPoint(dbObject, newObject);
            } 
                     
            if (autoAccept) {
                changeRequestManager.closeCrmSessionWithAutoAccept();
            }
            else {
                changeRequestManager.closeCrmSessionWithNoAccept();
            }            
            
            logger.info("generated revision : "+ revision.getShortDesc());
        } catch (RuntimeException ex) {
            crmSessionFactory.closeCurrentSessionPanic();
            throw(ex);
        }

        logger.debug("CRM diff completed");
        return revision;
    }
    
    /**
     * creates map: dataObject.globalId -> dataObject
     */
    public static Map<String, DataObject> toMap(List<DataObject> list) {
    	Map<String, DataObject> map = new HashMap<String, DataObject>();
    	
    	for (DataObject dataObject : list)
    		map.put(dataObject.getGlobalId(), dataObject);
    	
    	return map;
    }
    
    private Revision getCurrentRevision() {
    	return changeRequestManager.getCurrentRevision();
    }
    
    /*
    private DataObject getFirstEquals(List<DataObject> list, DataObject toDataObject) {
        for (DataObject o: list) {
            if (o != null && o.equals(toDataObject))
                return o;
        }
        
        return null;
    }*/

    /**
     * Zmiany pomiędzy obiektami oldVersionObject i newVersionObject zostaną nagrane do revision
     */
    private void detectChangesOnOneModPoint(DataObject oldVersionObject, DataObject newVersionObject) {
        if (oldVersionObject == null) throw new IllegalArgumentException("oldVersionObject == null");
        if (newVersionObject == null) throw new IllegalArgumentException("newVersionRoot == null");
        
        for (Method getter : ReflectionUtil.getPersistentGetters(oldVersionObject.getWrappedClass()) ) {
            
            MethodType mType = BCodeGeneratorImpl.determineMethodType(getter);
            
            if (mType.equals(MethodType.CrmExcluded)) continue; 
                       
            if (mType.equals(MethodType.simpleValue) || mType.equals(MethodType.dataObjectValue)) 
                doDiffOnValue(getter,  oldVersionObject, newVersionObject, mType);
            
            if (mType.equals(MethodType.dataObjectCollection))
                doDiffOnCollection(getter,  oldVersionObject, newVersionObject);     
            
        }
    }
    
    private void doDiffOnValue(Method getter, DataObject oldVersion, DataObject newVersion, MethodType mType) {
        //logger.debug(".... doing diff on [Value] getter:"+ getter.getDeclaringClass().getSimpleName()+"."+getter.getName()+"()");          
       
        Object oldValue  = ReflectionUtil.invokeGetterEvenIfPrivate(getter, oldVersion);
        Object newValue  = ReflectionUtil.invokeGetterEvenIfPrivate(getter, newVersion);
        
        if (!ObjectUtils.nullSafeEquals(oldValue, newValue)) {      	
        	Class valueClass = getClass(oldValue, newValue);
        	getCurrentRevision().registerValueChange(newVersion, getter.getName(), oldValue, newValue, valueClass);
        	
        	//in proxy version:
            //Method setter = ReflectionUtil.getterToSetter(getter, null);            
            //ReflectionUtil.invokeSetter(setter, oldVersion, newValue);
        }       
    }
    
    private Class getClass(Object first, Object second) {
    	if (first != null)
    		return first.getClass();
    	
    	if (second != null)
    		return second.getClass();
    	
    	return null;
    }
    
    private void doDiffOnCollection(Method getter, DataObject oldVersion, DataObject newVersion) {
        //logger.debug(".. doing diff on [Collection] getter:"+ getter.getDeclaringClass().getSimpleName()+"."+getter.getName()+"()");       
        //logger.debug(".. oldVersion: "+ dataObjectDAO.getObjectShortDesc(oldVersion));
        //logger.debug(".. newVersion: "+ dataObjectDAO.getObjectShortDesc(newVersion));
     
        Collection oldCollection  = (Collection)ReflectionUtil.invokeGetterEvenIfPrivate(getter, oldVersion);        
        Collection newCollection  = (Collection)ReflectionUtil.invokeGetterEvenIfPrivate(getter, newVersion);
                     
        for (PropertyChange change : BeanUtil.doDiffOnRefCollections(newVersion, getter, oldCollection, newCollection)) {
        	if (change.getRecType() == RecType.CHILD_ADD) {
        		DataObject child = (DataObject)change.getNewValue();  
        		getCurrentRevision().registerChildAdd(newVersion, getter.getName(), child);
        	}
        	if (change.getRecType() == RecType.CHILD_REMOVE) {
        		DataObject child = (DataObject)change.getOldValue();  
        		getCurrentRevision().registerChildRemove(newVersion, getter.getName(), child);
        	}
        }
    }    
    
    //-- setters
    
 }
