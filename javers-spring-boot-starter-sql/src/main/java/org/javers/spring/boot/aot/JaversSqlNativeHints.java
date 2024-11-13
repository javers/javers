package org.javers.spring.boot.aot;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.util.Arrays;

public class JaversSqlNativeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        registerNativeHints(hints);
    }

    static boolean registerNativeHints(RuntimeHints hints) {
        hints.resources().registerResourceBundle("org.aspectj.weaver.weaver-messages");

        var classes = Arrays.asList(
                // Package: org.javers.repository.jql
                "org.javers.repository.jql.ChangesQueryRunner",
                "org.javers.repository.jql.QueryCompiler",
                "org.javers.repository.jql.QueryRunner",
                "org.javers.repository.jql.ShadowQueryRunner",
                "org.javers.repository.jql.ShadowStreamQueryRunner",
                "org.javers.repository.jql.SnapshotQueryRunner",

                // Package: org.javers.repository.sql
                "org.javers.repository.sql.ConnectionProvider",
                "org.javers.repository.sql.DialectName",
                "org.javers.repository.sql.JaversSqlRepository",
                "org.javers.repository.sql.SqlRepositoryBuilder",
                "org.javers.repository.sql.SqlRepositoryConfiguration",
                "org.javers.repository.sql.finders.CdoSnapshotFinder",
                "org.javers.repository.sql.finders.CommitPropertyFinder",
                "org.javers.repository.sql.repositories.CdoSnapshotRepository",
                "org.javers.repository.sql.repositories.CommitMetadataRepository",
                "org.javers.repository.sql.repositories.GlobalIdRepository",
                "org.javers.repository.sql.schema.FixedSchemaFactory",
                "org.javers.repository.sql.schema.JaversSchemaManager",
                "org.javers.repository.sql.schema.SchemaNameAware",
                "org.javers.repository.sql.schema.TableNameProvider",
                "org.javers.repository.sql.session.Session",

                // Package: org.javers.hibernate.integration
                "org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook",

                // Package: org.javers.spring.auditable.aspect.springdatajpa
                "org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect"
        );

        classes.forEach(clazz -> {
            try {
                hints.reflection().registerType(Class.forName(clazz), builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        });
        return true;
    }
}
