package org.javers.core.json

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
        def json = jsonConverter.toJson(12)

        then:
        json == "12"
    }

    def shouldConvertIntFromJson() {
        when:
        int value = jsonConverter.fromJson("12",Integer.class)

        then:
        value == 12
    }
    
    def shouldConvertDoubleToJson() {
        when:
        double value = 1/3D
        def json = jsonConverter.toJson(value)

        then:
        json == "0.3333333333333333"
    }

    
    def shouldConvertDoubleFromJson() {
        when:
        def value = jsonConverter.fromJson("0.3333333333333333", Double)

        then:
        value == 1/3D
    }

    def shouldConvertBigDecimalToJson(){
        when:
        def value= new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
        def json = jsonConverter.toJson(value)

        then:
        json == "22.220"
    }

    def shouldConvertBigDecimalFromJson() {
        when:
        def value = jsonConverter.fromJson("22.220",BigDecimal.class)

        then:
        value == new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
    }

    def shouldConvertNullToJson() {
        when:
        def json = jsonConverter.toJson(null)

        then:
        json == "null"
    }

    
    def shouldConvertNullFromJson() {
        when:
        def value = jsonConverter.fromJson("null", Integer.class)

        then:
        value == null
    }
}
