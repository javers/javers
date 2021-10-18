package org.javers.core.cases


import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * see https://github.com/javers/javers/issues/1121
 *
 * @author narsereg
 */
class Case1121VersionNotNumber extends Specification {
    class Clazz {
    }

     def "should Not Fail When Select Only Deleted Changes"() {
         given:
         def javers = JaversBuilder.javers().build()
         javers.commitShallowDelete("me", new Clazz())
         def query = QueryBuilder.byClass(Clazz)
                 .withChildValueObjects()
                 .build()

         when:
         def changes = javers.findChanges(query)

         then:
         changes.size() == 0

         when:
         def snapshots = javers.findSnapshots(query)

         then:
         snapshots.size() == 1
    }
}
