package org.javers.repository.sql;

import org.javers.repository.sql.codecs.CdoSnapshotStateCodec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author bartosz.walacik
 */
public class H2RepositoryBuilder {

    private final SqlRepositoryBuilder sqlRepository;

    public H2RepositoryBuilder() {
        sqlRepository = SqlRepositoryBuilder.sqlRepository();
    }

    public H2RepositoryBuilder withCdoSnapshotStateCodec(CdoSnapshotStateCodec cdoSnapshotStateCodec) {
        sqlRepository.withCdoSnapshotStateCodec(cdoSnapshotStateCodec);
        return this;
    }

    public JaversSqlRepository build() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;");

            return sqlRepository.
                    withConnectionProvider(() -> conn).
                    withDialect(DialectName.H2).
                    build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
