package org.javers.hibernate.integration.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javers.hibernate.integration.config.HibernateConfig.TENANT1;


public class TenantContext {

    private static final Logger logger = LoggerFactory.getLogger(TenantContext.class);

    private static final ThreadLocal<String> TENANT_IDENTIFIER = new ThreadLocal<>();

    public static void setTenant(String tenantIdentifier) {
        TENANT_IDENTIFIER.set(tenantIdentifier);
    }

    public static void reset() {
        TENANT_IDENTIFIER.remove();
    }

    public TenantContext() {
        logger.warn("init");
    }

    public static class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

        @Override
        public String resolveCurrentTenantIdentifier() {
            String currentTenantId = TENANT_IDENTIFIER.get();
            logger.warn("currentTenantId {}", currentTenantId);
            return currentTenantId != null
                    ? currentTenantId
                    : TENANT1;
        }

        @Override
        public boolean validateExistingCurrentSessions() {
            return false;
        }
    }

}
