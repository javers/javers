package org.javers.spring.boot.mongo.dbref

import org.javers.core.Changes
import org.javers.core.Javers
import org.javers.repository.jql.JqlQuery
import org.javers.spring.boot.mongo.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static org.javers.repository.jql.QueryBuilder.byInstanceId

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class Case959FindChangesContainsDBRefChanges extends Specification {

    @Autowired
    MyDummyEntityRepository dummyEntityRepository

    @Autowired
    MyDummyRefEntityRepository dummyRefEntityRepository

    @Autowired
    Javers javers

    def "should return changes for parent object containing changes in @DBRef objects"() {
        given: "there is parent ref object"
            def refEntity = new MyDummyRefEntity(name: "bert")
            refEntity = dummyRefEntityRepository.save(refEntity)

        and: "there is parent object that contains ref object"
            def author = new MyDummyEntity(refEntity: refEntity, name: "kaz")
            author = dummyEntityRepository.save(author)

        and: "parent object is updated"
            refEntity.name = "hubert"
            dummyRefEntityRepository.save(refEntity)

        and: "ref object is updated"
            author.name = "kazik"
            dummyEntityRepository.save(author)

        when: "find changes for parent object is executed"
            JqlQuery query = byInstanceId(author.id, MyDummyEntity.class)
                    .limit(Integer.MAX_VALUE)
                    .build()
            Changes changes = javers.findChanges(query)

        then: "changes contains two elements (one of parent object and one of ref object)"
            changes.size() == 2
    }
}
