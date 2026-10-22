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

    def "default prettyPrint() should be an alias of prettyPrint(Order.NORMAL)"() {
        given:
        def changes = threeCommitsOfChanges()

        expect:
        changes.prettyPrint() == changes.prettyPrint(Changes.Order.NORMAL)
    }

    def "prettyPrint(NORMAL) should list commits newest first"() {
        given:
        def changes = threeCommitsOfChanges()

        when:
        def printed = changes.prettyPrint(Changes.Order.NORMAL)
        def commitIds = changes.groupByCommit()*.commit*.id*.value()

        then:
        // newest commit (highest id/version) comes first in the printout
        commitIds == commitIds.sort(false).reverse()
        printed.indexOf(commitIds[0]) < printed.indexOf(commitIds[-1])
    }

    def "prettyPrint(CHRONOLOGICAL) should list commits oldest first"() {
        given:
        def changes = threeCommitsOfChanges()

        when:
        def printed = changes.prettyPrint(Changes.Order.CHRONOLOGICAL)
        def commitIdsNormalOrder = changes.groupByCommit()*.commit*.id*.value()
        def oldestCommitId = commitIdsNormalOrder[-1]
        def newestCommitId = commitIdsNormalOrder[0]

        then:
        printed.indexOf(oldestCommitId) < printed.indexOf(newestCommitId)
    }

    def "prettyPrint(CHRONOLOGICAL) should be the exact reverse of prettyPrint(NORMAL) at the commit-group level"() {
        given:
        def changes = threeCommitsOfChanges()

        expect:
        changes.groupByCommit()*.commit.id ==
                changes.groupByCommit().reverse()*.commit.id.reverse()
    }

    def "a single commit should print the same way regardless of order"() {
        given:
        def javers = javers().build()
        javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        expect:
        changes.prettyPrint(Changes.Order.NORMAL) == changes.prettyPrint(Changes.Order.CHRONOLOGICAL)
    }

    def "an empty Changes list should print just the header, regardless of order"() {
        given:
        def javers = javers().build()
        def changes = javers.findChanges(QueryBuilder.byInstanceId(999, SnapshotEntity).build())

        expect:
        changes.isEmpty()
        changes.prettyPrint(Changes.Order.NORMAL) == "Changes:\n"
        changes.prettyPrint(Changes.Order.CHRONOLOGICAL) == "Changes:\n"
    }
}
