package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.model.SednoDate;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceEmbeddedObjectTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceEmbeddedObjectTest.class);
    
    @Before
    public void before() {     
        super.before();
    }
    
    @Test
    public void testEmbededSednoDate() {
        work.setSednoDate(new SednoDate(2001,1,1));
        
        Revision revision = crmDiffService.generateRevision(work, "mock");
        Assert.assertEquals(1, revision.getChangesCount());
        
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertNull(dbWork.getSednoDate());
        
        
        //accept and version check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals("2001-01-01", dbWork.getSednoDate().toString());
        
    }
}
