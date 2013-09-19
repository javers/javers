package org.javers.model.mapping;

import org.fest.assertions.api.Assertions;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.type.*;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.fail;

/**
 * @author bartosz walacik
 */
public abstract class EntityConstructionTest {
    protected EntityFactory entityFactory;

    @Test
    public void shouldHoldReferenceToSourceClass() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasSourceClass(DummyUser.class);
    }

    @Test
    public void dronioTest() {
        fail("This is fail to test drone.io");
    }

    @Test
    public void shouldScanAllProperties() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        Assertions.assertThat(entity.getProperties()).hasSize(14);
    }

    @Test
    public void shouldScanEntityReferenceProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("supervisor")
                .hasJaversType(EntityReferenceType.class)
                .hasJavaType(DummyUser.class);
    }

    @Test
    public void shouldScanInheritedProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("inheritedInt");
    }

    @Test
    public void shouldNotScanTransientProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasntGotProperty("someTransientField");
    }


    @Test
    public void shouldScanSetProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("stringSet")
                .hasJaversType(CollectionType.class)
                .hasJavaType(Set.class);
    }
    //"nabled when value object will be supported
    @Test(enabled = false)
    public void shouldScanValueObjectProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUserDetails.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("dummyAddress")
                .hasJaversType(ValueObjectType.class);
    }

    @Test
    public void shouldScanListProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("integerList")
                .hasJaversType(CollectionType.class)
                .hasJavaType(List.class);
    }

    @Test
    public void shouldScanArrayProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("intArray")
                    .hasJaversType(ArrayType.class)
                    .hasJavaType(Array.class);
    }

    @Test
    public void shouldScanIntProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("age")
                    .hasJaversType(PrimitiveType.class)
                    .hasJavaType(Integer.TYPE);
    }

    @Test
    public void shouldScanEnumProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("sex")
                    .hasJaversType(PrimitiveType.class)
                    .hasJavaType(Enum.class);
    }

    @Test
    public void shouldScanIntegerProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("largeInt")
                    .hasJaversType(PrimitiveType.class)
                    .hasJavaType(Integer.class);
    }

    @Test
    public void shouldScanBooleanProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("flag")
                    .hasJaversType(PrimitiveType.class)
                    .hasJavaType(Boolean.TYPE);
    }

    @Test
    public void shouldScanBigBooleanProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("bigFlag")
                    .hasJaversType(PrimitiveType.class)
                    .hasJavaType(Boolean.class);
    }

    @Test
    public void shouldScanStringProperty() {
        //when
        Entity entity = entityFactory.createEntity(DummyUser.class);

        //then
        EntityAssert.assertThat(entity).hasProperty("name")
                    .hasJaversType(PrimitiveType.class)
                    .hasJavaType(String.class);
    }
}
