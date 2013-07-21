package pl.edu.icm.crmanager.model;

import static pl.edu.icm.sedno.common.model.ADataObject.formatGlobalId;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.hibernate.StringPersistedUserType;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.PropertyChange;
import pl.edu.icm.sedno.common.util.ReflectionUtil;
import pl.edu.icm.sedno.common.util.UnproxyHVisitor;
import pl.edu.icm.sedno.patterns.Visitor;

import com.google.common.base.Preconditions;

@Entity(name="crm_request")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@javax.persistence.SequenceGenerator(name = "seq_crm_request", allocationSize = 20, sequenceName = "seq_crm_request")
public class ChangeRequest implements Serializable {
	private int idChangeRequest;
	private int requestNo;
	private Revision revision;	
	private RecType   recType;
	private ValueType valueType;
	
	
	private DataObject oldReference__;	
    private DataObject newReference__;		
	private DataObject node__;	
	private List<PropertyChange> propertyChanges__; 	
	private transient Method getterMethod__;
	
	private String nodeClass;
	private int nodeId;
	private String getterName;
	
	private String  oldReferenceClass;
	private int     oldReferenceId;
	
	private String  newReferenceClass;
	private int     newReferenceId;
	
	private String  oldStringValue;
	private String  newStringValue;
	
	private Date    oldDateValue;
	private Date    newDateValue;

	private Double  oldDecimalValue;
	private Double  newDecimalValue;
	
	private Boolean oldBooleanValue;
	private Boolean newBooleanValue;
	    
	private Integer oldIntValue;
    private Integer newIntValue;
    
    private List oldPrimitiveListValue;
    private List newPrimitiveListValue;
    
    private boolean changeImportant;
	
    public ChangeRequest() {}
	
    public ChangeRequest(DataObject node, String getterName, RecType recType, ValueType valueType ) {
	
    	DataObject ref = unproxyCH(node);
        this.node__ =  ref;
        this.nodeClass = ref.getClass().getName();
        this.nodeId =  ref.getId();    
        this.getterName = getterName;
        this.recType = recType;
        this.valueType = valueType;
	}   
    
    public void cleanTransientReferences() {
    	node__ = null;
    	oldReference__ = null;
    	newReference__ = null;
    	getterMethod__ = null;
    }
    
    /**
     * full unproxyH of transient references
     */
    @Deprecated
    public void unproxyTransientReferences() {
    	node__ = fullUnproxyH(node__);
    	oldReference__ = fullUnproxyH(oldReference__);
    	newReference__ = fullUnproxyH(newReference__);
    }
    
    private static <T extends DataObject> T fullUnproxyH(T obj) {
    	if (obj == null) {
			return null;
		}
		
		T unproxied = ReflectionUtil.unproxyH(obj);
		
		Visitor visitor = new UnproxyHVisitor();
		unproxied.accept(visitor);
		
		return unproxied;
	} 
    
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_crm_request")
	public int getIdChangeRequest() {
		return idChangeRequest;
	}
    
	/**
	 * @see ChangeImportant
	 */
	@Column(name="is_change_important", columnDefinition="boolean default false")
    public boolean isChangeImportant() {
        return changeImportant;
    }    

	/**
	 * Nazwa klasy (simple) obiektu zmienianego
	 */
	@Column(length=150, name="node_class")
	public String getNodeClass() {
		return nodeClass;
	}

	/**
	 * PK obiektu zmienianego
	 */
	@Column(name="node_id")
	public int getNodeId() {
		return nodeId;
	}	

	/**
	 * @return Equivalent of {@link DataObject#getGlobalId()}
	 */
	@Transient
	public String getNodeGlobalId() {
            return formatGlobalId(getNodeClass(), getNodeId());
	}

	/**
	 * Persistent getter, powiązany ze zmianą
	 */
	@Column(length=150)
	public String getGetterName() {
		return getterName;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name = "rec_type")
	public RecType getRecType() {
		return recType;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "value_type")
	public ValueType getValueType() {
		return valueType;
	}
	
	/**
	 * numer zmiany w obrębie revision
	 */
	@Column(name="request_no")
	public int getRequestNo() {
		return requestNo;
	}
	
	@Index(name="crm_request_fk_revision_idx")
	@ManyToOne(fetch=FetchType.LAZY)
	public Revision getRevision() {
		return revision;
	}
	
	@Column(length=150, name="old_reference_class")
	public String getOldReferenceClass() {
		return oldReferenceClass;
	}	
	
	@Column(length=150, name="new_reference_class")
	public String getNewReferenceClass() {
		return newReferenceClass;
	}
	
	@Column(name="old_reference_id")
	public int getOldReferenceId() {
		return oldReferenceId;
	}
	
	@Column(name="new_reference_id")
	public int getNewReferenceId() {
		return newReferenceId;
	}
	
	@Transient
	private CrmSimpleEmbeddable getOldStringEmbeddable() {
	    return createStringEmbeddable( getOldStringValue() );
	}
		
	@Transient
	private CrmSimpleEmbeddable getNewStringEmbeddable() {
	    return createStringEmbeddable( getNewStringValue() );
	}	
	
	@Transient
	private Object getOldStringPersistedValueObject() {
		return createStringPersistedValueObject( getOldStringValue() );
	}
	
	@Transient
	private Object getNewStringPersistedValueObject() {
		return createStringPersistedValueObject( getNewStringValue() );
	}
	
	@Transient
	private Enum getNewEnum() {
	    return createEnum(getNewStringValue());
	}
	
	@Transient
	private Enum getOldEnum() {
	    return createEnum(getOldStringValue());    
	}
	
	private Enum createEnum(String fromValue) {
        if (fromValue == null)
            return null;	    
        
        Method getter = getterMethod();  
        
        if (!Enum.class.isAssignableFrom(getter.getReturnType()))
            throw new CrmRuntimeException("createEnum('"+fromValue+"') : !getter.getReturnType().isAssignableFrom(Enum), getter: "+getter);
        
        try {
            Method valueOf = getter.getReturnType().getMethod("valueOf", new Class[]{String.class} );
            return (Enum) valueOf.invoke(null, fromValue);
        } catch (Exception e) {
            throw new CrmRuntimeException("createEnum('"+fromValue+"') failed",e);
        }    
	}
	
	private CrmSimpleEmbeddable createStringEmbeddable(String fromValue) {
	    if (fromValue == null)
	        return null;
	    
	    Method getter = getterMethod();        
        
        if (!CrmSimpleEmbeddable.class.isAssignableFrom(getter.getReturnType()))
            throw new CrmRuntimeException("!getter.getReturnType().isAssignableFrom(CrmSimpleEmbeddable.class), getter: "+getter);
        
        return (CrmSimpleEmbeddable) ReflectionUtil.invokeConstructor( getter.getReturnType(), fromValue);
        
	}
	
	private Object createStringPersistedValueObject(String fromValue) {
		Class valueClass = getterMethod().getReturnType();
		StringPersistedUserType userType = ChangeRequestFactory.getUserType(valueClass);
		
		if (userType==null) {
			return fromValue; 
		}else {
			return userType.readFromDatabase(fromValue);
		}
	}
	
	@Transient
	public Class nodeClass() {
		 Class nodeClazz = null;
	     if (node__ != null) {
	         nodeClazz = node__.getClass();
	     }
	     else {      
	         nodeClazz = ReflectionUtil.forName(nodeClass);
	     }
	     return nodeClazz;
	}
	
	/**
	 * modPoint getter.
	 * In case of complexEmbeddable - embedded modPoint getter (rightmost getter in chain)
	 * 
	 */
	@Transient
    public Method getterMethod() {		
		if (getterMethod__ == null) {
	                 
	        getterMethod__ = ReflectionUtil.findGetter(getterName, nodeClass());
		}        
        return getterMethod__;
    }
		
	@Transient
	public boolean isComplexEmbeddable() {
		if (getterName == null) {
			return false;
		}
		return getterName.contains(".");
	}
	
	@Transient
	private Class<?> getComplexEmbeddedType() {
		return getterToEmbeddedModPoint().getReturnType();
	}
	
	/**
	 * if {@link #isComplexEmbeddable} - 
	 * returns embeddable object owned by modPoint.
	 * If embeddable is null, would be created & set into modPoit
	 */
	@Transient
	private Object getEmbeddedModPoint(DataObject modPoint) {
		Preconditions.checkState(isComplexEmbeddable());
		
		Method getterToEmbedded = getterToEmbeddedModPoint();
		Object embeddedModPoint = ReflectionUtil.invokeGetterEvenIfPrivate(getterToEmbedded, modPoint);
		
		if (embeddedModPoint == null) {
			embeddedModPoint = ReflectionUtil.invokeConstructor(getComplexEmbeddedType());
			Method setterToEmbedded = ReflectionUtil.getterToSetter(getterToEmbedded, modPoint.getClass()) ;
			ReflectionUtil.invokeSetterEvenIfPrivate(setterToEmbedded, modPoint, embeddedModPoint);
		}
		
		return embeddedModPoint;
	}
	
	/**
	 * if {@link #isComplexEmbeddable} -
	 * returns getter TO embeddable object owned by modPoint
	 */ 
	@Transient
    private Method getterToEmbeddedModPoint() {		
		Preconditions.checkState(isComplexEmbeddable());
		String getterChain[] = getterName.split("\\.");
		
		return ReflectionUtil.findGetter(getterChain[0], nodeClass());
    }
	
	@Transient
	public Method setterMethod() {
		if (isComplexEmbeddable()) {
			return ReflectionUtil.getterToSetter(getterMethod(), getComplexEmbeddedType());
		}else {
			return  ReflectionUtil.getterToSetter(getterMethod(), nodeClass());
		}
	}
	
	public void applyValueChange(DataObject modPoint, Object valueOrRef) {
		Method setter = setterMethod();
        
        try {
            if (isComplexEmbeddable()) {
            	Object embeddedModPoint = getEmbeddedModPoint(modPoint); //instantiates if necessary
            	ReflectionUtil.invokeSetterEvenIfPrivate(setter, embeddedModPoint, valueOrRef);
            	//System.out.println(".. applied value ["+valueOrRef+"] on embeddedModPoint: "+ embeddedModPoint);            	
            }   else {
            	ReflectionUtil.invokeSetterEvenIfPrivate(setter, modPoint, valueOrRef);            	
            	//System.out.println(".. applied value ["+valueOrRef+"] on modPoint: "+ modPoint);
            }                    	        	        	
        } catch (Exception e) {
            throw new CrmRuntimeException("error calling setter ["+modPoint.getGlobalId()+"."+setter.getName()+"()]",e);
        }      
	}
		
	/**
	 * @return null if not @OneToMany
	 */
	@Transient
	public Method getBackRefSetter() {
		 
		 if (isOneToMany()) {
			 return ReflectionUtil.getBackRefSetter( getterMethod() );
		 }
		 return null;
	}
	
	@Transient
	public boolean isOneToMany(){
		return getterMethod().isAnnotationPresent(OneToMany.class);
	}
	
	/*@Transient
	private boolean isManyToMany(){
		return getterMethod().isAnnotationPresent(ManyToMany.class);
	}*/
	
	@Column(name="old_string_value")
	public String getOldStringValue() {
		return oldStringValue;
	}
	
	@Column(name="new_string_value")
	public String getNewStringValue() {
		return newStringValue;
	}
	
	@Column(name="old_date_value")
	public Date getOldDateValue() {
		return oldDateValue;
	}
	
	@Column(name="new_date_value")
	public Date getNewDateValue() {
		return newDateValue;
	}

	@Column(name="old_decimal_value", scale=4, precision=14)
	public Double getOldDecimalValue() {
		return oldDecimalValue;
	}
	
	@Transient
	public BigDecimal getOldBigDecimalValue() {
        return fromDouble(oldDecimalValue);
    }
	
    @Transient
    public BigDecimal getNewBigDecimalValue() {
        return fromDouble(newDecimalValue);
    }
	
	@Column(name="new_decimal_value", scale=4, precision=14)
	public Double getNewDecimalValue() {
		return newDecimalValue;
	}
	
	@Column(name="old_int_value")
	public Integer getOldIntValue() {
        return oldIntValue;
    }
	
	@Column(name="new_int_value")
	public Integer getNewIntValue() {
        return newIntValue;
    }
	
	@Column(name="new_boolean_value")
	public Boolean getNewBooleanValue() {
            return newBooleanValue;
        }
	
	@Column(name="old_boolean_value")
	public Boolean getOldBooleanValue() {
        return oldBooleanValue;
    }
        
        @Type(type = "pl.edu.icm.sedno.common.hibernate.StringListUserType")
        @Column(name="new_primitive_list_value")
        public List getNewPrimitiveListValue() {
            return this.newPrimitiveListValue;
        }
        
        @Type(type = "pl.edu.icm.sedno.common.hibernate.StringListUserType")
        @Column(name="old_primitive_list_value")
        public List getOldPrimitiveListValue() {
            return this.oldPrimitiveListValue;
        }
	
	private String spaceRPad(String str, int size){
	    return StringUtils.rightPad(str, size);
	}
	
	@Transient
	public boolean hasTransientReference() {
	    return isOldReferenceTransient() || isNewReferenceTransient() || isNodeReferenceTransient();
	}
	
	public int tryToResolveTransientReference() {
	    int ret = 0;
	    if (isOldReferenceTransient() && oldReference__ != null && oldReference__.getId() > 0) {
	        oldReferenceId = oldReference__.getId();
	        ret++;
	    }
	    
	    if (isNewReferenceTransient() && newReference__ != null && newReference__.getId() > 0) {
	        newReferenceId = newReference__.getId();
	        ret++;
	    }
	    
	    if (isNodeReferenceTransient() && node__ != null && node__.getId() > 0) {
	        nodeId = node__.getId();
	        ret++;
	    }
	    
	    return ret;
	}
	
	@Transient
	private boolean isOldReferenceTransient() {
	    return (getOldReferenceClass() != null && getOldReferenceId() == 0);
	}
	
    @Transient
    private boolean isNodeReferenceTransient() {
        return (getNodeClass() != null && getNodeId() == 0);
    }
	
	@Transient
	private boolean isNewReferenceTransient() {
	    return (getNewReferenceClass() != null && getNewReferenceId() == 0);
	}
	
	@Transient
    public String getShortDesc() {
	    String imp = " ";
	    if (isChangeImportant()) imp = "*";
	    
	    String propChangesCnt = "";
	    if (CollectionUtils.isNotEmpty(getPropertyChanges__())) {
	    	propChangesCnt = " propertyChanges.size: "+ getPropertyChanges__().size();
	    }
	    
        return "request ["+getRequestNo()+"-"+spaceRPad(getRecType().toString(), 16)+
               imp+
               "modPoint:"+ spaceRPad(getModPointDesc(),50) + 
               "[ lSide:"+spaceRPad(getOldSideDesc(),30)+
               " rSide:"+spaceRPad(getNewSideDesc(),30)+
               "]"+
               propChangesCnt
               +" isComplexE:"+isComplexEmbeddable()
               +", valueType:"+valueType;
    }
	
    @Transient
    private String getOldReferenceDesc() {
        if (getOldReferenceClass() != null) {
            return getOldReferenceClassSimple() + "#" + getOldReferenceId();
        }
        return "";
    }

    @Transient
    private String getNewReferenceDesc() {
        if (getNewReferenceClass() != null) {
            return getNewReferenceClassSimple() + "#" + getNewReferenceId();
        }
        return "";
    }
	
    @Transient
    public String getOldSideDesc() {
        if (valueType == ValueType.REFERENCE) {
            return getOldReferenceDesc();
        }
        return getOldValue() + "";
    }

    @Transient
    public String getNewSideDesc() {
        if (valueType == ValueType.REFERENCE) {
            return getNewReferenceDesc();
        }
        return getNewValue() + "";
    }

    @Transient
    public Object getOldValueOrReference() {
    	if (valueType == ValueType.REFERENCE) {
    		return getOldReference__();
    	}
    	return getOldValue();    		
    }
    
    @Transient
    public Object getOldValue() {
        if (valueType == ValueType.DATE) {
            return getOldDateValue();
        }
        if (valueType == ValueType.STRING) {
            return getOldStringValue();
        }
        if (valueType == ValueType.STRING_EMBEDDABLE) {
            return getOldStringEmbeddable();
        }        
        if (valueType == ValueType.ENUM) {
            return getOldEnum();
        }
        if (valueType == ValueType.BOOLEAN) {
            return getOldBooleanValue();
        }
        if (valueType == ValueType.INT) {
            return getOldIntValue();
        }       
        if (valueType == ValueType.DECIMAL) {
            return getOldDecimalValue();
        }
        if (valueType == ValueType.BIG_DECIMAL) {
            return getOldBigDecimalValue();
        }        
        if (valueType == ValueType.STRING_PERSISTED_VALUE_OBJECT) {
        	return getOldStringPersistedValueObject();
        }
        if (valueType == ValueType.LIST) {
            return getOldPrimitiveListValue();
        }
        return null;
    }
    
    @Transient
    public Object getNewValueOrReference() {
    	if (valueType == ValueType.REFERENCE) {
    		return getNewReference__();
    	}
    	return getNewValue();    		
    }
    
    @Transient
    public Object getNewValue() {
        if (valueType == ValueType.DATE) {
            return getNewDateValue();
        }
        if (valueType == ValueType.STRING) {
            return getNewStringValue();
        }
        if (valueType == ValueType.STRING_EMBEDDABLE) {
            return getNewStringEmbeddable();
        }
        if (valueType == ValueType.ENUM) {
            return getNewEnum();
        }
        if (valueType == ValueType.BOOLEAN) {
            return getNewBooleanValue();
        }
        if (valueType == ValueType.INT) {
            return getNewIntValue();
        }       
        if (valueType == ValueType.DECIMAL) {
            return getNewDecimalValue();
        }
        if (valueType == ValueType.BIG_DECIMAL) {
            return getNewBigDecimalValue();
        }
        if (valueType == ValueType.STRING_PERSISTED_VALUE_OBJECT) {
        	return getNewStringPersistedValueObject();
        }
        if (valueType == ValueType.LIST) {
                return getNewPrimitiveListValue();
        }
        return null;
    }
    
    @Transient
    private String getNodeClassSimple() {
        String a[] = getNodeClass().split("\\.");
        return a[a.length - 1];
    }

    @Transient
    private String getOldReferenceClassSimple() {
        String a[] = getOldReferenceClass().split("\\.");
        return a[a.length - 1];
    }

    @Transient
    private String getNewReferenceClassSimple() {
        String a[] = getNewReferenceClass().split("\\.");
        return a[a.length - 1];
    }
	
	@Transient
	private String getModPointDesc() {
	    return getNodeClassSimple()+"#"+getNodeId()+"."+getGetterName()+"()";
	}
	
	@Transient
	public DataObject getNode__() {
        return node__;
    }
	
	@Transient
	public DataObject getNewReference__() {
        return newReference__;
    }
	
	@Transient
	public DataObject getOldReference__() {
        return oldReference__;
    }
	
	/**
	 * Miejsce do przechowywania szczegółowego wyniku nested diffa dla
	 * zmian na dużych Value Object'ach,
	 * np {@link ValueType#STRING_PERSISTED_VALUE_OBJECT}
	 */
	@Transient
	public List<PropertyChange> getPropertyChanges__() {
		return propertyChanges__;
	}
					
	/**
	 * combo setter
	 */
    public void setNewReferenceCombo(DataObject newReference) {
        DataObject ref =  unproxyCH(newReference);
   	    this.newReference__ =  ref;
	    this.newReferenceClass = ref.getClass().getName();
	    this.newReferenceId =    ref.getId();
	}
	    
    /**
     * combo setter
     */
	public void setOldReferenceCombo(DataObject oldReference) {
	    DataObject ref =  unproxyCH(oldReference);
	    this.oldReference__ =    ref;
	    this.oldReferenceClass = ref.getClass().getName();
	    this.oldReferenceId =    ref.getId();
	}
	    
    private DataObject unproxyCH(DataObject ref) {
    	return (DataObject)CrmReflectionUtil.unproxyCH(ref);
    }
	
	//setters
    public void setIdChangeRequest(int idChangeRequest) {
        this.idChangeRequest = idChangeRequest;
    }

    public void setRequestNo(int requestNo) {
        this.requestNo = requestNo;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }

    private void setRecType(RecType recType) {
        this.recType = recType;
    }

    private void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    private void setNodeClass(String nodeClass) {
        this.nodeClass = nodeClass;
    }

    private void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * this setter should be used only to initialize transient references
     */
    public void setNode__(DataObject node) {
    	Preconditions.checkState(this.node__ == null);
    	this.node__ = (DataObject)CrmReflectionUtil.unproxyCH(node);
    }       
    
    public void setPropertyChanges__(List<PropertyChange> propertyChanges__) {
		this.propertyChanges__ = propertyChanges__;
	}
    
    /**
     * this setter should be used only to initialize transient references
     */    
    public void setNewReference__(DataObject newReference__) {
    	Preconditions.checkState(this.newReference__ == null);
		this.newReference__ = (DataObject)CrmReflectionUtil.unproxyCH(newReference__);
	}
    
    /**
     * this setter should be used only to initialize transient references
     */
    public void setOldReference__(DataObject oldReference__) {
    	Preconditions.checkState(this.oldReference__ == null);
		this.oldReference__ = (DataObject)CrmReflectionUtil.unproxyCH(oldReference__);
	}
    
    private void setGetterName(String getterName) {
        this.getterName = getterName;
    }

    public void setOldReferenceClass(String oldReferenceClass) {
        this.oldReferenceClass = oldReferenceClass;
    }

    public void setOldReferenceId(int oldReferenceId) {
        this.oldReferenceId = oldReferenceId;
    }

    public void setNewReferenceClass(String newReferenceClass) {
        this.newReferenceClass = newReferenceClass;
    }

    public void setNewReferenceId(int newReferenceId) {
        this.newReferenceId = newReferenceId;
    }

    public void setOldStringValue(String oldStringValue) {
        this.oldStringValue = oldStringValue;
    }

    public void setNewStringValue(String newStringValue) {
        this.newStringValue = newStringValue;
    }

    public void setOldDateValue(Date oldDateValue) {
        this.oldDateValue = oldDateValue;
    }

    public void setNewDateValue(Date newDateValue) {
        this.newDateValue = newDateValue;
    }

    public void setOldDecimalValue(Double oldDecimalValue) {
        this.oldDecimalValue = oldDecimalValue;
    }

    public void setOldBigDecimalValue(BigDecimal oldDecimalValue) {
    	if (oldDecimalValue != null) {
    		this.oldDecimalValue = oldDecimalValue.doubleValue();	
    	}
    	else {
    		this.oldDecimalValue = null;
    	}       
    }
    
    public void setNewBigDecimalValue(BigDecimal newDecimalValue) {
    	if (newDecimalValue != null) {
    		this.newDecimalValue = newDecimalValue.doubleValue();
    	} else {
    		this.newDecimalValue = null;
    	}
    }
    
    public void setNewDecimalValue(Double newDecimalValue) {
    	this.newDecimalValue = newDecimalValue;
    }
    
    public void setOldBooleanValue(Boolean oldBooleanValue) {
        this.oldBooleanValue = oldBooleanValue;
    }

    public void setNewBooleanValue(Boolean newBooleanValue) {
        this.newBooleanValue = newBooleanValue;
    }

    public void setOldIntValue(Integer oldIntValue) {
        this.oldIntValue = oldIntValue;
    }

    public void setNewIntValue(Integer newIntValue) {
        this.newIntValue = newIntValue;
    }
    
    public void setOldPrimitiveListValue(List oldPrimitiveListValue) {
        this.oldPrimitiveListValue = oldPrimitiveListValue;
    }
    
    public void setNewPrimitiveListValue(List newPrimitiveListValue) {
        this.newPrimitiveListValue = newPrimitiveListValue;
    }
    
    public void setChangeImportant(boolean changeImportant) {
        this.changeImportant = changeImportant;
    }
    
    private BigDecimal fromDouble(Double value) {
    	if (value == null) {
    		return null;
    	}
        return new BigDecimal(value).setScale(4,RoundingMode.HALF_UP);
    }
}
