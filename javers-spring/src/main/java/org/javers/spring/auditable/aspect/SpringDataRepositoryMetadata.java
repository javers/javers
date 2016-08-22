package org.javers.spring.auditable.aspect;


/**
 * @author pszymczyk
 */
public class SpringDataRepositoryMetadata {

    private final Class domainType;
    private final Class idType;

    public SpringDataRepositoryMetadata(Class domainType, Class idType) {
        this.domainType = domainType;
        this.idType = idType;
    }

    public Class getDomainType() {
        return domainType;
    }

    public Class getIdType() {
        return idType;
    }

}
