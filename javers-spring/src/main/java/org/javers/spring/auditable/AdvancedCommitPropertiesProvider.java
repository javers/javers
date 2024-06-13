package org.javers.spring.auditable;

import org.javers.core.Javers;
import org.javers.repository.api.JaversRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.Map;

/**
 * A new version of {@link CommitPropertiesProvider}.
 * <br/><br/>
 *
 * Use it to provide commit properties
 * for {@link Javers#commit(String, Object, Map)}
 * called by the auto-audit aspect &mdash; {@link JaversSpringDataAuditable}.
 * Implementation has to be thread-safe.
 * <br/><br/>
 *
 * By implementing this interface, you can leverage information from {@link AuditedMethodExecutionContext}
 * to generate commit properties that might be aware of a current operation being performed.
 * <br/><br/>
 *
 * <b>Usage</b>
 * <br/>
 * Create a bean in your Spring context, for example:
 * <pre>
 * {@code
 *     @Bean
 *     @ConditionalOnMissingBean
 *     public AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider() {
 *         return new MyAdvancedCommitPropertiesProvider();
 *     }
 * }
 * </pre>
 *
 * @author Xiangcheng Kuo
 * @see CommitPropertiesProvider
 * @see AuditedMethodExecutionContext
 * @since 7.5
 */
public interface AdvancedCommitPropertiesProvider {

	/**
	 * Provides Javers commit properties when a given object is committed (saved or updated)
	 * to {@link JaversRepository}.
	 * <br/><br/>
	 *
	 * This method is called by the {@link JaversSpringDataAuditable} aspect
	 * to get properties for Javers commit created when
	 * {@link CrudRepository#save(Object)} and
	 * {@link CrudRepository#saveAll(Iterable)} methods are called.
	 *
	 * @param ctx          an audited method call context
	 * @param domainObject an object being saved
	 * @return a map of commit properties
	 */
	default Map<String, String> provideForCommittedObject(AuditedMethodExecutionContext ctx, Object domainObject) {
		return Collections.emptyMap();
	}

	/**
	 * Provides Javers commit properties when a given object is deleted from {@link JaversRepository}.
	 * <br/><br/>
	 *
	 * This method is called by {@link JaversSpringDataAuditable} aspect
	 * to get properties for Javers commit created when
	 * {@link CrudRepository#delete(Object)} and
	 * {@link CrudRepository#deleteAll(Iterable)} methods are called.
	 *
	 * <br/><br/>
	 * Default implementation delegates to {@link #provideForCommittedObject(AuditedMethodExecutionContext, Object)}
	 *
	 * @param ctx          an audited method call context
	 * @param domainObject an object being deleted
	 * @return a map of commit properties
	 */
	default Map<String, String> provideForDeletedObject(AuditedMethodExecutionContext ctx, Object domainObject) {
		return Collections.emptyMap();
	}

	/**
	 * Provides Javers commit properties when a given object is deleted from {@link JaversRepository}
	 * by its Id.
	 * <br/><br/>
	 *
	 * This method is called by {@link JaversSpringDataAuditable} aspect
	 * to get properties for Javers commit created when
	 * {@link CrudRepository#deleteById(Object)} is called.
	 *
	 * <br/><br/>
	 * Default implementation returns empty Map
	 *
	 * @param ctx               an audited method call context
	 * @param domainObjectClass a class of the object being deleted
	 * @param domainObjectId    an ID of the object being deleted
	 * @return a map of commit properties
	 */
	default Map<String, String> provideForDeleteById(AuditedMethodExecutionContext ctx, Class<?> domainObjectClass, Object domainObjectId) {
		return Collections.emptyMap();
	}

	/**
	 * Default implementation returning empty maps.
	 */
	static AdvancedCommitPropertiesProvider empty() {
		return new EmptyAdvancedCommitPropertiesProvider();
	}

	class EmptyAdvancedCommitPropertiesProvider implements AdvancedCommitPropertiesProvider {
	}
}
