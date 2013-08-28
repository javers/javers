package org.javers.model;

import org.fest.assertions.api.Assertions;
import org.javers.core.model.DummyAddress;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.ReferenceType;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.Test;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FIledBaseEntityFactoryReference {


    @Test
    public void shouldCreaceReferencedType(){

        //given
        TypeMapper mapper = new TypeMapper();
        mapper.addType(new ReferenceType(DummyAddress.class));
        FieldBasedEntityFactory entityFactory = new FieldBasedEntityFactory(mapper);

        //when
        Entity<MultiDummyClass> entity = entityFactory.create(MultiDummyClass.class);

        //then
        Property dummyAddress = entity.getProperty("dummyAddress");

        Assertions.assertThat(dummyAddress.getType()).isInstanceOf(ReferenceType.class);

    }

    static class MultiDummyClass {

        private int aha;
        private String oho;
        private DummyAddress dummyAddress;


        private String getOho() {
            return oho;
        }

        private void setOho(String oho) {
            this.oho = oho;
        }

        private DummyAddress getDummyAddress() {
            return dummyAddress;
        }

        private void setDummyAddress(DummyAddress dummyAddress) {
            this.dummyAddress = dummyAddress;
        }

        private int getAha() {

            return aha;
        }

        private void setAha(int aha) {
            this.aha = aha;
        }
    }


}
