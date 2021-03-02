package org.javers.core.json

import groovy.json.JsonOutput
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.type.ValueType
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz.walacik
 */
class JsonConverterUtilTypesTest extends Specification{
    @Shared
    def javers = new JaversBuilder().withPrettyPrint(false).build()

    @Unroll
    def "should convert #expectedType (#givenValue) to and from JSON"(){
        expect:
        javers.jsonConverter.toJson( givenValue ) == expectedJson
        javers.jsonConverter.fromJson( expectedJson, expectedType ) == givenValue
        javers.getTypeMapping(expectedType) instanceof ValueType

        where:
        expectedType << [UUID, Currency, URI, URL, File]
        givenValue   << [new UUID(123456,654321),
                         Currency.getInstance("PLN"),
                         new URI("http://example.com"),
                         new URL("http://example.com"),
                         new File("/tmp/file.txt")
        ]
        expectedJson << ['"00000000-0001-e240-0000-00000009fbf1"',
                         '"PLN"',
                         '"http://example.com"',
                         '"http://example.com"',
                         JsonOutput.toJson(new File('/tmp/file.txt').toString())
        ]
    }
}
