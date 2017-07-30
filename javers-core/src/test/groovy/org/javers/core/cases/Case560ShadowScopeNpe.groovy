package org.javers.core.cases

import org.bson.types.ObjectId
import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

class Case560ShadowScopeNpe extends Specification {

    def "should not throw NPE while getting Shadows "(){
        given:
        def javers = JaversBuilder.javers().build()
        def id = ObjectId.get()
        def entity = new MongoStoredEntity(id, "alg1", "1.0", "name")
        javers.commit(id.toString(),entity)

        when:
        def query = QueryBuilder.byInstanceId(id, MongoStoredEntity.class).withScopeCommitDeep().build();
        def shadows = javers.findShadows(query)

        then:
        shadows
    }
}
