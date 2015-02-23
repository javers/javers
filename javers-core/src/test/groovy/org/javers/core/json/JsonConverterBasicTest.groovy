package org.javers.core.json

import org.javers.core.JaversTestBuilder
import spock.lang.Shared
import spock.lang.Specification

import java.math.RoundingMode

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class JsonConverterBasicTest extends Specification{

    @Shared
    def jsonConverter = javersTestAssembly().jsonConverter

    def shouldConvertIntToJson() {
        when:
        String json = jsonConverter.toJson(12)

        then:
        json == "12"
    }

    def void shouldConvertIntFromJson() {
        when:
        int value = jsonConverter.fromJson("12",Integer.class)

        then:
        value == 12
    }
    
    def void shouldConvertDoubleToJson() {
        when:
        double value = 1/3D
        String json = jsonConverter.toJson(value)

        then:
        json == "0.3333333333333333"
    }

    
    def void shouldConvertDoubleFromJson() {
        when:
        double value = jsonConverter.fromJson("0.3333333333333333", Double)

        then:
        value == 1/3D
    }

    def void shouldConvertBigDecimalToJson(){
        when:
        BigDecimal value= new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
        String json = jsonConverter.toJson(value)

        then:
        json == "22.220"
    }

    def void shouldConvertBigDecimalFromJson() {
        when:
        BigDecimal value = jsonConverter.fromJson("22.220",BigDecimal.class)

        then:
        value == new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
    }

    def void shouldConvertNullToJson() {
        when:
        String json = jsonConverter.toJson(null)

        then:
        json == "null"
    }

    
    def void shouldConvertNullFromJson() {
        when:
        Integer value = jsonConverter.fromJson("null", Integer.class)

        then:
        value == null
    }
}
