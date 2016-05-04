package org.javers.spring.auditable;

/**
 * Should provide a commit author, typically a user name taken from current user session.
 * <br/><br/>
 *
 * Implementation has to be thread-safe.
 * <br/><br/>
 *
 * See {@link SpringSecurityAuthorProvider} - implementation for Spring Security
 *
 * @author Pawel Szymczyk
 */
public interface AuthorProvider {
    String provide();
}
