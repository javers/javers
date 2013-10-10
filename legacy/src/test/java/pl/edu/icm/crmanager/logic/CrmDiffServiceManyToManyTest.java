package pl.edu.icm.crmanager.logic;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceManyToManyTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceManyToManyTest.class);
    
    private WorkTestEntity w1 = new WorkTestEntity();
    private WorkTestEntity w2 = new WorkTestEntity();
    
    @Before
    public void before() {     
        super.before();
               
        dataObjectDAO.saveOrUpdate(w1, w2);        
        work.getManyToManyWorkList().add(w1);
    	dataObjectDAO.saveOrUpdate(work);                
    }
    
    @Test
    public void testAddItemAndAccept() {          	
    	
    	//act
    	work.getManyToManyWorkList().add(w2);        
        Revision revision = crmDiffService.generateRevision(work, "mock");   
        
        //assert
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1);
                
        //act 2 - accept
        changeRequestManager.acceptRevision(revision.getId(), "acceptedBy");
        
        //assert 2
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1, w2);        
    }
        
    @Test
    public void testRemoveItemAndAccept() {
    	//change
    	work.getManyToManyWorkList().clear();
    	
    	//act
        Revision revision = crmDiffService.generateRevision(work, "mock");   
        
        //assert
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1);
        
        
        //act 2 - accept
        changeRequestManager.acceptRevision(revision.getId(), "acceptedBy");
        
        //assert 2
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).isEmpty();      
    }
    
    @Test
    public void testRemoveItemWithAutoAccept() {
    	//change
    	work.getManyToManyWorkList().clear();
    	
    	//act
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");   
        
        //assert
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).isEmpty();            
    }
    
    @Test
    public void testAddItemWithAutoAccept() {          
    	//act
    	work.getManyToManyWorkList().add(w2);        
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");   
        
        //assert
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1, w2);    	
    }
}
