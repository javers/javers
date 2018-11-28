package org.javers.spring.boot.mongo.dbref

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceId
import org.javers.spring.boot.mongo.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class DBRefUnproxyObjectAccessHookTest extends Specification {

    @Autowired
    MyDummyEntityRepository dummyEntityRepository

    @Autowired
    MyDummyRefEntityRepository dummyRefEntityRepository

    @Autowired
    Javers javers

    def "should unproxy a LazyLoadingProxy of DBRef before direct committing to JaVers"() {
        given:
        def refEntity = new MyDummyRefEntity(name: "bert")
        refEntity = dummyRefEntityRepository.save(refEntity)

        def author = new MyDummyEntity(refEntity: refEntity)
        author = dummyEntityRepository.save(author)

        def loaded = dummyEntityRepository.findById(author.getId()).get()
        assert loaded.refEntity instanceof LazyLoadingProxy

        when:
        javers.commit("me", loaded)
        def authorSnapshot = javers.getLatestSnapshot(author.id, MyDummyEntity)
        def refEntitySnapshot = javers.getLatestSnapshot(refEntity.id, MyDummyRefEntity)

        then:
        refEntitySnapshot.isPresent()
        authorSnapshot.get().getPropertyValue("refEntity") instanceof InstanceId
        authorSnapshot.get().getPropertyValue("refEntity").value() == MyDummyRefEntity.name + "/" + refEntity.id
    }
}
