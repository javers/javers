package pl.edu.icm.crmanager.model;

import javax.persistence.*;

/**
 * 
 * @author bart
 */
@Entity
@Table(name = "TE_contribution")
public class ContributionTestEntity extends AbstractTestEntity {

    private String contributorName;
    private int idContribution;
    private WorkTestEntity work;
    
    public static int iC = 0;
    private int icc;
      
    public ContributionTestEntity() {
        icc = iC;        
        //System.out.println("new ("+icc+")"+this.getClass().getSimpleName()+ " # "+ System.identityHashCode(this));
             
        iC++;
    }
    
    public ContributionTestEntity(int idContribution) {
        this.idContribution = idContribution;
    }
    
    public ContributionTestEntity(String contributorName) {
        this.contributorName = contributorName;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public int getIdContribution() {
        return idContribution;
    }
    
    public String getContributorName() {
        return contributorName;
    }
    
    
    @ManyToOne
    public WorkTestEntity getWork() {
        return work;
    }


    public void setIdContribution(int idContribution) {
        this.idContribution = idContribution;
    }

    public void setWork(WorkTestEntity work) {
        this.work = work;
    }
    
    public void setContributorName(String contributorName) {
        this.contributorName = contributorName;
    }
}
