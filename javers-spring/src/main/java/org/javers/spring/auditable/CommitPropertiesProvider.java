package org.javers.spring.auditable;

import org.javers.core.Javers;
import org.javers.repository.api.JaversRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;

import java.util.Collections;
import java.util.Map;

/**
 * Should provide commit properties passed by Auto-audit aspect
 * to {@link Javers#commit(String, Object, Map)}
 * <br/><br/>
 *
 * Implementation has to be thread-safe
 * and has to play along with {@link AuthorProvider}
 * <br/><br/>
 *
 * @author bartosz.walacik
 */
public interface CommitPropertiesProvider {

    /**
     * Provides object-specific commit properties when given object is committed (saved or updated)
     * to {@link JaversRepository}.
     *
     * <br/><br/>
     * This method is called by {@link JaversSpringDataAuditable} aspect after TODO
     *
     * <br/><br/>
     * Default implementation returns empty Map
     *
     * @param domainObject affected object
     */
    default Map<String, String> provideForCommittedObject(Object domainObject) {
        return Collections.emptyMap();
    }

    /**
     * Provides object-specific commit properties when given object is deleted from {@link JaversRepository}.
     *
     * <br/><br/>
     * This method is called by {@link JaversSpringDataAuditable} aspect after TODO
     *
     * <br/><br/>
     * Default implementation delegates to {@link #provideForCommittedObject(Object)}
     *
     * @param domainObject affected object
     */
    default Map<String, String> provideForDeletedObject(Object domainObject) {
        return provideForCommittedObject(domainObject);
    }

    /**
     * Provides object-specific commit properties when given object is deleted from {@link JaversRepository}
     * by its Id.
     *
     * <br/><br/>
     * This method is called by {@link JaversSpringDataAuditable} aspect after TODO
     */
    default Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId) {
        return Collections.emptyMap();
    }

    /**
     * This method is deprecated
     * and replaced with {@link #provideForCommittedObject(Object)}
     *
     * @Deprecated
     */
    @Deprecated
    default Map<String, String> provide() {
        return Collections.emptyMap();
    }
}
