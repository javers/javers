package org.javers.repository.sql.domain

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.commit.Commit
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.core.json.JsonConverterBuilder
import org.javers.core.model.DummyUser
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.SqlRepositoryTestBuilder
import org.joda.time.LocalDateTime
import spock.lang.Specification

import java.sql.Connection
import java.sql.DriverManager


class CommitRepositoryTest extends Specification {
    
    def "should save commit"() {

        given:
        def jsonConverter = JsonConverterBuilder.jsonConverter().build()

        def connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/pawel.szymczyk", "pawel.szymczyk", "");
        connection.setAutoCommit(false);

        def builder = SqlRepositoryTestBuilder.sqlRepository().withConnectionProvider(new ConnectionProvider() {
            @Override
            Connection getConnection() {
                connection
            }
        }).withDialect(DialectName.POSTGRES).withJSONConverter(jsonConverter)

        builder.build()

        def commitRepository = builder.getComponent(CommitRepository)

        when:
        def id = commitRepository.save(new CommitMetadata("author", LocalDateTime.now(), new CommitId(1L,0)))
        connection.commit()

        then:
        true

//        then:
//        globalIdRepository.get(id).id == "kazik"
//        globalIdRepository.get(id).class == DummyUser
//
//        when:
//        def nextId = globalIdRepository.save(globalId)
//
//        then:
//        nextId == id

    }
}
