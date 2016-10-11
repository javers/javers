package org.javers.repository.sql

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
