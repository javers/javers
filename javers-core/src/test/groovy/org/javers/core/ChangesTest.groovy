package org.javers.core

import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers

/**
 * Tests for {@link Changes#prettyPrint(Changes.Order)}.
 */
class ChangesTest extends Specification {

    private static Changes threeCommitsOfChanges() {
        def javers = javers().build()
        def entity = new SnapshotEntity(id: 1, intProperty: 0)
        javers.commit("author", entity)

        entity.intProperty = 1
        javers.commit("author", entity)

        entity.intProperty = 2
        javers.commit("author", entity)

        entity.intProperty = 3
        javers.commit("author", entity)

        javers.findChanges(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
    }

    def "default prettyPrint() should be an alias of prettyPrint(Order.DESC_BY_DATE)"() {
        given:
        def changes = threeCommitsOfChanges()

        expect:
        changes.prettyPrint() == changes.prettyPrint(Changes.Order.DESC_BY_DATE)
    }

    def "prettyPrint(DESC_BY_DATE) should list commits newest first"() {
        given:
        def changes = threeCommitsOfChanges()

        when:
        def printed = changes.prettyPrint(Changes.Order.DESC_BY_DATE)
        def commitIds = changes.groupByCommit()*.commit*.id*.value()

        then:
        // newest commit (highest id/version) comes first in the printout
        commitIds == commitIds.sort(false).reverse()
        printed.indexOf(commitIds[0]) < printed.indexOf(commitIds[-1])
    }

    def "prettyPrint(ASC_BY_DATE) should list commits oldest first"() {
        given:
        def changes = threeCommitsOfChanges()

        when:
        def printed = changes.prettyPrint(Changes.Order.ASC_BY_DATE)
        def oldestCommit = changes.groupByCommit()[-1]
        def newestCommit = changes.groupByCommit()[0]

        then:
        def oldestCommitIdx = printed.indexOf(oldestCommit.prettyPrint())
        oldestCommitIdx >= 0 // make sure commitId was actually found
        def newestCommitIdx = printed.indexOf(newestCommit.prettyPrint())
        newestCommitIdx >= 0
        oldestCommitIdx < newestCommitIdx
    }

    def "a single commit should print the same way regardless of order"() {
        given:
        def javers = javers().withInitialChanges(true).build()
        javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        expect:
        changes.size() == 2
        changes.prettyPrint(Changes.Order.DESC_BY_DATE) == changes.prettyPrint(Changes.Order.ASC_BY_DATE)
    }

    def "an empty Changes list should print just the header, regardless of order"() {
        given:
        def javers = javers().build()
        def changes = javers.findChanges(QueryBuilder.byInstanceId(999, SnapshotEntity).build())

        expect:
        changes.isEmpty()
        changes.prettyPrint(Changes.Order.DESC_BY_DATE) == "Changes:\n"
        changes.prettyPrint(Changes.Order.ASC_BY_DATE) == "Changes:\n"
    }
}
