package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;
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
import pl.edu.icm.crmanager.model.Revision.RevisionStatus;
import pl.edu.icm.crmanager.model.WorkTestEntity;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceTest.class);
    
   
    @Before
    public void before() {     
        super.before();
    }
    
    @Test
    public void testSimpleDiffOnPrimitiveField() {
        
        //change
        work.setSomeInt(6);
        
        Revision revision = crmDiffService.generateRevision(work, "mock");
        assertThat(revision.isChangeImportatnt()).isFalse();
        
        assertThat(revision.getChangesCount()).isEqualTo(1);
        ChangeRequest cr = revision.getChangeRequests().get(0);
        
        assertThat(cr.getOldIntValue()).isEqualTo(5);
        assertThat(cr.getNewIntValue()).isEqualTo(6);
        assertThat(revision.getRevisionStatus()).isEqualTo(RevisionStatus.NEW);
        
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        assertThat(dbWork.getSomeInt()).isEqualTo(5);
        assertThat(dbWork.getCrmVersionNo()).isNull();
        
        //accept and version check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        assertThat(dbWork.getSomeInt()).isEqualTo(6);
        assertThat(dbWork.getCrmVersionNo()).isEqualTo(1);        
    }
    
    @Test()
    public void testNoUnsupportedMappingException() {
        //change
        work.setMainContributor(new ContributionTestEntity("e"));
        
        Revision revision = crmDiffService.generateRevision(work, "mock");        
        Assert.assertEquals(2, revision.getChangesCount());
    }
    
    
    @Test
    public void testReferenceChange() {
        logger.info("-- testReferenceChange() -- ");
        
        ContributionTestEntity con = new ContributionTestEntity("eee");
        dataObjectDAO.saveOrUpdate(con);
        
        
        //change
        work.setMainContributor(con);
        
        Revision revision = crmDiffService.generateRevision(work, "mock");        
        Assert.assertEquals(2, revision.getChangesCount()); //CHILD_ADD & NEW_OBJECT
        
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertNull(dbWork.getMainContributor());
       
        //accept and check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertFalse(dbWork.getMainContributor().isNew());
        Assert.assertEquals("eee", dbWork.getMainContributor().getContributorName());
    }
    
    
    @Test
    public void testCollectionDiff_changeAndAccept() {
        logger.info("-- testCollectionDiff_changeAndAccept() -- ");
        
        //change
        ContributionTestEntity con2 = getContribByName(work, "e2");
        boolean removed = work.getContributions().remove(con2);
        Assert.assertTrue(removed);
        ContributionTestEntity c3 = new ContributionTestEntity("e3");
        work.addContribution(c3);
        
        Revision revision = crmDiffService.generateRevision(work, "mock");  
        Assert.assertTrue(revision.isChangeImportatnt());
        
        Assert.assertNotNull(dataObjectDAO.get(ContributionTestEntity.class, c3.getId()));   
        Assert.assertEquals(3, revision.getChangesCount());
        
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(2, dbWork.getContributions().size());
        Assert.assertTrue ( containsContribName(dbWork, "e1"));
        Assert.assertTrue ( containsContribName(dbWork, "e2"));
        
        //accept and check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(2, dbWork.getContributions().size());
        Assert.assertTrue  ( containsContribName(dbWork, "e1"));
        Assert.assertTrue  ( containsContribName(dbWork, "e3"));
        Assert.assertFalse (    getContribByName(dbWork, "e3").isNew());

    }
    
    @Test
    public void testCollectionDiff_autoAccept() {
        logger.info("-- testCollectionDiff_autoAccept() -- ");
        
        //change
        ContributionTestEntity con2 = getContribByName(work, "e2");
        boolean removed = work.getContributions().remove(con2);
        Assert.assertTrue(removed);
        ContributionTestEntity c3 = new ContributionTestEntity("e3");
        work.addContribution(c3);
        
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");  
        Assert.assertTrue(revision.isChangeImportatnt());
        
        Assert.assertEquals(3, revision.getChangesCount());
        
        // check
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(2, dbWork.getContributions().size());
        Assert.assertTrue  ( containsContribName(dbWork, "e1"));
        Assert.assertTrue  ( containsContribName(dbWork, "e3"));
        Assert.assertFalse (    getContribByName(dbWork, "e3").isNew());

    }

    
}
