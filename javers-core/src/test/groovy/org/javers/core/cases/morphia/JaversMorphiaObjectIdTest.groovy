package org.javers.core.cases.morphia

import org.bson.types.ObjectId
import org.javers.core.JaversBuilder
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JaversMorphiaObjectIdTest extends Specification {
    def "should ..."() {
        given:
        def javers = JaversBuilder.javers().build();

        def id = ObjectId.get();

        def entity1 = new TopLevelEntity(id, "alg1", "1.0", "name1");
        def entity2 = new TopLevelEntity(id, "alg1", "1.0", "name1");
        entity2.setDescription("A new description");

        when:
        def diff = javers.compare(entity1, entity2)
        println("diff: " + javers.toJson(diff))

        then:
        diff.getPropertyChanges("description").size() == 1
    }
}
