package org.javers.spring.auditable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.javers.common.collections.Maps;
import org.javers.core.Javers;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.PrimitiveOrValueType;
import org.javers.spring.annotation.JaversAuditableDelete;
import org.javers.spring.auditable.AspectUtil;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;

import static org.javers.repository.jql.InstanceIdDTO.instanceId;

/**
 * @author Pawel Szymczyk
 */
public class JaversCommitAdvice {

    private final Javers javers;
    private final AuthorProvider authorProvider;
    private final CommitPropertiesProvider commitPropertiesProvider;

    public JaversCommitAdvice(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        this.javers = javers;
        this.authorProvider = authorProvider;
        this.commitPropertiesProvider = commitPropertiesProvider;
    }

    public void commitSaveMethodArguments(JoinPoint pjp) {
        for (Object arg : AspectUtil.collectArguments(pjp)) {
            commitObject(arg);
        }
    }

    public void commitDeleteMethodArguments(JoinPoint jp) {
        for (Object arg : AspectUtil.collectArguments(jp)) {
            JaversType typeMapping = javers.getTypeMapping(arg.getClass());
            if (typeMapping instanceof EntityType) {
                commitShallowDelete(arg);
            } else if (typeMapping instanceof PrimitiveOrValueType) {
                commitShallowDeleteById(arg, getDomainTypeForDelete(jp));
            }
        }
    }

    private Class<?> getDomainTypeForDelete(JoinPoint jp) {
        JaversAuditableDelete javersAuditableDelete =
                ((MethodSignature) jp.getSignature()).getMethod().getAnnotation(JaversAuditableDelete.class);
        Class<?> domainType = javersAuditableDelete.domainType();
        if (domainType == Void.class) {
            throw new IllegalStateException("Committing by id requires a domain type");
        }
        return domainType;
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
