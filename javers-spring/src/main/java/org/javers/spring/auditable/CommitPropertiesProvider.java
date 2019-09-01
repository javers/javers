package org.javers.spring.auditable;

import org.javers.core.Javers;
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
     * Provide commit properties when save/update {@link CommitPropertiesProviderContext} a domainObject.
     *
     * @param context the persist context for the domain object parameter.
     * @param domainObject affected object.
     *
     * @return Context dependant commit properties map.
     */
    Map<String, String> provide(CommitPropertiesProviderContext context, Object domainObject);

    /**
     * Provide commit properties when delete an object via id only.
     *
     * @param domainObjectClass class of deleted object.
     * @param domainObjectId id of deleted object.
     *
     * @return commit properties map for case delete by id.
     */
    Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId);

}
