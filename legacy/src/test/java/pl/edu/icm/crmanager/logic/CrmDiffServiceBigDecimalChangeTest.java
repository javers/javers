package pl.edu.icm.crmanager.logic;

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

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceBigDecimalChangeTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceBigDecimalChangeTest.class);
    
    private static final BigDecimal dec = new BigDecimal(1.11).setScale(4, RoundingMode.HALF_UP);
   
    @Before
    public void before() {     
        super.before();
    }
    
    @Test
    public void testChangeFromNullTo1() {            
    	
        //change
        work.setSomeBigDecimal(dec);
        
        //act
        Revision revision = crmDiffService.generateRevision(work, "mock");
      
        assertThat(revision.getChangesCount()).isEqualTo(1);
        ChangeRequest cr = revision.getChangeRequests().get(0);
        
        assertThat(cr.getOldBigDecimalValue()).isNull();
        assertThat(cr.getNewBigDecimalValue()).isEqualTo(dec);
        assertThat(revision.getRevisionStatus()).isEqualTo(RevisionStatus.NEW);
        
        dataObjectDAO.refresh(work);
        //zmiany jeszcze nie widać
        assertThat(work.getSomeBigDecimal()).isNull();
        
        //accept
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        //zmiana jest już widoczna
        dataObjectDAO.refresh(work);
        assertThat(work.getSomeBigDecimal().setScale(4, RoundingMode.HALF_UP)).isEqualTo(dec);
        
    }
    
    @Test
    public void testChangeFromNullTo1WithAutoAccept() {
    	//change
        work.setSomeBigDecimal(dec);
        
        //act
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");
      
        //assert
        assertThat(revision.getChangesCount()).isEqualTo(1);
        
        //zmiana jest już widoczna
        dataObjectDAO.refresh(work);
        assertThat(work.getSomeBigDecimal().setScale(4, RoundingMode.HALF_UP)).isEqualTo(dec);
    }

    
}
