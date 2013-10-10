package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.ContributionTestEntity;
import pl.edu.icm.crmanager.model.WorkTestEntity;
import pl.edu.icm.sedno.common.dao.DataObjectDAO;

import java.util.HashSet;
import java.util.Set;

/**
 * @author bart
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class CrmProxyExceptionTest {
    Logger logger = LoggerFactory.getLogger(CrmProxyExceptionTest.class);
    
    WorkTestEntity work ;
    WorkTestEntity workProxy ;
    
    @Autowired
    private CrmProxyFactory crmProxyFactory;
    
    @Autowired
    private CrmSessionFactory crmSessionFactory;
    
    @Autowired
    private DataObjectDAO dataObjectDAO;
    
    @Autowired
    private RevisionServiceTestBeanI revisionServiceTestBean;
    
    @Before
    public void before() {
        crmSessionFactory.startNewSession("test");
        
        work = new WorkTestEntity();
        work.setIdWork(1);
        work.setContributions(new HashSet<ContributionTestEntity>());
        work.setMainContributor(new ContributionTestEntity(1));      
        workProxy = (WorkTestEntity)crmProxyFactory.createRedoLogProxy(work);      
    }
    
    @After
    public void after() {
        crmSessionFactory.closeCurrentSessionIfFound();  
    }
    
    @Test(expected=CrmRuntimeException.class)
    public void testIfChangeAfterClosingSessionThrowsException() {
      
        crmSessionFactory.closeCurrentSession();   
        workProxy.setSomeInt(5);
    }
    
    @Test(expected=CrmRuntimeException.class)
    public void testProxyDetach(){
        
        
        ContributionTestEntity prevContrib =  workProxy.getMainContributor();    
        ContributionTestEntity prevContrib2 = workProxy.getMainContributor();
        
        Assert.assertEquals(prevContrib, prevContrib2);
        
        //detatch
        workProxy.setMainContributor(new ContributionTestEntity(2));
        
        workProxy.getMainContributor().setContributorName("eee");
        
        //change on detached proxy
        prevContrib.setContributorName("eee"); 
    }
    
    @Test(expected=CrmRuntimeException.class)
    public void testColProxyReplace() {
        Set<ContributionTestEntity> prevColProxy = workProxy.getContributions();
        
        logger.info("prevColProxy: "+ prevColProxy);
        
        //colProxy replace
        workProxy.setContributions(prevColProxy);
    }
    
    @Test(expected=CrmRuntimeException.class)
    public void testAttachingPersistentObjectForNoAcceptSession() {
        crmSessionFactory.closeCurrentSession();
        revisionServiceTestBean.testAttachingPersistentObjectForNoAcceptSession();
    }
}
