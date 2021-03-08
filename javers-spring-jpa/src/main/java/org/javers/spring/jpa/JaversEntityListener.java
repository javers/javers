package org.javers.spring.jpa;

import org.javers.common.collections.Maps;
import org.javers.core.Javers;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * JPA entity listener to commit auditing changes. To get this one flying be
 * sure you add @EnableJaversEntityListeners to your application config
 * and add this entity listener in your {@code orm.xml} as follows:
 *
 * <pre>
 * &lt;persistence-unit-metadata&gt;
 *     &lt;persistence-unit-defaults&gt;
 *         &lt;entity-listeners&gt;
 *             &lt;entity-listener class="org.javers.spring.jpa.JaversEntityListener" /&gt;
 *         &lt;/entity-listeners&gt;
 *     &lt;/persistence-unit-defaults&gt;
 * &lt;/persistence-unit-metadata&gt;
 * </pre>
 *
 * Or configure it directly on entity as follows:
 *<pre>
 * {@code
 * @EntityListeners(org.javers.spring.jpa.JaversEntityListener)
 * @Entity
 * class MyEntity {...} }
 *</pre>
 */
@Configurable
public class JaversEntityListener {

    private ObjectProvider<Javers> javers;
    private ObjectProvider<AuthorProvider> authorProvider;
    private ObjectProvider<CommitPropertiesProvider> commitPropertiesProvider;

    /**
     * Default constructor is required for JPA
     */
    public JaversEntityListener() {
    }

    /**
     * commits audit changes after entity is persisted to the database
     *
     * @param entity not null
     */
    @PostPersist
    public void commitOnPersist(Object entity) {
        requireNonNull(entity, "entity");
        if (isInitialized()) {
            commitObject(entity);
        }
    }

    /**
     * commits audit changes after entity is updated against the database
     *
     * @param entity not null
     */
    @PostUpdate
    public void commitOnUpdate(Object entity) {
        requireNonNull(entity, "entity");
        if (isInitialized()) {
            commitObject(entity);
        }
    }

    /**
     * commits audit delete after entity is removed from the database
     *
     * @param entity not null
     */
    @PostRemove
    public void commitOnRemove(Object entity) {
        requireNonNull(entity, "entity");
        if (isInitialized()) {
            commitShallowDelete(entity);
        }
    }

    private void commitObject(Object domainObject) {
        String author = authorProvider.getObject().provide();
        javers.getObject().commit(author, domainObject, propsForCommit(domainObject));
    }

    private void commitShallowDelete(Object domainObject) {
        String author = authorProvider.getObject().provide();

        javers.getObject().commitShallowDelete(author, domainObject, Maps.merge(
                commitPropertiesProvider.getObject().provideForDeletedObject(domainObject),
                commitPropertiesProvider.getObject().provide()));
    }

    private Map<String, String> propsForCommit(Object domainObject) {
        return Maps.merge(
                commitPropertiesProvider.getObject().provideForCommittedObject(domainObject),
                commitPropertiesProvider.getObject().provide());
    }

    private boolean isInitialized() {
        return javers != null && authorProvider != null && commitPropertiesProvider != null;
    }

    @Autowired
    public void setJavers(ObjectProvider<Javers> javers) {
        this.javers = javers;
    }

    @Autowired
    public void setAuthorProvider(ObjectProvider<AuthorProvider> authorProvider) {
        this.authorProvider = authorProvider;
    }

    @Autowired
    public void setCommitPropertiesProvider(ObjectProvider<CommitPropertiesProvider> commitPropertiesProvider) {
        this.commitPropertiesProvider = commitPropertiesProvider;
    }
}
