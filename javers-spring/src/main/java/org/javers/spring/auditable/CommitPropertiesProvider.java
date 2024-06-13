package org.javers.spring.auditable;

import org.javers.core.Javers;
import org.javers.repository.api.JaversRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.Map;

/**
 * Provides commit properties
 * for {@link Javers#commit(String, Object, Map)}
 * called by the auto-audit aspect &mdash; {@link JaversSpringDataAuditable}.
 * <br/><br/>
 *
 * Implementation has to be thread-safe.
 * <br/><br/>
 *
 * This interface is deprecated since Javers 7.5, yet there are no plans to phase it out.
 * <br/>
 * For new code, use the upgraded version &mdash; {@link AdvancedCommitPropertiesProvider}, which
 * provides a method call context.
 *
 * @author bartosz.walacik
 */
@Deprecated
public interface CommitPropertiesProvider {

    /**
     * Provides object-specific Javers commit properties when a given object is committed (saved or updated)
     * to {@link JaversRepository}.
     *
     * <br/><br/>
     * This method is called by the {@link JaversSpringDataAuditable} aspect
     * to get properties for Javers commit created when
     * {@link CrudRepository#save(Object)} and
     * {@link CrudRepository#saveAll(Iterable)} methods are called.
     *
     * <br/><br/>
     * Default implementation returns empty Map
     *
     * @param domainObject saved object
     */
    default Map<String, String> provideForCommittedObject(Object domainObject) {
        return Collections.emptyMap();
    }

    /**
     * Provides object-specific Javers commit properties when a given object is deleted from {@link JaversRepository}.
     *
     * <br/><br/>
     * This method is called by {@link JaversSpringDataAuditable} aspect
     * to get properties for Javers commit created when
     * {@link CrudRepository#delete(Object)} and
     * {@link CrudRepository#deleteAll(Iterable)} methods are called.
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
     * Provides object-specific commit properties when a given object is deleted from {@link JaversRepository}
     * by its Id.
     *
     * <br/><br/>
     * This method is called by {@link JaversSpringDataAuditable} aspect
     * to get properties for Javers commit created when
     * {@link CrudRepository#deleteById(Object)} is called.
     *
     * <br/><br/>
     * Default implementation returns empty Map
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
