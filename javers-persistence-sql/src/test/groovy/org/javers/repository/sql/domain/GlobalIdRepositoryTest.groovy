package org.javers.repository.sql.domain

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.json.JsonConverterBuilder
import org.javers.core.model.DummyUser
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryTestBuilder
import spock.lang.Ignore
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager

class GlobalIdRepositoryTest extends Specification {

    @Ignore
    def "should select or insert"() {
        given:

        Javers javers = JaversBuilder.javers().build();
        def instanceId = javers.idBuilder().instanceId(new DummyUser("kazik"))

        def jsonConverter = JsonConverterBuilder.jsonConverter().build()

        def connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/bartosz.galek", "bartosz.galek", "");
        connection.setAutoCommit(false);

        def builder = SqlRepositoryTestBuilder.sqlRepository().withConnectionProvider(new ConnectionProvider() {
            @Override
            Connection getConnection() {
                connection
            }
        }).withDialect(DialectName.POSTGRES).withJSONConverter(jsonConverter)

        builder.build()

        def globalIdRepository = builder.getComponent(GlobalIdRepository)

        when:
        def id = globalIdRepository.save(instanceId)
        connection.commit()

        then:
        true

        then:
        globalIdRepository.get(id).id == "kazik"
        globalIdRepository.get(id).class == DummyUser

        when:
        def nextId = globalIdRepository.save(globalId)

        then:
        nextId == id
    }
}