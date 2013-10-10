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
import pl.edu.icm.sedno.common.model.DataObject.DataObjectStatus;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceTransientChainTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceTransientChainTest.class);
    
    @Before
    public void before() {     
        super.before();
    }
    
    @Test
    public void testTransientChain() {
       logger.info("work: "+ work);
       logger.info("work: "+ dataObjectDAO.getObjectShortDesc(work));
       
       //add long transient chain
       WorkTestEntity strangeWork = new WorkTestEntity();
       work.addStrangeWork(strangeWork);
       strangeWork.setMainContributor(new ContributionTestEntity());
       
       Revision revision = crmDiffService.generateRevision(work, "mock");
      // logger.info("rev: "+ revision.getShortDesc());
       Assert.assertEquals(3, revision.getChangesCount());
       
       WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
       Assert.assertEquals (0, dbWork.getStrangeWorkList().size()); //nowe obiekty nie powinny być jeszcze widoczne


       //accept and version check
       changeRequestManager.acceptRevision(revision.getId(), "moccck");
       dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
       //nowe obiekty powinny być już widoczne
       Assert.assertEquals   (DataObjectStatus.ACTIVE, dbWork.getStrangeWorkList().get(0).getDataObjectStatus() );
       Assert.assertEquals   (DataObjectStatus.ACTIVE, dbWork.getStrangeWorkList().get(0).getMainContributor().getDataObjectStatus() );          
    }
}
