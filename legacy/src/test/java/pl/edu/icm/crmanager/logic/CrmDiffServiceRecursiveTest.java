package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceRecursiveTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceRecursiveTest.class);    

    @Before
    public void before() {     
        super.before();
    }  
    
    @Test
    public void testRecursiveDiff() {
        logger.info("-- testRecursiveDiff() -- ");
        
        //change
        ContributionTestEntity con2 = getContribByName(work, "e2");
        con2.setContributorName("e2.1");
        
        Revision revision = crmDiffService.generateRevision(work, "mock");  
        Assert.assertEquals(1, revision.getChangesCount());
        logger.info(revision.getShortDesc());

        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(2, dbWork.getContributions().size());
        Assert.assertTrue (    containsContribName(dbWork, "e1"));
        Assert.assertTrue (    containsContribName(dbWork, "e2"));
        
        //accept and check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(2, dbWork.getContributions().size());
        Assert.assertTrue (    containsContribName(dbWork, "e1"));
        Assert.assertTrue (    containsContribName(dbWork, "e2.1"));
        
    }
}
