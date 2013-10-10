package org.javers.model.mapping.util.managedClassPropertyScanner;

import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.javers.model.mapping.PropertiesAssert;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.*;
import org.javers.model.mapping.util.managedClassPropertyScanner.Scanner;
import org.javers.test.assertion.Assertions;
import org.testng.annotations.Test;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

/**
 * @author pawel szymczyk
 */
public abstract class ScannerTest {

    protected Scanner scanner;

    @Test
    public void shouldScanAllProperties() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        Assertions.assertThat(properties).hasSize(14);
    }

    @Test
    public void shouldScanEntityReferenceProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("supervisor")
            .hasJaversType(EntityReferenceType.class)
            .hasJavaType(DummyUser.class);
    }



    @Test
    public void shouldScanInheritedProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("inheritedInt");
    }

    @Test
    public void shouldNotScanTransientProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasntGotProperty("someTransientField");
    }


    @Test
    public void shouldScanSetProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("stringSet")
                .hasJaversType(CollectionType.class)
                .hasJavaType(Set.class);
    }

    @Test
    public void shouldScanListProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("integerList")
                .hasJaversType(CollectionType.class)
                .hasJavaType(List.class);
    }

    @Test
    public void shouldScanArrayProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("intArray")
                .hasJaversType(ArrayType.class)
                .hasJavaType(Array.class);
    }

    @Test
    public void shouldScanIntProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("age")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Integer.TYPE);
    }

    @Test
    public void shouldScanEnumProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("sex")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Enum.class);
    }

    @Test
    public void shouldScanIntegerProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("largeInt")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Integer.class);
    }

    @Test
    public void shouldScanBooleanProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("flag")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Boolean.TYPE);
    }

    @Test
    public void shouldScanBigBooleanProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("bigFlag")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Boolean.class);
    }

    @Test
    public void shouldScanStringProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("name")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(String.class);
    }

    @Test
    public void shouldScanValueObjectProperty() {
        //when
        List<Property> properties = scanner.scan(DummyUserDetails.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("dummyAddress")
                .hasJaversType(ValueObjectType.class);
    }
}
