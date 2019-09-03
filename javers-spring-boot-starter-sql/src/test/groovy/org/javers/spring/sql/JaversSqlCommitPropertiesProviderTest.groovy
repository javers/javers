package org.javers.spring.sql

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.boot.*
import org.javers.spring.boot.sql.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import javax.transaction.Transactional
import java.util.function.Predicate

import static org.javers.repository.jql.QueryBuilder.byInstanceId

/**
 * @author Oai Ha
 */
@SpringBootTest(classes = [TestApplicationWithComplexPropertiesProvider])
@ActiveProfiles("test")
@Transactional
class JaversSqlCommitPropertiesProviderTest extends Specification {

    @Autowired
    Javers javers

    @Autowired
    EmployeeRepositoryWithJavers employeeRepositoryWithJavers

    @Autowired
    ShallowEntityRepository shallowEntityRepository

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def """should commit and query with properties provided by CommitPropertiesProvider
           when saving to audited Repository"""() {
        when:
        def employeeEntity = new EmployeeEntity(id:UUID.randomUUID())
        employeeRepositoryWithJavers.save(employeeEntity)

        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject()
                .withCommitProperty("employeeId", employeeEntity.id.toString()).build())

        then:
        snapshots.size() == 1
        snapshots[0].globalId.typeName == EmployeeEntity.name
        with(snapshots[0].commitMetadata) {
            assert it.properties.size() == 2
            assert it.properties["employeeId"] == employeeEntity.id.toString()
            assert it.properties["commit"] == "seems fine"
        }

        when:
        def shallowEntity = new ShallowEntity(id:1, value: "kaz")
        shallowEntityRepository.save(shallowEntity)

        snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject()
                .withCommitProperty("ShallowEntity.value", "kaz").build())

        then:
        snapshots.size() == 1
        snapshots[0].globalId.typeName == ShallowEntity.name
        with(snapshots[0].commitMetadata) {
            assert it.properties.size() == 2
            assert it.properties["ShallowEntity.value"] == "kaz"
            assert it.properties["commit"] == "seems fine"
        }
    }

    def """should commit and query with properties provided by CommitPropertiesProvider
           when deleting from audited Repository"""() {
        when:
        def employeeEntity = new EmployeeEntity(id:UUID.randomUUID())
        def jFreshEmployee = employeeRepositoryWithJavers.save(employeeEntity)
        employeeRepositoryWithJavers.delete(jFreshEmployee)

        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject()
                .withCommitProperty("deleted employeeId", employeeEntity.id.toString()).build())

        then:
        snapshots.size() == 1
        snapshots[0].globalId.typeName == EmployeeEntity.name
        with(snapshots[0].commitMetadata) {
            assert it.properties.size() == 2
            assert it.properties["deleted employeeId"] == employeeEntity.id.toString()
            assert it.properties["commit"] == "seems fine"
        }
    }

    def """should commit and query with properties provided by CommitPropertiesProvider
           when saving to audited Repository with saveAll()"""() {
        when:
        def dept1 = new DepartmentEntity(id: UUID.randomUUID())
        def dept2 = new DepartmentEntity(id: UUID.randomUUID())

        def employees = []
        4.times { employees.add(new EmployeeEntity(id:UUID.randomUUID(), department: dept1)) }
        4.times { employees.add(new EmployeeEntity(id:UUID.randomUUID(), department: dept2)) }

        def save = employeeRepositoryWithJavers.saveAll(employees)

        def employeeSnapshots = javers.findSnapshots(QueryBuilder.byClass(EmployeeEntity)
                .withCommitProperty("departmentId", dept1.id.toString()).build())

        then:
        employeeSnapshots.size() == 4
        employeeSnapshots.each {
            it.getPropertyValue("department").cdoId == dept1.id
        }
    }

    def """should commit and query with properties provided by CommitPropertiesProvider
           when deleting from audited Repository by Id"""() {
        when:
        def employee = employeeRepositoryWithJavers.save(
                new EmployeeEntity(id:UUID.randomUUID()))
        employeeRepositoryWithJavers.deleteById(employee.id)

        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject()
                .withCommitProperty("employee deletedById", employee.id.toString()).build())

        then:
        snapshots.size() == 1
        snapshots[0].globalId.typeName == EmployeeEntity.name
        with(snapshots[0].commitMetadata) {
            assert it.properties["employee deletedById"] == employee.id.toString()
        }
    }
}
