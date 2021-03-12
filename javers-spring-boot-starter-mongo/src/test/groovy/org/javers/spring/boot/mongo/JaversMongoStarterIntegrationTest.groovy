package org.javers.spring.boot.mongo

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.javers.core.Javers
import org.javers.core.json.JsonTypeAdapter
import org.javers.core.metamodel.type.EntityType
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication, CustomJsonTypeAdapter])
@ActiveProfiles("test")
class JaversMongoStarterIntegrationTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default javers instance with auto-audit aspect" () {
        when:
        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["dummyEntityId"] == dummyEntity.id + ""
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should scan given packages for classes with @TypeName"() {
        expect:
        javers.getTypeMapping("AnotherEntity") instanceof EntityType
    }

    def "should register custom json type adapter from spring context"() {
        expect:
        javers.jsonConverter.toJson(new DummyCustomTypeEntity(BigDecimal.TEN)) == "[10]"
    }

    private static class DummyCustomTypeEntity {
        BigDecimal value

        DummyCustomTypeEntity(BigDecimal value) {
            this.value = value
        }
    }

    private static class CustomJsonTypeAdapter implements JsonTypeAdapter<DummyCustomTypeEntity> {

        @Override
        DummyCustomTypeEntity fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
            return new DummyCustomTypeEntity((json as JsonArray).get(0).asBigDecimal)
        }

        @Override
        JsonElement toJson(DummyCustomTypeEntity sourceValue, JsonSerializationContext jsonSerializationContext) {
            return new JsonArray().tap {
                add(sourceValue.value)
            }
        }

        @Override
        List<Class> getValueTypes() {
            return [DummyCustomTypeEntity]
        }
    }
}
