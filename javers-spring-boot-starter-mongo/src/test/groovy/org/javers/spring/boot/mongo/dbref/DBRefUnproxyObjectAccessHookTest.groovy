package org.javers.spring.boot.mongo.dbref

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceId
import org.javers.spring.boot.mongo.BaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.convert.LazyLoadingProxy
import spock.lang.Unroll

class DBRefUnproxyObjectAccessHookTest extends BaseSpecification {

    @Autowired
    MyDummyEntityRepository dummyEntityRepository

    @Autowired
    MyDummyRefEntityRepository dummyRefEntityRepository

    @Autowired
    Javers javers

    @Unroll
    def "should unproxy a LazyLoadingProxy of DBRef before #commitKind commit to JaVers"() {
        given:
        def refEntity = new MyDummyRefEntity(name: "bert")
        refEntity = dummyRefEntityRepository.save(refEntity)

        def author = new MyDummyEntity(refEntity: refEntity, name: "kaz")
        author = dummyEntityRepository.save(author)

        def loaded = dummyEntityRepository.findById(author.getId()).get()
        assert loaded.refEntity instanceof LazyLoadingProxy

        when:
        loaded.name = "mad kaz"
        commit(loaded, javers, dummyEntityRepository)

        def authorSnapshot = javers.getLatestSnapshot(author.id, MyDummyEntity)
        def refEntitySnapshot = javers.getLatestSnapshot(refEntity.id, MyDummyRefEntity)

        then:
        refEntitySnapshot.isPresent()
        authorSnapshot.get().getPropertyValue("name") == "mad kaz"
        authorSnapshot.get().getPropertyValue("refEntity") instanceof InstanceId
        authorSnapshot.get().getPropertyValue("refEntity").value() == MyDummyRefEntity.name + "/" + refEntity.id

        where:
        commitKind << ["direct", "AOP"]
        commit     << [
                {loaded_, javers_, dummyEntityRepository_ -> javers_.commit("me", loaded_)},
                {loaded_, javers_, dummyEntityRepository_ -> dummyEntityRepository_.save(loaded_)}
        ]
    }
}
