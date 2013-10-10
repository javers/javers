package pl.edu.icm.crmanager.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.exception.UnsupportedMapping;
import pl.edu.icm.crmanager.model.Change;
import pl.edu.icm.crmanager.model.ChangeImportant;
import pl.edu.icm.crmanager.model.ChangeImportantVoter;
import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.ChangeVoter;
import pl.edu.icm.crmanager.model.RecType;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.Revision.RevisionStatus;
import pl.edu.icm.crmanager.model.RootModPoint;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;
import pl.edu.icm.sedno.common.dao.ObjectNotFoundException;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.model.DataObject.DataObjectStatus;
import pl.edu.icm.sedno.common.util.ReflectionUtil;
import pl.edu.icm.sedno.patterns.PersistVisitor;
import pl.edu.icm.sedno.patterns.Visitor;

/**
 * 
 * @author bart
 */
@Service("revisionService")
public class RevisionServiceImpl implements RevisionService {
    private static final Logger logger = LoggerFactory.getLogger(RevisionServiceImpl.class);
    
    @Autowired
    private DataObjectDAO dataObjectDAO;
    
    private ApplicationContext applicationContext;
    
    /**
     * voters instances cache
     */
    private Map<Class<? extends ChangeVoter>, ChangeVoter> changeVoters = 
    		new ConcurrentHashMap<Class<? extends ChangeVoter>, ChangeVoter>();
       
    @Override
    public void flushWithNoAccept(Revision revision) {
    	
       //logger.debug("flushWithNoAccept(), rev: " + revision.getShortDesc());	
       int n=0;
       int c=0;
       for (ChangeRequest cr : revision.getChangeRequests()) {
           DataObject modPoint = cr.getNode__();
           //logger.debug(".. modPoint: "+ dataObjectDAO.getObjectShortDesc(modPoint) );
           
           analyzeNoAcceptChange(cr, modPoint);
           calcChangeImportant  (cr, modPoint);
           
           if (cr.getRecType() == RecType.NEW_OBJECT) {                  
              n++;             
              if (modPoint.isNew()!=true)
                  throw new CrmRuntimeException("crm internal error, NEW_OBJECT have to be NEW!");
              
              dataObjectDAO.saveOrUpdate(modPoint);
           }
           else {
              if (modPoint.isNew()) {
                  //already managed
                  continue;
              }
               
              if (modPoint.isTransient()) {
                  throw new CrmRuntimeException("crm internal error, transient object have to be NEW");
              }
              
              if (dataObjectDAO.isPersistent(modPoint)) {
                  throw new CrmRuntimeException("error in CrmSession flushWithNoAccept() - modPoint ["+modPoint.getGlobalId()+"] is persistent. CRM is not going to evict it.");
              } 
              
              c++;                           
           }
           
           //don't ask why
           if (cr.getRecType().equals(RecType.CHILD_ADD) || cr.getRecType().equals(RecType.CHILD_REMOVE)) {
        	   Method getter = ReflectionUtil.findGetter(cr.getGetterName(), modPoint.getWrappedClass());
               String collectionName = ReflectionUtil.getterToField(getter);
               dataObjectDAO.evictCollectionFromSecondLevelCache(getter.getDeclaringClass(), modPoint.getId(),collectionName);
           }
       }
   
       logger.debug("Flushing revison ["+revision.getIdRevision()+"] with NoAccept, newObjects: "+n+", changedObjects:"+c);
       revision.moveToNew();
       flush(revision);
    }

    /**
     * ustawia flagę {@link ChangeRequest#isChangeImportant()}
     */
    private void calcChangeImportant(ChangeRequest cr, DataObject modPoint) {
        if (cr.getRecType().equals(RecType.NEW_OBJECT) || cr.getRecType().equals(RecType.DELETED)) {
            cr.setChangeImportant(false);
            return;
        }
            
        Method getter = ReflectionUtil.findGetter(cr.getGetterName(), modPoint.getWrappedClass());
                
        boolean basicAnnotationResult = getter.isAnnotationPresent(ChangeImportant.class);
        
        boolean voterResult = false;        
        if (getter.isAnnotationPresent(ChangeImportantVoter.class)) {
        	ChangeImportantVoter ann = getter.getAnnotation(ChangeImportantVoter.class);
        	ChangeVoter voter = getChangeVoter(ann.voterClass());
        	voterResult = voter.isChangeImportant(modPoint, cr.getOldValueOrReference(), cr.getNewValueOrReference(), cr);
        }
               
        cr.setChangeImportant(basicAnnotationResult || voterResult);        
    }
    
    /**
     * cached
     */
    private ChangeVoter getChangeVoter(Class<? extends ChangeVoter<?,?>> voterClass) {
    	if (!changeVoters.containsKey(voterClass)) {
    		ChangeVoter voter = ReflectionUtil.invokeConstructor(voterClass);
    		changeVoters.put(voterClass, voter);
    	}

		return changeVoters.get(voterClass);
    }
    
    /**
     * sprawdza czy mapping ORM'owy jest ok
     */
    private void analyzeNoAcceptChange(ChangeRequest cr, DataObject modPoint) {
            	    	
        if (cr.getRecType().equals(RecType.REFERENCE_CHANGE))  {
        	Method getter = cr.getterMethod();
            
            boolean owner = ReflectionUtil.isPersistentGetterOwnerOfRelation(getter);
            
            if (owner && !modPoint.isTransient() && cr.getNewReference__() != null && cr.getNewReference__().isTransient()) {
                throw new UnsupportedMapping("Can't perform NoAccept reference change on owner side (usualy @ManyToOne). modPoint ["+modPoint.getGlobalId()+"] is persistent|detached but new reference ["+cr.getNewReference__().getGlobalId()+"] is transient."+
                        " Problematic getter: "+getterDesc(getter));
            }            
        }
        
        if (cr.getRecType().equals(RecType.CHILD_REMOVE))  {
        	Method getter = cr.getterMethod();
        	
        	if (getter.isAnnotationPresent(ManyToMany.class)){
        		ManyToMany manyToMany = getter.getAnnotation(ManyToMany.class);
        		if (FetchType.EAGER == manyToMany.fetch()){
        			throw new UnsupportedMapping("Can't perform NoAccept child remove. @ManyToMany with FetchType.EAGER is not supported. Getter method: "+ getter);
        		}
        	}	
        }
        
        
    }
    
    /**
     * sprawdza czy mapping ORM'owy jest ok
     */
    private void analyzeAutoAcceptChange(ChangeRequest cr, DataObject modPoint) {
    	if (cr.getRecType().equals(RecType.CHILD_REMOVE) && cr.isOneToMany())  {
    		
    		//czy kolekcja jest orphanRemoval=true
    		Method getter = ReflectionUtil.findGetter(cr.getGetterName(), modPoint.getWrappedClass());
    		OneToMany oneToMany = getter.getAnnotation(OneToMany.class);
    		if (oneToMany.orphanRemoval()) {
    			throw new UnsupportedMapping(
    					"Can't perform AutoAccept CHILD_REMOVE, found orphanRemoval==true on @OneToMany side. Removed object is expected to change only its status to DELETED." +
    					" Problematic getter: "+getterDesc(getter));
    		}
    		
    	}
    	    
    }    
    
    private String getterDesc(Method getter) {
        return getter.getReturnType().getSimpleName() +" " +
               getter.getDeclaringClass().getSimpleName()+"."+
               getter.getName()+"()";
    }
    
    @Override
    public void flushWithAutoAccept(Revision revision) {     
  
        if (revision.getRootModPoint() != null) {
        	revision.getRootModPoint().accept( new PersistVisitor(dataObjectDAO) );
        }
                  
    	int n=0;
        int c=0;
        for (ChangeRequest cr : revision.getChangeRequests()) {
           DataObject modPoint = cr.getNode__();
           
          // logger.debug(".. "+ dataObjectDAO.getObjectShortDesc(modPoint) +", cr: "+ cr.getShortDesc());
           
           //if (!dataObjectDAO.isPersistent(modPoint)) {
           dataObjectDAO.saveOrUpdate(modPoint);
           //}
           
           analyzeAutoAcceptChange(cr, modPoint);
           calcChangeImportant  (cr, modPoint);
            
           if (cr.getRecType() == RecType.NEW_OBJECT) {                  
                n++;
                if (!modPoint.isNew()) {
                    throw new CrmRuntimeException("crm internal error, NEW_OBJECT have to be NEW!");
                }
                
                modPoint.setDataObjectStatus(DataObjectStatus.ACTIVE);
            }  else {
            	 if (cr.getRecType() == RecType.CHILD_REMOVE) {
            		 DataObject childToRemove = cr.getOldReference__();
            		 if (cr.isOneToMany()) {
            		     dataObjectDAO.reattach(childToRemove);
            			 safeDelete(childToRemove);
            			 updateBackRef(cr, childToRemove, null);
            		 }
            	  }              	
                c++;  
            }  
            
            setRootModPointMetadata(revision, modPoint);
        }
        
        logger.debug("Flushing revison ["+revision.getIdRevision()+"] with AutoAccept, newObjects: "+n+", changedObjects:"+c);
        //logger.debug(revision.getShortDesc());
        revision.accept(revision.getAuthorId());
        
        flush(revision);
    }  
    
    /**
     * marks node and its subtree as DELETED
     */
    private void safeDelete(DataObject childToRemove) { 
		childToRemove.accept(new delVisitor());
    }

    public class delVisitor implements Visitor<DataObject> {

    	@Override
		public void visit(DataObject childToRemove) {
				childToRemove.setDataObjectStatus(DataObjectStatus.DELETED);
		}
    }
    
    /*
    private void deepReattach(DataObject dataObject) {
        Visitor v = new ReattachVisitor(dataObjectDAO);
        dataObject.accept(v);
    }*/
    
    private void setRootModPointMetadata(Revision revision, DataObject modPoint) {

        if (modPoint instanceof RootModPoint
                && !revision.equals(((RootModPoint) modPoint).getLastRevision()))
        {
            if (!dataObjectDAO.isPersistent(modPoint)) {
                throw new CrmRuntimeException("internal error during accept, modPoint has to be persistent ");
            }
            ((RootModPoint) modPoint).setLastRevision(revision);
            ((RootModPoint) modPoint).incrementCrmVersionNo();
            
            
        }
    }
    
    private void flush(Revision revision) {
        
        if (dataObjectDAO.isDetached(revision)) {
            throw new CrmRuntimeException("error in CrmSession flush() - Revision is is detached, you are not handling transactions properly");
        }
        
        persist(revision);
                
        dataObjectDAO.flush();    
        
        //logger.debug("Revision ["+revision.getIdRevision()+"] flushed, status: "+revision.getRevisionStatus());
    }
    
    @Override
    public void accept(Revision revision, String acceptedBy) {
        int n = 0;
        int c = 0;

        for (ChangeRequest cr : revision.getChangeRequests()) {

            DataObject modPoint = loadModPoint(cr);
            //logger.debug(".. doing acc on "+cr.getShortDesc() );            
            
            setRootModPointMetadata(revision, modPoint);
            
            if (cr.getRecType() == RecType.NEW_OBJECT) {                                 
                if (!modPoint.isNew()) {
                    throw new CrmRuntimeException("data integrity error, can't apply NEW_OBJECT request, dataObject ["+modPoint.getGlobalId()+"] is not NEW");
                }
                n++;
                modPoint.setDataObjectStatus(DataObjectStatus.ACTIVE);
             }
             else {
                if (modPoint.isNew()) {
                     throw new CrmRuntimeException("data integrity error, can't apply CHANGE request, dataObject ["+modPoint.getGlobalId()+"] is NEW");
                }
                c++;
                applyChange(modPoint, cr);
             }         
            dataObjectDAO.saveOrUpdate(modPoint, Change.forRequest(cr));
        }
                            
        revision.accept(acceptedBy);
        persist(revision);      
        
        logger.info("Revision ["+revision.getIdRevision()+"] accepted, newObjects: "+n+", changedObjects:"+c);
    }

    @Override
    public void cancel(Revision revision, String cancelledBy) {
        revision.cancel(cancelledBy);
        
        int d=0;
        for (ChangeRequest cr : revision.getChangeRequests()) {
            DataObject modPoint = loadModPointNullAllowed(cr);
            
            if (modPoint == null) continue; //child-object mógł być wcześniej usunięty przez cascade
            
            if (cr.getRecType() == RecType.NEW_OBJECT) {
                d++;
                logger.debug(".. about to delete NEW object: "+ modPoint.getGlobalId());
                dataObjectDAO.delete(modPoint);
            }
        }
        
        persist(revision);      
        
        logger.info("Revision ["+revision.getIdRevision()+"] cancelled, "+d +" NEW object(s) deleted");
    }
      
    @Override
    public void initializeTransientReferences(Revision revision){
    	
    	DataObject rootModPoint = loadReference(revision.getRootModPointId(), revision.getRootModPointClass(), false);
    	    	    	
    	dataObjectDAO.initialize   (rootModPoint);    	
    	revision.setRootModPoint__ (rootModPoint);
    	
    	for (ChangeRequest cr : revision.getChangeRequests()){
    		initializeTransientReferences(cr);
    	}
    }
   
    private void initializeTransientReferences(ChangeRequest cr){
    	
    	DataObject node = loadModPoint(cr);
    	dataObjectDAO.initialize   (node);
    	cr.setNode__( node );

    	DataObject oldRef = loadOldReference(cr);
    	if (oldRef != null) {
    		dataObjectDAO.initialize   (oldRef);
    	}
    	cr.setOldReference__(oldRef);
    	   
    	DataObject newRef = loadNewReference(cr);
    	if (newRef != null) {
    		dataObjectDAO.initialize   (newRef);
    	}
    	cr.setNewReference__(newRef);

    }
    
    private DataObject loadNewReference(ChangeRequest cr){
        return loadReference(cr.getNewReferenceId(), cr.getNewReferenceClass(), false);
    }
    
    private DataObject loadOldReference(ChangeRequest cr){
        return loadReference(cr.getOldReferenceId(), cr.getOldReferenceClass(), false);
    }
       
    private DataObject loadModPoint(ChangeRequest cr) {
        return loadReference(cr.getNodeId(), cr.getNodeClass(), false);
    } 
    
    private DataObject loadModPointNullAllowed(ChangeRequest cr) {
        return loadReference(cr.getNodeId(), cr.getNodeClass(), true);
    } 
    
    public DataObject loadReference(int refId, String refClass, boolean nullAllowed) {
        if (refId == 0) 
            return null;
        
        Class clazz;
        try {
            clazz = Class.forName(refClass);
        } catch (ClassNotFoundException e) {
            throw new CrmRuntimeException("error reading revision", e);
        }
        
        DataObject newRef;
        if (nullAllowed)
            newRef = dataObjectDAO.getNullAllowed(clazz, refId);
        else
            newRef = dataObjectDAO.get(clazz, refId);
        
        return newRef;      
    }
    
    private void applyChange(DataObject modPoint, ChangeRequest cr) {
        if (cr.getRecType() == RecType.VALUE_CHANGE ||
            cr.getRecType() == RecType.REFERENCE_CHANGE)
        {
            applyValueOrReferenceChange(modPoint, cr);
        } else if (cr.getRecType() == RecType.CHILD_ADD)
        {
            applyChildAdd(modPoint, cr);
        } else if (cr.getRecType() == RecType.CHILD_REMOVE)
        {
            applyChildRemove(modPoint, cr);
        } else {
            throw new CrmRuntimeException("RecType."+cr.getRecType()+ " not supported");
        }
    }
    
    private void applyValueOrReferenceChange(DataObject modPoint, ChangeRequest cr) {
        Class methodSrc = modPoint.getWrappedClass();
        
        Object newValue;
        
        if (cr.getRecType().equals(RecType.VALUE_CHANGE)) {
            newValue = cr.getNewValue();
        } else 
        if (cr.getRecType().equals(RecType.REFERENCE_CHANGE)) {
            newValue = loadNewReference(cr);
        } else {
            throw new RuntimeException("this is not value or reference change");
        }

        cr.applyValueChange(modPoint, newValue);
    }
    
    private void applyChildAdd(DataObject modPoint, ChangeRequest cr) {
        DataObject childToAdd = loadNewReference(cr);
        if (childToAdd == null) {
            throw new CrmRuntimeException("applyChildAdd ["+cr.getNewReferenceClass()+"#"+cr.getNewReferenceId()+"] not found");
            
        }
        operationOnCollection(modPoint, cr, OpType.ADD, childToAdd);
    }
    
    private void applyChildRemove(DataObject modPoint, ChangeRequest cr) {
        DataObject childToRemove = loadOldReference(cr);
        if (childToRemove == null) {
            throw new CrmRuntimeException("childToRemove ["+cr.getNewReferenceClass()+"#"+cr.getNewReferenceId()+"] not found");
            
        }
        operationOnCollection(modPoint, cr, OpType.REMOVE, childToRemove);
    }

    public void dbSafeOperationOnCollection(DataObject modPoint, ChangeRequest cr, OpType opType, DataObject ref) {
        doOperationOnCollection(modPoint, cr, opType, ref, false);
    }
      
    private void operationOnCollection(DataObject modPoint, ChangeRequest cr, OpType opType, DataObject ref) {
        doOperationOnCollection(modPoint, cr, opType, ref, true);
    }

    private void doOperationOnCollection(DataObject modPoint, ChangeRequest cr, OpType opType, DataObject ref, boolean changeDb) {
        if (!changeDb && dataObjectDAO.isPersistent(modPoint)) {
            throw new IllegalArgumentException("cchangeDb=false but modPoint is persistent");
        }

    	Method getter  = cr.getterMethod();
        Class methodSrc = modPoint.getWrappedClass();
        
        Collection collection = null;
        try {
        	collection = (Collection)ReflectionUtil.invokeGetterEvenIfPrivate(getter,  modPoint);
            //collection = (Collection)getter.invoke(modPoint, new Object[]{});
        } catch (Exception e) {
            throw new CrmRuntimeException("error calling getter ["+modPoint.getGlobalId()+"."+getter.getName()+"()]",e);
        }
        
        //if col is null, crate it and set to object
        if (collection == null) {
            
            if (List.class.isAssignableFrom(getter.getReturnType())) {
                collection = new ArrayList<DataObject>();
            } else if (Set.class.isAssignableFrom(getter.getReturnType())) {
                collection = new HashSet<DataObject>();
            } else {
                throw new RuntimeException("Don't know how to handle retType collection: "+ getter.getReturnType().getName());
            }
          
            
            Method setter = cr.setterMethod();          
            Object[] args = new Object[] {collection};
            try {
                setter.invoke(modPoint, args);
            } catch (Exception e) {
                throw new CrmRuntimeException("error calling setter ["+modPoint.getGlobalId()+"."+getter.getName()+"()]",e);
            }
        }
          
        //col operation
        if (opType.equals(OpType.ADD) && !collection.contains(ref)) {
            //logger.info("** add    "+ collection.contains(ref));
            collection.add(ref);             
            updateBackRef(cr, ref, modPoint);
        }
        if (opType.equals(OpType.REMOVE)){
        	logger.debug("** deleting "+ ref.getGlobalId());
      	
        	if (cr.isOneToMany()) {
        	    if (changeDb) {
        	        safeDelete(ref);
        	    } else {
        	        collection.remove(ref);
        	    }
        	    updateBackRef(cr, ref, null);
        	} else { //ManyToMany      	        
	            boolean removed =  collection.remove(ref);
	            if (!removed) {
	            	logger.error("** error while deleting "+ ref.getGlobalId() +" from collection: "+ collection+", removed == false");
	            	logger.error("Collection ["+modPoint.getGlobalId()+"."+cr.getGetterName()+"()] doesn't contain reference: "+ref.getGlobalId());
	                throw new CrmRuntimeException("Collection ["+modPoint.getGlobalId()+"."+cr.getGetterName()+"()] doesn't contain reference: "+ref.getGlobalId());
	           }
        	}
        }       
    }

    private static void updateBackRef(ChangeRequest cr, DataObject ref, DataObject backRef) {
        Method backRefSetter = cr.getBackRefSetter();    
        if (backRefSetter != null) {
            ReflectionUtil.invokeSetterEvenIfPrivate(backRefSetter, ref, backRef);
        }
    }

    @Override
    public void persistNew(Revision revision) {
        if (revision.getRevisionStatus() != RevisionStatus.TRANSIENT) {
            throw new CrmRuntimeException("persist() - state error, revisionStatus = TRANSIENT");
        }
        dataObjectDAO.saveOrUpdate(revision); 
    }
    
    @Override
    public void persist(Revision revision) {
        if (revision.getRevisionStatus() == RevisionStatus.TRANSIENT) {
            throw new CrmRuntimeException("persist() - state error, revisionStatus = TRANSIENT");
        }
        
        if (revision.getRootModPointClass() != null) {
        	updateRootModPointFrozenFlag(revision);
        }
        
        dataObjectDAO.saveOrUpdate(revision);       
    }
    
    public void setDataObjectDAO(DataObjectDAO dataObjectDAO) {
        this.dataObjectDAO = dataObjectDAO;
    }

	private void updateRootModPointFrozenFlag(Revision revision) {
		Class rClass = ReflectionUtil.forName(revision.getRootModPointClass());
		
		if (!RootModPoint.class.isAssignableFrom(rClass)) { 
			return;
		}
		
		boolean isFrozen = revision.isOpen();
				
		//uwaga! update musi iść bokiem, przez hql, żeby nie burzyć skomplikowanej sytuacji rootModPoint'a w sesji		
		String hql = "update "+revision.getRootModPointClass()+" set frozen = ?2 where id = ?1";
		dataObjectDAO.executeUpdate(hql, revision.getRootModPointId(), isFrozen);
		
	}
}
