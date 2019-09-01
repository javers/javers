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
@SpringBootTest(classes = [TestApplication])
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

    def "should commit with properties provided by CommitPropertiesProvider on audited crudRepository.save(Object) and query for the main object from commit properties"() {
        when:
        def jEmployee = createEmployee()
        def jFreshEmployee = employeeRepositoryWithJavers.save(jEmployee)
        println(jFreshEmployee)

        def snapshots = javers.findSnapshots(QueryBuilder.byClass(EmployeeEntity.class)
                .withCommitProperty("departmentId", jFreshEmployee.department.id.toString()).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["departmentId"] == jFreshEmployee.department.id.toString()
        assert snapshots[0].commitMetadata.properties["employeeId"] == jFreshEmployee.id.toString()
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should commit with properties provided by CommitPropertiesProvider on audited crudRepository.save(Object) and query for the tracked linked object from commit properties"() {
        when:
        def jEmployee = createEmployee()
        def jFreshEmployee = employeeRepositoryWithJavers.save(jEmployee)
        println(jFreshEmployee)

        def snapshots = javers.findSnapshots(QueryBuilder.byClass(EmployeeEntity.class)
                .withCommitProperty("departmentId", jFreshEmployee.department.id.toString()).build())
        def department = javers.findSnapshots(byInstanceId(snapshots[0].commitMetadata.properties["departmentId"], DepartmentEntity.class).build())

        then:
        assert department.size() == 1
        // Here the commit metadata still share the same with the snaphot one.
        assert department[0].commitMetadata.properties["departmentId"] == jFreshEmployee.department.id.toString()
        assert department[0].commitMetadata.properties["employeeId"] == jFreshEmployee.id.toString()
        assert department[0].commitMetadata.author == "unauthenticated"
    }

    def "should commit with properties provided by CommitPropertiesProvider on audited crudRepository.save(Object) with different commit properties map"() {
        when:
        def shallow = shallowEntityRepository.save(ShallowEntity.random())

        def entity = DummyEntity.random()
        entity.name = "a"
        entity.shallowEntity = shallow
        dummyEntityRepository.save(entity)

        def dummySnapshots = javers.findSnapshots(QueryBuilder.byClass(DummyEntity.class).withCommitProperty("shallowId", shallow.id.toString()).build())
        def shallowIdFromCommitProperties = dummySnapshots[0].commitMetadata.properties["shallowId"]
        def verifyShallow = javers.findSnapshots(byInstanceId(Integer.valueOf(shallowIdFromCommitProperties), ShallowEntity.class).build())

        then:
        assert verifyShallow.size() == 1
        assert verifyShallow[0].commitMetadata.properties["key"] == "ok"
        assert verifyShallow[0].commitMetadata.author == "unauthenticated"
    }

    def "should commit with properties provided by CommitPropertiesProvider on audited crudRepository.delete(Object)"() {
        when:
        def jEmployee = createEmployee()
        def jFreshEmployee = employeeRepositoryWithJavers.save(jEmployee)
        employeeRepositoryWithJavers.delete(jFreshEmployee)

        def snapshots = javers.findSnapshots(QueryBuilder.byClass(EmployeeEntity.class)
                .withCommitProperty("departmentId", jFreshEmployee.department.id.toString()).build())

        then:
        assert snapshots.size() == 2
        assert snapshots[0].commitMetadata.properties.size() == 1
        assert snapshots[0].commitMetadata.properties["departmentId"] == jFreshEmployee.department.id.toString()
        assert snapshots[0].commitMetadata.author == "unauthenticated"
        assert snapshots[1].commitMetadata.properties.size() == 2
        assert snapshots[1].commitMetadata.properties["departmentId"] == jFreshEmployee.department.id.toString()
        assert snapshots[1].commitMetadata.properties["employeeId"] == jFreshEmployee.id.toString()
        assert snapshots[1].commitMetadata.author == "unauthenticated"
    }

    def "should commit with properties provided by CommitPropertiesProvider on audited crudRepository.saveAll(Object)"() {
        when:
        def empWithDepartment = employeeRepositoryWithJavers.save(createEmployee())
        def employees = []
        for (int i = 0; i < 3; i++) {
            def entity = createEmployee()
            entity.department = empWithDepartment.department
            employees[i] = entity
        }
        def save = employeeRepositoryWithJavers.saveAll(employees)
        save.add(empWithDepartment)

        def employeeSnapshots = javers.findSnapshots(QueryBuilder.byClass(EmployeeEntity.class)
                .withCommitProperty("departmentId", save.get(0).department.id.toString()).build())

        then:
        assert employeeSnapshots.size() == 4
        assert match(save, employeeSnapshots[0].commitMetadata.properties["employeeId"])
        assert match(save, employeeSnapshots[1].commitMetadata.properties["employeeId"])
        assert match(save, employeeSnapshots[2].commitMetadata.properties["employeeId"])
        assert match(save, employeeSnapshots[3].commitMetadata.properties["employeeId"])
    }

    def "should commit with properties provided by CommitPropertiesProvider on audited crudRepository.deleteById(Object)"() {
        when:
        def empWithDepartment = employeeRepositoryWithJavers.save(createEmployee())
        employeeRepositoryWithJavers.deleteById(empWithDepartment.id)

        def employeeSnapshots = javers.findSnapshots(QueryBuilder.byClass(EmployeeEntity.class)
                .withCommitProperty("employeeId", empWithDepartment.id.toString()).build())

        then:
        assert employeeSnapshots.size() == 2
        assert employeeSnapshots[0].commitMetadata.properties.size() == 1
        assert employeeSnapshots[0].commitMetadata.properties["employeeId"] == empWithDepartment.id.toString()
        assert employeeSnapshots[1].commitMetadata.properties.size() == 2
        assert employeeSnapshots[1].commitMetadata.properties["employeeId"] == empWithDepartment.id.toString()
        assert employeeSnapshots[1].commitMetadata.properties["departmentId"] == empWithDepartment.department.id.toString()
    }

    boolean match(List<EmployeeEntity> employees, String id) {
        return employees.stream().filter(new Predicate<EmployeeEntity>() {
            @Override
            boolean test(EmployeeEntity e) {
                return e.id.toString() == id
            }
        }).count() == 1
    }

    EmployeeEntity createEmployee() {
        DepartmentEntity departmentEntity = new DepartmentEntity()
        EmployeeEntity employeeEntity = new EmployeeEntity()
        employeeEntity.setId(UUID.randomUUID())
        employeeEntity.setDepartment(departmentEntity)
        employeeEntity
    }
}
