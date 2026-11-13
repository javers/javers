package org.javers.spring.auditable.aspect

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.Javers
import org.javers.core.metamodel.type.ManagedType
import org.javers.core.metamodel.type.PrimitiveOrValueType
import org.javers.spring.annotation.JaversAuditableDelete
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.CommitPropertiesProvider
import spock.lang.Specification

/**
 * Unit tests pinning down the behavior of {@link JaversCommitAdvice}'s delete handling,
 * in particular that it now delegates to {@code commitCascadingDelete}/{@code commitCascadingDeleteById}
 * instead of {@code commitShallowDelete}/{@code commitShallowDeleteById}
 * (changed together with the cascading-delete feature).
 */
class JaversCommitAdviceTest extends Specification {

    def javers = Mock(Javers)
    def authorProvider = Mock(AuthorProvider) {
        provide() >> "author"
    }
    def commitPropertiesProvider = Mock(CommitPropertiesProvider) {
        provide() >> [:]
        provideForDeletedObject(_) >> [:]
        provideForDeleteById(_, _) >> [:]
    }

    def advice = new JaversCommitAdvice(javers, authorProvider, commitPropertiesProvider)

    JoinPoint joinPointWithArgs(Object... args) {
        Mock(JoinPoint) {
            getArgs() >> args
        }
    }

    def "should commit a cascading delete (not a shallow delete) for a managed-type argument of a @JaversAuditableDelete method"() {
        given:
        def domainObject = new Object()
        javers.getTypeMapping(domainObject.getClass()) >> Mock(ManagedType)
        def jp = joinPointWithArgs(domainObject)

        when:
        advice.commitDeleteMethodArguments(jp)

        then:
        1 * javers.commitCascadingDelete("author", domainObject, [:])
        0 * javers.commitShallowDelete(*_)
        0 * javers.commitShallowDeleteById(*_)
    }

    def "should commit a cascading-delete-by-id (not a shallow delete) for a primitive/value id argument of a @JaversAuditableDelete method"() {
        given:
        def id = "some-id"
        javers.getTypeMapping(id.getClass()) >> Mock(PrimitiveOrValueType)
        def jp = joinPointWithArgs(id)
        def method = TestDeleteByIdOps.getMethod("deleteById", Object)
        jp.getSignature() >> Stub(MethodSignature) {
            getMethod() >> method
        }

        when:
        advice.commitDeleteMethodArguments(jp)

        then:
        1 * javers.commitCascadingDeleteById("author", _, [:])
        0 * javers.commitCascadingDelete(*_)
        0 * javers.commitShallowDelete(*_)
        0 * javers.commitShallowDeleteById(*_)
    }

    def "should commit a cascading delete for each managed-type object returned by a conditional-delete method"() {
        given:
        def deleted1 = new Object()
        def deleted2 = new Object()
        javers.getTypeMapping(_) >> Mock(ManagedType)
        def jp = Mock(JoinPoint)

        when:
        advice.commitDeleteMethodResult(jp, [deleted1, deleted2])

        then:
        1 * javers.commitCascadingDelete("author", deleted1, [:])
        1 * javers.commitCascadingDelete("author", deleted2, [:])
        0 * javers.commitShallowDelete(*_)
    }

    def "should throw when a conditional-delete method returns a non-managed-type object"() {
        given:
        def notManaged = "a String"
        javers.getTypeMapping(String) >> Mock(PrimitiveOrValueType)
        def jp = Mock(JoinPoint) {
            getSignature() >> Stub(MethodSignature) {
                getMethod() >> TestDeleteByIdOps.getMethod("deleteById", Object)
            }
        }

        when:
        advice.commitDeleteMethodResult(jp, notManaged)

        then:
        JaversException e = thrown()
        e.code == JaversExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_CONDITIONAL_DELETE
    }

    def "commitCascadingDeleteById() should delegate directly to javers.commitCascadingDeleteById()"() {
        given:
        def jp = Mock(JoinPoint) {
            getSignature() >> Stub(MethodSignature) {
                getMethod() >> TestDeleteByIdOps.getMethod("deleteById", Object)
            }
        }

        when:
        advice.commitCascadingDeleteById(jp, "some-id", Object)

        then:
        1 * javers.commitCascadingDeleteById("author", _, [:])
        0 * javers.commitShallowDeleteById(*_)
    }

    static class TestDeleteByIdOps {
        @JaversAuditableDelete(entity = Object)
        void deleteById(Object id) {}
    }
}
