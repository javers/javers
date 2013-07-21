package pl.edu.icm.sedno.common.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import pl.edu.icm.crmanager.model.CrmImmutable;
import pl.edu.icm.crmanager.model.CrmTransparent;
import pl.edu.icm.sedno.common.util.ReflectionUtil;
import pl.edu.icm.sedno.patterns.Visitor;

import com.google.common.base.Objects;

/**
 * Defaultowa implementacja DataObject
 * 
 * @author bart
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class ADataObject implements DataObject, Serializable {

    protected static final String IS_ACTIVE = "data_object_status = 'ACTIVE'";

    private   int ver;
    private   Date createDate = new Date(); 
    //private   boolean isNew;
    private   DataObjectStatus dataObjectStatus = DataObjectStatus.ACTIVE;
   
    /**
     * for hibernate optimistic locking
     */
    @Version
    @Column(columnDefinition="integer DEFAULT 0")
    @CrmImmutable
    public int getVer() {
      return ver;
    } 
    
    /**
     * timestamp utworzenia rekordu
     */
    @Column(columnDefinition="timestamp")
    @CrmImmutable
    public Date getCreateDate() {
        return createDate;
    }
    
    @Transient
    @Override
    public boolean isNew() {
        if (dataObjectStatus == null) 
            return false;
        return dataObjectStatus.equals(DataObjectStatus.NEW);
    }
    
    @Transient
    public boolean isDeleted() {
        if (dataObjectStatus == null) 
            return false;
        return dataObjectStatus.equals(DataObjectStatus.DELETED);
    }
    
    
    @Override
    @CrmTransparent
    @Enumerated(EnumType.STRING)
    @Column(name="data_object_status", columnDefinition="VARCHAR(10) default 'ACTIVE'")
    public DataObjectStatus getDataObjectStatus(){
        return dataObjectStatus;
    }
    
    /**
     * Globalny identyfikator obiektu w formacie <b>wrapped</b>ClassName#id, <br/>
     * 
     * Jeśli obiekt nie ma jeszcze wartości klucza głównego wynikiem jest
     * ClassName#Transient#System.identityHashCode
     */
    @Transient  
    @Override
    public final String getGlobalId() {
        if ( getId() == 0) {
            return this.getWrappedClass().getName() + "#Transient#"+  System.identityHashCode(this);
        } else {
            return formatGlobalId(this.getWrappedClass().getName(), getId());
        }
    }

    public static final String formatGlobalId(String className, int localId) {
        return className+'#'+localId;
    }

    /**
     * Generyczna metoda zwracająca Id (Primary Key) obiektu.<br/>
     * Wynikiem jest wartość odpowiedniego gettera z this.getClass().
     */
    @Transient 
    @Override
    public int getId(){
        Method IDGetter = ReflectionUtil.getIDGetter(this.getClass());
        try {
            return (Integer)IDGetter.invoke(this, new Object[0]);
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e);
        }
    }

    /**
     * Jeśli obiekt jest HibernateProxy, zwraca klasę proxowanej instancji <br/>
     */
    @Transient
    @Override
    public Class getWrappedClass(){
        return this.getClass();
    }
    
    /**
     * HashCode na podstawie {@link #getGlobalId()} <br/><br/>
     * 
     * <b>Uwaga!</b> hashCode może się zmienić jeśli obiekt wykona przejście fazowe 
     * z transient na persistent. <br/>
     * 
     * Stąd, jeśli ADataObiect zostanie wstawiony np. jako klucz do HashMapy w stanie transient a następnie spersystowany -
     * nie będzie go można odlaneźć. W takiej sytuacji należy używać np {@link IdentityHashMap}
     */
    @Override
    public int hashCode() {
        if (getId() == 0) {
            return super.hashCode();
        }
                        
        return getGlobalId().hashCode();
    }
        
     /**
      * Warunkiem równości jest równość klucza głównego obiektów, <br>
      * patrz {@link #getGlobalId} <br><br>
      * 
      * Jeśli this jest transient - return this == o
      */
     @Override
     public boolean equals(Object o) {
         if( o == null ) return false;
     
         if( this == o ) return true;

         /*
         Różne obiekty mogą dostać ten sam system.identityHashCode,
         ponieważ Sun wylicza go na podstawie adresu obiektu na heap'ie a GC potrafi defragmentować heap*/
         if ( this.getId() == 0 ) {
             return this == o;
         }
         
         //porównanie po getGlobalId jeśli this jest persystentny
         return
                 ADataObject.class.isAssignableFrom(o.getClass())
                 &&
                 this.getGlobalId().equals( ((ADataObject)o).getGlobalId() );
     }

    @Override
    @Transient
    public boolean isTransient() {
        return getId() == 0;
    } 
    
    @Override
    public String toString() {
    	return Objects.toStringHelper(this)
    	   .add("id", getId())
    	   .add("ver", ver)
    	   .add("status", dataObjectStatus)
           .toString();
    }
    
    /**
     * Defaultowa implementacja Visitable.accept() wzorca VISITOR
     * 
     * Znajduje odpowiednią metodę visitora i wywołuje ją,
     * metodę należy nadpisywać w klasach konkretnych jeśli zawierają child-obiekty
     * 
     * @see ReflectionUtil#findVisitorMethod
     */
    @Override
    public void accept(Visitor<DataObject> visitor) {
        try {
            Method visit = ReflectionUtil.findVisitorMethod(this.getClass(), visitor.getClass());
            
            visit.invoke(visitor, new Object[]{this});
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("where is your visit method?",e);
        }
        catch (InvocationTargetException e) {
        	if (e.getCause() != null && e.getCause() instanceof RuntimeException)
        	throw (RuntimeException)e.getCause();
		}
        catch (IllegalAccessException e) {
        	throw new RuntimeException(e);
		}
        
       // catch (Exception ei) { //invocation exception
       //    throw new RuntimeException(ei);
       // }
    }
    
    @Override
    public void initialize() {}
     
    //-- setters
    private void setVer(int ver) {
         this.ver = ver;
    }

    private void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
    //@Override
    //public void setNew(boolean isNew) {
    //    this.isNew = isNew;
    //}
    
    @Override
    public void setDataObjectStatus(DataObjectStatus dataObjectStatus) {
        this.dataObjectStatus = dataObjectStatus;
    }

    private HashMap<String, Object> virtualProperties = new HashMap<String, Object>();

    /**
     * Retrieves the value of a virtual property (used by the JAXB bindings).
     *
     * @param name property name
     * @return property value
     */
    @Transient
    public Object getVirtualProperty(String name) {
        return virtualProperties.get(name);
    }

    /**
     * Sets the value of a virtual property (used by the JAXB bindings).
     *
     * @param name property name
     * @param value proprty value
     */
    public void setVirtualProperty(String name, Object value) {
        virtualProperties.put(name, value);
    }
}
