package org.javers.spring.auditable;

/**
 * Commit context for {@link CommitPropertiesProvider}.
 *
 * @author Oai Ha
 */
public enum CommitPropertiesProviderContext {
    SAVE_UPDATE,
    DELETE
}
