package org.javers.hibernate.integration

import org.hibernate.proxy.HibernateProxy
import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.hibernate.integration.config.JaversBeanProxyManagerConfig
import org.javers.hibernate.integration.config.JaversFieldProxyManagerConfig
import org.javers.hibernate.integration.entity.*
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

class HibernateProxyManagerSpec extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    def "should unproxy hibernate entity with Field Mapping Type and save it to Javers repository"() {
        given:
        context = new AnnotationConfigApplicationContext(HibernateConfig, JaversFieldProxyManagerConfig)
        def javers = context.getBean(Javers)
        def repository = context.getBean(PersonCrudRepository)

        def person1 = new Person("1", "kaz")
        def person2 = new Person("2", "pawel")
        person1.boss = person2
        repository.save(person2)
        repository.save(person1)

        def person = repository.findOne("1")
        assert person.boss instanceof HibernateProxy

        when:
        person.name = "bartosz"
        repository.save(person)

        then:
        def snapshot = javers.getLatestSnapshot("1", Person)
        snapshot.get().getPropertyValue("boss")
    }

    def "should unproxy hibernate entity with Bean Mapping Type and save it to Javers repository"() {
        given:
        context = new AnnotationConfigApplicationContext(HibernateConfig, JaversBeanProxyManagerConfig)
        def javers = context.getBean(Javers)
        def ebookRepository = context.getBean(EbookCrudRepository)
        def authorRepository = context.getBean(AuthorCrudRepository)

        def author = new Author("1", "George RR Martin")
        authorRepository.save(author);
        def ebook = new Ebook("1", "Throne of Games", author, ["great book"])
        ebookRepository.save(ebook)

        def book = ebookRepository.findOne("1")
        assert book.author instanceof HibernateProxy

        when:
        book.title = "Game of Thrones"
        book.comments = ["very bad"]
        ebookRepository.save(book)

        then:
        def snapshot = javers.getLatestSnapshot("1", Ebook)
        snapshot.get().getPropertyValue("author")
        snapshot.get().getPropertyValue("comments")
    }
}