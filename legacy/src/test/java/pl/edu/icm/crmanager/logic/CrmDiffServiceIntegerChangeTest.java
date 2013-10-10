package pl.edu.icm.crmanager.logic;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.ChangeRequest;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.Revision.RevisionStatus;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceIntegerChangeTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceIntegerChangeTest.class);
    
   
    @Before
    public void before() {     
        super.before();
    }
    
    @Test
    public void testChangeFromNullTo1() {
        
        //change
        work.setSomeInteger(1);
        
        //act
        Revision revision = crmDiffService.generateRevision(work, "mock");
      
        assertThat(revision.getChangesCount()).isEqualTo(1);
        ChangeRequest cr = revision.getChangeRequests().get(0);
        
        assertThat(cr.getOldIntValue()).isNull();
        assertThat(cr.getNewIntValue()).isEqualTo(1);
        assertThat(revision.getRevisionStatus()).isEqualTo(RevisionStatus.NEW);
        
        dataObjectDAO.refresh(work);
        //zmiany jeszcze nie widać
        assertThat(work.getSomeInteger()).isNull();
        
        //accept
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        //zmiana jest już widoczna
        dataObjectDAO.refresh(work);
        assertThat(work.getSomeInteger()).isEqualTo(1);
        
    }
    
    @Test
    public void testChangeFromNullTo1WithAutoAccept() {
    	//change
        work.setSomeInteger(1);
        
        //act
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");
      
        //assert
        assertThat(revision.getChangesCount()).isEqualTo(1);
        
        //zmiana jest już widoczna
        dataObjectDAO.refresh(work);
        assertThat(work.getSomeInteger()).isEqualTo(1);
    }

    
}
