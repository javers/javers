package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder;
import org.junit.Test;

public class ComplexObjectGraphComparisonExample {
	@Test
	public void shouldCompareTwoComplexObjects() {

		 Javers javers = JaversBuilder.javers()
		 .registerEntity(EntityDefinitionBuilder.entityDefinition(ComplexEntity.class).withIdPropertyName("code")
		 .build())
		 .registerEntity(EntityDefinitionBuilder.entityDefinition(TenderLocation.class)
		 .withIdPropertyName("complexEntity").build())
		 .registerEntity(EntityDefinitionBuilder.entityDefinition(TenderAirline.class).withIdPropertyName("code")
		 .build())
		 .build();

//		Javers javers = JaversBuilder.javers()
//				.registerEntity(EntityDefinitionBuilder.entityDefinition(ComplexEntity.class).withIdPropertyName("code")
//						.build())
//				.registerEntity(EntityDefinitionBuilder.entityDefinition(TenderLocation.class)
//						.withCompositeId("location", "complexEntity").build())
//				.registerEntity(EntityDefinitionBuilder.entityDefinition(TenderAirline.class)
//						.withCompositeId("code", "tenderLocation").build())
//				.build();

		ComplexEntity baseEntity = DummyComplexEntityFactory.getDummyComplexEntity();
		ComplexEntity refEntity = DummyComplexEntityFactory.getDummyComplexEntity();

		// Diff diff = javers.compare(baseEntity, refEntity);
		// assertThat(diff.getChanges()).hasSize(0);

		// do a change
		refEntity.getTenderLocationList().iterator().next().setSize(20);

		Diff diff = javers.compare(baseEntity, refEntity);
		// assertThat(diff.getChanges()).hasSize(1);

		// Diff diff = javersTest.compare(baseEntity, refEntity);

		// // there should be one change of type {@link ValueChange}
		// ValueChange change = diff.getChangesByType(ValueChange.class).get(0);
		//
		// assertThat(change.getPropertyName()).isEqualTo("location");
		// assertThat(change.getLeft()).isEqualTo("BV1");
		// assertThat(change.getRight()).isEqualTo("Difference_1");
		//
		// // do another change
		// referenceEntity.getTenderList().get(1).getTenderAirlines().get(1).setCode("Difference_2");
		//
		// // second difference is not detected, failing the commented test
		// diff = javers.compare(baseEntity, referenceEntity);
		// assertThat(diff.getChanges()).hasSize(2);

		System.out.println(diff);
	}
}
