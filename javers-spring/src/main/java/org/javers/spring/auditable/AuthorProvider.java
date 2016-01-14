package org.javers.spring.auditable;

/**
 * Implementation has to be thread-safe and has to provide
 * an author (typically a user login), to current user session.
 * <br/><br/>
 *
 * See {@link SpringSecurityAuthorProvider} - implementation for Spring Security
 *
 * @author Pawel Szymczyk
 */
public interface AuthorProvider {
    String provide();
}
