package pl.edu.icm.crmanager.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.Hibernate;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.model.SednoDate;
import pl.edu.icm.sedno.patterns.Visitor;

import com.google.common.collect.Lists;

/**
 * 
 * @author bart
 */
@Entity
@Table(name = "TE_WORK")
@Cacheable(false)
public class WorkTestEntity extends AbstractTestEntity {

    public enum WorkType {A, B}
    
    private int idWork;
    private int someInt;
    private Integer someInteger;
    private Set<ContributionTestEntity> contributions;
    private ContributionTestEntity mainContributor;
    private List<WorkTestEntity> strangeWorkList;   
    private List<WorkTestEntity> manyToManyWorkList = Lists.newArrayList();  
    private SednoDate sednoDate;
    private WorkTestEntity parentWork;
    private WorkType type;
    private Double someDouble;
    private Person testPerson;
    private BigDecimal someBigDecimal;
    private FullText fullText;
    
    public static int iC = 0;
    private int icc;     
    
    public WorkTestEntity(int idWork) {
        this.idWork = idWork;
    }
    
    public WorkTestEntity() {
        icc = iC;        
        //System.out.println("new ("+icc+")"+this.getClass().getSimpleName()+ " # "+ System.identityHashCode(this));
             
        iC++;
    }
    
    @Override
    public void initialize() {
	    Hibernate.initialize(  getManyToManyWorkList());
    }
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getIdWork() {
        return idWork;
    }
    
    public Integer getSomeInteger() {
		return someInteger;
	}
    
    @Column(scale=4, precision=6)
    public BigDecimal getSomeBigDecimal() {
		return someBigDecimal;
	}
    
    public Double getSomeDouble() {
		return someDouble;
	}
    
    @Type(type = "pl.edu.icm.crmanager.model.FullTextUserType")  
    @Column(columnDefinition="text")
	public FullText getFullText() {
		return fullText;
	}
    
    @Embedded
    public Person getTestPerson() {
        return testPerson;
    }
    
    @Embedded
    @AttributeOverrides( {
        @AttributeOverride(name="databaseValue", column = @Column(name="my_sedno_date") )})
    public SednoDate getSednoDate() {
        return sednoDate;
    }
    
    @Enumerated(EnumType.STRING)
    public WorkType getType() {
        return type;
    }
    
    @ManyToOne()
    public ContributionTestEntity getMainContributor() {
        return mainContributor;
    }  
    
    @ChangeImportant
    //@OneToMany(mappedBy="work", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    @OneToMany(mappedBy="work", fetch=FetchType.EAGER)
    @org.hibernate.annotations.Cascade(value={CascadeType.PERSIST, CascadeType.SAVE_UPDATE, CascadeType.REMOVE})
    @Where(clause="data_object_status = 'ACTIVE'")
    public Set<ContributionTestEntity> getContributions() {
        return contributions;
    }
    
    @OneToMany(mappedBy="parentWork", fetch=FetchType.EAGER) 
    @Where(clause="data_object_status = 'ACTIVE'")
    public List<WorkTestEntity> getStrangeWorkList() {
        return strangeWorkList;
    }
    
    
    @ManyToMany(targetEntity=WorkTestEntity.class)
    @JoinTable(
            name="te_work_many_to_many",
            joinColumns=@JoinColumn(name="fk_work_left"),
            inverseJoinColumns=@JoinColumn(name="fk_work_right")

    )  
    public List<WorkTestEntity> getManyToManyWorkList() {
		return manyToManyWorkList;
	}
    
    @ManyToOne
    public WorkTestEntity getParentWork() {
        return parentWork;
    }
    
    
    public void removeContribution(ContributionTestEntity contrib) {
    	contrib.setWork(null);
    	boolean removed = getContributions().remove(contrib);
    	if (!removed) {
    		throw new RuntimeException("nu such contrib "+contrib);
    	}
    }
    
    public void addContribution(ContributionTestEntity contrib) {
        if (getContributions() == null)
            setContributions( new HashSet<ContributionTestEntity>() );
        getContributions().add(contrib);
        contrib.setWork(this);
    }
            
    public void addStrangeWork(WorkTestEntity work) {
        if (strangeWorkList == null) {
            setStrangeWorkList(new ArrayList<WorkTestEntity>());
        }
        getStrangeWorkList().add(work);
        work.setParentWork(this);
    }
    
    public void addManyToManyWork(WorkTestEntity work) {
        if (manyToManyWorkList == null) {
            setManyToManyWorkList(new ArrayList<WorkTestEntity>());
        }
        getManyToManyWorkList().add(work);
        work.setParentWork(this);
    }
    
    public void removeStrangeWork(WorkTestEntity work) {
    	work.setParentWork(null);
    	
    	boolean removed = getStrangeWorkList().remove(work);
    	if (!removed) {
    		throw new RuntimeException("nu such StrangeWork "+work);
    	}
    }
    
    public void removeManyToManyWork(WorkTestEntity work) {
    	work.setParentWork(null);
    	
    	boolean removed = getManyToManyWorkList().remove(work);
    	if (!removed) {
    		throw new RuntimeException("nu such StrangeWork "+work);
    	}
    }
    
    public int getSomeInt() {
        return someInt;
    }

    public void setIdWork(int idWork) {
        this.idWork = idWork;
    }

    public void setSomeInt(int someInt) {
        this.someInt = someInt;
    }

    public void setContributions(Set<ContributionTestEntity> contributions) {
        this.contributions = contributions;
    }
  
    
    public void setMainContributor(ContributionTestEntity mainContributor) {
        this.mainContributor = mainContributor;
    }

    public void setStrangeWorkList(List<WorkTestEntity> strangeWorkList) {
        this.strangeWorkList = strangeWorkList;
    }    
    
    @Override
    public void accept(Visitor<DataObject> visitor) {
       
        visitor.visit(this);
        
        if (getMainContributor() != null) {
            visitor.visit(getMainContributor());
        }
        
        if (getContributions() != null)            
            for (ContributionTestEntity c : getContributions()) {
                 c.accept(visitor);
            }
        
        if (getStrangeWorkList() != null) {
            for (WorkTestEntity w : getStrangeWorkList()) {
                w.accept(visitor);
            }
        }
    }
    
    public void setSednoDate(SednoDate sednoDate) {
        this.sednoDate = sednoDate;
    }
    
    public void setParentWork(WorkTestEntity parentWork) {
        this.parentWork = parentWork;
    }
    
    public void setType(WorkType type) {
        this.type = type;
    }
    
    public void setTestPerson(Person testPerson) {
        this.testPerson = testPerson;
    }
    
    public void setFullText(FullText fullText) {
		this.fullText = fullText;
	}
    
    public void setManyToManyWorkList(List<WorkTestEntity> manyToManyWorkList) {
		this.manyToManyWorkList = manyToManyWorkList;
	}
    
    public void setSomeInteger(Integer someInteger) {
		this.someInteger = someInteger;
	}
    
    public void setSomeDouble(Double someDouble) {
		this.someDouble = someDouble;
	}
    public void setSomeBigDecimal(BigDecimal someBigDecimal) {
		this.someBigDecimal = someBigDecimal;
	}   
}
