package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.javers.common.collections.Maps;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
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
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;
        this.executor = null;
    }

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider, Executor executor) {
		this.javers = javers;
		this.authorProvider = authorProvider;
		this.commitPropertiesProvider = commitPropertiesProvider;
    	this.executor = executor;
	}

    public void commitSaveMethodArguments(JoinPoint pjp) {
        for (Object arg : AspectUtil.collectArguments(pjp)) {
            commitObject(arg);
        }
    }

    public void commitDeleteMethodArguments(JoinPoint pjp) {
        for (Object arg : AspectUtil.collectArguments(pjp)) {
            commitShallowDelete(arg);
        }
    }

	public void commitSaveMethodArgumentsAsync(JoinPoint pjp) {
		for (Object arg : AspectUtil.collectArguments(pjp)) {
			commitObjectAsync(arg);
		}
	}

	public void commitObjectAsync(Object domainObject) {
		String author = this.authorProvider.provide();
		this.javers.commitAsync(author, domainObject, Maps.merge(
				commitPropertiesProvider.provideForCommittedObject(domainObject),
				commitPropertiesProvider.provide()),executor);
	}

    public void commitObject(Object domainObject) {
        String author = authorProvider.provide();

        javers.commit(author, domainObject, Maps.merge(
            commitPropertiesProvider.provideForCommittedObject(domainObject),
            commitPropertiesProvider.provide()));
    }

    public void commitShallowDelete(Object domainObject) {
        String author = authorProvider.provide();

        javers.commitShallowDelete(author, domainObject, Maps.merge(
                commitPropertiesProvider.provideForDeletedObject(domainObject),
                commitPropertiesProvider.provide()));
    }

    public void commitShallowDeleteById(Object domainObjectId, Class<?> domainType) {
        String author = authorProvider.provide();

        javers.commitShallowDeleteById(author, instanceId(domainObjectId, domainType), Maps.merge(
                commitPropertiesProvider.provideForDeleteById(domainType, domainObjectId),
                commitPropertiesProvider.provide()));
    }
}
