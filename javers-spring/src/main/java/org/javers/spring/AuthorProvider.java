package org.javers.spring;

/**
 * Implementation has to be thread-safe and has to provide
 * an author (typically a user login), bounded to current transaction.
 *
 * @author Pawel Szymczyk
 */
public interface AuthorProvider {
    String provide();
}
