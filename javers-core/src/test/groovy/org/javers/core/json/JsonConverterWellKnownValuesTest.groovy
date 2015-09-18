package org.javers.core.json

import org.javers.core.diff.Change
import static org.javers.core.JaversTestBuilder.javersTestAssembly
import spock.lang.Shared
import spock.lang.Specification
import java.text.SimpleDateFormat
import static java.math.RoundingMode.HALF_UP

/**
 * @author bartosz walacik
 */
class JsonConverterWellKnownValuesTest extends Specification {
    @Shared
    def jsonConverter = javersTestAssembly().jsonConverter

    @Shared
    SimpleDateFormat sdfIso = new SimpleDateFormat(JsonConverterBuilder.ISO_DATE_TIME_FORMAT)

    def "should convert BigDecimal TO json with proper scale"() {
        given:
        def date = new BigDecimal(1).setScale(3,HALF_UP)

        when:
        def json = jsonConverter.toJson(date)

        then:
        json == '1.000'
    }

    def "should convert BigDecimal FROM json with proper scale"() {
        when:
        def date = jsonConverter.fromJson('1.000', BigDecimal)

        then:
        date  == new BigDecimal(1.0).setScale(3,HALF_UP)
    }

    def "should convert java.util.Date TO json in ISO format"() {
        given:
        def date = new Date();

        when:
        def json = jsonConverter.toJson(date)

        then:
        json == "\"${sdfIso.format(date)}\""
    }

    def "should convert java.util.Date FROM json in ISO format"() {
        when:
        def date = jsonConverter.fromJson('"2014-01-12T20:04:48+0100"', Date)

        then:
        date == sdfIso.parse("2014-01-12T20:04:48+0100")
    }

    def "should be null safe when converting to and from JSON"(){
        expect:
        jsonConverter.toJson(null) == "null"
        jsonConverter.fromJson("null", Integer) == null
    }
}
