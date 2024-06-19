package org.javers.spring.auditable;

import org.javers.core.Javers;
import org.javers.repository.api.JaversRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.Map;

/**
 * This interface gives possibility to provide commit properties for
 * {@link Javers#commit(String, Object, Map)} and
 * {@link Javers#commitShallowDelete(String, Object, Map)} when called
 * by the {@link JaversSpringDataAuditable} auto-audit aspect.
 * <br/><br/>
 *
 * Implementation has to be thread-safe.
 * <br/><br/>
 *
 * <b>Usage</b>
 * <br/>
 * Create a bean in your Spring context, for example:
 * <pre>
 * {@code
 *     @Bean
 *     @ConditionalOnMissingBean
 *     public CommitPropertiesProvider commitPropertiesProvider() {
 *         return new MyCommitPropertiesProvider();
 *     }
 * }
 * </pre>
 *
 * See the extended version of this interface &mdash; {@link AdvancedCommitPropertiesProvider},
 * which works similarly, but additionally gives you access to an audited method execution context.
 * <br/><br/>
 *
 * @author bartosz.walacik
 */
public interface CommitPropertiesProvider {

    /**
     * Provides object-specific Javers commit properties when a given object is committed (saved or updated)
     * to {@link JaversRepository}.
     * <br/><br/>
     *
     * This method is called by the {@link JaversSpringDataAuditable} aspect
     * to get properties for Javers commit created when
     * {@link CrudRepository#save(Object)} and
     * {@link CrudRepository#saveAll(Iterable)} methods are called.
     *
     * <br/><br/>
     * Default implementation returns empty Map.
     *
     * @return a map of commit properties
     * @see AdvancedCommitPropertiesProvider#provideForCommittedObject(Object, AuditedMethodExecutionContext)
     */
    default Map<String, String> provideForCommittedObject(Object savedDomainObject) {
        return Collections.emptyMap();
    }

    /**
     * Provides object-specific Javers commit properties when a given object is deleted from {@link JaversRepository}.
     * <br/><br/>
     *
     * This method is called by {@link JaversSpringDataAuditable} aspect
     * to get properties for Javers commit created when
     * {@link CrudRepository#delete(Object)} and
     * {@link CrudRepository#deleteAll(Iterable)} methods are called.
     *
     * <br/><br/>
     * Default implementation delegates to {@link #provideForCommittedObject(Object)}.
     *
     * @return a map of commit properties
     * @see AdvancedCommitPropertiesProvider#provideForDeletedObject(Object, AuditedMethodExecutionContext)
     */
    default Map<String, String> provideForDeletedObject(Object deletedDomainObject) {
        return provideForCommittedObject(deletedDomainObject);
    }

    /**
     * Provides object-specific commit properties when a given object is deleted from {@link JaversRepository}
     * by its Id.
     * <br/><br/>
     *
     * This method is called by {@link JaversSpringDataAuditable} aspect
     * to get properties for Javers commit created when
     * {@link CrudRepository#deleteById(Object)} is called.
     *
     * <br/><br/>
     * Default implementation returns empty Map.
     *
     * @return a map of commit properties
     * @see AdvancedCommitPropertiesProvider#provideForDeleteById(Class, Object, AuditedMethodExecutionContext)
     */
    default Map<String, String> provideForDeleteById(Class<?> deletedDomainObjectClass, Object deletedDomainObjectId) {
        return Collections.emptyMap();
    }
}
