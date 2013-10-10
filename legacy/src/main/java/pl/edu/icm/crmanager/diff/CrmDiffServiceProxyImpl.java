package pl.edu.icm.crmanager.diff;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.exception.UnsupportedMapping;
import pl.edu.icm.crmanager.logic.*;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.model.ADataObjectUtil;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.ReflectionUtil;
import pl.edu.icm.sedno.patterns.EvictVisitor;
import pl.edu.icm.sedno.patterns.InitializeVisitor;
import pl.edu.icm.sedno.patterns.Visitor;

import java.lang.reflect.Method;
import java.util.*;

/**
 * stara implementacja działająca na proxy
 * @author bart
 */
@Deprecated
public class CrmDiffServiceProxyImpl implements CrmDiffService {
    private static final Logger logger = LoggerFactory.getLogger(CrmDiffServiceProxyImpl.class);
    
    @Autowired
    private DataObjectDAO dataObjectDAO;
    
    @Autowired
    private ChangeRequestManager changeRequestManager;
    
    @Autowired
    private BCodeGenerator bCodeGenetartor;
    
    @Autowired
    private CrmSessionFactory crmSessionFactory;
    
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
        if (!dataObjectDAO.isDetached(newObjectState)) {
            throw new CrmRuntimeException("generateRevision(): object ["+newObjectState.getGlobalId()+"] should be Detached");
        }
        
        newObjectState = ADataObjectUtil.unproxyH(newObjectState);
              
        changeRequestManager.openCrmSession(login);
        Revision revision = changeRequestManager.getCurrentRevision();
        
        DataObject dbObjectState = dataObjectDAO.get(newObjectState.getWrappedClass(), newObjectState.getId());

        if (dbObjectState == newObjectState) {
            throw new CrmRuntimeException("dbObjectState == newObjectState, so newObjectState is not Detached");
        }
        
        try {
            Visitor i = new InitializeVisitor();
            dbObjectState.accept(i);
            
            Visitor e = new EvictVisitor(dataObjectDAO);
            dbObjectState.accept(e);   //and evict
           
            dbObjectState = changeRequestManager.attach(dbObjectState);
            
            if (!dbObjectState.equals(newObjectState))
                throw new CrmRuntimeException("bad equals() impl in "+dbObjectState.getWrappedClass().getSimpleName()+".class");                
            
            ExtractPersistentComponents eVisitor = new ExtractPersistentComponents();
            dbObjectState.accept(eVisitor);
            List<DataObject> dbObjectsList = eVisitor.getResult();
            
            eVisitor = new ExtractPersistentComponents();
            newObjectState.accept(eVisitor);
            List<DataObject> newObjectsList = eVisitor.getResult();
            
            /*
            logger.debug("db  objects: ");
            for (DataObject d: dbObjectsList) {
                logger.info(".. "+ d.getGlobalId()  +" "+ dataObjectDAO.getObjectShortDesc(d));
            }           
            logger.debug("new objects: ");
            for (DataObject d: newObjectsList) {
                logger.debug(".. "+ d.getGlobalId() +" "+ dataObjectDAO.getObjectShortDesc(d));
            }*/
            
            //TODO: fix this ugly nested loops
            for (DataObject dbObject: dbObjectsList)
            {
                DataObject newObject = getFirstEquals(newObjectsList, dbObject);
                
                if (newObject == null) continue;
                
                if (newObject == dbObject)
                    throw new RuntimeException("Something is really wrong!");
                
                //logger.debug(".. start doing diff on pair : " +dbObject.getGlobalId());
                detectChangesOnOneModPoint(dbObject, newObject);
            } 
            
            if (autoAccept) {
                changeRequestManager.closeCrmSessionWithAutoAccept();
            }
            else {
                changeRequestManager.closeCrmSessionWithNoAccept();
            }
        } catch (RuntimeException e) {
            
            
            crmSessionFactory.closeCurrentSessionPanic();
            throw(e);
        }

        return revision;
    }
    
    private DataObject getFirstEquals(List<DataObject> list, DataObject toDataObject) {
        for (DataObject o: list) {
            if (o != null && o.equals(toDataObject))
                return o;
        }
        
        return null;
    }

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
//        logger.debug(".... doing diff on [Value] getter:"+ getter.getDeclaringClass().getSimpleName()+"."+getter.getName()+"()");          
        
        Object oldValue  = ReflectionUtil.invokeGetter(getter, oldVersion);
        Object newValue  = ReflectionUtil.invokeGetter(getter, newVersion);
        
        if (!ObjectUtils.nullSafeEquals(oldValue, newValue)) {
            Method setter = ReflectionUtil.getterToSetter(getter, null);
            //record change
            ReflectionUtil.invokeSetter(setter, oldVersion, newValue);
        }
        
        /*recursive
        if ( mType.equals(MethodType.dataObjectValue) &&
             oldValue != null && newValue != null &&
             ObjectUtils.nullSafeEquals(oldValue, newValue))
        {
            detectChangesOnTree(oldVersion, newVersion);
           
        }*/
    }
    
    private void doDiffOnCollection(Method getter, DataObject oldVersion, DataObject newVersion) {
     //logger.debug(".. doing diff on [Collection] getter:"+ getter.getDeclaringClass().getSimpleName()+"."+getter.getName()+"()");
        
        //logger.debug(".. oldVersion: "+ dataObjectDAO.getObjectShortDesc(oldVersion));
        //logger.debug(".. newVersion: "+ dataObjectDAO.getObjectShortDesc(newVersion));
     
        Collection oldCollection  = (Collection)ReflectionUtil.invokeGetter(getter, oldVersion);        
        Collection newCollection  = (Collection)ReflectionUtil.invokeGetter(getter, newVersion);
             
        oldCollection = wrapNull(oldCollection, getter.getReturnType());
        newCollection = wrapNull(newCollection, getter.getReturnType());
        
        //logger.debug(".... oldVersion.col:  s:"+oldCollection.size()+", type "+oldCollection.getClass().getSimpleName());
        //logger.debug(".... newVersion.col:  s:"+newCollection.size()+", type "+newCollection.getClass().getSimpleName());
     
        
        if ( oldCollection.equals(newCollection))
            return;
                  
        Collection added =   CollectionUtils.subtract(newCollection, oldCollection);
        Collection removed = CollectionUtils.subtract(oldCollection, newCollection);
        
        //póki co, wspieramy tylko dodawanie na końcu listy  
        for (Object obj : added) {
            //record change
            oldCollection.add(obj);
        }
        
        for (Object obj : removed) {
            //record change
            oldCollection.remove(obj);
        }
    }
    
    private Collection wrapNull(Collection col, Class colType) {
        if (col!=null) return col;
        
        if (Set.class.isAssignableFrom(colType))
            return new HashSet();
        
        if (List.class.isAssignableFrom(colType))
            return new ArrayList();
        
        throw new UnsupportedMapping("This collection type: "+colType.getClass().getName() +" is not supported");              
    }
    
    //-- setters
    
    public void setDataObjectDAO(DataObjectDAO dataObjectDAO) {
        this.dataObjectDAO = dataObjectDAO;
    }
    
    public void setChangeRequestManager(ChangeRequestManager changeRequestManager) {
        this.changeRequestManager = changeRequestManager;
    }
    
    public void setbCodeGenetartor(BCodeGenerator bCodeGenetartor) {
        this.bCodeGenetartor = bCodeGenetartor;
    }
    
    public void setCrmSessionFactory(CrmSessionFactory crmSessionFactory) {
        this.crmSessionFactory = crmSessionFactory;
    }

	@Override
	public Revision addObjectAndAccept(DataObject domainObject, String login) {
		// TODO Auto-generated method stub
		return null;
	}
}
