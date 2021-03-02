package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import spock.lang.Specification

import java.time.Instant
import java.time.LocalDateTime

import static org.javers.repository.jql.QueryBuilder.*

/**
 * @author https://github.com/lnxmad
 */
class CaseWithChangedPropertyType extends Specification {
    @TypeName("ModelWithDateTime")
    static class Model1 {
        @Id int id

        LocalDateTime datetime
    }

    @TypeName("ModelWithDateTime")
    static class Model2 {
        @Id int id

        Instant datetime
    }

    def "should allow for property type change, from LocalDateTime to Instant"() {
        given:
        def javers = JaversBuilder.javers().build()

        def localDateNow = LocalDateTime.now()
        def instantNow = Instant.now()

        javers.commit("author", new Model1(id: 1, datetime: localDateNow))
        javers.commit("author", new Model2(id: 1, datetime: instantNow))


        when:
        def snapshots = javers.findSnapshots(byInstanceId(1, "ModelWithDateTime").build())

        then:
        snapshots.size() == 2

        snapshots[0].getPropertyValue("datetime") == instantNow
        LocalDateTime.parse(snapshots[1].getPropertyValue("datetime")) == localDateNow

        when:
        def changes = javers.findChanges(byInstanceId(1, "ModelWithDateTime").build())

        then:
        println changes.prettyPrint()
        changes.size() == 1
        changes[0] instanceof ValueChange

        when:
        def shadows = javers.findShadows(byInstanceId(1, "ModelWithDateTime").build())

        then:
        shadows.size() == 2
        shadows[0].get().datetime == instantNow
        shadows[1].get().datetime == null
    }
}
