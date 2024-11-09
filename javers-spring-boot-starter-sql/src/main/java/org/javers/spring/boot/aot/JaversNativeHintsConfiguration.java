package org.javers.spring.boot.aot;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.Arrays;

@Configuration
@ImportRuntimeHints(JaversNativeHintsConfiguration.ApplicationRuntimeHints.class)
public class JaversNativeHintsConfiguration {
    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            registerNativeHints(hints);
        }
    }

    static boolean registerNativeHints(RuntimeHints hints) {
        hints.resources().registerResourceBundle("org.aspectj.weaver.weaver-messages");

        var classes = Arrays.asList(
                "java.util.ArrayList",

                // Package: org.javers.core
                "org.javers.core.Javers",
                "org.javers.core.JaversBuilder",
                "org.javers.core.JaversBuilderPlugin",
                "org.javers.core.JaversCore",
                "org.javers.core.JaversCoreProperties",
                "org.javers.core.JaversCoreProperties$PrettyPrintDateFormats",
                "org.javers.core.commit.Commit",
                "org.javers.core.commit.CommitFactory",
                "org.javers.core.commit.CommitId",
                "org.javers.core.commit.CommitIdFactory",
                "org.javers.core.commit.CommitSeqGenerator",
                "org.javers.core.commit.DistributedCommitSeqGenerator",
                "org.javers.core.diff.DiffFactory",
                "org.javers.core.diff.appenders.ArrayChangeAppender",
                "org.javers.core.diff.appenders.CollectionAsListChangeAppender",
                "org.javers.core.diff.appenders.MapChangeAppender",
                "org.javers.core.diff.appenders.NewObjectAppender",
                "org.javers.core.diff.appenders.ObjectRemovedAppender",
                "org.javers.core.diff.appenders.OptionalChangeAppender",
                "org.javers.core.diff.appenders.ReferenceChangeAppender",
                "org.javers.core.diff.appenders.SetChangeAppender",
                "org.javers.core.diff.appenders.SimpleListChangeAppender",
                "org.javers.core.diff.appenders.ValueChangeAppender",
                "org.javers.core.graph.CollectionsCdoFactory",
                "org.javers.core.graph.LiveCdoFactory",
                "org.javers.core.graph.LiveGraphFactory",
                "org.javers.core.graph.ObjectAccessHook",
                "org.javers.core.graph.ObjectHasher",
                "org.javers.core.graph.TailoredJaversFieldFactory",
                "org.javers.core.json.JsonConverter",
                "org.javers.core.json.JsonConverterBuilder",
                "org.javers.core.json.typeadapter.change.ArrayChangeTypeAdapter",
                "org.javers.core.json.typeadapter.change.ChangeTypeAdapter",
                "org.javers.core.json.typeadapter.change.ListChangeTypeAdapter",
                "org.javers.core.json.typeadapter.change.MapChangeTypeAdapter",
                "org.javers.core.json.typeadapter.change.NewObjectTypeAdapter",
                "org.javers.core.json.typeadapter.change.ObjectRemovedTypeAdapter",
                "org.javers.core.json.typeadapter.change.ReferenceChangeTypeAdapter",
                "org.javers.core.json.typeadapter.change.SetChangeTypeAdapter",
                "org.javers.core.json.typeadapter.change.ValueChangeTypeAdapter",
                "org.javers.core.json.typeadapter.commit.CdoSnapshotStateTypeAdapter",
                "org.javers.core.json.typeadapter.commit.CdoSnapshotTypeAdapter",
                "org.javers.core.json.typeadapter.commit.CommitIdTypeAdapter",
                "org.javers.core.json.typeadapter.commit.CommitMetadataTypeAdapter",
                "org.javers.core.json.typeadapter.commit.GlobalIdTypeAdapter",
                "org.javers.core.metamodel.object.CdoSnapshot",
                "org.javers.core.metamodel.object.GlobalId",
                "org.javers.core.metamodel.object.GlobalIdFactory",
                "org.javers.core.metamodel.scanner.AnnotationNamesProvider",
                "org.javers.core.metamodel.scanner.ClassAnnotationsScanner",
                "org.javers.core.metamodel.scanner.ClassScanner",
                "org.javers.core.metamodel.scanner.FieldBasedPropertyScanner",
                "org.javers.core.metamodel.type.EntityType",
                "org.javers.core.metamodel.type.TypeMapper",
                "org.javers.core.snapshot.ChangedCdoSnapshotsFactory",
                "org.javers.core.snapshot.SnapshotDiffer",
                "org.javers.core.snapshot.SnapshotFactory",
                "org.javers.core.snapshot.SnapshotGraphFactory",

                // Package: org.javers.guava
                "org.javers.guava.MultimapChangeAppender",
                "org.javers.guava.MultisetChangeAppender",

                // Package: org.javers.hibernate.integration
                "org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook",

                // Package: org.javers.repository.api
                "org.javers.repository.api.JaversExtendedRepository",
                "org.javers.repository.api.JaversRepository",
                "org.javers.repository.api.QueryParams",

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

                // Package: org.javers.shadow
                "org.javers.shadow.ShadowFactory",

                // Package: org.javers.spring
                "org.javers.spring.JaversSpringProperties",
                "org.javers.spring.RegisterJsonTypeAdaptersPlugin",

                // Package: org.javers.spring.annotation
                "org.javers.spring.annotation.JaversAuditable",
                "org.javers.spring.annotation.JaversAuditableConditionalDelete",
                "org.javers.spring.annotation.JaversAuditableDelete",
                "org.javers.spring.annotation.JaversSpringDataAuditable",

                // Package: org.javers.spring.auditable
                "org.javers.spring.auditable.AuthorProvider",
                "org.javers.spring.auditable.CommitPropertiesProvider",

                // Package: org.javers.spring.auditable.aspect
                "org.javers.spring.auditable.aspect.JaversAuditableAspect",
                "org.javers.spring.auditable.aspect.JaversCommitAdvice",

                // Package: org.javers.spring.auditable.aspect.springdata
                "org.javers.spring.auditable.aspect.springdata.AbstractSpringAuditableRepositoryAspect",

                // Package: org.javers.spring.auditable.aspect.springdatajpa
                "org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect",

                // Package: org.javers.core.graph
                "org.javers.core.graph.SnapshotObjectHasher",

                // Package: org.javers.core.metamodel.type
                "org.javers.core.metamodel.type.ListType"
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
