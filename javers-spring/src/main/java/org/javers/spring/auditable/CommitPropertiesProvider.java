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
    Map<String, String> provide();
}
