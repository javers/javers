package pl.edu.icm.crmanager.logic;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.fest.HibernateAssert;

/**
 * 
 * @author bart
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class ChangeRequestManagerTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(ChangeRequestManagerTest.class);

    private Revision revision;
    private ContributionTestEntity oldMainContributor;
    private ContributionTestEntity newMainContributor;
    
    
    @Before
    public void before() {     
        super.before();
        
        oldMainContributor = new ContributionTestEntity();
        newMainContributor = new ContributionTestEntity();        
        dataObjectDAO.saveOrUpdate(oldMainContributor, newMainContributor);
        work.setMainContributor(oldMainContributor);
        dataObjectDAO.saveOrUpdate(work);
        
        
        //do change
        work.setMainContributor(newMainContributor);  
        revision = crmDiffService.generateRevisionAndAccept(work, "mock");
        logger.info("revision :" + revision.getShortDesc());
    }

	@Test
	public void testLoadRevisionWithReferences() {
		
		//act
		revision = changeRequestManager.loadRevisionWithReferences(revision.getId());
		
		Assertions.assertThat(revision.getChangeRequests()).hasSize(2);	
		
		HibernateAssert.assertThat(revision.getRootModPoint()).isInitialized();		
		
		ChangeRequest first =  revision.getChangeRequests().get(0);
		ChangeRequest second = revision.getChangeRequests().get(1);
		
		HibernateAssert.assertThat(first.getNode__() ).isInitialized().isEqualTo(newMainContributor);
		     Assertions.assertThat(first.getOldReference__()).isNull();
		     Assertions.assertThat(first.getNewReference__()).isNull();
		
		HibernateAssert.assertThat(second.getNode__()).isInitialized().isEqualTo(work);
		HibernateAssert.assertThat(second.getOldReference__()).isInitialized().isEqualTo(oldMainContributor);
		HibernateAssert.assertThat(second.getNewReference__()).isInitialized().isEqualTo(newMainContributor);
	}
}
