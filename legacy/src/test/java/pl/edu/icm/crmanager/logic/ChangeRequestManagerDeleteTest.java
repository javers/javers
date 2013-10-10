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
import pl.edu.icm.crmanager.model.RecType;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.sedno.common.model.DataObject.DataObjectStatus;

/**
 * 
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class ChangeRequestManagerDeleteTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(ChangeRequestManagerDeleteTest.class);

        
    @Before
    public void before() {     
        super.before();       
    }

	@Test
	public void testDelete() {
		
		//act
		Revision revision = changeRequestManager.deleteTree(work, "mock");
		
		logger.info(revision.getShortDesc());
		
		//assert
		Assertions.assertThat(revision.getChangeRequests()).hasSize(3);
		for (ChangeRequest cr : revision.getChangeRequests()) {
			Assertions.assertThat(cr.getRecType()).isEqualTo(RecType.DELETED);
		}
		
		for (ContributionTestEntity c : work.getContributions()) {
			dataObjectDAO.refresh(c);
			Assertions.assertThat(c.getDataObjectStatus()).isEqualTo(DataObjectStatus.DELETED);
		}
		
		dataObjectDAO.refresh(work);
		Assertions.assertThat(work.getDataObjectStatus()).isEqualTo(DataObjectStatus.DELETED);
		
	}
}
