package pl.edu.icm.sedno.common.dao;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.internal.SessionImpl;
import org.hibernate.stat.SecondLevelCacheStatistics;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import pl.edu.icm.crmanager.model.Change;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.model.ADataObjectUtil;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.model.Indexable;
import pl.edu.icm.sedno.common.model.ObjectState;
import pl.edu.icm.sedno.common.util.ReflectionUtil;
import pl.edu.icm.sedno.patterns.EvictVisitor;
import pl.edu.icm.sedno.patterns.InitializeVisitor;

import com.google.common.base.Preconditions;

/**
 * Spring 3.1 impl (no hibernateTemplate)
 * 
 * @author bart
 */
@Service("dataObjectDAO")
public class DataObjectDAOImpl implements DataObjectDAO {
    Logger logger = LoggerFactory.getLogger(DataObjectDAOImpl.class);

    private List<SaveOrUpdateHook> saveOrUpdateHooks;
   
    @Autowired 
    private SessionFactory sessionFactory;
        
    @Override
    public String getCurrentSessionInfo() {
        return "sid:"+getCurrentSession().hashCode()+", t:"+getCurrentSession().getTransaction().hashCode();
    } 
    
    public void initializeAndEvict(DataObject obj) {
    	initialize(obj);
        EvictVisitor e = new EvictVisitor(this);
        obj.accept(e);
    }
    
    @Override
    public void initialize(DataObject obj) {
        if (obj==null) {
            return;
        }
        InitializeVisitor v = new InitializeVisitor();
        obj.accept(v);
    }
    
    public ObjectState getObjectState(DataObject obj) {
        
        obj = CrmReflectionUtil.unproxyC(obj);
        if (obj.isTransient() && !getCurrentSession().contains(obj)) {
            return ObjectState.TRANSIENT;
        } else if (isDetached(obj)) {
            return ObjectState.DETACHED;
        }
        else if(isPersistent(obj)) {
            return ObjectState.PERSISTENT;
        }           
        
        throw new RuntimeException("getObjectState() - giving up");
    }
    
    public String getObjectShortDesc(DataObject obj) {
               
        if (ADataObjectUtil.isHibernateProxy(obj)) {
        	DataObject target = ReflectionUtil.unproxyH(obj);
        	return "hProxy #"+System.identityHashCode(this)+" -> "+target.getGlobalId()+"."+getObjectState(target) + " #"+System.identityHashCode(target);
        }
        
        String crmProxy="";
        if (obj instanceof CrmProxy) {
            crmProxy="CrmProxy ->";
        }
        
        return crmProxy+obj.getGlobalId()+"."+getObjectState(obj) + " #"+System.identityHashCode(obj);
    }
    
    @Override
    public boolean isDetached(DataObject obj) {
    	if (obj instanceof CrmProxy) {
            obj = CrmReflectionUtil.unproxyC(obj);
        }
    	
        return !obj.isTransient() && !getCurrentSession().contains(obj);
    }
    
    @Override
    public boolean isPersistent(DataObject obj) {
        return getCurrentSession().contains(obj);
    }
    
    /** REATTACH_BY_LOCK, if hasDirtyCollection then REATTACH_BY_UPDATE */
    @Override
    public <T extends DataObject> T reattach(T obj) {
       
        if (isDetached(obj)) {
            logger.debug("reattaching {}", getObjectShortDesc(obj));
            
            if (obj instanceof CrmProxy) {
	            obj = CrmReflectionUtil.unproxyC(obj);
	        }
            
            if ( ! hasDirtyCollection(obj)) {
            	getCurrentSession().buildLockRequest(LockOptions.NONE).lock(obj);
                logger.debug(".. fast reattach by lock");
            }
            else {
            	getCurrentSession().update(obj);
                logger.debug(".. object hasDirtyCollection, so reattach by update");
            }
        
            resetModDate(obj); 
        }
                               
        return obj;
    }
           
    private void resetModDate(DataObject obj) {
        if (obj instanceof Indexable)
            ((Indexable) obj).resetModDate();    
    }
    
    @Override
    public void flush() {
        getCurrentSession().flush();
    }

    @Override
    public void clear() {
    	getCurrentSession().clear();
    }
        
    @Override
    public void clearSecondLevelCache() {
        sessionFactory.getCache().evictEntityRegions();
        sessionFactory.getCache().evictQueryRegions();
        sessionFactory.getCache().evictCollectionRegions();
    }
    
    @Override
    public void evict(DataObject obj) {
        getCurrentSession().evict(obj);
    }
    
    @Override
    public void evictEntityFromSecondLevelCache(DataObject obj) {
    	sessionFactory.getCache().evictEntity( obj.getWrappedClass(), obj.getId() );
    }
    
    @Override
    public void evictCollectionFromSecondLevelCache(Class ownerClass, int ownerId, String collectionProperty) {
    	
    	String role = ownerClass.getName() + "."+collectionProperty;
    	if (sessionFactory.getCache().containsCollection(role, ownerId)) {
    		sessionFactory.getCache().evictCollection(role, ownerId);    
    	}
    }
    
    @Override
    public <T extends DataObject> T get(Class<T> desiredClass, int id) {
        T ret = (T)getCurrentSession().get(desiredClass, id);        
        
        if (ret == null) {
            throw new ObjectNotFoundException("Object " + desiredClass.getSimpleName() + "." + id + " not found");
        }      
        
        return ret;
    }
    
    @Override
    public void refresh(DataObject obj) {
    	getCurrentSession().refresh(obj);   
    }
    
    @Override
    public void reattachAndInitialize(DataObject obj) {
        reattach(obj);
        InitializeVisitor v = new InitializeVisitor();
        obj.accept(v);
    }
    
    @Override
    public void refreshAndInitialize(DataObject obj) {
    	getCurrentSession().refresh(obj);   
        InitializeVisitor v = new InitializeVisitor();
        obj.accept(v);    	
    }
   
    @Override
    public <T extends DataObject> T getNullAllowed(Class<T> desiredClass, int id) {
        return (T)getCurrentSession().get(desiredClass, id);
    }  
  
    @Override
    public void saveOrUpdate(DataObject... dataObjects) {
    	for (DataObject dataObject : dataObjects) {
	        saveOrUpdate(dataObject, null);
    	}
    }

    public void saveOrUpdate(DataObject dataObject, Change change) {
        logger.debug("persisting {}",  getObjectShortDesc(dataObject));
        if (dataObject instanceof CrmProxy) {
            dataObject = CrmReflectionUtil.unproxyC(dataObject);
        }

        resetModDate(dataObject);

        getCurrentSession().saveOrUpdate(dataObject);   

        for (SaveOrUpdateHook hook : getSaveOrUpdateHooks()) {
        	hook.afterSaveOrUpdate(dataObject, change);
        }
    }

    @Override
    public void persistInAutonomousTransaction(List<? extends DataObject> dataObjects) {
        for (DataObject o : dataObjects) {
            saveOrUpdate(o);
        }
    }
    
    @Override
    public void delete(DataObject obj) {
        if (isDetached(obj)) {
            obj = reattach(obj); 
        }
        getCurrentSession().delete(obj);
    }  
   
    @Override
    public <T extends DataObject> int deleteAll__(Class<T> desiredClass) {
        return executeUpdate("delete "+desiredClass.getName());
    }
    
    public <T extends DataObject> int deleteAllCascade__(Class<T> desiredClass) {
        int i = 0;
        for (DataObject o : getAll(desiredClass)) {
            delete(o);
            i++;
        }
        return i;
    }
    
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    @Override
    public int executeUpdate(String hql, Object... positionalParams) {
    	    	
    	  Query queryObject = getCurrentSession().createQuery(hql);
          if (positionalParams != null) {
              for (int i = 0; i < positionalParams.length; i++) {
                  queryObject.setParameter(i+1+"", positionalParams[i]);
              }
          }
          return queryObject.executeUpdate();
    }  
    	
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
	@Override
	@SuppressWarnings("unchecked")
	public <T extends DataObject> List<T> getAll(Class<T> desiredClass) {
		List<T> list = null;
		
		list = findByHQL("select it FROM "+desiredClass.getName()+" it");
		
		return list;		
	}
	
	/**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    @Override
    public <T extends Object> List<T> findByHQL(String hql, Object... positionalParams) {
        return findByHQLmax(hql, Integer.MAX_VALUE, positionalParams);
	}	
	
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
	@Override
	public List findByHQLmax(String hql, int maxResults, Object... positionalParams) {
        Query queryObject = getCurrentSession().createQuery(hql);
        queryObject.setMaxResults(maxResults);
                
        if (positionalParams != null) {
            for (int i = 0; i < positionalParams.length; i++) {
                queryObject.setParameter(i+1+"", positionalParams[i]);
            }
        }
        return queryObject.list();
	}
          
	@Override
	public <T extends DataObject> T getOneByParameter(Class<T> desiredClass, String paramName, Object paramValue) {
	    Map<String, Object> map = new HashMap<String, Object>(1);
	    map.put(paramName, paramValue);
	    return getOneByParameters(desiredClass, map);
	}

	@Override
	public <T extends DataObject> T getOneByParameters(Class<T> desiredClass, Map<String, Object> paramsMap) {
	    List<T> resultList = runParametersQuery(desiredClass, paramsMap, 2);

	    if (resultList == null || resultList.size() == 0) {
	        return null;
	    }

	    if (resultList.size() > 1) {
	        throw new CriterionIsNotUnique("getOneByParameter(): criterion ["
	                + paramsMap + "] is not unique for entity "
	                + desiredClass.getName());
	    }

	    return resultList.get(0);
	}
	    
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    @Override
    public List findByCriteria(final DetachedCriteria criteria, final int maxResults) {
    	Preconditions.checkArgument(maxResults > 0);

        Criteria executableCriteria = criteria.getExecutableCriteria(getCurrentSession());

        executableCriteria.setMaxResults(maxResults);
       
        List result = executableCriteria.list();
        if (maxResults >=100 && result.size() == maxResults) {
        	logger.warn("findByCriteria: result.size() == maxResults ("+maxResults+"), it means that you should be more specific");
        }
        
        return result;
    }
    
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    @Override
    public <T extends DataObject> List<T> findByParameter(Class<T> desiredClass, String paramName, Object paramValue) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put(paramName, paramValue);
        return findByParameters(desiredClass, map);
    }

    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataObject> List<T> findByParameters(Class<T> desiredClass, Map<String, Object> paramsMap) {
        List<T> resultList = runParametersQuery(desiredClass, paramsMap, Integer.MAX_VALUE);

        if (resultList == null) {
            return Collections.EMPTY_LIST;
        }

        return resultList;
    }
        
	/**
	 * Spring 3.1 impl (no hibernateTemplate)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Object> List<T> findByHQLnamedParam(String hql, Map<String, Object> namedParams) {
		return findByHQLnamedMax(hql, Integer.MAX_VALUE, namedParams);
	}
    
	@Override
	public <T extends Object> List<T> findByHQLnamedMax(String hql, int maxResults, Map<String, Object> namedParams) {
		// map => 2 arrays
		if (namedParams == null)
			namedParams = Collections.EMPTY_MAP;

		String[] paramNames = new String[namedParams.size()];
		Object[] values = new Object[namedParams.size()];

		int i = 0;
		for (Entry<String, Object> entry : namedParams.entrySet()) {
			paramNames[i] = entry.getKey();
			values[i] = entry.getValue();
			i++;
		}
		// eof map to arrays

		return findByHQLnamedMax(hql, maxResults, paramNames, values);		
	} 
	
	@Override
	public <T extends Object> T getOneByHQL(String hql, Object... params) {
	    List<T> foundObjects = findByHQL(hql, params);
	    return (T)getUnique(foundObjects);
	}
	
	@Override
	public <T extends Object> T getOneByHQLnamedParam(String hql, Map<String, Object> namedParams) {
	    List<T> foundObjects = findByHQLnamedParam(hql, namedParams);
	    return (T)getUnique(foundObjects);
	}
	

	
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
	@Override
	public <T extends Object> List<T> findByHQLnamedMax(String hql, int maxResults, String[] paramNames, Object[] paramValues) {
        Query queryObject = getCurrentSession().createQuery(hql);
        if (maxResults != Integer.MAX_VALUE) {
            queryObject.setMaxResults(maxResults);
        }
        
        if (paramValues.length > 0 ) {
            for (int j = 0; j < paramValues.length; j++) {
                applyNamedParameterToQuery(queryObject, paramNames[j], paramValues[j]);
            }
        }
        @SuppressWarnings("unchecked")
		List<T> result = queryObject.list();
        return result;
	}

    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    @Override
    public Integer queryForIntNamed(String hql, Map<String, Object> namedParams) {
        List result = findByHQLnamedParam(hql, namedParams);
        
        return processQueryForIntResult(result, hql);
    }
      
    @Override
    public Integer queryForInt(String hql, Object... ordinalParams) {
    	 List result = findByHQL(hql, ordinalParams);
    	 
    	 return processQueryForIntResult(result, hql);
    }    
    
    @Override
    public Integer queryForIntNamedNALimit1(String hql, Map<String, Object> namedParams){
    	List result = findByHQLnamedMax(hql, 1, namedParams);
    	
    	if (result.isEmpty())
    		return null;
    	
    	return getFirstRowAsInteger(result);  	
    }
    
    @Override
    public Integer queryForIntNALimit1(String hql, Object... ordinalParams) {
    	List result = findByHQLmax(hql, 1, ordinalParams);
    	
    	if (result.isEmpty())
    		return null;
    	
    	return getFirstRowAsInteger(result);
    }
    
    private Integer processQueryForIntResult (List result, String hql) {
        if (result.isEmpty())
            throw new ObjectNotFoundException("queryForInt_("+hql+") : got empty result set");
        
        if (result.size() > 1) 
            throw new CriterionIsNotUnique("queryForInt_("+hql+") : got "+result.size()+" rows");
        
        return getFirstRowAsInteger(result);
    }
    
    private Integer getFirstRowAsInteger(List result) {
    	Object num = result.get(0);
        
        if (num == null)
            return null;
        
        if (num instanceof Long)
            return ((Long)num).intValue();
        
        if (num instanceof Integer) 
            return (Integer)num;
        
        throw new RuntimeException("queryForInt_() : cant handle ret value type - "+ num.getClass().getName());
    }
    
    @Override
    public  Session getCurrentSession(){
        return sessionFactory.getCurrentSession();
	}
       
    @Override
    public void printSessionFactoryStats() {
    	logger.info("sessionFactoryStats:");
        Statistics stats = sessionFactory.getStatistics();
                
        String [] cacheRegionNames = stats.getSecondLevelCacheRegionNames();      
        logger.info("-- second-lvel-cache region details: ");
        for (int i=0; i<cacheRegionNames.length; i++) {
            logger.info(i +". "+ cacheRegionNames[i]);
            SecondLevelCacheStatistics regionStats = stats.getSecondLevelCacheStatistics(cacheRegionNames[i]);            
           
            if (regionStats.getElementCountInMemory() > 0) {
            	 logger.info(".. cache-region-count-in-memory  :"+regionStats.getElementCountInMemory());
            	 logger.info(".. cache-region-put  :"+regionStats.getPutCount());
            	 logger.info(".. cache-region-hit  :"+regionStats.getHitCount());
            	 logger.info(".. cache-region-miss :"+regionStats.getMissCount());
            }
           
        }
                                 
        logger.info("-- second-lvel-cache summary: ");
        logger.info("cache-put:  " +  stats.getSecondLevelCachePutCount());
        logger.info("cache-hit:  " +  stats.getSecondLevelCacheHitCount());
        logger.info("cache-miss: " + stats.getSecondLevelCacheMissCount());
        
        logger.info("query cache-put:  " +  stats.getQueryCachePutCount());
        logger.info("query cache-hit:  " +  stats.getQueryCacheHitCount());
        logger.info("query cache-miss: " + stats.getQueryCacheMissCount());                
        
        logger.info("-- ");
        logger.info("session.openCount: "+ stats.getSessionOpenCount());        
        logger.info("session.entityFetchCount:      "+stats.getEntityFetchCount());
        logger.info("session.prepareStatementCount: "+stats.getPrepareStatementCount());

        
        logger.info(".");
    }
    
    @Override
    public Set<DataObject> getSessionContent() {
    	Set<DataObject> ret = new HashSet<DataObject>();
    	
        SessionImpl session = (SessionImpl)getCurrentSession();
        PersistenceContext ctx = session.getPersistenceContext();
        Map entities = ctx.getEntitiesByKey();
    	
        for (Object obj : entities.entrySet()) {
        	if (obj != null && obj instanceof DataObject)
        		ret.add((DataObject)obj);
        }
        
    	return ret;
    }
    
    @Override
    public void printSessionContent() {
        
        SessionImpl session = (SessionImpl)getCurrentSession();

        PersistenceContext ctx = session.getPersistenceContext();

        Map entities = ctx.getEntitiesByKey();
        
        logger.info("printSessionContent() : "+ getCurrentSessionInfo() );
        int i=0;
        
        
        Set copiedSet= new HashSet(entities.keySet());
        
        for (Object key : copiedSet) {
            Object obj =  entities.get(key);
            if (obj != null) {
                
                EntityEntry entity = ctx.getEntry(obj);
                if (entity != null) {
                    String exists = "";                 
                    
                    if ( ! entity.isExistsInDatabase()){
                        exists = "! false !";
                    }
                    else {
                        exists = "true";
                    }
                    
                    String transientRefCnt = "";
                    String desc = "";
                    
                    if (DataObject.class.isAssignableFrom(obj.getClass()) )
                    {
                        /*
                        int t = ReflectionUtil.getTransientRefCnt( (DataObject)obj  );
                        
                        if (t > 0) {
                            transientRefCnt = ", ! transientRefCnt:"+t+" !!!";
                        }
                        else {
                            transientRefCnt = ", transientRefCnt:"+t;
                        }
                        */
                        desc = ((DataObject)obj).getGlobalId();
                    }
                    else {
                        desc = entity.getEntityName()+"#"+entity.getId();
                    }
                    
                    logger.info(i+". "+
                                   desc+
                                   ", status:"+entity.getStatus()+
                                 ", existsInDB:"+ exists+
                                //transientRefCnt+
                                 ", s.idc:"+System.identityHashCode(obj));
                    i++;
                }
            }
            
        }
        logger.info("done, "+i+" object(s) in session");
    }
    
    //-- private
    /**
     * Spring 3.1 impl (no hibernateTemplate)
     */
    private <T extends Object> List<T> runParametersQuery(Class<T> desiredClass,
                                                          Map<String, Object> paramsMap,
                                                          int maxResults) {
        if (paramsMap == null || paramsMap.size() == 0)
            throw new IllegalArgumentException("runParametersQuery(): no paramsMap given");

        DetachedCriteria crit = DetachedCriteria.forClass(desiredClass);
        
        for (Entry<String, Object> entry : paramsMap.entrySet()) {
        	String paramName = entry.getKey();
            Object paramValue = entry.getValue();
            if (paramValue == null) {
                crit.add(Restrictions.isNull(paramName));
            } else {
                crit.add(Restrictions.eq(paramName, paramValue));
            }
        }
            
        return findByCriteria(crit, maxResults); 
    }
    
    /**
     * bw: copy-paste z HibernateTemplate
     * 
     * Apply the given name parameter to the given Query object.
     * @param queryObject the Query object
     * @param paramName the name of the parameter
     * @param value the value of the parameter
     * @throws HibernateException if thrown by the Query object
     */
    private void applyNamedParameterToQuery(Query queryObject, String paramName, Object value) {
        if (value instanceof Collection) {
            queryObject.setParameterList(paramName, (Collection) value);
        }
        else if (value instanceof Object[]) {
            queryObject.setParameterList(paramName, (Object[]) value);
        }
        else {
            queryObject.setParameter(paramName, value);
        }
    }
    
    /**
     * Czy obiekt ma jakies PersistentSet'y, ktore sa w statusie Dirty
     */
    private boolean hasDirtyCollection(DataObject dataObject) {
        Object[] args = new Object[0]; 
        Iterator<Method> it = ReflectionUtil.getPersistentGetters(dataObject.getClass()).iterator();
        while (it.hasNext()) {
            Method m = it.next();            
            if ( ReflectionUtil.isCollectionClass(m.getReturnType()))
            {
                try {
                    
                    if (!m.isAccessible()) {
                        m.setAccessible(true);
                    }
                    Collection collection = (Collection)m.invoke(dataObject, args);
                    
                    if (collection != null &&
                        collection instanceof AbstractPersistentCollection) 
                    {
                        
                        if ( ((AbstractPersistentCollection)collection).isDirty()) {
                         //   logger.info("Object "+ dataObject.getGlobalId()+" has dirty collection: "+ m.getName());
                            return true;
                        }
                    }
                    
                   
                    if (collection != null &&
                        ! (collection instanceof AbstractPersistentCollection) ) {
                        //this collection is not loaded from data base, so it is dirty
                        return true;
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException("hasDirtyCollection() [for "+m+"] : "+ e.getClass()+" - "+e.getMessage(),e);
                }
            }
        }
        
        return false;
    }
    
    
    private <T extends Object> T getUnique(List<T> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return null;
        }
        if (objects.size() > 1) {
            throw new javax.persistence.NonUniqueResultException("More than one object found");
        }
        return objects.get(0);
    }
    
    private List<SaveOrUpdateHook> getSaveOrUpdateHooks() {
    	if (saveOrUpdateHooks == null) {
    		return Collections.EMPTY_LIST;
    	}
    	return saveOrUpdateHooks;
    }
    

	@Override
	public void setSaveOrUpdateHooks(List<SaveOrUpdateHook> hooks) {
		this.saveOrUpdateHooks = hooks;		
	}

    @Override
    public List<?> findBySql(String sql) {
        List<?> ids = getCurrentSession().createSQLQuery(sql).list();
        return ids;
    }
}
