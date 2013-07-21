package pl.edu.icm.crmanager.logic;

import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.crmanager.model.WorkTestEntity;

@RunWith(JUnit4ClassRunner.class) 
public class BCodeGeneratorTest {
    private static final Logger logger = LoggerFactory.getLogger(BCodeGeneratorTest.class);
    
    @Test
    public void testSampleClassGen(){
        Class testForClass = WorkTestEntity.class;
        
        BCodeGeneratorImpl gen = new BCodeGeneratorImpl();
        gen.initialize();
        
        Class clazz = gen.createCrmProxyClass(testForClass, true);
        
        logger.info("class: "+ clazz);
    }
}
