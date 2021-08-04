package org.javers.repository.sql

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.jql.QueryBuilder

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
        def changes = javers.findChanges(QueryBuilder.byInstanceId(1, Entity)
            .withChildValueObjects()
            .withScopeDeepPlus()
            .build())

        then:
        def actual = changes.prettyPrint()
            .replaceAll("\\d{2} [a-zA-Z]{3} \\d{4}, (\\d{2}:?){3}", "02 Aug 2021, 00:00:00")

        actual == '''\
        Changes:
        Commit 4.00 done by author at 02 Aug 2021, 00:00:00 :
        * changes on org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1 :
          - 'values' collection changes :
             0. '...ListItemRemovalAndReadditionTest$Entity/1#values/0' added
          - 'values/0.name' = 'name'
        Commit 3.00 done by author at 02 Aug 2021, 00:00:00 :
        * changes on org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1 :
          - 'values' collection changes :
             0. '...ListItemRemovalAndReadditionTest$Entity/1#values/0' removed
        Commit 2.00 done by author at 02 Aug 2021, 00:00:00 :
        * changes on org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1 :
          - 'values' collection changes :
             0. '...ListItemRemovalAndReadditionTest$Entity/1#values/0' added
          - 'values/0.name' = 'name'
        Commit 1.00 done by author at 02 Aug 2021, 00:00:00 :
        * new object: org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1
        '''.stripIndent()

//        Use this assertion to see passing test
//        As you can see Commit 4 has info only about some item being added to the list but zero
//        info about what actually was added in opposition to Commit 2.
//        (if class Value had another property and value of this property was changed to something else
//        before re-adding item to the list then this change would be visible in Commit 4,
//        this suggests that diff is performed between Commit 4.00 and Commit 2.00 and removal at the Commit 3.00
//        is simply ignored)
//
//        actual == '''\
//        Changes:
//        Commit 4.00 done by author at 02 Aug 2021, 00:00:00 :
//        * changes on org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1 :
//          - 'values' collection changes :
//             0. '...ListItemRemovalAndReadditionTest$Entity/1#values/0' added
//        Commit 3.00 done by author at 02 Aug 2021, 00:00:00 :
//        * changes on org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1 :
//          - 'values' collection changes :
//             0. '...ListItemRemovalAndReadditionTest$Entity/1#values/0' removed
//        Commit 2.00 done by author at 02 Aug 2021, 00:00:00 :
//        * changes on org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1 :
//          - 'values' collection changes :
//             0. '...ListItemRemovalAndReadditionTest$Entity/1#values/0' added
//          - 'values/0.name' = 'name'
//        Commit 1.00 done by author at 02 Aug 2021, 00:00:00 :
//        * new object: org.javers.repository.sql.ListItemRemovalAndReadditionTest$Entity/1
//        '''.stripIndent()
    }
}
