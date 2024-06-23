package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.javers.common.collections.Maps;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.PrimitiveOrValueType;
import org.javers.spring.annotation.JaversAuditableDelete;
import org.javers.spring.auditable.AdvancedCommitPropertiesProvider;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuditedMethodExecutionContext;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

/**
 * @author Pawel Szymczyk
 */
public class JaversCommitAdvice {

    private final Javers javers;
    private final AuthorProvider authorProvider;
    private final CommitPropertiesProvider commitPropertiesProvider;

    private final Executor executor;

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this(javers, authorProvider, commitPropertiesProvider, null);
    }

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, Executor executor) {
		this.javers = javers;
		this.authorProvider = authorProvider;
		this.commitPropertiesProvider = commitPropertiesProvider;
    	this.executor = executor;
	}

	void commitSaveMethodArguments(JoinPoint jp) {
        for (Object arg : AspectUtil.collectArguments(jp)) {
            commitObject(jp, arg);
        }
    }

    void commitDeleteMethodArguments(JoinPoint jp) {
        AuditedMethodExecutionContext ctx = AuditedMethodExecutionContext.from(jp);

        for (Object arg : AspectUtil.collectArguments(jp)) {
            JaversType javersType = javers.getTypeMapping(arg.getClass());
            if (javersType instanceof ManagedType) {
                commitShallowDelete(jp, arg);
            } else if (javersType instanceof PrimitiveOrValueType) {
                commitShallowDeleteById(jp, arg, getDomainTypeToDelete(jp, arg));
            }
        }
    }

    void commitDeleteMethodResult(JoinPoint jp, Object entities) {
        AuditedMethodExecutionContext ctx = AuditedMethodExecutionContext.from(jp);
        for (Object arg : AspectUtil.collectReturnedObjects(entities)) {
            JaversType javersType = javers.getTypeMapping(arg.getClass());
            if (javersType instanceof ManagedType) {
                commitShallowDelete(jp, arg);
            } else {
                Method method = ((MethodSignature) jp.getSignature()).getMethod();
                throw new JaversException(JaversExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_CONDITIONAL_DELETE, method);
            }
        }
    }

    private Class<?> getDomainTypeToDelete(JoinPoint jp, Object id) {
        Method method = ((MethodSignature) jp.getSignature()).getMethod();
        JaversAuditableDelete javersAuditableDelete = method.getAnnotation(JaversAuditableDelete.class);
        Class<?> entity = javersAuditableDelete.entity();
        if (entity == Void.class) {
            throw new JaversException(JaversExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_DELETE, id, method);
        }
        return entity;
    }

    public void commitObject(JoinPoint jp, Object domainObject) {
        String author = authorProvider.provide();
        javers.commit(author, domainObject, propsForCommit(jp, domainObject));
    }

    public void commitShallowDelete(JoinPoint jp, Object domainObject) {
        String author = authorProvider.provide();

        javers.commitShallowDelete(author, domainObject, propsForDeletedObject(jp, domainObject));
    }

    public void commitShallowDeleteById(JoinPoint jp, Object domainObjectId, Class<?> domainType) {
        String author = authorProvider.provide();

        javers.commitShallowDeleteById(author, instanceId(domainObjectId, domainType),
                propsForDeletedById(jp, domainType, domainObjectId));
    }

    Optional<CompletableFuture<Commit>> commitSaveMethodArgumentsAsync(JoinPoint pjp) {
        List<CompletableFuture<Commit>> futures = AspectUtil.collectArguments(pjp)
                .stream()
                .map(arg -> commitObjectAsync(pjp, arg))
                .collect(Collectors.toList());

        return futures.size() == 0 ? Optional.empty() : Optional.of(futures.get(futures.size() - 1));
    }

    CompletableFuture<Commit> commitObjectAsync(JoinPoint jp, Object domainObject) {
        String author = this.authorProvider.provide();
        return this.javers.commitAsync(author, domainObject, propsForCommit(jp, domainObject), executor);
    }

    private Optional<AdvancedCommitPropertiesProvider> getAdvancedCommitPropertiesProvider() {
        if (this.commitPropertiesProvider instanceof AdvancedCommitPropertiesProvider) {
            return Optional.of((AdvancedCommitPropertiesProvider) this.commitPropertiesProvider);
        }
        return Optional.empty();
    }

    private Map<String, String> propsForCommit(JoinPoint jp, Object domainObject) {
        var basicProps = Maps.merge(
                this.commitPropertiesProvider.provideForCommittedObject(domainObject),
                this.commitPropertiesProvider.provide());

        return getAdvancedCommitPropertiesProvider()
                .map(adv -> Maps.merge(adv.provideForCommittedObject(domainObject, AuditedMethodExecutionContext.from(jp)), basicProps))
                .orElse(basicProps);
    }

    private Map<String, String> propsForDeletedObject(JoinPoint jp, Object domainObject) {
        var basicProps = Maps.merge(
                this.commitPropertiesProvider.provideForDeletedObject(domainObject),
                this.commitPropertiesProvider.provide());

        return getAdvancedCommitPropertiesProvider()
                .map(adv -> Maps.merge(adv.provideForDeletedObject(domainObject, AuditedMethodExecutionContext.from(jp)), basicProps))
                .orElse(basicProps);
    }

    private Map<String, String> propsForDeletedById(JoinPoint jp, Class<?> domainObjectClass, Object domainObjectId) {
        var basicProps = Maps.merge(
                this.commitPropertiesProvider.provideForDeleteById(domainObjectClass, domainObjectId),
                this.commitPropertiesProvider.provide());

        return getAdvancedCommitPropertiesProvider()
                .map(adv -> Maps.merge(adv.provideForDeleteById(domainObjectClass, domainObjectId, AuditedMethodExecutionContext.from(jp)), basicProps))
                .orElse(basicProps);
    }
}
