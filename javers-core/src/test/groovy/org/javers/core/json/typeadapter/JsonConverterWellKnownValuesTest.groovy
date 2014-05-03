package org.javers.core.json.typeadapter

import org.javers.core.json.JsonConverter
import spock.lang.Shared
import spock.lang.Specification

import java.text.SimpleDateFormat

import static java.math.RoundingMode.HALF_UP
import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class JsonConverterWellKnownValuesTest extends Specification {

    @Shared
    SimpleDateFormat sdfIso = new SimpleDateFormat(JsonConverter.ISO_DATE_TIME_FORMAT)

    def "should convert BigDecimal TO json with proper scale"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        BigDecimal date = new BigDecimal(1).setScale(3,HALF_UP)
        String json = jsonConverter.toJson(date)

        then:
        json == '1.000'
    }

    def "should convert BigDecimal FROM json with proper scale"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        BigDecimal date = jsonConverter.fromJson('1.000', BigDecimal)

        then:
        date  == new BigDecimal(1.0).setScale(3,HALF_UP)
    }

    def "should convert java.util.Date TO json in ISO format"() {
        given:
        Date date = new Date();
        JsonConverter jsonConverter = jsonConverter().build()

        when:

        String json = jsonConverter.toJson(date)

        then:
        json == "\"${sdfIso.format(date)}\""
    }

    def "should convert java.util.Date FROM json in ISO format"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        Date date = jsonConverter.fromJson('"2014-01-12T20:04:48+0100"', Date)

        then:
        date == sdfIso.parse("2014-01-12T20:04:48+0100")
    }
}
