package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.icm.crmanager.model.FullText;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceStringPersistedUserTypeTest extends CrmDiffServiceTestBase {
	Logger logger = LoggerFactory.getLogger(CrmDiffServiceEmbeddedObjectTest.class);

	@Before
	public void before() {
		super.before();
	}

	@Test
	public void testAutoAccept() {
		//perform and accept first change (new object)
		work.setFullText(new FullText("blah blah"));        
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");
       
        Assert.assertEquals(1, revision.getChangesCount());
        Assert.assertEquals("blah blah", revision.getChangeRequests().get(0).getNewStringValue());
        dataObjectDAO.refreshAndInitialize(work);
        Assert.assertEquals("blah blah", work.getFullText().getText());     
                  
        // --------------------------------------------------------------------
         		
        //perform and accept second change
        work.getFullText().setText("bum bum");       
        revision = crmDiffService.generateRevisionAndAccept(work, "mock");
        
        Assert.assertEquals(1, revision.getChangesCount());        
        Assert.assertEquals("bum bum",   revision.getChangeRequests().get(0).getNewStringValue());
        Assert.assertEquals("blah blah", revision.getChangeRequests().get(0).getOldStringValue());
        dataObjectDAO.refreshAndInitialize(work);
        Assert.assertEquals("bum bum", work.getFullText().getText());              
	}
	
	@Test
	public void testChangeAndAccept() {
		//perform first change (new object)
		work.setFullText(new FullText("blah blah"));        
        Revision revision = crmDiffService.generateRevision(work, "mock");
        
        Assert.assertEquals(1, revision.getChangesCount());        
        dataObjectDAO.refreshAndInitialize(work);
        Assert.assertNull(work.getFullText());        
        
        //accept first change
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        dataObjectDAO.refreshAndInitialize(work);
        Assert.assertEquals("blah blah", work.getFullText().getText());
        
        // --------------------------------------------------------------------
        
		//perform second change
        work.getFullText().setText("bum bum");       
        revision = crmDiffService.generateRevision(work, "mock");
        
        Assert.assertEquals(1, revision.getChangesCount());        
        Assert.assertEquals("bum bum",   revision.getChangeRequests().get(0).getNewStringValue());
        Assert.assertEquals("blah blah", revision.getChangeRequests().get(0).getOldStringValue());
        work = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals("blah blah", work.getFullText().getText());      
        
        //accept second change
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        dataObjectDAO.refreshAndInitialize(work);
        Assert.assertEquals("bum bum", work.getFullText().getText());      
	}
}
