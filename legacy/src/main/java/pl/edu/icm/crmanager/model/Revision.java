package pl.edu.icm.crmanager.model;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.model.ADataObject;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.PropertyChange;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
@Entity(name="crm_revision")
@Table(appliesTo="crm_revision", indexes=
    @Index(
            name="crm_revision_rootModPoint_idx",
            columnNames = {"root_mod_point_class", "root_mod_point_id"}
          ))         
@javax.persistence.SequenceGenerator(name = "seq_crm_revision", allocationSize = 10, sequenceName = "seq_crm_revision")
public class Revision extends ADataObject {
    
	private static final Logger logger = LoggerFactory.getLogger(Revision.class);
        
	public enum RevisionStatus {TRANSIENT, NEW, ACCEPTED, CANCELLED};
	
	private int 				idRevision;	
	private List<ChangeRequest> changeRequests;
	private RevisionStatus      revisionStatus;
	private String              authorId;
	private Date                cancelledDate;
	private String              cancelledUserId;
    private Date                acceptedDate;
    private String              acceptedUserId;	
    
    private String              rootModPointClass;
    private int                 rootModPointId;
    @Transient
    private DataObject    	    rootModPoint;
	
    @Deprecated
    private Set<DataObject>     nonCrmModPoints = Sets.newHashSet();
    
	@Transient
	private CrmSession session;
	
	public Revision() {}
	
	@Override
	public void initialize() {
	    Hibernate.initialize(getChangeRequests());
	}
		
	public Revision(CrmSession session, String authorId) {
	    this.revisionStatus = RevisionStatus.TRANSIENT;
	    this.session = session;
	    this.authorId = authorId;
	    changeRequests = new ArrayList<ChangeRequest>(10);
	}
	
	public void cleanSession() {
		this.session = null;
	}

	/**
	 * prepare for sending via Remoting
	 */	
	@Id
	@Column(name="id_revision")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_crm_revision")
	public int getIdRevision() {
		return idRevision;
	}
	
	@OneToMany(mappedBy="revision", cascade=CascadeType.ALL)
	@Basic(fetch=FetchType.LAZY)
	//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OrderBy("requestNo")
	public List<ChangeRequest> getChangeRequests() {
		return changeRequests;
	}

	@Column(length=150, name="root_mod_point_class")
	public String getRootModPointClass() {
        return rootModPointClass;
    }
	
	@Column(name="root_mod_point_id")
	public int getRootModPointId() {
        return rootModPointId;
    }
	
	@Transient
	public ChangeRequest getLastCR() {
	    return getChangeRequests().get(getChangeRequests().size()-1);
	}
		
	@Transient
	public boolean isOpen() {
		return getRevisionStatus() == RevisionStatus.NEW;
	}
	
	@Enumerated(EnumType.STRING)
	@Column(name="revision_status")
	public RevisionStatus getRevisionStatus() {
		return revisionStatus;
	}

	@Column(length=150, name="author_id")
	public String getAuthorId() {
        return authorId;
    }
	
	@Column(length=150, name="accepted_user_id")
	public String getAcceptedUserId() {
        return acceptedUserId;
    }
	
	@Column(length=150, name="cancelled_user_id")
	public String getCancelledUserId() {
        return cancelledUserId;
    }
	
	public Date getCancelledDate() {
        return cancelledDate;
    }
	
	public Date getAcceptedDate() {
        return acceptedDate;
    }
	
	public void moveToNew() {
        if (getRevisionStatus() != RevisionStatus.TRANSIENT) {
            throw new CrmRuntimeException("accept() - state error, revisionStatus != TRANSIENT)");
        }
        setRevisionStatus(RevisionStatus.NEW);	    
	}
	
	public void accept(String acceptedBy) {
        if (getRevisionStatus() != RevisionStatus.NEW && getRevisionStatus() != RevisionStatus.TRANSIENT) {
            throw new CrmRuntimeException("accept() - state error, revisionStatus not in (NEW, TRANSIENT)");
        }
	    setRevisionStatus(RevisionStatus.ACCEPTED);
	    setAcceptedDate(new Date());
	    setAcceptedUserId(acceptedBy);
	}
	
	public void cancel(String cancelledBy) {
        if (getRevisionStatus() != RevisionStatus.NEW) {
            throw new CrmRuntimeException("cancel() - state error, revisionStatus != NEW");
        }
        setRevisionStatus(RevisionStatus.CANCELLED);
        setCancelledDate(new Date());
        setCancelledUserId(cancelledBy); 
	}
	
	@Transient
	public List<ChangeRequest> getChangeRequests(RecType ofType) {
	    List<ChangeRequest> ret= new ArrayList<ChangeRequest>();
        for (ChangeRequest ch : changeRequests) {
            if (ch.getRecType().equals(ofType))
                ret.add(ch);
        }
        return ret;
	}
	
	@Transient
	public DataObject getRootModPoint() {
		return rootModPoint;
	}
	
    private void appendRecord(ChangeRequest rec) {           
        getChangeRequests().add(rec);
        
        rec.setRequestNo(getChangeRequests().size());
        rec.setRevision(this);
    }

    private void appendRecords(List<ChangeRequest> recs) {           
    	for (ChangeRequest rec : recs) {
    		appendRecord(rec);
    	} 
    }
    
    /**
     * pomocnicza kolekcja, służy do persystowania zmian na getterach z {@link CrmTransparent}
     */
    @Transient
    @Deprecated
    public Set<DataObject> getNonCrmModPoints() {
		return nonCrmModPoints;
	}
    
    @Deprecated
    public void addNonCrmModPoint(DataObject modPoint) {
    	getNonCrmModPoints().add(modPoint);
    }
    
    public void resolveTransientReferences() {
        int r = 0;
        for (ChangeRequest ch : changeRequests) {
            r += ch.tryToResolveTransientReference();
        } 
        
        if (getRootModPoint() != null && getRootModPointId() == 0) {
        	setRootModPointId(getRootModPoint().getId());
        }
        
        if (r > 0) {
            logger.debug("resolveTransientReferences() : "+r + " transient reference(s) resolved");
        }
    }   
    
    public void checkTransientReferences() {
        for (ChangeRequest ch : changeRequests) {
            if  (ch.hasTransientReference())
                throw new CrmRuntimeException("found transient reference in changeRequest, it can't be commited - "+ch.getShortDesc());
        }
    }
    
    public void registerDelete(DataObject deletedNode) {
    	checkCommited();    
    	
    	deletedNode.setDataObjectStatus(DataObjectStatus.DELETED);        
        ChangeRequest change = ChangeRequestFactory.createForDeletedObject(deletedNode);
		appendRecord(change);
    }
    
    public void registerNewObject(DataObject node) {
        checkCommited();
               
        //already added?
        for (ChangeRequest cr : getChangeRequests()) {
            if (cr.getRecType() == RecType.NEW_OBJECT &&
                node.equals(cr.getNode__())) {
                return;
            }
        }
        
        node.setDataObjectStatus(DataObjectStatus.NEW);
        
        ChangeRequest change = ChangeRequestFactory.createForNewObject(node);
		appendRecord(change);
    }
    
    public void registerChildAdd(DataObject node, String getterName, DataObject child) {
        checkCommited();
        
        ChangeRequest change = ChangeRequestFactory.createChildAdd(node, getterName, child);
		appendRecord(change);
    }
    
    public void registerChildRemove(DataObject node, String getterName, DataObject child) {
        checkCommited();
                
        ChangeRequest change = ChangeRequestFactory.createChildRemove(node, getterName, child);
		appendRecord(change);
    }    
        
    public void registerValueChange(DataObject node, String getterName, Object oldValue, Object newValue, Class valueClass)
    {   
        if(Objects.equal(oldValue, newValue))
        	return;
    	
    	checkCommited();
                
    	ValueType valueType = ValueType.determineValueType(valueClass);
    	
    	if (valueType == ValueType.COMPLEX_STRING_EMBEDDABLE) {
    		List<ChangeRequest> changes = ChangeRequestFactory.detectChangesOnComplexEmbeddable(node, getterName, oldValue, newValue, valueClass);
    		appendRecords(changes);    		
    	}
    	else {
    		ChangeRequest change = ChangeRequestFactory.createChange(node, getterName, oldValue, newValue, valueClass);
    		appendRecord(change);
    	}
    }
 
    @Transient
    public int getChangesCount() {
        return changeRequests.size();
    }
    
    @Transient
    public String getShortDesc() {
        StringBuffer buf = new StringBuffer();
        
        buf.append("\n revision["+getIdRevision()+"] " + revisionStatus+", rootModPoint: "+getRootModPointClass()+"#"+getRootModPointId());
        
        if (changeRequests.size() > 0 ) {
        	buf.append("\n");
	        buf.append(". changes: \n");
	       
	        for (ChangeRequest ch : changeRequests) {
	            buf.append(".. "+ ch.getShortDesc()+" \n");
	            
	            if (CollectionUtils.isNotEmpty(ch.getPropertyChanges__())) {
	            	for (PropertyChange pc : ch.getPropertyChanges__()) {
	            		buf.append(".. .."+ pc +" \n");
	            	}
	            }
	        }
        } else {
        	buf.append(" no changes \n");
        }
        return buf.toString();
    }
    
    /**
     * true jeśli w zbiorze cr'ów jest przynajmniej jeden z {@link ChangeRequest#isChangeImportant()} == true
     */
    @Transient
    public boolean isChangeImportatnt() {
        for (ChangeRequest cr : changeRequests) {
            if (cr.isChangeImportant())
                return true;
        }
            
        return false;
    }
    
    /**
     * combo setter
     */ 
    public void setRootModPointCombo(DataObject node) {
        setRootModPoint__(node);
        
        this.rootModPointClass = getRootModPoint().getClass().getName();
        this.rootModPointId =    getRootModPoint().getId();    
    }
        
    /**
     * this setter should be used only to initialize transient references
     */
    public void setRootModPoint__(DataObject node) {
    	Preconditions.checkState(this.rootModPoint == null);
    	
    	DataObject ref =  (DataObject)CrmReflectionUtil.unproxyCH(node);
        this.rootModPoint = ref;
    }
    
    @Deprecated
    private Object unproxyCH(Object obj) {
        if (obj == null) {
            return null;
        }
        
        if (!(obj instanceof DataObject)) {
            return obj;
        }
        
        return CrmReflectionUtil.unproxyCH((DataObject)obj);
    }
      
    private void checkCommited() {
        if (!session.isOpened()) {
            throw new CrmRuntimeException("you can't write to commited revision");
        }
    }

    private void setRevisionStatus(RevisionStatus revisionStatus) {
        this.revisionStatus = revisionStatus;
    }
    
    public void setIdRevision(int idRevision) {
        this.idRevision = idRevision;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
	
    public void setChangeRequests(List<ChangeRequest> changeRequests) {
        this.changeRequests = changeRequests;
    }

    private void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    private void setCancelledUserId(String cancelledUserId) {
        this.cancelledUserId = cancelledUserId;
    }

    private void setAcceptedDate(Date acceptedDate) {
        this.acceptedDate = acceptedDate;
    }

    private void setAcceptedUserId(String acceptedUserId) {
        this.acceptedUserId = acceptedUserId;
    }
    
    private void setRootModPointClass(String rootModPointClass) {
        this.rootModPointClass = rootModPointClass;
    }
    
    private void setRootModPointId(int rootModPointId) {
        this.rootModPointId = rootModPointId;
    }
}
