package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.WorkTestEntity;

/**
 * 
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class RevisionServiceAutoAcceptTest extends RevisionServiceTestParent {
    Logger logger = LoggerFactory.getLogger(RevisionServiceAutoAcceptTest.class);      
    
    @Autowired
    private RevisionServiceTestBeanI revisionServiceTestBean;
    
    @Autowired
    private ChangeRequestManager changeRequestManager;
    
    //@Test
    public void testFlushAndAutoAccept_simpleChangeOnPersistentObject() {
        WorkTestEntity work = createPersistentWork();
        int revisionId = revisionServiceTestBean.testFlush_simpleChangeOnPersistentObject(work, true);
        
        //test if changes are in model - autoAccept test
        WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());             
        Assert.assertEquals(6, freshWork.getSomeInt());
       
    }
    
    //  complexChange - simpleChange + child add
    //@Test
    public void testFlushAndAutoAccept_complexChangeOnTransientObject() {
        WorkTestEntity work = createTransientWork();
        int revisionId = revisionServiceTestBean.testFlush_complexChangeOnTransientObject(work,true);
        
        //test if changes are in model - autoAccept test
        WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());             
        Assert.assertEquals(6, freshWork.getSomeInt());
        Assert.assertEquals(false, freshWork.isNew());
        ContributionTestEntity freshContrib = freshWork.getContributions().iterator().next();
        Assert.assertEquals(false, freshContrib.isNew());    
    }
    
    @Test
    public void testFlushAndAutoAccept_addTransientChildToPersistetObject() {
        WorkTestEntity work = createTransientWork();
        dataObjectDAO.saveOrUpdate(work);
        
        int revisionId = revisionServiceTestBean.testFlush_addTransientChildToPersistetObject(work, true);
        
        //test czy child'a jest w kolekcji ze statusem !NEW
        WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());  
        Assert.assertEquals(1,freshWork.getContributions().size());
        Assert.assertFalse(freshWork.getContributions().iterator().next().isNew());
        
    }
    

}
