package pl.edu.icm.sedno.common.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.icm.sedno.common.model.SednoDate;
import pl.edu.icm.sedno.common.util.MergeTestBean.MTestEnum;

/**
 * @author bart
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BeanUtilComplexMergeTest {
	Logger logger = LoggerFactory.getLogger(BeanUtilSimpleMergeTest.class);
	
	private MergeTestBean source;
    private MergeTestBean target;
	 
	@Before
	public void before() {
		source = new MergeTestBean();	
		target = new MergeTestBean();	
	}
	
	@Test
	public void testAddEntryToSimpleMap() {

		 source.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 source.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());		 
		 target.setSimpleMap(null);
		
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultUpdateIfEmpty());
		 					 
		 assertThat(fields).isEqualTo(1);
		 assertThat(target.getSimpleMap().get(MTestEnum.VALUE1)).isEqualTo(SednoDate.today());
	}
	
	@Test
	public void testNullSourceSimpleMap() {	 
		 source.setSimpleMap(null);
		 target.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 target.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());
		 
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultUpdateIfEmpty());
			 
		 assertThat(fields).isZero();
		 assertThat(target.getSimpleMap().get(MTestEnum.VALUE1)).isEqualTo(SednoDate.today());
	}
	
	@Test
	public void testManyChangesInSimpleMap() {
		 source.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 target.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 
		 source.getSimpleMap().put(MTestEnum.VALUE1, new SednoDate(2000,1,1));
		 source.getSimpleMap().put(MTestEnum.VALUE2, SednoDate.today());
		 
		 target.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());
		 
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultUpdateIfEmpty());
		 
		 assertThat(fields).isEqualTo(1);
		 
		 assertThat(target.getSimpleMap().get(MTestEnum.VALUE1)).isEqualTo(SednoDate.today());
		 assertThat(target.getSimpleMap().get(MTestEnum.VALUE2)).isEqualTo(SednoDate.today());
	}
	
	
	@Test
	public void testAddEntryToNestedMap() {
		 
		 source.putNestedMapEntry(MTestEnum.VALUE1, "a","b");
		 target.putNestedMapEntry(MTestEnum.VALUE2, "x","y");
		 
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultUpdateIfEmpty());
		 					 
		 assertThat(fields).isEqualTo(1);
		 assertThat(target.getNestedMap().get(MTestEnum.VALUE1)).containsExactly("a","b");
		 assertThat(target.getNestedMap().get(MTestEnum.VALUE2)).containsExactly("x","y");
	}
	
	@Test
	public void testRemoveEntryFromNestedMap() {
		 
		 source.putNestedMapEntry(MTestEnum.VALUE1, "a");
		 target.putNestedMapEntry(MTestEnum.VALUE1, "a","b");
		 target.putNestedMapEntry(MTestEnum.VALUE2, "x","y");
		 
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultUpdateIfEmpty());
		 
		 assertThat(fields).isEqualTo(0);
		 assertThat(target.getNestedMap().get(MTestEnum.VALUE1)).containsExactly("a","b");
		 assertThat(target.getNestedMap().get(MTestEnum.VALUE2)).containsExactly("x","y"); 
	}
	
	@Test
	public void testChangeValuesInNestedMapEntry() {
		 source.putNestedMapEntry(MTestEnum.VALUE1, "a","c");
		 target.putNestedMapEntry(MTestEnum.VALUE1, "a","b","z");
		 
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultUpdateIfEmpty());
		 					 
		 assertThat(fields).isEqualTo(1);
		 assertThat(target.getNestedMap().get(MTestEnum.VALUE1)).containsExactly("a","b","z","c");
	}
	
	@Test
	public void testManyChangesInNestedMap_policySmartOverwrite() {
		 
		 source.putNestedMapEntry(MTestEnum.VALUE1, "a","b");
		 source.putNestedMapEntry(MTestEnum.VALUE2, "a","c");
		 target.putNestedMapEntry(MTestEnum.VALUE2, "a","x","y");
		 		 
		 int fields = BeanUtil.mergeProperties(source, target, BeanMergePolicy.defaultSmartOverwrite());
		 					 
		 assertThat(fields).isEqualTo(1);
		 assertThat(source.getNestedMap()).isEqualTo(target.getNestedMap());
		 assertThat(source.getNestedMap() == target.getNestedMap()).isFalse();
	}		
}
