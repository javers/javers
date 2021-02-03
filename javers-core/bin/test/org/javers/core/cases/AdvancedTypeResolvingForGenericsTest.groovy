package org.javers.core.cases

import com.google.gson.reflect.TypeToken
import org.javers.common.reflection.ConcreteWithActualType
import org.javers.core.JaversBuilder
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/77
 *
 * To resolve this issue, we added {@link org.javers.common.reflection.JaversMember},
 * which cures:
 * JaversException: CLASS_EXTRACTION_ERROR JaVers bootstrap error - Don't know how to extract Class from type 'T'
 *
 * @author bartosz walacik
 */
class AdvancedTypeResolvingForGenericsTest extends Specification{

    def "should resolve actual types of Generic fields when inherited from Generic superclass"() {
        given:
        def javers = JaversBuilder.javers().build();

        when:
        def jType = javers.getTypeMapping(ConcreteWithActualType)

        then:
        jType.getProperty("id").genericType == String
        jType.getProperty("value").genericType == new TypeToken<List<String>>(){}.type
    }
}
