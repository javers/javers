package org.javers.spring.auditable;

import java.util.Collections;
import java.util.Map;

/**
 * An advanced version of {@link CommitPropertiesProvider} that supplies commit properties for Javers during auditing.
 * <p>
 * By implementing this interface, you can leverage information from the {@link AuditingExecutionContext}
 * to generate commit properties that are specific to the current operation being performed.
 * </p>
 *
 * @author Xiangcheng Kuo
 * @see CommitPropertiesProvider
 * @see AuditingExecutionContext
 * @see org.javers.core.Javers
 * @since 2024-06-07
 */
public interface AdvancedCommitPropertiesProvider {

	/**
	 * Provides commit properties specific to the object being saved.
	 *
	 * @param ctx          the auditing context
	 * @param domainObject the object being saved
	 * @return a map of commit properties
	 */
	default Map<String, String> provideForCommittedObject(AuditingExecutionContext ctx, Object domainObject) {
		return Collections.emptyMap();
	}

	/**
	 * Provides commit properties specific to the object being deleted.
	 *
	 * @param ctx          the auditing context
	 * @param domainObject the object being deleted
	 * @return a map of commit properties
	 */
	default Map<String, String> provideForDeletedObject(AuditingExecutionContext ctx, Object domainObject) {
		return Collections.emptyMap();
	}

	/**
	 * Provides commit properties specific to the object being deleted by its ID.
	 *
	 * @param ctx               the auditing context
	 * @param domainObjectClass the class of the object being deleted
	 * @param domainObjectId    the ID of the object being deleted
	 * @return a map of commit properties
	 */
	default Map<String, String> provideForDeleteById(AuditingExecutionContext ctx, Class<?> domainObjectClass, Object domainObjectId) {
		return Collections.emptyMap();
	}

	/**
	 * Returns an instance of an empty implementation of {@code AdvancedCommitPropertiesProvider}.
	 *
	 * @return an instance of {@code EmptyAdvancedCommitPropertiesProvider}
	 */
	static AdvancedCommitPropertiesProvider empty() {
		return new EmptyAdvancedCommitPropertiesProvider();
	}

	/**
	 * An empty implementation of {@code AdvancedCommitPropertiesProvider} that returns empty maps for all methods.
	 */
	class EmptyAdvancedCommitPropertiesProvider implements AdvancedCommitPropertiesProvider {

	}

}
