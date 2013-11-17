package org.javers.repository.jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.fest.assertions.api.Assertions;
import org.javers.repository.jdbc.schema.FixedSchemaFactory;
import org.junit.Test;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.query.QueryFactory;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.query.SimpleQueryRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.fest.assertions.api.Assertions.*;
import static org.javers.repository.jdbc.JdbcDiffRepositoryBuilder.*;

/**
 * @author bartosz walacik
 */
public class JdbcDiffRepositoryBuilderTest {

    @Test
    public void shouldBuildJdbcDiffRepositoryWithDefaultH2Config() {
        //when
        JdbcDiffRepositoryBuilder builder = jdbcDiffRepository();
        builder.build();

        //then
        assertThat(builder.getContainerComponent(JdbcDiffRepository.class)).isNotNull();
        assertThat(builder.getContainerComponent(Dialect.class).getCode()).isEqualTo("H2");
        assertThat(builder.getContainerComponent(BasicDataSource.class).getUrl()).isEqualTo("jdbc:h2:mem:test");
    }

    @Test
    public void shouldCreateSchemaIfNotExists() {
        //when
        JdbcDiffRepositoryBuilder builder = jdbcDiffRepository();
        builder.build();

        //then
        DataSource ds = builder.getContainerComponent(BasicDataSource.class);
        int cnt = queryForInt(ds,
                        "SELECT  count(*) \n" +
                        "FROM    INFORMATION_SCHEMA.TABLES\n"+
                        "WHERE   TABLE_NAME      = '" + FixedSchemaFactory.DIFF_TABLE_NAME.toUpperCase() + "'\n");

        assertThat(cnt).isEqualTo(1);
    }

    /**
     * some jdbc boilerplate
     */
    private int queryForInt(DataSource ds, String sql) {
        int result;
        try {
            Connection con = ds.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            ResultSet rset = stmt.executeQuery();
            rset.next();
            result = rset.getInt(1);

            rset.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    //DataSource dataSource = DataSourceFactory.create(dialect,"jdbc:postgresql://localhost/javers_devel","javers","p_javers");
}
