package pl.edu.icm.crmanager.model;

import pl.edu.icm.sedno.common.model.ADataObject;

import javax.persistence.*;
import java.util.Date;

/**
 * ROOT ENTITY of an AGGREGATE
 * 
 * @author bart
 * @see RootModPoint
 */
@MappedSuperclass
public class RootDataObject extends ADataObject implements RootModPoint {

    private Revision lastRevision;
    private Integer  crmVersionNo;
    private boolean  frozen;
    
    /**
     * Obiekt jest zamrożony i nie może być edytowany jeśli ma otwarte Revision 
     */
    @Column(name="is_frozen", columnDefinition="boolean default false")
    @CrmTransparent
    @Override
    public boolean isFrozen() {
		return frozen;
	}
    
    /**
     * Last ACCEPTED revision
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @Override
    @CrmTransparent
    public Revision getLastRevision() {
        return lastRevision;
    }

    @Override
    @CrmTransparent
    public Integer getCrmVersionNo() {
        return crmVersionNo;
    }

    @Transient
    @Override
    public String getLastChangeAuthor() {
        return getLastRevision().getAuthorId();
    }

    /**
     * last revision date
     */
    @Transient
    @Override
    public Date getRevisionDate() {
        return getLastRevision().getCreateDate();
    }

    @Override
    public void incrementCrmVersionNo() {
        if (crmVersionNo == null)
            crmVersionNo = new Integer(0);
        
        crmVersionNo = new Integer(crmVersionNo.intValue()+1);
    }
    
    @Override
    public void setLastRevision(Revision lastRevision) {
        this.lastRevision = lastRevision;
    }

    @SuppressWarnings("unused")
    private void setCrmVersionNo(Integer crmVersionNo) {
        this.crmVersionNo = crmVersionNo;
    }

    
    public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
}
