package org.javers.model.mapping

import org.javers.core.model.DummyUser
import org.javers.model.mapping.Entity
import org.javers.model.mapping.EntityFactory
import org.javers.model.mapping.type.ArrayType
import org.javers.model.mapping.type.CollectionType
import org.javers.model.mapping.type.EntityReferenceType
import org.javers.model.mapping.type.PrimitiveType
import org.javers.test.assertion.EntityAssert
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Array

import static org.javers.test.CustomAssert.*


/**
 * @author Pawel Cierpiatka
 */

abstract class EntityConstructionTest extends Specification {

    @Shared
    def EntityFactory entityFactory;

    def "should hold reference to source class"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        entity.getSourceClass() == DummyUser.class
    }

    def "should scan all properties"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity.getProperties()).hasSize(14);
    }

    def "should scan entity reference property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("supervisor")
                .hasJaversType(EntityReferenceType.class)
                .hasJavaType(DummyUser.class)

    }

    def "should scan inherited property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("inheritedInt");
    }

    def "should not scan transient property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        EntityAssert.assertThat(entity).hasntGotProperty("someTransientField");
    }


    def "should scan set property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("stringSet")
                .hasJaversType(CollectionType.class)
                .hasJavaType(Set.class);
    }


    def "should scan list property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("integerList")
                .hasJaversType(CollectionType.class)
                .hasJavaType(List.class);
    }

    def "should scan array property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("intArray")
                .hasJaversType(ArrayType.class)
                .hasJavaType(Array.class);
    }

    def "should scan int property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("age")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Integer.TYPE);
    }

    def "should scan enum property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("sex")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Enum.class);
    }

    def "should scan integer property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("largeInt")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Integer.class);
    }

    def "should scan boolean property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("flag")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Boolean.TYPE);
    }

    def "should scan big boolean property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("bigFlag")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(Boolean.class);
    }

    def "should scan string property"() {
        when:
        Entity entity = entityFactory.createEntity(DummyUser.class);

        then:
        assertThat(entity).hasProperty("name")
                .hasJaversType(PrimitiveType.class)
                .hasJavaType(String.class);
    }
}