package org.javers.core.json

import org.javers.core.json.JsonConverter
import spock.lang.Specification

import java.math.RoundingMode

import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class JsonConverterBasicTest extends Specification{

    def shouldConvertIntToJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        String json = jsonConverter.toJson(12)

        then:
        json == "12"
    }

    def void shouldConvertIntFromJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        int value = jsonConverter.fromJson("12",Integer.class)

        then:
        value == 12
    }
    
    def void shouldConvertDoubleToJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        double value = 1/3D
        String json = jsonConverter.toJson(value)

        then:
        json == "0.3333333333333333"
    }

    
    def void shouldConvertDoubleFromJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        double value = jsonConverter.fromJson("0.3333333333333333", Double)

        then:
        value == 1/3D
    }

    def void shouldConvertBigDecimalToJson(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        BigDecimal value= new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
        String json = jsonConverter.toJson(value)

        then:
        json == "22.220"
    }

    def void shouldConvertBigDecimalFromJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        BigDecimal value = jsonConverter.fromJson("22.220",BigDecimal.class)

        then:
        value == new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
    }

    def void shouldConvertNullToJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        String json = jsonConverter.toJson(null)

        then:
        json == "null"
    }

    
    def void shouldConvertNullFromJson() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        Integer value = jsonConverter.fromJson("null", Integer.class)

        then:
        value == null
    }
}
