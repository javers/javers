package org.javers.spring.sql

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import org.javers.core.Javers
import org.javers.core.json.JsonTypeAdapter
import org.javers.spring.boot.DummyEntity
import org.javers.spring.boot.TestApplication
import org.javers.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.transaction.Transactional

import static org.javers.repository.jql.QueryBuilder.byInstanceId

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication, CustomJsonTypeAdapter])
@ActiveProfiles("test")
@Transactional
class JaversSqlStarterIntegrationTest extends Specification {

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should build default javers instance with auto-audit aspect"() {
        when:
        def entity = dummyEntityRepository.save(DummyEntity.random())
        assert dummyEntityRepository.getOne(entity.id)

        def snapshots = javers.findSnapshots(byInstanceId(entity.id, DummyEntity).build())

        then:
        snapshots.size() == 1
        snapshots[0].commitMetadata.properties.size() == 1

        and: "should support deprecated CommitPropertiesProvider.provide() "
        snapshots[0].commitMetadata.properties["deprecated commitPropertiesProvider.provide()"] == "still works"
        snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should call auto-audit aspect when saving iterable "(){
      given:
      List entities = (1..5).collect{ DummyEntity.random()}

      when:
      List persisted = dummyEntityRepository.saveAll(entities)

      then:
      persisted.collect {p -> javers.getLatestSnapshot(p.id, DummyEntity)}
               .each {s -> assert s.isPresent() }
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
