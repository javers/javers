package org.javers.core.json

import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

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

    def void shouldConvertIntFromJson() {
        when:
        int value = jsonConverter.fromJson("12",Integer.class)

        then:
        value == 12
    }
    
    def void shouldConvertDoubleToJson() {
        when:
        double value = 1/3D
        def json = jsonConverter.toJson(value)

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
        def value= new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
        def json = jsonConverter.toJson(value)

        then:
        json == "22.220"
    }

    def void shouldConvertBigDecimalFromJson() {
        when:
        def value = jsonConverter.fromJson("22.220",BigDecimal.class)

        then:
        value == new BigDecimal(22.22).setScale(3, RoundingMode.HALF_UP)
    }

    def void shouldConvertNullToJson() {
        when:
        def json = jsonConverter.toJson(null)

        then:
        json == "null"
    }

    
    def void shouldConvertNullFromJson() {
        when:
        def value = jsonConverter.fromJson("null", Integer.class)

        then:
        value == null
    }

    @Unroll
    def "should #expectedType.simpleName convert from GlobalIdRawDTO"() {
        when:
        def globalId = jsonConverter.fromDto(dto)

        then:
        globalId.class == expectedType
        globalId.value() == expectedValue


        where:
        dto << [new GlobalIdRawDTO(SnapshotEntity.class.name,"1",null,null),
                new GlobalIdRawDTO(DummyAddress.class.name,null,"/",null),
                new GlobalIdRawDTO(DummyAddress.class.name,null,"address",new GlobalIdRawDTO(SnapshotEntity.class.name,"1",null,null) )
               ]
        expectedType <<  [InstanceId, UnboundedValueObjectId, ValueObjectId]
        expectedValue << [SnapshotEntity.class.name+"/1", DummyAddress.class.name+"/", SnapshotEntity.class.name+"/1#address"]
    }

}
