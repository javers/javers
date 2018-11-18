package org.javers.spring.boot.mongo.DBRef

import org.javers.spring.boot.mongo.DummyEntity
import org.javers.spring.boot.mongo.DummyEntityRepository
import org.javers.spring.boot.mongo.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Ignore
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
@Ignore //TODO
class DBRefUnproxyObjectAccessHookTest extends Specification {

    @Autowired
    DummyEntityRepository dummyEntityRepository

    def "should unproxy and commit DBRef to JaVers" () {
        given:
        def author = new DummyEntity(1, "George", null)
        dummyEntityRepository.save(author)

        def coAuthor = new DummyEntity(2, "Richard", author)
        dummyEntityRepository.save(coAuthor)

        def loaded = dummyEntityRepository.findById(2).get()
        println loaded
        println loaded.class
        println loaded.ref
        println loaded.ref.class

        //assert book.author instanceof HibernateProxy
        //assert !Hibernate.isInitialized(book.author)

        when:
        def x

        //book.author.name = "kazik"
        //ebookRepository.save(book.author)

        then:
        false
        //def snapshot = javers.getLatestSnapshot("1", Author).get()
        //snapshot.getPropertyValue("name") == "kazik"
    }
}
