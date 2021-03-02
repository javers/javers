package org.javers.core.cases

import org.bson.types.ObjectId
import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/64
 *
 * @author bartosz walacik
 */
class JaversMorphiaObjectIdTest extends Specification {
    def "should compare Entities with Morphia ObjectId as @Id"() {
        given:
        def javers = JaversBuilder.javers().build();

        def id = ObjectId.get();

        def entity1 = new MongoStoredEntity(id, "alg1", "1.0", "name1");
        def entity2 = new MongoStoredEntity(id, "alg1", "1.0", "name1");
        entity2.setDescription("A new description");

        when:
        def diff = javers.compare(entity1, entity2)
        println("diff: " + javers.getJsonConverter().toJson(diff))

        then:
        diff.getPropertyChanges("_description").size() == 1
    }

    def "should allow committing and querying by Morphia ObjectId"() {
        given:
        def javers = JaversBuilder.javers().build();
        def id1 = ObjectId.get();
        def entity1 = new MongoStoredEntity(id1, "alg1", "1.0", "name1");

        def id2 = ObjectId.get();
        def entity2 = new MongoStoredEntity(id2, "alg1", "1.0", "name2");

        javers.commit("author",entity1)
        def commit = javers.commit("author",entity2)
        //println (javers.jsonConverter.toJson(commit.snapshots))

        when:
        def list = javers.findSnapshots(QueryBuilder.byInstanceId(id2, MongoStoredEntity).build())

        then:
        commit.snapshots.size() == 1
        commit.snapshots[0].globalId.cdoId == id2
        list.size() == 1
        list[0].getPropertyValue("_name") == "name2"
        list[0].globalId.cdoId == id2
    }
}
