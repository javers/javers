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
import org.javers.spring.auditable.AuditingExecutionContext;
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

    private final AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider;
    private final Executor executor;

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;
        this.advancedCommitPropertiesProvider = advancedCommitPropertiesProvider;
        this.executor = null;
    }

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider, Executor executor) {
		this.javers = javers;
		this.authorProvider = authorProvider;
		this.commitPropertiesProvider = commitPropertiesProvider;
        this.advancedCommitPropertiesProvider = advancedCommitPropertiesProvider;
    	this.executor = executor;
	}

	void commitSaveMethodArguments(JoinPoint pjp) {
        AuditingExecutionContext ctx = AuditingExecutionContext.from(pjp);
        for (Object arg : AspectUtil.collectArguments(pjp)) {
            commitObject(ctx, arg);
        }
    }

    void commitDeleteMethodArguments(JoinPoint jp) {
        AuditingExecutionContext ctx = AuditingExecutionContext.from(jp);

        for (Object arg : AspectUtil.collectArguments(jp)) {
            JaversType javersType = javers.getTypeMapping(arg.getClass());
            if (javersType instanceof ManagedType) {
                commitShallowDelete(ctx, arg);
            } else if (javersType instanceof PrimitiveOrValueType) {
                commitShallowDeleteById(ctx, arg, getDomainTypeToDelete(jp, arg));
            }
        }
    }

    void commitDeleteMethodResult(JoinPoint jp, Object entities) {
        AuditingExecutionContext ctx = AuditingExecutionContext.from(jp);
        for (Object arg : AspectUtil.collectReturnedObjects(entities)) {
            JaversType javersType = javers.getTypeMapping(arg.getClass());
            if (javersType instanceof ManagedType) {
                commitShallowDelete(ctx, arg);
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

    public void commitObject(AuditingExecutionContext ctx, Object domainObject) {
        String author = authorProvider.provide();
        javers.commit(author, domainObject, propsForCommit(ctx, domainObject));
    }

    public void commitShallowDelete(AuditingExecutionContext ctx, Object domainObject) {
        String author = authorProvider.provide();

        javers.commitShallowDelete(author, domainObject, Maps.merge(
                advancedCommitPropertiesProvider.provideForDeletedObject(ctx, domainObject),
                commitPropertiesProvider.provideForDeletedObject(domainObject),
                commitPropertiesProvider.provide()
            )
        );
    }

    public void commitShallowDeleteById(AuditingExecutionContext ctx, Object domainObjectId, Class<?> domainType) {
        String author = authorProvider.provide();

        javers.commitShallowDeleteById(author, instanceId(domainObjectId, domainType), Maps.merge(
            advancedCommitPropertiesProvider.provideForDeleteById(ctx, domainType, domainObjectId),
            commitPropertiesProvider.provideForDeleteById(domainType, domainObjectId),
            commitPropertiesProvider.provide()
        ));
    }

    Optional<CompletableFuture<Commit>> commitSaveMethodArgumentsAsync(JoinPoint pjp) {
        var ctx = AuditingExecutionContext.from(pjp);

        List<CompletableFuture<Commit>> futures = AspectUtil.collectArguments(pjp)
                .stream()
                .map(arg -> commitObjectAsync(ctx, arg))
                .collect(Collectors.toList());

        return futures.size() == 0 ? Optional.empty() : Optional.of(futures.get(futures.size() - 1));
    }

    CompletableFuture<Commit> commitObjectAsync(AuditingExecutionContext ctx, Object domainObject) {
        String author = this.authorProvider.provide();
        return this.javers.commitAsync(author, domainObject, propsForCommit(ctx, domainObject), executor);
    }

    private Map<String, String> propsForCommit(AuditingExecutionContext ctx, Object domainObject) {
        return Maps.merge(
            advancedCommitPropertiesProvider.provideForCommittedObject(ctx, domainObject),
            commitPropertiesProvider.provideForCommittedObject(domainObject),
            commitPropertiesProvider.provide()
        );
    }
}
