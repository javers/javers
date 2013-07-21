package pl.edu.icm.sedno.common.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Cache;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;

import pl.edu.icm.crmanager.model.Change;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.model.ObjectState;
import pl.edu.icm.sedno.patterns.InitializeVisitor;

/**
 * Super common DAO,
 * ogólne dao do prostych operacji CRUD na bazie danych. <br/>
 *
 * Stanowi <b>fasadę</b> ukrywającą złożoność właściwego persistent API (JPA lub Hibernate). <br/>
 *
 * Dodatkowo dodaje kilka prostych i przydatnych funkcji, np {@link #getObjectState(DataObject)}
 *
 * @author bart
 */
public interface DataObjectDAO {
    //static final int DEFAULT_MAX_RESULTS = 500;

    /**
     * @see ObjectState
     */
     ObjectState getObjectState(DataObject obj);

    /**
     * @see ObjectState
     */
     boolean isDetached(DataObject obj);

    /**
     * @see ObjectState
     */
     boolean isPersistent(DataObject obj);

    /**
     * Wymuszenie posta do DB zmian zbuforowanych w persistent context
     */
     void flush();


     /**
      * clear first level cache (session)
      */
     void clear();

     /**
      * czyści second level cache, używać ostrożnie!
      *
      * @see Cache#evictEntityRegions()
      */
     void clearSecondLevelCache();

    /**
     * wykonuje reattach() oraz {@link InitializeVisitor}
     */
     void reattachAndInitialize(DataObject obj);

    /**
     * wykonuje {@link InitializeVisitor} oraz {@lnk EvictVisitor}
     */
     void initializeAndEvict(DataObject obj);

     /**
      * wykonuje {@link InitializeVisitor}
      * Does nothing if the passed obj == null
      */
     void initialize(DataObject obj);

    /**
     * current transaction info
     */
     String  getCurrentSessionInfo();

     Session getCurrentSession();

    /**
     * dataObjects in persistence context
     */
     Set<DataObject> getSessionContent();

    /**
     * prints persistence context
     */
     void printSessionContent();

     String getObjectShortDesc(DataObject obj);

    /**
     * fast reattach
     *
     * @return the same obj
     */
     <T extends DataObject> T reattach(T obj);

     void evict(DataObject obj);

     void evictEntityFromSecondLevelCache(DataObject obj);

     void evictCollectionFromSecondLevelCache(Class ownerClass, int ownerId, String collectionProperty);

    /**
     * never returns null
     *
     * @throws ObjectNotFoundException if not found
     */
     <T extends DataObject> T get(Class<T> desiredClass, int id)
    throws ObjectNotFoundException;

    void refresh(DataObject obj);

    void refreshAndInitialize(DataObject obj);

    /**
     * to samo co {@link #get} ale null if not found
     */
     <T extends DataObject> T getNullAllowed(Class<T> desiredClass, int id);

    /** saveOrUpdate */
     void saveOrUpdate(DataObject... dataObjects);

     /**
      * Save or update with a change descriptor that can be used by hooks
      *
      * @param modPoint modified object
      * @param change change descriptor
      */
     void saveOrUpdate(DataObject modPoint, Change change);

    /**
     * wymaga konfiguracji w tx-advice
     */
     void persistInAutonomousTransaction(List<? extends DataObject> dataObjects);

     void delete(DataObject obj);

    /**
     * Uwaga, czyści całą tabelę!
     * @return deleted rows count
     */
     <T extends DataObject> int deleteAll__(Class<T> desiredClass);

    /**
     * Uwaga, czyści całą tabelę!
     * @return deleted rows count
     */
     <T extends DataObject> int deleteAllCascade__(Class<T> desiredClass);

     void printSessionFactoryStats();


    //-- search functions --

    /**
     * unlimited, używać tylko dla małych tabel
     */
     <T extends DataObject> List<T> getAll(Class<T> desiredClass);

    /**
     * Since Hibernate 4.1.1, Hibernate-specific (JDBC-style) positional parameters are <b>deprecated</b> in favor of the JPA-style.
     * <br/>
     *
     * Use <b>JPA-style positional parameters</b>, ex. "from Cat c where c.name = ?1"
     *
     * @see https://hibernate.onjira.com/browse/HHH-7023
     *
     * @param  positionalParams lista wartości parametrów podstawianych pod placeholdery ?1 .. ?n
     * @return unlimited, never returns null
     */
     <T extends Object> List<T> findByHQL(String hql, Object... positionalParams);

    /**
     * limited to maxResults, never returns null
     *
     * @see {@link DataObjectDAO#findByHQL(String, Object...)}
     */
     //TODO sparametryzowac typem
     List findByHQLmax(String hql, int maxResults, Object... positionalParams);

    /**
     * executes update|delete statement
     * <br/>
     *
     * Use <b>JPA-style positional parameters</b>, ex. "from Cat c where c.name = ?1"
     *
     * @param  positionalParams lista wartości parametrów podstawianych pod placeholdery ?1 .. ?n
     * @return rows affected
     */
     int executeUpdate(String hql, Object... positionalParams);

    /**
     * @return null if not found
     * @throws CriterionIsNotUnique if not unique
     */
     <T extends DataObject> T getOneByParameter(Class<T> desiredClass, String paramName, Object paramValue)
    throws CriterionIsNotUnique;

    /**
     * null if not found
     *
     * @throws CriterionIsNotUnique if not unique
     */
     <T extends DataObject> T getOneByParameters(Class<T> desiredClass, Map<String, Object> paramsMap)
    throws CriterionIsNotUnique;

    /**
     * @param maxResults should be > 0
     * limited to maxResults, never returns null
     */
     List findByCriteria(final DetachedCriteria criteria, final int maxResults);

    /**
     * unlimited,
     * never returns null
     */
     <T extends DataObject> List<T> findByParameter(Class<T> desiredClass, String paramName, Object paramValue);

    /**
     * unlimited,
     * never returns null
     */
     <T extends DataObject> List<T> findByParameters(Class<T> desiredClass, Map<String, Object> paramsMap);

    /**
     * unlimited,
     * never returns null
     */
     <T extends Object> List<T> findByHQLnamedParam(String hql, Map<String, Object> namedParams);

    /**
     * limited to maxResults, never returns null
     */
     <T extends Object> List<T> findByHQLnamedMax(String hql, int maxResults, Map<String, Object> namedParams);

    /**
     * limited to maxResults, never returns null
     */
     <T extends Object> List<T> findByHQLnamedMax(String hql, int maxResults, String[] paramNames, Object[] paramValues);

     
     List<?> findBySql(String sql);
     
    /**
     * Wywołuje findByHQLnamedParam() i zwraca (Integer)result.get(0)
     *
     * @throws ObjectNotFoundException jeśli result.isEmpty
     * @throws CriterionIsNotUnique jeśli result.size > 1
     */
     Integer queryForIntNamed(String hql, Map<String, Object> namedParams);

    /**
     * Wywołuje findByHQL() i zwraca (Integer)result.get(0)
     *
     * @throws ObjectNotFoundException jeśli result.isEmpty
     * @throws CriterionIsNotUnique jeśli result.size > 1
     */
     Integer queryForInt(String hql, Object... ordinalParams);

    /**
     * wariant queryForIntNamed: null allowed, limit 1
     */
     Integer queryForIntNamedNALimit1(String hql, Map<String, Object> namedParams);

    /**
     * wariant queryForInt: null allowed, limit 1
     */
     Integer queryForIntNALimit1(String hql, Object... ordinalParams);

    /**
     * Uses {@link #findByHQL(String, Object...)}.
     * @throws {@link NonUniqueResultException} if there is more than one record for the given arguments
     * @return found object or null if there is no object meeting given criteria
     */
     <T extends Object> T getOneByHQL(String hql, Object... params);

    /** Uses {@link #findByHQLnamedParam(String, Map<String, Object>)}.
     * @throws {@link NonUniqueResultException} if there is more than one record for the given arguments
     * @return found object or null if there is no object meeting given criteria
     * */
     <T extends Object> T getOneByHQLnamedParam(String hql, Map<String, Object> namedParams);

    //-- EOF search functions --

    void setSaveOrUpdateHooks(List<SaveOrUpdateHook> hooks);
}
