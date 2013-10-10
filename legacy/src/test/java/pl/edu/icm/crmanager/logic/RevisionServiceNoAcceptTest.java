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
import pl.edu.icm.sedno.common.dao.DataObjectDAO;

/**
 * 
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class RevisionServiceNoAcceptTest extends RevisionServiceTestParent {
    Logger logger = LoggerFactory.getLogger(RevisionServiceNoAcceptTest.class);      
    
    @Autowired
    private RevisionServiceTestBeanI revisionServiceTestBean;
    
    @Autowired
    private DataObjectDAO dataObjectDAO;
    
    @Autowired
    private ChangeRequestManager changeRequestManager;
    
    @Test   
    public void testFlushAndNoAccept_simpleChangeOnPersistentObject() {
        WorkTestEntity work = createPersistentWork();
        int revisionId = revisionServiceTestBean.testFlush_simpleChangeOnPersistentObject(work, false);
        
        //test if changes are not in model - evict test
        WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());             
        Assert.assertEquals(5, freshWork.getSomeInt());
        Assert.assertEquals(false, freshWork.isNew());
        
        changeRequestManager.acceptRevision(revisionId,"mock2");
        
        //test if changed are applied - accept() test
        WorkTestEntity freshWork2 = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(6, freshWork2.getSomeInt());
    }
    
    //  complexChange - simpleChange + child add
    @Test
    public void testFlushAndNoAccept_complexChangeOnTransientObject() {
        WorkTestEntity work = createTransientWork();

        int revisionId = revisionServiceTestBean.testFlush_complexChangeOnTransientObject(work, false);
        
        //test if changes are in model - but with NEW status
        WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());             
        Assert.assertEquals(6, freshWork.getSomeInt());
        Assert.assertEquals(true, freshWork.isNew());
        //FIXME contrib już jest dodany, co robić jeśli w CHILD-ADD mod point to de-facto child?
        ContributionTestEntity freshContrib = freshWork.getContributions().iterator().next();
        Assert.assertEquals(false, freshContrib.isNew());
        
        changeRequestManager.acceptRevision(revisionId,"mock2");
        
        //test if changed are applied - accept() test
        WorkTestEntity freshWork2 = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(6, freshWork2.getSomeInt());
        Assert.assertEquals(false, freshWork2.isNew());

    }
    
    @Test
    public void testFlushAndNoAccept_addTransientChildToPersistetObject() {
        WorkTestEntity work = createTransientWork();
        dataObjectDAO.saveOrUpdate(work);
        
        int revisionId = revisionServiceTestBean.testFlush_addTransientChildToPersistetObject(work, false);
        
        //test czy child'a nie ma w kolekcji
        WorkTestEntity freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());  
        Assert.assertEquals(0,freshWork.getContributions().size());        
        
        //po accept, child ma być już w kolekcji
        changeRequestManager.acceptRevision(revisionId,"mock2");
        
        //test if changed are applied - accept() test
        freshWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assert.assertEquals(1,freshWork.getContributions().size());
        Assert.assertFalse(freshWork.getContributions().iterator().next().isNew());
    }

}
