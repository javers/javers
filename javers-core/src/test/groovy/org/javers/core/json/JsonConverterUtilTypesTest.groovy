package org.javers.core.json

import groovy.json.JsonOutput
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.type.ValueType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Path

/**
 * @author bartosz.walacik
 */
class JsonConverterUtilTypesTest extends Specification{
    @Shared
    def javers = new JaversBuilder().withPrettyPrint(false).build()

    @Unroll
    def "should convert #expectedType (#givenValue) to and from JSON"(){
        expect:
        javers.getTypeMapping(expectedType) instanceof ValueType
        javers.jsonConverter.toJson( givenValue ) == expectedJson
        javers.jsonConverter.fromJson( expectedJson, expectedType ) == givenValue

        where:
        expectedType << [UUID, Currency, URI, URL, File, Path]
        givenValue   << [new UUID(123456,654321),
                         Currency.getInstance("PLN"),
                         new URI("http://example.com"),
                         new URL("http://example.com"),
                         new File("/tmp/file.txt"),
                         Path.of("file")
        ]
        expectedJson << ['"00000000-0001-e240-0000-00000009fbf1"',
                         '"PLN"',
                         '"http://example.com"',
                         '"http://example.com"',
                         JsonOutput.toJson(new File('/tmp/file.txt').toString()),
                         '"file"'
        ]
    }
}
