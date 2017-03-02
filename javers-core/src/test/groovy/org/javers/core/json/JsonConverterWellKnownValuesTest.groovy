package org.javers.core.json

import org.javers.common.collections.Arrays
import java.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static java.math.RoundingMode.HALF_UP
import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class JsonConverterWellKnownValuesTest extends Specification {
    @Shared
    def jsonConverter = javersTestAssembly().jsonConverterMinifiedPrint

    @Unroll
    def "should convert array of type #arrayType to and from JSON"(){
      expect:
      jsonConverter.toJson(givenArray) == expectedJson
      def from = jsonConverter.fromJson(expectedJson, arrayType)
      from == givenArray
      from.class == arrayType

      where:
      expectedJson << ["[1,2]"] * 2
      arrayType <<    [Arrays.INTEGER_ARRAY_TYPE, Arrays.INT_ARRAY_TYPE]
      givenArray <<   [ [1,2].toArray(), Arrays.intArray(1,2) ]
    }

    def "should convert BigDecimal to and from JSON with proper scale"() {
        given:
        def v = new BigDecimal(1).setScale(3,HALF_UP)
        def expectedJson = '1.000'

        expect:
        jsonConverter.toJson(v) == expectedJson
        jsonConverter.fromJson(expectedJson, BigDecimal) == new BigDecimal(1.0).setScale(3,HALF_UP)
    }
    
    def "should convert BigInteger to and from JSON without issues"() {
        given:
        def v = new BigInteger("1")
        def expectedJson = '1'

        expect:
        jsonConverter.toJson(v) == expectedJson
        jsonConverter.fromJson(expectedJson, BigInteger) == new BigInteger("1")
    }

    def "should be null safe when converting to and from JSON"(){
        expect:
        jsonConverter.toJson(null) == "null"
        jsonConverter.fromJson("null", Integer) == null
    }

    class WithLocalDateTime {
        LocalDateTime ldt
    }

    def "should be null safe when converting objects to JSON"() {
        when:
        def json = jsonConverter.toJson(new WithLocalDateTime())

        then:
        assert json.contains('"ldt":null')
    }

    def "should be null safe when converting objects from JSON"() {
        when:
        def value = jsonConverter.fromJson('{"ldt":null}', WithLocalDateTime)

        then:
        value.ldt == null
    }
}
