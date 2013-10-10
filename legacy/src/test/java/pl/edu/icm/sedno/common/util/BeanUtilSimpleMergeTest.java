package pl.edu.icm.sedno.common.util;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.sedno.common.model.SednoDate;
import pl.edu.icm.sedno.common.util.MergeTestBean.MTestEnum;

import java.math.BigDecimal;

/**
 * @author bart
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BeanUtilSimpleMergeTest extends BeanUtilTest{
	 Logger logger = LoggerFactory.getLogger(BeanUtilSimpleMergeTest.class);
	 
	 @Test
	 public void testOverwriteAll(){
		 logger.info("\n testOverwriteAll()");
		 
		 MergeTestBean b1 = new MergeTestBean();
		 MergeTestBean b2 = new MergeTestBean();
		 
		 setValues1(b1);
		 setValues2(b2);
		 
		 //logger.info("b1 "+b1);
		 
		 BeanUtil.printPersistentProperties(b1, BeanMergePolicy.defaultOverwrite());
		  
		 int merged = BeanUtil.mergeProperties(b1, b2, BeanMergePolicy.defaultOverwrite());
		 Assert.assertEquals(11, merged);
		 
		 
		 Assert.assertEquals(2, b2.someInt); //id is not copied
		 Assert.assertEquals(22, b2.someTransient); //transient is not copied
		 Assert.assertEquals(true, b2.someBool);
		 Assert.assertEquals(Boolean.TRUE, b2.someBoolean);
		 assertBulk1(b2);
	 }
	 
	 @Test
	 public void testUpdateEmptySource() {
		 logger.info("\n testUpdateEmptySource()");
		 
		 MergeTestBean b1 = new MergeTestBean();
		 MergeTestBean b2 = new MergeTestBean();
		 setValues2(b2);
						 
		 int merged = BeanUtil.mergeProperties(b1, b2, BeanMergePolicy.defaultUpdateIfEmpty());
		 
		 Assert.assertEquals(0, merged);
		 
		 Assert.assertEquals(false, b2.someBool);
		 Assert.assertEquals(Boolean.FALSE, b2.someBoolean);
		 assertBulk2(b2);
	 }
	
	 
	 @Test
	 public void testUpdateEmptyTarget() {
		 logger.info("\n testUpdateEmptyTarget()");
		 
		 MergeTestBean b1 = new MergeTestBean();
		 MergeTestBean b2 = new MergeTestBean();
		 setValues1(b1);
		 		 
		 int merged = BeanUtil.mergeProperties(b1, b2, BeanMergePolicy.defaultUpdateIfEmpty());
		 Assert.assertEquals(10, merged);
		 
		 Assert.assertEquals(false, b2.someBool);
		 Assert.assertEquals(Boolean.TRUE, b2.someBoolean);
		 assertBulk1(b2);
	 }
	 
	 @Test
	 public void testUpdateNotEmptyTarget() {
		 logger.info("\n testUpdateNotEmptyTarget()");
		 
		 MergeTestBean b1 = new MergeTestBean();
		 MergeTestBean b2 = new MergeTestBean();
		 setValues1(b1);
		 setValues2(b2);
		 		 
		 int merged = BeanUtil.mergeProperties(b1, b2, BeanMergePolicy.defaultUpdateIfEmpty());
		
		 Assert.assertEquals(0, merged);
		 
		 Assert.assertEquals(false, b2.someBool);
		 Assert.assertEquals(Boolean.FALSE, b2.someBoolean);
		 assertBulk2(b2);
	 }
	 
	 @Test
	 public void testSmartOverwrite() {
		 logger.info("\n testSmartOverwrite()");
		 
		 MergeTestBean b1 = new MergeTestBean();
		 MergeTestBean b2 = new MergeTestBean();
		 setValues1(b1);
		 setValues2(b2);
		 
		 b1.setSomeFloat(0);
		 b1.setSomeString("");
		 b1.setSomeDoub(0);	
		 
		 int merged = BeanUtil.mergeProperties(b1, b2, BeanMergePolicy.defaultSmartOverwrite());
		 
		 Assert.assertEquals(8, merged);
	 }
	 
	 protected void setValues2(MergeTestBean bean) {
		 bean.someInt =        2;
		 bean.someInteger    = 2;
		 bean.someDouble     = 0.2;
		 bean.someDoub 	   	 = 0.2;
		 bean.someBool       = false;
		 bean.someBoolean	 = Boolean.FALSE;
		 bean.someString	 = "magic string 2";
		 bean.someDate		 = DateUtil.getDate(2012, 1, 2);
		 bean.someBigDecimal = new BigDecimal(20);               
		 bean.someSednoDate  = new SednoDate("2012-01-02");	
		 bean.someEnum =	   MTestEnum.VALUE2;  
		 bean.someTransient  = 22;
		 bean.someFloat      = 0.2f;
	 }
	 
	 protected void assertBulk2( MergeTestBean b2 ) {
		 Assert.assertEquals(new Integer(2), b2.someInteger); 
		 Assert.assertEquals(0.2, b2.someDoub,   0.01);
		 Assert.assertEquals(0.2, b2.someDouble, 0.01);
		 Assert.assertEquals(0.2, b2.someFloat,  0.01);
		 Assert.assertEquals("magic string 2", b2.someString);
		 Assert.assertEquals(DateUtil.getDate(2012, 1, 2), b2.getSomeDate());
		 Assert.assertEquals(new BigDecimal(20), b2.someBigDecimal);
		 Assert.assertEquals(new SednoDate("2012-01-02"), b2.someSednoDate);
		 Assert.assertEquals(MTestEnum.VALUE2, b2.someEnum); 	 
	 }
}
