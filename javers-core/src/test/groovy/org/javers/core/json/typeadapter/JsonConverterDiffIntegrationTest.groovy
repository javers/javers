package org.javers.core.json.typeadapter

import org.javers.core.diff.Change
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class JsonConverterDiffIntegrationTest extends Specification {
    class ClassWithChange{
        Change change
    }

    def "should be null safe when converting to json"(){
        given:
        def jsonConverter = javersTestAssembly().jsonConverter

        when:
        def json = jsonConverter.toJson(new ClassWithChange())

        then:
        assert json.contains('"change": null')
    }

}
