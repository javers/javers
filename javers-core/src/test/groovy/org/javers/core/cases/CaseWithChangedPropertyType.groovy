package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.joda.time.LocalDateTime
import spock.lang.Specification

import java.time.Instant

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
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, "ModelWithDateTime").build())

        then:
        snapshots.size() == 2

        snapshots[0].getPropertyValue("datetime") == instantNow
        snapshots[1].getPropertyValue("datetime") == null

        when:
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1, "ModelWithDateTime").build())

        then:
        println changes.prettyPrint()
        changes.size() == 1
        changes[0] instanceof ValueChange
    }
}
