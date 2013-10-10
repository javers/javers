package pl.edu.icm.crmanager.logic;

import org.fest.assertions.Assertions;
import org.fest.assertions.Delta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.icm.crmanager.diff.CrmShadowService;
import pl.edu.icm.crmanager.model.*;
import pl.edu.icm.crmanager.model.WorkTestEntity.WorkType;
import pl.edu.icm.sedno.common.model.SednoDate;

import java.util.Set;

import static org.fest.assertions.Assertions.assertThat;

/**
 * 
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmShadowServiceTest extends CrmDiffServiceTestBase { 
	public static final int NEW_INT_VALUE = 34;
	public static final int OLD_INT_VALUE = 17;
	
	public static final double OLD_DOUBLE_VALUE = 1.;
	public static final double NEW_DOUBLE_VALUE = 2.;
	
	public static final SednoDate OLD_SEDNO_DATE = new SednoDate(2001,1,1);
	public static final SednoDate NEW_SEDNO_DATE = new SednoDate(2002,2,2);
	
	public static final Person    OLD_PERSON = new Person("old","person");
	public static final Person    NEW_PERSON = new Person("new","personn");
	
	public static final FullText  OLD_FULL_TEXT = new FullText("old text");
	public static final FullText  NEW_FULL_TEXT = new FullText("new text");
	
	public static final WorkType  OLD_WORK_TYPE = WorkType.A;
	public static final WorkType  NEW_WORK_TYPE = WorkType.B;
	
	Logger logger = LoggerFactory.getLogger(CrmShadowServiceTest.class);
	
	@Autowired CrmShadowService crmShadowService;
	
	
	private Revision targetRevision;
	private WorkTestEntity w1 = new WorkTestEntity();
	private WorkTestEntity w2 = new WorkTestEntity();
	private ContributionTestEntity contrib1 = new ContributionTestEntity("contrib 1");
	private ContributionTestEntity contrib2 = new ContributionTestEntity("contrib 2");
	
	@Before
	public void before() {
		 deleteTestWorks(template);        
         
         work  =  new WorkTestEntity();
         dataObjectDAO.saveOrUpdate(work,contrib1,w1,w2);
         
         dataObjectDAO.evict(work);
         
         //old (target) state
	     work.setSomeInt(OLD_INT_VALUE);
	     work.setSomeDouble(OLD_DOUBLE_VALUE);
	     work.setSednoDate(OLD_SEDNO_DATE);
	     work.setTestPerson(OLD_PERSON);
	     work.setFullText(OLD_FULL_TEXT);   
	     work.setType(OLD_WORK_TYPE);
	     work.addContribution(contrib1);
	     work.addManyToManyWork(w1);
	    	     	    
	     targetRevision = crmDiffService.generateRevisionAndAccept(work, "mock");    
	     Assertions.assertThat(targetRevision.getChangesCount()).isEqualTo(10);
	}
	
	@Test
	public void testIntChangeRevert() {
		//prepare
	    work.setSomeInt(NEW_INT_VALUE);
	    crmDiffService.generateRevisionAndAccept(work, "mock");
	     
	     
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getSomeInt(): "+ shadow.getSomeInt());
	    Assertions.assertThat(shadow.getSomeInt()).isEqualTo(OLD_INT_VALUE);
	}
	
	@Test
	public void testDoubleChangeRevert() {
		//prepare
	    work.setSomeDouble(NEW_DOUBLE_VALUE);
	    crmDiffService.generateRevisionAndAccept(work, "mock");
	     	     
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getSomeDouble(): "+ shadow.getSomeDouble());		
		assertThat(shadow.getSomeDouble()).isEqualTo(OLD_DOUBLE_VALUE, Delta.delta(0.1));
	}

	@Test
	public void testEnumChangeRevert() {
		//prepare
	    work.setType(NEW_WORK_TYPE);
	    crmDiffService.generateRevisionAndAccept(work, "mock");
	     	     
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getType(): "+ shadow.getType());		
		assertThat(shadow.getType()).isEqualTo(OLD_WORK_TYPE);
	}
	
	@Test
        public void testChildModifyRevert() {
            Set<ContributionTestEntity> cs = work.getContributions();
            for (ContributionTestEntity c : cs) {
                c.setContributorName("new contributor");
            }
            crmDiffService.generateRevisionAndAccept(work, "mock");

            WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);

            assertThat(shadow.getContributions()).onProperty("contributorName").excludes("new contributor");
        }

	@Test
	public void testChildAddRevert() {
		logger.info("testChildAddRevert()");
		//prepare
		work.addContribution(contrib2);
	    crmDiffService.generateRevisionAndAccept(work, "mock");
	     
	     
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getContributions(): "+ shadow.getContributions());		
		assertThat(shadow.getContributions()).hasSize(1);
	}
	
	@Test
	public void testChildRemoveRevert() {
		logger.info("testChildRemoveRevert()");
		//prepare
		work.removeContribution(contrib1);
		crmDiffService.generateRevisionAndAccept(work, "mock");
		
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getContributions(): "+ shadow.getContributions());		
		assertThat(shadow.getContributions()).hasSize(1);
		assertThat(shadow.getContributions()).onProperty("work").containsOnly(work);
	}
	
	@Test
	public void testEmbeddedObjectChangeRevert() {
		logger.info("testEmbeddedObjectChangeRevert()");
		//prepare
		work.setSednoDate(NEW_SEDNO_DATE);
		crmDiffService.generateRevisionAndAccept(work, "mock");
		
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getSednoDate(): "+ shadow.getSednoDate());		
		assertThat(shadow.getSednoDate()).isEqualTo(OLD_SEDNO_DATE);
	}
	
	@Test
	public void testComplexEmbeddedChangeRevert() {
		logger.info("testComplexEmbeddedChangeRevert()");
		//prepare
		work.setTestPerson(NEW_PERSON);
		crmDiffService.generateRevisionAndAccept(work, "mock");
		
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getTestPerson(): "+ shadow.getTestPerson());		
		assertThat(shadow.getTestPerson()).isEqualTo(OLD_PERSON);
	}

	
	@Test
	public void testStringPersistedTypeChangeRevert() {
		logger.info("testStringPersistedTypeChangeRevert()");
		//prepare
		work.setFullText(NEW_FULL_TEXT);
		crmDiffService.generateRevisionAndAccept(work, "mock");
		
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		logger.info("shadow.getFullText(): "+ shadow.getFullText());		
		assertThat(shadow.getFullText()).isEqualTo(OLD_FULL_TEXT);
	}
		
	@Test
	public void testManyToManyChildAddRevert() {
		logger.info("testManyToManyChildAddRevert()");
		//prepare
		work.addManyToManyWork(w2);
	    crmDiffService.generateRevisionAndAccept(work, "mock");
	     	     
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
				
		assertThat(shadow.getManyToManyWorkList()).hasSize(1);
	}
		
	@Test
	public void testManyToManyChildRemoveRevert() {
		logger.info("testManyToManyChildRemoveRevert()");
		//prepare
		work.removeManyToManyWork(w1);
	    crmDiffService.generateRevisionAndAccept(work, "mock");
	     	     
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
				
		assertThat(shadow.getManyToManyWorkList()).hasSize(1);
	}
}
