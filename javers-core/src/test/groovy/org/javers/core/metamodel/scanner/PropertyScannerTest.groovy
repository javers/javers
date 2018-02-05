package org.javers.core.metamodel.scanner

import com.google.gson.reflect.TypeToken
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.DiffInclude
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyIgnoredPropertiesType
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import java.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static PropertyScanAssert.assertThat

/**
 * @author pawel szymczyk
 */
abstract class PropertyScannerTest extends Specification {

    @Shared PropertyScanner propertyScanner

    class EntityWithDiffInclude extends JaversEntity {
        @DiffInclude String includedField

        @DiffInclude String getIncludedField() {
            return includedField
        }
    }

    def "should scan included property"() {
        when:
        def properties = propertyScanner.scan(EntityWithDiffInclude)

        then:
        assertThat(properties).hasProperty("includedField").isIncluded()
    }

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

    @Unroll
    def "should scan and get #scopedProperty"() {
        when:
        def properties = propertyScanner.scan(PropertyScannerEntity)

        then:
        assertThat(properties).hasProperty(scopedProperty).hasValue(new PropertyScannerEntity(),1)

        where:
        scopedProperty << ["privateProperty","protectedProperty","packagePrivateProperty","publicProperty"]
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

    def "should scan @Transient property as transient"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("propertyWithTransientAnn").isTransient()
    }

    def "should scan @DiffIgnore property as transient"() {
        when:
        def properties = propertyScanner.scan(DummyUser)

        then:
        assertThat(properties).hasProperty("propertyWithDiffIgnoreAnn").isTransient()
    }

    def "should scan all properties of classes marked as @IgnoreDeclaredProperties as transient"() {
        when:
        def properties = propertyScanner.scan(DummyIgnoredPropertiesType, true)

        then:
        assertThat(properties).hasProperty("propertyThatShouldBeIgnored").isTransient()
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

    def "should use name from @PropertyName when given"() {
        when:
        def properties = propertyScanner.scan(DummyUserDetails)

        then:
        assertThat(properties).hasProperty("Customized Property")
                              .hasJavaType(String)
    }
}
