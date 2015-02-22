package org.javers.spring.data.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.javers.common.collections.Lists
import org.javers.core.Javers
import org.javers.spring.AuthorProvider
import org.javers.spring.data.handler.AuditChangeHandler
import org.javers.spring.data.integration.testdata.DummyObject
import org.javers.spring.data.integration.testdata.DummyRepository
import spock.lang.Specification

/**
 * Created by gessnerfl on 22.02.15.
 */
class JaversSpringDataRepositoryAspectTest extends Specification {

    def javers = Mock(Javers.class);
    def authorProvider = Mock(AuthorProvider.class);
    def saveHandler = Mock(AuditChangeHandler.class);
    def deleteHandler = Mock(AuditChangeHandler.class)

    def changedObject = Mock(DummyObject.class);
    def pjp = Mock(ProceedingJoinPoint.class)

    def sut = new JaversSpringDataRepositoryAspect(javers, authorProvider, saveHandler, deleteHandler)

    def "Should trigger save handler for single object"(){
        setup:
        initRepository()
        pjp.getArgs() >> [changedObject]

        when:
        sut.onSaveExecuted(pjp)

        then:
        1 * saveHandler.onAfterRepositoryCall(_, changedObject)
    }

    def "Should trigger save handler for multiple objects"(){
        setup:
        initRepository()
        def changes = Lists.asList(changedObject, changedObject)
        pjp.getArgs() >> [changes]

        when:
        sut.onSaveExecuted(pjp)

        then:
        2 * saveHandler.onAfterRepositoryCall(_, changedObject)
    }

    def "Should trigger delete handler for single object"(){
        setup:
        initRepository()
        pjp.getArgs() >> [changedObject]

        when:
        sut.onDeleteExecuted(pjp)

        then:
        1 * deleteHandler.onAfterRepositoryCall(_, changedObject)
    }

    def "Should trigger delete handler for multiple objects"(){
        setup:
        initRepository()
        def changes = Lists.asList(changedObject, changedObject)
        pjp.getArgs() >> [changes]

        when:
        sut.onDeleteExecuted(pjp)

        then:
        2 * deleteHandler.onAfterRepositoryCall(_, changedObject)
    }

    def initRepository(){
        def repo = Mock(DummyRepository.class)
        pjp.getTarget() >> repo
    }
}
