package org.javers.spring.boot.mongo.dbref


import org.javers.spring.boot.mongo.DBRefUnproxyObjectAccessHook
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

    def "should unproxy and load"() {
        given:

        def refEntity = new MyDummyRefEntity()
        refEntity.setName("Bert")
        refEntity = dummyRefEntityRepository.save(refEntity);

        def author = new MyDummyEntity();
        author.setRefEntity(refEntity)
        author = dummyEntityRepository.save(author)

        def loaded = dummyEntityRepository.findById(author.getId()).get()
        println loaded
        println loaded.class
        println loaded.refEntity
        println loaded.refEntity.class

        assert loaded.refEntity instanceof LazyLoadingProxy

        def realObj = ((LazyLoadingProxy) loaded.refEntity).getTarget()

        assert realObj instanceof MyDummyRefEntity
        assert "Bert".equals(realObj.name)
        assert "Bert".equals(loaded.refEntity.name)
    }

    def "should unproxy and load via hook"() {
        given:

        def refEntity = new MyDummyRefEntity()
        refEntity.setName("Bert")
        refEntity = dummyRefEntityRepository.save(refEntity);

        def author = new MyDummyEntity();
        author.setRefEntity(refEntity)
        author = dummyEntityRepository.save(author)

        def loaded = dummyEntityRepository.findById(author.getId()).get()

        def hook = new DBRefUnproxyObjectAccessHook()
        hook.createAccessor(loaded.refEntity).ifPresent { access ->
            def realObj = access.access()
            assert "Bert".equals(realObj.name)
            assert !(realObj instanceof LazyLoadingProxy)
            assert realObj instanceof MyDummyRefEntity
        }
    }
}
