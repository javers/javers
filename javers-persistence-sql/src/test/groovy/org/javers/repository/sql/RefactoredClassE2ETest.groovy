package org.javers.repository.sql

import groovy.sql.Sql
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ValueObject

/**
 * E2E test to reproduce NPE that occurs when JaVers tries to commit changes
 * but encounters old snapshot
 *
 * The bug scenario:
 * 1. Original class has property "totals" as Map<String,Object>
 * 2. Class is refactored to use dedicated ValueObject "Totals" for the property
 * 3. Old snapshots still exist in DB with Map structure JSON
 * 4. When JaVers tries to commit new instance, it reads old snapshots for comparison
 * 5. Deserialization fails with NPE in GlobalIdTypeAdapter.parseUnboundedValueObject()
 *    because old JSON doesn't have expected "valueObject" field
 */
class RefactoredClassE2ETest extends H2SqlRepositoryE2ETest {

    static class EntityBeforeRefactoring {
        @Id
        int id
        Map<String, Object> totals
    }

    static class EntityAfterRefactoring {
        @Id
        int id
        Totals totals
    }

    @ValueObject
    static class Totals {
        int revenue
        int cost
    }

    def "should be able to handle commits for refactored class when new type was introduced"() {
        given:
        def entityBeforeRefactoring = new EntityBeforeRefactoring(id: 1, totals: [revenue: 1000, cost: 500])
        javers.commit("user", entityBeforeRefactoring)
        def sql = new Sql(getConnection());
        // Simulate the old snapshot in the database after refactoring has been made
        sql.executeUpdate("""
            UPDATE jv_snapshot 
            SET managed_type = ${EntityAfterRefactoring.class.name}
        """);

        sql.executeUpdate("""
            UPDATE jv_global_id
            SET type_name = ${EntityAfterRefactoring.class.name}
        """)

        when:
        def entityAfterRefactoring = new EntityAfterRefactoring(id: 1, totals: new Totals(revenue: 1500, cost: 600))

        javers.commit("user", entityAfterRefactoring)

        then:
        noExceptionThrown()
    }
}
