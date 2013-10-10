package pl.edu.icm.crmanager.logic;

import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.icm.crmanager.model.Person;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmDiffServiceComplexEmbeddedTest extends CrmDiffServiceTestBase {
    Logger logger = LoggerFactory.getLogger(CrmDiffServiceComplexEmbeddedTest.class);

    @Before
    public void before() {     
        super.before();
    }
    
    
    @Test
    public void testNewComplexEmbedded() {
        work.setTestPerson(new Person("Ala","Ma"));
        
        Revision revision = crmDiffService.generateRevisionAndAccept(work, "mock");
        logger.info("revision :" + revision.getShortDesc());
        
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(2);
    }
    
    @Test
    public void testNewComplexEmbeddedWithAccept() {
    	work.setTestPerson(new Person("Ala","Ma"));
    	
    	Revision revision = crmDiffService.generateRevision(work, "mock");        
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(2);
        
        //accept and version check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
        
        //assert
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assertions.assertThat(dbWork.getTestPerson()).isEqualTo(new Person("Ala","Ma"));
    }
        
    @Test
    public void testChangeComplexEmbeddedWithAccept() {
    	//prepare
    	work.setTestPerson(new Person("Ala","Ma"));
    	dataObjectDAO.saveOrUpdate(work);
    	    
    	//act
    	work.getTestPerson().setLastName("ma koks");
    	Revision revision = crmDiffService.generateRevision(work, "mock");
        Assertions.assertThat(revision.getChangesCount()).isEqualTo(1);
        
        //accept and version check
        changeRequestManager.acceptRevision(revision.getId(), "moccck");
                
        //assert
        WorkTestEntity dbWork = dataObjectDAO.get(WorkTestEntity.class, work.getId());
        Assertions.assertThat(dbWork.getTestPerson()).isEqualTo(new Person("Ala","ma koks"));
    }
}
