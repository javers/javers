package pl.edu.icm.crmanager.logic;

import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.model.RecType;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.model.WorkTestEntity;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmProxyChangesRecordingTest {
    Logger logger = LoggerFactory.getLogger(CrmProxyChangesRecordingTest.class);
    
    private WorkTestEntity work;
    private WorkTestEntity workProxy;
    
    @Autowired
    private CrmProxyFactory crmProxyFactory;
    
    @Autowired
    private CrmSessionFactoryImpl crmSessionFactory;
    
    @Test
    public void testRecordingFieldChange() {
        logger.info("testRecordingFieldChange()" );
               
        logger.info("workProxy.someInt: " + workProxy.getSomeInt());       
        
        //change
        workProxy.setSomeInt(6);
                 
        Revision rev = crmSessionFactory.getCurrentSession().getRevision(); 

        Assert.assertEquals(5, rev.getChangeRequests().get(1).getOldValue());
        Assert.assertEquals(6, rev.getChangeRequests().get(1).getNewValue());
        Assert.assertTrue(work != workProxy);
        Assert.assertEquals(6, work.getSomeInt());
        Assert.assertEquals(6, workProxy.getSomeInt());
        Assert.assertTrue(workProxy instanceof CrmProxy);
        Assert.assertTrue(rev.getChangesCount() == 2);
        Assert.assertEquals("WorkTestEntity" , workProxy.getWrappedClass().getSimpleName());          
    }
    
    @Test
    public void testRecordingReferenceChangeAndFieldChangeOnImplicitProxy() {
        logger.info("testRecordingReferenceChangeAndChildFieldChange()" );
        
        logger.info("workProxy.mainContributor: " + workProxy.getMainContributor()); 
        
        ContributionTestEntity c = new ContributionTestEntity();
        
        //change
        workProxy.setMainContributor(c);
        workProxy.getMainContributor().setContributorName("Jaś Kowalski");
        
        Revision rev = crmSessionFactory.getCurrentSession().getRevision();
        
        Assert.assertEquals("Jaś Kowalski", workProxy.getMainContributor().getContributorName());
        Assert.assertTrue(rev.getChangesCount() == 4);
        Assert.assertEquals("Jaś Kowalski", rev.getChangeRequests().get(3).getNewValue());
        
    }
    
    @Test
    public void testChangeRecordingChildAdd() {
        logger.info("testChangeRecordingChildAdd()" );
        ContributionTestEntity c1 = new ContributionTestEntity();

        
        workProxy.setContributions(new HashSet<ContributionTestEntity>());
        workProxy.getContributions().add(c1);
      
        Revision rev = crmSessionFactory.getCurrentSession().getRevision();
        
        Assert.assertTrue(rev.getChangesCount() == 3);
        Assert.assertEquals(RecType.CHILD_ADD,    rev.getChangeRequests().get(1).getRecType());
    }        
    
    @Test
    public void testChangeRecordingChildAddAndRemove() {
        logger.info("testChangeRecordingChildAdd()" );
        
        ContributionTestEntity c1 = new ContributionTestEntity();
        ContributionTestEntity c2 = new ContributionTestEntity();
        ContributionTestEntity c3 = new ContributionTestEntity();
        
        workProxy.setContributions(new HashSet<ContributionTestEntity>());
        workProxy.getContributions().add(c1);
        workProxy.getContributions().add(c2);
        workProxy.getContributions().add(c3);
        workProxy.getContributions().remove(c2);
        c2.setIdContribution(100); //mock persist
        
        Revision rev = crmSessionFactory.getCurrentSession().getRevision();
        
        Assert.assertTrue(rev.getChangesCount() == 8);
        Assert.assertEquals(RecType.CHILD_ADD,       rev.getChangeRequests().get(1).getRecType());
        Assert.assertEquals(RecType.CHILD_REMOVE,    rev.getChangeRequests().get(7).getRecType());
    }
       
    @Test
    public void testChangeRecordingOnImplicitProxy() {
        logger.info("testChangeRecordingOnImplicitProxy()" );
        
        ContributionTestEntity c1 = new ContributionTestEntity();
        workProxy.setContributions(new HashSet<ContributionTestEntity>());
        workProxy.getContributions().add(c1);
        c1 = workProxy.getContributions().iterator().next();
        c1.setContributorName("Jan");
        
        Revision rev = crmSessionFactory.getCurrentSession().getRevision();
        
        Assert.assertTrue(rev.getChangesCount() == 4);
        Assert.assertEquals(RecType.CHILD_ADD,       rev.getChangeRequests().get(1).getRecType());
        Assert.assertEquals(RecType.VALUE_CHANGE,    rev.getChangeRequests().get(3).getRecType());
        Assert.assertEquals("Jan",                   rev.getChangeRequests().get(3).getNewValue());
        
    }
    
    @Before
    public void prepareData() {
        logger.info("-- before -- ");
        
        crmSessionFactory.startNewSession("test");
        
        
        work = new WorkTestEntity();
        work.setSomeInt(5);
        
            
        workProxy = (WorkTestEntity)crmProxyFactory.createRedoLogProxy(work);
        
        logger.info("work: " + work);
        logger.info("workProxy.wrappedClass: " + workProxy.getWrappedClass().getName());
        logger.info("workProxy: " + workProxy);

    }
    
    @After
    public void closeSession() {
        mockPersist(); 
        Revision rev = crmSessionFactory.getCurrentSession().getRevision(); 
        crmSessionFactory.closeCurrentSession();
        logger.info("rev:"+ rev.getShortDesc()); 
    }
    
    private void mockPersist() {
        work.setIdWork(1);
        int cc = 0;
        
        if (work.getContributions() != null) {

            for (ContributionTestEntity c : work.getContributions()) {
                c.setIdContribution(++cc);
            }
        }
        
        if (work.getMainContributor() != null) {
            work.getMainContributor().setIdContribution(++cc);
        }
    }
    
}
