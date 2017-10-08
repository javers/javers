package org.javers.hibernate.integration

import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.javers.core.Javers
import org.javers.hibernate.entity.Author
import org.javers.hibernate.entity.AuthorCrudRepository
import org.javers.hibernate.entity.Ebook
import org.javers.hibernate.entity.EbookCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = JaversBeanHibernateProxyConfig)
class ObjectAccessHookBeanSpec extends Specification {

    @Autowired
    Javers javers

    @Autowired
    EbookCrudRepository ebookRepository

    @Autowired
    AuthorCrudRepository authorRepository

    def "should unproxy hibernate entity with Bean MappingType and save it to Javers repository"() {
        given:
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