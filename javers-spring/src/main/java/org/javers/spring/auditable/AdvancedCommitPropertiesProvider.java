package org.javers.spring.auditable;

import org.javers.core.Javers;
import org.javers.repository.api.JaversRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.CrudRepository;

import java.util.Collections;
import java.util.Map;

/**
 * This is an extended version of {@link CommitPropertiesProvider}.
 * <br/>
 * Both interfaces exist because of backward compatibility reasons.
 * <br/><br/>
 *
 * Choose this extended version of the interface to generate commit properties that might
 * depend on a current method being executed. See {@link AuditedMethodExecutionContext}.
 * <br/><br/>
 * It's not recommended to implement both interfaces in your class, if so, Javers merges the results.
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
public interface AdvancedCommitPropertiesProvider extends CommitPropertiesProvider {

	/**
	 * Extended version of {@link #provideForCommittedObject(Object)},
	 * which gives access to an audited method execution context.
	 * <br/><br/>
	 *
	 * These two method variants exist because of backward compatibility reasons.
	 * Pick one you want to override. If you override both of them (which is not recommended) &mdash;
	 * Javers merges the results.<br/>
	 * Both method variants returns empty Map by default.
	 *
	 * @return a map of commit properties
	 * @see #provideForCommittedObject(Object)
	 */
	default Map<String, String> provideForCommittedObject(Object savedDomainObject, AuditedMethodExecutionContext ctx) {
		return Collections.emptyMap();
	}

	/**
	 * Extended version of {@link #provideForDeletedObject(Object)},
	 * which gives access to an audited method execution context.
	 * <br/><br/>
	 *
	 * These two method variants exist because of backward compatibility reasons.
	 * Pick one you want to override. If you override both of them (which is not recommended) &mdash;
	 * Javers merges the results.<br/>
	 * Default impl delegates to {@link #provideForCommittedObject(Object, AuditedMethodExecutionContext)}.
	 *
	 * @return a map of commit properties
	 * @see #provideForDeletedObject(Object)
	 */
	default Map<String, String> provideForDeletedObject(Object deletedDomainObject, AuditedMethodExecutionContext ctx) {
		return provideForDeletedObject(deletedDomainObject, ctx);
	}

	/**
	 * Extended version of {@link #provideForDeleteById(Class, Object)},
	 * which gives access to an audited method execution context.
	 * <br/><br/>
	 *
	 * These two method variants exist because of backward compatibility reasons.
	 * Pick one you want to override. If you override both of them (which is not recommended) &mdash;
	 * Javers merges the results.<br/>
	 * Both method variants returns empty Map by default.
	 *
	 * @return a map of commit properties
	 * @see #provideForDeleteById(Class, Object)
	 */
	default Map<String, String> provideForDeleteById(Class<?> deletedDomainObjectClass, Object deletedDomainObjectId, AuditedMethodExecutionContext ctx) {
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
