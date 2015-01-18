package org.javers.repository.sql;

import org.javers.core.AbstractJaversBuilder
import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.pico.JaversSqlModule;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bartosz walacik
 */
class SqlRepositoryTestBuilder {

    SqlRepositoryBuilder builder

    private SqlRepositoryTestBuilder() {
        builder = SqlRepositoryBuilder.sqlRepository()
    }

    public static SqlRepositoryTestBuilder sqlRepository() {
        return new SqlRepositoryTestBuilder();
    }

    def getComponent(Class ofClass) {
        return builder.getContainerComponent(ofClass)
    }

    def withJSONConverter(JsonConverter jsonConverter) {
        builder.withJSONConverter(jsonConverter)
        this
    }

    def build() {
        builder.build()
    }

    def withConnectionProvider(ConnectionProvider connectionProvider) {
        builder.withConnectionProvider(connectionProvider)
        this
    }

    def withDialect(DialectName dialectName) {
        builder.withDialect(dialectName)
        this
    }
}
