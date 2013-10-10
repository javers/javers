package pl.edu.icm.crmanager.logic;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.edu.icm.crmanager.model.WorkTestEntity;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-changeRequestManager-test.xml" })
public class ProxyEqualsTest {
    Logger logger = LoggerFactory.getLogger(ProxyEqualsTest.class);
    
    @Autowired CrmProxyFactory crmProxyFactory;
    @Autowired ChangeRequestManager changeRequestManager;
    
    @Test
    public void testIfProxyEqualsInstance() {
        WorkTestEntity instance = new WorkTestEntity(5);
        
        changeRequestManager.openCrmSession("eee");
        
        WorkTestEntity proxy = (WorkTestEntity)crmProxyFactory.createRedoLogProxy(instance);
                
        Assert.assertFalse (instance == proxy);
        Assert.assertEquals(instance, proxy);
        
        try {
            changeRequestManager.closeCrmSessionWithNoAccept();
        }catch (Exception e) {
        }
    }
}
