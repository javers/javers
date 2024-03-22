package org.javers.core.cases


import org.javers.core.JaversBuilder
import org.javers.core.metamodel.type.MapType
import spock.lang.Specification

/**
 * Related also to issue https://github.com/javers/javers/issues/916 which suggest the in fact:
 * {@link org.javers.common.reflection.ReflectionUtil#calculateHierarchyDistance} should return not only direct
 * interfaces but superinterfaces as well.
 * @author simone giusso
 */
class Java21SortedMapShouldBeStillMappedToJaversMapType extends Specification{

    def "should map java 21 SortedMap to Javers MapType instead of ValueObjectType"() {
        given:
        final def javers = JaversBuilder.javers().build();

        when:
        final def jType = javers.getTypeMapping(SortedMap)

        then:
        jType instanceof MapType
    }
}
