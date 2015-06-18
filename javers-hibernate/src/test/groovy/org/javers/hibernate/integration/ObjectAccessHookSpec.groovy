package org.javers.hibernate.integration

import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.hibernate.integration.config.JaversBeanHibernateProxyConfig
import org.javers.hibernate.integration.config.JaversFieldHibernateProxyConfig
import org.javers.hibernate.integration.entity.*
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class ObjectAccessHookSpec extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Unroll
    def "should unproxy hibernate entity with Field MappingType when modPointLevel is #modPointLevel and savePointLevel is #savePointLevel"() {
        given:
        context = new AnnotationConfigApplicationContext(HibernateConfig, JaversFieldHibernateProxyConfig)
        def javers = context.getBean(Javers)
        def repository = context.getBean(PersonCrudRepository)

        def developer = new Person("0", "kaz")
        def manager =   new Person("1", "pawel")
        def director =  new Person("2", "Steve")
        developer.boss = manager
        manager.boss = director
        repository.save([director, manager, developer])

        def loadedDeveloper = repository.findOne(developer.id)

        def proxy = loadedDeveloper.getBoss(modPointLevel)
        assert proxy instanceof HibernateProxy
        assert !Hibernate.isInitialized(proxy)

        when:
        proxy.name = "New Name"
        def savePoint = loadedDeveloper.getBoss(savePointLevel)
        repository.save(savePoint)

        then:
        def snapshot = javers.getLatestSnapshot(proxy.id, Person).get()
        snapshot.getPropertyValue("name") == "New Name"

        where:
        savePointLevel <<     [0, 1, 0, 1]
        modPointLevel  <<     [1, 1, 2, 2]
    }

    def "should unproxy hibernate entity with Bean MappingType and save it to Javers repository"() {
        given:
        context = new AnnotationConfigApplicationContext(HibernateConfig, JaversBeanHibernateProxyConfig)
        def javers = context.getBean(Javers)
        def ebookRepository = context.getBean(EbookCrudRepository)
        def authorRepository = context.getBean(AuthorCrudRepository)

        def author = new Author("1", "George RR Martin")
        authorRepository.save(author);
        def ebook = new Ebook("1", "Throne of Games", author, ["great book"])
        ebookRepository.save(ebook)

        def book = ebookRepository.findOne("1")
        assert book.author instanceof HibernateProxy
        assert !Hibernate.isInitialized(book.author)

        when:
        book.author.name = "kazik"
        ebookRepository.save(book.author)

        then:
        def snapshot = javers.getLatestSnapshot("1", Author).get()
        snapshot.getPropertyValue("name") == "kazik"
    }
}