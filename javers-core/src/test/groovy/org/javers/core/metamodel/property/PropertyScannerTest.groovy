package org.javers.core.metamodel.property

import com.google.gson.reflect.TypeToken
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.joda.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.metamodel.property.PropertiesAssert.assertThat

/**
 * @author pawel szymczyk
 */
abstract class PropertyScannerTest extends Specification {

    @Shared PropertyScanner propertyScanner

    def "should scan and get inherited property"() {
        when:
        def properties = propertyScanner.scan(DummyAddress)

        then:
        assertThat(properties).hasProperty("inheritedInt").hasValue(new DummyAddress(),0)
    }

    def "should ignore static properties"() {
        when:
        def properties = propertyScanner.scan(DummyAddress)

        then:
        assertThat(properties).hasntGotProperty("staticInt");
    }

    def "should scan and get private property"() {
        when:
        def properties = propertyScanner.scan(ManagedClass)

        then:
        assertThat(properties).hasProperty("privateProperty").hasValue(new ManagedClass(),0)
    }

    def "should scan Id"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("name").looksLikeId()
    }

    def "should scan value map property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("valueMap")
                              .hasJavaType(new TypeToken<Map<String, LocalDateTime>>(){}.getType())
    }

    def "should scan inherited property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("inheritedInt")
                              .hasJavaType(int)
    }

    def "should ignore @Transient property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("propertyWithTransientAnn")
    }

    def "should ignore @DiffIgnore property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasntGotProperty("propertyWithDiffIgnoreAnn")
    }

    def "should scan set property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("stringSet")
                              .hasJavaType(new TypeToken<Set<String>>(){}.type)
    }

    def "should scan list property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("integerList")
                              .hasJavaType(new TypeToken<List<Integer>>(){}.type)
    }

    def "should scan array property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("intArray")
                              .hasJavaType(int[])
    }

    def "should scan int property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("age")
                              .hasJavaType(Integer.TYPE)
    }

    def "should scan Enum property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("sex")
                              .hasJavaType(DummyUser.Sex)
    }

    def "should scan Integer property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("largeInt")
                              .hasJavaType(Integer)
    }

    def "should scan boolean property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("flag")
                              .hasJavaType(Boolean.TYPE)
    }

    def "should scan big Boolean property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("bigFlag")
                              .hasJavaType(Boolean)
    }

    def "should scan String property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("name")
                              .hasJavaType(String)
    }

    def "should scan Entity reference property"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("supervisor")
                              .hasJavaType(DummyUser)
    }

    def "should scan ValueObject property"() {
        when:
        def properties = propertyScanner.scan(DummyUserDetails)

        then:
        assertThat(properties).hasProperty("dummyAddress")
                              .hasJavaType(DummyAddress)
    }

    class ManagedClass {
        int privateProperty
        int getPrivateProperty() {
            privateProperty
        }
    }
}
