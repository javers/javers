package pl.edu.icm.sedno.common.util;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.crmanager.model.RecType;
import pl.edu.icm.sedno.common.model.SednoDate;
import pl.edu.icm.sedno.common.util.MergeTestBean.MTestEnum;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author bart
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class BeanUtilDiffTest {
	Logger logger = LoggerFactory.getLogger(BeanUtilTest.class);
	
	private MergeTestBean b1;
    private MergeTestBean b2;
	 
	@Before
	public void before() {
		b1 = new MergeTestBean();	
		b2 = new MergeTestBean();	
	}
	
	@Test
	public void testSimplePropertyChange() {
		 logger.info("\n testSimplePropertyChange()");		
		 
		 //act
		 b2.setSomeInt(5);		 
		 b2.setSomeSednoDate(SednoDate.today());
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes) {
			 logger.info(".. "+ change);
		 }	
		
		 //assert
		 assertThat(changes.size()).isEqualTo(2);
		 PropertyChange ch1 = changes.get(0);
		 PropertyChange ch2 = changes.get(1);
		 
		 assertThat(ch1.getModPointGetterName()).isEqualTo("getSomeInt");
		 assertThat(ch1.getOldValue()).isEqualTo(0);
		 assertThat(ch1.getNewValue()).isEqualTo(5);
		 assertThat(ch1.getModPoint()).isEqualTo(b2);
		 assertThat(ch1.getRecType()).isEqualTo(RecType.VALUE_CHANGE);
		 		 
		 assertThat(ch2.getModPointGetterName()).isEqualTo("getSomeSednoDate");
		 assertThat(ch2.getOldValue()).isNull();
		 assertThat(ch2.getNewValue()).isEqualTo(SednoDate.today());
		 assertThat(ch2.getModPoint()).isEqualTo(b2);
		 assertThat(ch2.getRecType()).isEqualTo(RecType.VALUE_CHANGE);
		 	 		
	}
	
	@Test
	public void testReferenceChange() {
		 logger.info("\n testReferenceChange()");		
		 
		 MergeTestBean oldRef = new MergeTestBean();
		 MergeTestBean newRef = new MergeTestBean();
		 
		 //act
		 b1.setSomeReference(oldRef);
		 b2.setSomeReference(newRef);
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 
		 //assert
		 assertThat(changes.size()).isEqualTo(1);
		 PropertyChange ch = changes.get(0);
		 
		 assertThat(ch.getOldValue()).isEqualTo(oldRef);
		 assertThat(ch.getNewValue()).isEqualTo(newRef);
		 assertThat(ch.getModPoint()).isEqualTo(b2);
		 assertThat(ch.getRecType()).isEqualTo(RecType.REFERENCE_CHANGE);
	}

	@Test
	public void testChildAdd() {
		logger.info("\n testChildAdd()");
		
		 MergeTestBean ref1 = new MergeTestBean("ref1");
		 MergeTestBean ref2 = new MergeTestBean("ref2");
		 
		 b1.setChildren(null);
		 b2.setChildren(Lists.newArrayList(ref1, ref2));
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 			 
		 //assert
		 assertThat(changes.size()).isEqualTo(2);


		 assertThat(changes).onProperty("newValue").containsOnly(ref1, ref2);		 
		 for (PropertyChange ch : changes) {
			 assertThat(ch.getOldValue()).isNull();
			 assertThat(ch.getModPoint()).isEqualTo(b2);
			 assertThat(ch.getRecType()).isEqualTo(RecType.CHILD_ADD);
		 }
	}
	
	@Test
	public void testChildRemove() {
		 logger.info("\n testChildRemove()");
		
		 MergeTestBean ref1 = new MergeTestBean("ref1");
		 MergeTestBean ref2 = new MergeTestBean("ref2");
		 
		 b1.setChildren(Lists.newArrayList(ref1, ref2));
		 b2.setChildren(Lists.newArrayList(ref1));
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 //assert
		 assertThat(changes.size()).isEqualTo(1);
		 PropertyChange ch = changes.get(0);
		 
		 assertThat(ch.getOldValue()).isEqualTo(ref2);
		 assertThat(ch.getNewValue()).isNull();
		 assertThat(ch.getModPoint()).isEqualTo(b2);
		 assertThat(ch.getRecType()).isEqualTo(RecType.CHILD_REMOVE);		
	}
	
	
	@Test
	public void testAddEntryToSimpleMap() {
		 logger.info("\n testAddEntryToSimpleMap()");
		 
		 b1.setSimpleMap(null);
		 b2.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 b2.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());
		 b2.getSimpleMap().put(MTestEnum.VALUE2, new SednoDate(2000,1,1));
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 assertThat(changes.size()).isEqualTo(2);
		 
		 assertThat(changes).onProperty("newValue").containsOnly( new SimpleEntry(MTestEnum.VALUE1, SednoDate.today()),
				                                                  new SimpleEntry(MTestEnum.VALUE2, new SednoDate(2000,1,1)) );		 
		 for (PropertyChange ch : changes) {
			 assertThat(ch.getOldValue()).isNull();
			 assertThat(ch.getModPointGetterName()).isEqualTo("getSimpleMap");
			 assertThat(ch.getModPoint()).isEqualTo(b2);			 
			 assertThat(ch.getRecType()).isEqualTo(RecType.VALUE_ADD);
		 }
	}
	
	@Test
	public void testRemoveEntryFromSimpleMap() {
		 logger.info("\n testRemoveEntryFromSimpleMap()");
		 
		 b1.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 b2.setSimpleMap(new 	HashMap<MTestEnum, SednoDate>());
		 
		 b1.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());		 
		 b1.getSimpleMap().put(MTestEnum.VALUE2, new SednoDate(2000,1,1));
		 
		 b2.getSimpleMap().put(MTestEnum.VALUE2, new SednoDate(2000,1,1));
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 assertThat(changes.size()).isEqualTo(1);

		 PropertyChange ch = changes.get(0);
		
		 assertThat(ch.getNewValue()).isNull();
		 assertThat(ch.getOldValue()).isEqualTo( new SimpleEntry(MTestEnum.VALUE1, SednoDate.today()) );
		 assertThat(ch.getModPointGetterName()).isEqualTo("getSimpleMap");
		 assertThat(ch.getModPoint()).isEqualTo(b2);			 
		 assertThat(ch.getRecType()).isEqualTo(RecType.VALUE_REMOVE);
	}
	
	@Test
	public void testChangeValueInSimpleMapEntry() {
		 logger.info("\n testChangeValueInSimpleMapEntry()");
		 
		 b1.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 b2.setSimpleMap(new HashMap<MTestEnum, SednoDate>());
		 
		 b1.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());		 
		 b1.getSimpleMap().put(MTestEnum.VALUE2, new SednoDate(2000,1,1));
		 
		 b2.getSimpleMap().put(MTestEnum.VALUE1, SednoDate.today());		 
		 b2.getSimpleMap().put(MTestEnum.VALUE2, new SednoDate(2000,2,2)); //!
		 
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 assertThat(changes.size()).isEqualTo(1);

		 PropertyChange ch = changes.get(0);
				 		
		 assertThat(ch.getOldValue()).isEqualTo( new SimpleEntry(MTestEnum.VALUE2, new SednoDate(2000,1,1)) );
		 assertThat(ch.getNewValue()).isEqualTo( new SimpleEntry(MTestEnum.VALUE2, new SednoDate(2000,2,2)) );
		 assertThat(ch.getModPointGetterName()).isEqualTo("getSimpleMap");
		 assertThat(ch.getModPoint()).isEqualTo(b2);			 
		 assertThat(ch.getRecType()).isEqualTo(RecType.VALUE_CHANGE);
	}
	
	@Test
	public void testAddEntryToNestedMap() {
		 logger.info("\n testAddEntryToNestedMap()");
		 
		 b2.putNestedMapEntry(MTestEnum.VALUE1, "a","b");
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 assertThat(changes.size()).isEqualTo(2); 
		 			
		 assertThat(changes).onProperty("newValue").containsOnly( new SimpleEntry(MTestEnum.VALUE1, "a"),
                                                                  new SimpleEntry(MTestEnum.VALUE1, "b" ) );			 
		 for (PropertyChange ch : changes) {
			assertThat(ch.getOldValue()).isNull();
			assertThat(ch.getModPointGetterName()).isEqualTo("getNestedMap");
			assertThat(ch.getModPoint()).isEqualTo(b2);			 
			assertThat(ch.getRecType()).isEqualTo(RecType.VALUE_ADD);
		 }
	}
	
	@Test
	public void testRemoveEntryFromNestedMap() {
		 logger.info("\n testRemoveEntryFromNestedMap()");
		 
		 b1.putNestedMapEntry(MTestEnum.VALUE1, "a","b");
		 b1.putNestedMapEntry(MTestEnum.VALUE2, "x","y");
		 b2.putNestedMapEntry(MTestEnum.VALUE1, "a","b");
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 assertThat(changes.size()).isEqualTo(2); 
		 			
		 assertThat(changes).onProperty("oldValue").containsOnly( new SimpleEntry(MTestEnum.VALUE2, "x"),
                                                                  new SimpleEntry(MTestEnum.VALUE2, "y" ) );	
		 
		 for (PropertyChange ch : changes) {
			assertThat(ch.getNewValue()).isNull();
			assertThat(ch.getModPointGetterName()).isEqualTo("getNestedMap");
			assertThat(ch.getModPoint()).isEqualTo(b2);			 
			assertThat(ch.getRecType()).isEqualTo(RecType.VALUE_REMOVE);
		 }		 
	}
	
	@Test
	public void testChangeValuesInNestedMapEntry() {
		 logger.info("\n testRemoveEntryFromNestedMap()");
		 
		 b1.putNestedMapEntry(MTestEnum.VALUE1, "a","b","c");
		 b2.putNestedMapEntry(MTestEnum.VALUE1, "a","b","z");
		 
		 List<PropertyChange> changes = BeanUtil.diff(b1, b2, new BeanOperationPolicy());
		 logger.info("changes : ");
		 for (PropertyChange change : changes)
			 logger.info(".. "+ change);
		 
		 assertThat(changes.size()).isEqualTo(2); 
		 			
		 assertThat(changes).onProperty("newValue").contains( new SimpleEntry(MTestEnum.VALUE1, "z" ) );
		 assertThat(changes).onProperty("oldValue").contains( new SimpleEntry(MTestEnum.VALUE1, "c" ) );
		 
		 for (PropertyChange ch : changes) {
			assertThat(ch.getModPointGetterName()).isEqualTo("getNestedMap");
			assertThat(ch.getModPoint()).isEqualTo(b2);	
				
			if (ch.getRecType() == RecType.VALUE_REMOVE) {
				assertThat(ch.getOldValue()).isEqualTo(new SimpleEntry(MTestEnum.VALUE1, "c" ));
			} else {
				assertThat(ch.getNewValue()).isEqualTo(new SimpleEntry(MTestEnum.VALUE1, "z" ));
			}
			
		 }			 
	}
}

