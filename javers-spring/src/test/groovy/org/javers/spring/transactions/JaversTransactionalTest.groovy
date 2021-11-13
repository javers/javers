package org.javers.spring.transactions

import org.javers.core.Javers
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Entity
class User {
    @Id
    String name

    User() {
        this.name = UUID.randomUUID().toString()
    }
}

@Service
class UberService {
    @Autowired
    private UserService userService

    User createUserAndFail() {
        User u = new User()
        try {
            userService.doJaversCommitThanFail(u)
        } catch (Exception e) {
            println(e.class.name + " : " + e.message)
        }
        u
    }

    User createUser() {
        User u = new User()
        userService.doJaversCommit(u)
        u
    }
}

@Service
class UserService {
    @Autowired
    private Javers javers

    @Transactional
    def doJaversCommitThanFail(User d) {
        javers.commit("a", d)
        throw new RuntimeException("Rollback test!")
    }

    @Transactional
    def doJaversCommit(User d) {
        javers.commit("a", d)
    }
}

abstract class JaversTransactionalTest extends Specification {

    @Autowired
    private Javers javers

    @Autowired
    private UberService uberService

    def "should rollback a javers commit on application's transaction rollback" () {
        given:
        def commit1 = javers.commit("a", new User())
        println "commit1 " + commit1.id.value()

        when:
        def user = uberService.createUserAndFail()

        then:
        ! javers.findSnapshots(QueryBuilder.byInstance(user).build())
        def commit2 = javers.commit("a", new User())
        println "commit2 " + commit2.id.value()
        commit2.id.majorId == commit1.id.majorId+1
    }

    def "should do javers commit when application's transaction is committed" () {
        when:
        def user = uberService.createUser()

        then:
        javers.findSnapshots(QueryBuilder.byInstance(user).build())
    }
}
