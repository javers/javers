package pl.edu.icm.crmanager.logic;

import static org.fest.assertions.Assertions.assertThat;
import static pl.edu.icm.crmanager.logic.CrmShadowServiceTest.NEW_DOUBLE_VALUE;
import static pl.edu.icm.crmanager.logic.CrmShadowServiceTest.NEW_SEDNO_DATE;
import static pl.edu.icm.crmanager.logic.CrmShadowServiceTest.OLD_DOUBLE_VALUE;
import static pl.edu.icm.crmanager.logic.CrmShadowServiceTest.OLD_PERSON;
import static pl.edu.icm.crmanager.logic.CrmShadowServiceTest.OLD_SEDNO_DATE;

import org.fest.assertions.Assertions;
import org.fest.assertions.Delta;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.diff.CrmShadowService;
import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.Person;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.model.SednoDate;

/**
 * 
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmShadowServiceComplexTest extends CrmDiffServiceTestBase {	
	
	private static final String CONTRIB_2_OLD_NAME = "contrib 2";
	private static final String CONTRIB_2_NEW_NAME = "contrib 2 new";

	private static final String CONTRIB_1_OLD_NAME = "contrib 1";
	private static final String CONTRIB_1_NEW_NAME = "new name";

	Logger logger = LoggerFactory.getLogger(CrmShadowServiceComplexTest.class);
	
	@Autowired CrmShadowService crmShadowService;
		
	private Revision targetRevision;
	private ContributionTestEntity contrib1 = new ContributionTestEntity(CONTRIB_1_OLD_NAME);
	private ContributionTestEntity contrib2 = new ContributionTestEntity(CONTRIB_2_OLD_NAME);
	
	@Before
	public void before() {
		 deleteTestWorks(template);        
         
         work  =  new WorkTestEntity();
         dataObjectDAO.saveOrUpdate(work, contrib1);

         //old (target) state
	     work.addContribution(contrib1);
	     work.setSomeDouble(OLD_DOUBLE_VALUE);
	     work.setSednoDate(OLD_SEDNO_DATE);
	     work.setTestPerson(OLD_PERSON);
	    	     	    
        targetRevision = acceptRevision();
	     
	     //do some changes
        modifyChildProperty(CONTRIB_1_NEW_NAME); //contrib1
        acceptRevision();

        removeChildContrib1();
        acceptRevision();

        addChildContrib2();
        changeComplexEmbedded(null);
        changeSimpleEmbedded(new SednoDate(2003));
        changeSimpleProperty(3.);
        acceptRevision();

        modifyChildProperty("contrib 2 temp name"); // contrib2
        changeComplexEmbedded(new Person("some","person 4"));
        changeSimpleEmbedded(new SednoDate(2004));
        changeSimpleProperty(4.);
        acceptRevision();

        modifyChildProperty(CONTRIB_2_NEW_NAME); // contrib2
        changeComplexEmbedded(null);
        changeSimpleEmbedded(NEW_SEDNO_DATE);
        changeSimpleProperty(NEW_DOUBLE_VALUE);
        acceptRevision();

         checkCurrentDBState();	
	}

    private Revision acceptRevision() {
        return crmDiffService.generateRevisionAndAccept(work, "mock");
    }
	
	@Test
	public void testFewChangesRevert() {
	    logger.info("testFewChangesRevert()");
		//act
		WorkTestEntity shadow = crmShadowService.getShadow(targetRevision);
		
		//assert
		ContributionTestEntity contribShadow = shadow.getContributions().iterator().next();	
		
		assertThat(shadow.getSomeDouble()).isEqualTo(OLD_DOUBLE_VALUE, Delta.delta(0.1));
		assertThat(shadow.getTestPerson()).isEqualTo(OLD_PERSON);
		assertThat(shadow.getSednoDate()).isEqualTo(OLD_SEDNO_DATE);				
	    assertThat(contribShadow.getContributorName()).isEqualTo(CONTRIB_1_OLD_NAME);
	    
	    checkCurrentDBState();
	}

	//curent db state should not be touched by shadow operations
	private void checkCurrentDBState() {
		dataObjectDAO.refresh(work);
		
		Assertions.assertThat(work.getContributions()).hasSize(1);
		ContributionTestEntity contrib = work.getContributions().iterator().next();
		Assertions.assertThat(contrib.getContributorName()).isEqualTo(CONTRIB_2_NEW_NAME);
		
		assertThat(work.getSomeDouble()).isEqualTo(NEW_DOUBLE_VALUE, Delta.delta(0.1));
		assertThat(work.getTestPerson()).isNull();
		assertThat(work.getSednoDate()).isEqualTo(NEW_SEDNO_DATE);

		dataObjectDAO.refresh(contrib1);
		assertThat(contrib1.getContributorName()).isEqualTo(CONTRIB_1_NEW_NAME);
	}

	private void modifyChildProperty(String newName) {
		work.getContributions().iterator().next().setContributorName(newName);
	}

	private void removeChildContrib1() {
		work.removeContribution(contrib1);		
	}

	private void changeSimpleProperty(Double d) {
        work.setSomeDouble(d);
    }

    private void changeSimpleEmbedded(SednoDate date) {
        work.setSednoDate(date);
    }

    private void changeComplexEmbedded(Person person) {
        work.setTestPerson(person);
    }

    private void addChildContrib2() {
        work.addContribution(contrib2);
    }
}
