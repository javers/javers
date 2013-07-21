package pl.edu.icm.crmanager.logic;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.ChangeAction;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.model.DataObject.DataObjectStatus;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class ChangeRequestManagerManyToManyTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(ChangeRequestManagerManyToManyTest.class);
    
    private WorkTestEntity w1 = null;
    private WorkTestEntity w2 = null;
    
    @Before
    public void before() {     
        super.before();
        
        w1 = new WorkTestEntity();
        w2 = new WorkTestEntity();        
        dataObjectDAO.saveOrUpdate(w1, w2);        
        work.getManyToManyWorkList().add(w1);
    	dataObjectDAO.saveOrUpdate(work);                
    }
    
    @Test
    public void testAddItem() {          	
    	
    	ChangeAction<WorkTestEntity> action = addW2Action();
    	
        //act
    	Revision revision = changeRequestManager.doInCrmWithNoAccept(work, action, "login");
        logger.info(revision.getShortDesc());        
    	
        //assert    	
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1);
     
        //accept
        changeRequestManager.acceptRevision(revision.getId(), "acceptedBy");
        dataObjectDAO.refreshAndInitialize(work);
        
        //assert 2
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1,w2);
    }
        
    @Test
    public void testRemoveItem() {
    	ChangeAction<WorkTestEntity> action = clearAction();
    	
    	//act
    	Revision revision = changeRequestManager.doInCrmWithNoAccept(work, action, "login");
    	logger.info(revision.getShortDesc());   
    	
        //assert  
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1);     
        
        //accept
        changeRequestManager.acceptRevision(revision.getId(), "acceptedBy");
        dataObjectDAO.refreshAndInitialize(work);
        
        //assert 2                     
        for (WorkTestEntity w : dataObjectDAO.getAll(WorkTestEntity.class)) {
        	Assertions.assertThat(w.getDataObjectStatus()).isEqualTo(DataObjectStatus.ACTIVE);
        }
        Assertions.assertThat(work.getManyToManyWorkList()).isEmpty();
    }
       
    @Test
    public void testAddItemWithAutoAccept() {          	
    	
    	ChangeAction<WorkTestEntity> action = addW2Action();
    	
        //act
    	Revision revision = changeRequestManager.doInCrmWithAutoAccept(work, action, "login");
        logger.info(revision.getShortDesc());        
    	
        //assert    	
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        dataObjectDAO.refreshAndInitialize(work);
        Assertions.assertThat(work.getManyToManyWorkList()).containsOnly(w1,w2);
    }
    
    @Test
    public void testRemoveItemWithAutoAccept() {
    	ChangeAction<WorkTestEntity> action = clearAction();
	
    	//act
    	Revision revision = changeRequestManager.doInCrmWithAutoAccept(work, action, "login");
        
        //assert
        dataObjectDAO.refreshAndInitialize(work);
        
        for (WorkTestEntity w : dataObjectDAO.getAll(WorkTestEntity.class)) {
        	Assertions.assertThat(w.getDataObjectStatus()).isEqualTo(DataObjectStatus.ACTIVE);
        }
        Assertions.assertThat(work.getManyToManyWorkList()).isEmpty();     
    }
    
	private ChangeAction<WorkTestEntity> clearAction() {
		ChangeAction<WorkTestEntity> action = new ChangeAction<WorkTestEntity>() {
			public void execute(WorkTestEntity domainObjectProxy) {
				domainObjectProxy.getManyToManyWorkList().clear();
			}
		};
		return action;
	}
	
	private ChangeAction<WorkTestEntity> addW2Action() {
		ChangeAction<WorkTestEntity> action = new ChangeAction<WorkTestEntity>() {
			public void execute(WorkTestEntity domainObjectProxy) {
				domainObjectProxy.getManyToManyWorkList().add(w2);
			}
		};
		return action;
	}
}
