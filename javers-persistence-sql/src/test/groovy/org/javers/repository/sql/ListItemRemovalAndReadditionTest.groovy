package org.javers.repository.sql

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.jql.QueryBuilder
import org.javers.core.diff.changetype.Atomic
import org.javers.core.diff.changetype.InitialValueChange

import java.sql.Connection
import java.sql.DriverManager

class ListItemRemovalAndReadditionTest extends JaversSqlRepositoryE2ETest {

    @Override
    Connection createConnection() {
        DriverManager.getConnection("jdbc:h2:mem:test")
    }

    @Override
    DialectName getDialect() {
        DialectName.H2
    }

    @Override
    String getSchema() {
        return null
    }

    @Override
    boolean useRandomCommitIdGenerator() {
        false
    }

    static class Value {
        String name;
    }

    static class Entity {
        @Id
        int id;
        List<Value> values;
    }

    def "should treat re-added (previously removed) list item as new"() {
        given:
        def javers = JaversBuilder
            .javers()
            .withDateTimeProvider(prepareDateProvider())
            .registerJaversRepository(repository)
            .build()

        def entity = new Entity()
        entity.id = 1

        def value = new Value()
        value.name = "name"

        javers.commit("author", entity);

        entity.values = [value]
        javers.commit("author", entity)

        entity.values = []
        javers.commit("author", entity)

        entity.values = [value]
        javers.commit("author", entity)

        when:
        def commits = javers.findChanges(QueryBuilder.byInstanceId(1, Entity)
            .withChildValueObjects()
            .withScopeDeepPlus()
            .build())
            .groupByCommit()

        then:
        commits[0].changes.size() == 2
        def changes = new ArrayList(commits[0].changes).sort { it.class.name }

        changes[0] instanceof InitialValueChange
        changes[0].left == null
        changes[0].right == "name"
    }
}
