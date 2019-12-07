package org.javers.core.examples.JaversTest

import static org.junit.Assert.assertEquals;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.ValueChange;
import org.junit.Test;

public class JaversTest {
	private static final ObjectMapper MAPPER = new ObjectMapper();
	Javers javersInstance =JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
	
	@Test
	public void testListAndNodes() {
	
	/*
	 * Comparing two Json Nodes	
	 */
	Diff diff = javersInstance.compare(
				MAPPER.valueToTree(new Owner(1,new Addresss("Delhi",null,new CodeInfo(null)))),
				MAPPER.valueToTree(new Owner(2,new Addresss("Noida","SEZ",new CodeInfo("abc"))))
				);
	
    assertEquals(diff.getChangesByType(ValueChange.class).size(), 4);
    
    
    /* Comparing two objects */
    Diff diff1 = javersInstance.compare(
			new Owner(1,new Addresss("Delhi",null,new CodeInfo(null))),
			new Owner(2,new Addresss("Noida","SEZ",new CodeInfo("abc")))
			);
    assertEquals(diff1.getChangesByType(ValueChange.class).size(), 4);
    
    
    /*Comparing List */
    Owners Owners1 = new Owners();
    Owners1.add(new Owner(1,new Addresss("Delhi",null,new CodeInfo(null))));
    
    Owners Owners2 = new Owners();
    Owners2.add(new Owner(2,new Addresss("Noida","SEZ",new CodeInfo("abc"))));
    
    
	Diff diff2 = javersInstance.compare(Owners1, Owners2);
	
	Diff diff3 = javersInstance.compare(MAPPER.valueToTree(Owners1),MAPPER.valueToTree (Owners2));
	
	assertEquals(diff2.getChangesByType(ValueChange.class).size(), 4);
	assertEquals(diff3.getChangesByType(ValueChange.class).size(), 4);
    
	}
	
}
