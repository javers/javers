package org.javers.core.metamodel.property

import com.google.common.reflect.TypeToken
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.test.assertion.CustomAssert.assertThat

/**
 * @author Pawel Cierpiatka
 */

abstract class EntityConstructionTest extends Specification {

    @Shared
    def ManagedClassFactory entityFactory;

    def "should hold reference to source class"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        entity.getSourceClass() == DummyUser
    }

    def "should scan entity reference property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("supervisor")
                .hasJavaType(DummyUser)

    }

    def "should scan inherited property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("inheritedInt");
    }

    def "should scan Value Map property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("valueMap");
    }

    def "should scan Object Map property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("objectMap");
    }
    

    def "should not scan transient property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        EntityAssert.assertThat(entity).hasntGotProperty("someTransientField");
    }


    def "should scan set property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("stringSet")
                .hasJavaType(new TypeToken<Set<String>>(){}.getType());
    }


    def "should scan list property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("integerList")
                          .hasJavaType(new TypeToken<List<Integer>>(){}.getType());
    }

    def "should scan array property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("intArray")
                .hasJavaType(int[].class);
    }

    def "should scan int property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("age")
                .hasJavaType(Integer.TYPE);
    }

    def "should scan enum property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("sex")
                .hasJavaType(DummyUser.Sex.class);
    }

    def "should scan integer property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("largeInt")
                .hasJavaType(Integer.class);
    }

    def "should scan boolean property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("flag")
                .hasJavaType(Boolean.TYPE);
    }

    def "should scan big boolean property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("bigFlag")
                .hasJavaType(Boolean.class);
    }

    def "should scan string property"() {
        when:
        Entity entity = entityFactory.create(DummyUser);

        then:
        assertThat(entity).hasProperty("name")
                .hasJavaType(String.class);
    }
}