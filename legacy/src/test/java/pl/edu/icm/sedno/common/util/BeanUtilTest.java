package pl.edu.icm.sedno.common.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.sedno.common.model.SednoDate;
import pl.edu.icm.sedno.common.util.BeanOperationPolicy.PropertySubset;
import pl.edu.icm.sedno.common.util.MergeTestBean.MTestEnum;

/**
 * @author bart
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BeanUtilTest {
	 Logger logger = LoggerFactory.getLogger(BeanUtilTest.class);
	 
	 static List<Class> valueClasses = new ArrayList<Class>();
	 static {
		 valueClasses.add(SednoDate.class);		 
	 }
	 
	 @Test
	 public void testEqualsIfEqual() {
		 logger.info("\n testEqualsIfEqual()");
		 
		 MergeTestBean b1 = new MergeTestBean(22);
		 MergeTestBean b2 = new MergeTestBean();
		 
		 setValues1(b1);
		 setValues1(b2); 
		 
		 boolean equals = BeanUtil.equals(b1, b2, PropertySubset.DECLARED_ONLY);
		 
		 Assert.assertTrue(equals);
	 }
	 
	 @Test
	 public void testEqualsIfNotEqual() {
		 logger.info("\n testEqualsIfNotEqual()");
		 
		 MergeTestBean b1 = new MergeTestBean();
		 MergeTestBean b2 = new MergeTestBean();
		 
		 setValues1(b1);
		 setValues1(b2); 
		 b2.setSomeFloat(5f);
		 
		 boolean equals = BeanUtil.equals(b1, b2, PropertySubset.DECLARED_ONLY);
		 
		 Assert.assertFalse(equals);
	 }
	 	 	 	
	 //--
	 
	 protected void assertBulk1( MergeTestBean b2 ) {
		 Assert.assertEquals(new Integer(1), b2.someInteger); 
		 Assert.assertEquals(0.1, b2.someDoub,   0.01);
		 Assert.assertEquals(0.1, b2.someDouble, 0.01);
		 Assert.assertEquals(0.1, b2.someFloat,  0.01);
		 Assert.assertEquals("magic string 1", b2.someString);
		 Assert.assertEquals(DateUtil.getDate(2012, 1, 1), b2.getSomeDate());
		 Assert.assertEquals(new BigDecimal(10), b2.someBigDecimal);
		 Assert.assertEquals(new SednoDate("2012-01-01"), b2.someSednoDate);
		 Assert.assertEquals(MTestEnum.VALUE1, b2.someEnum); 	 
	 }
	 	 
	 protected void setValues1(MergeTestBean bean) {
		 bean.someInt =        1;
		 bean.someInteger    = 1;
		 bean.someDouble     = 0.1;
		 bean.someDoub 	   	 = 0.1;
		 bean.someBool       = true;
		 bean.someBoolean	 = Boolean.TRUE;
		 bean.someString	 = "magic string 1";
		 bean.someDate		 = DateUtil.getDate(2012, 01, 01);
		 bean.someBigDecimal = new BigDecimal(10);               
		 bean.someSednoDate  = new SednoDate("2012-01-01");	
		 bean.someEnum =	   MTestEnum.VALUE1;     
		 bean.someTransient  = 11;
		 bean.someFloat      = 0.1f;
	 }
		 
}
