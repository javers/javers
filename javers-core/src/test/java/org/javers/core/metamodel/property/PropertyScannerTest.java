package org.javers.core.metamodel.property;

import com.google.gson.reflect.TypeToken;
import org.javers.core.model.DummyAddress;
import org.javers.core.model.DummyUser;
import org.javers.core.model.DummyUserDetails;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pawel szymczyk
 */
public abstract class PropertyScannerTest {

    protected PropertyScanner propertyScanner;

    @Test
    public void shouldScanId() throws Throwable {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasId();
    }

    @Test
    public void shouldScanValueMapProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("valueMap")
                        .hasJavaType(new TypeToken<Map<String, LocalDateTime>>(){}.getType());
    }

    @Test
    public void shouldScanEntityReferenceProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("supervisor")
                        .hasJavaType(DummyUser.class);
    }

    @Test
    public void shouldScanInheritedProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("inheritedInt");
    }

    @Test
    public void shouldNotScanTransientProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasntGotProperty("someTransientField");
    }


    @Test
    public void shouldScanSetProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("stringSet")
                .hasJavaType(new TypeToken<Set<String>>(){}.getType());
    }

    @Test
    public void shouldScanListProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("integerList")
                .hasJavaType(new TypeToken<List<Integer>>(){}.getType());
    }

    @Test
    public void shouldScanArrayProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("intArray")
                .hasJavaType(int[].class);
    }

    @Test
    public void shouldScanIntProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("age")
                .hasJavaType(Integer.TYPE);
    }

    @Test
    public void shouldScanEnumProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("sex")
                .hasJavaType(DummyUser.Sex.class);
    }

    @Test
    public void shouldScanIntegerProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("largeInt")
                .hasJavaType(Integer.class);
    }

    @Test
    public void shouldScanBooleanProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("flag")
                .hasJavaType(Boolean.TYPE);
    }

    @Test
    public void shouldScanBigBooleanProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("bigFlag")
                .hasJavaType(Boolean.class);
    }

    @Test
    public void shouldScanStringProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUser.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("name")
                        .hasJavaType(String.class);
    }

    @Test
    public void shouldScanValueObjectProperty() {
        //when
        List<Property> properties = propertyScanner.scan(DummyUserDetails.class);

        //then
        PropertiesAssert.assertThat(properties).hasProperty("dummyAddress")
                        .hasJavaType(DummyAddress.class);
    }

    protected static class ManagedClass {

        private int privateProperty;

        private int getPrivateProperty() {
            return 0;
        }
    };
}
