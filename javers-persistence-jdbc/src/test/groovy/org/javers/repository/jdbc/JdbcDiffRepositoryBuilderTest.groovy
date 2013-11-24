package org.javers.repository.jdbc;

import org.apache.commons.dbcp.BasicDataSource;
import org.javers.repository.jdbc.schema.FixedSchemaFactory
import org.polyjdbc.core.dialect.Dialect;
import spock.lang.Specification;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.javers.repository.jdbc.JdbcDiffRepositoryBuilder.*;

/**
 * @author bartosz walacik
 */
class JdbcDiffRepositoryBuilderTest extends Specification{

    def "should build jdbcDiffRepository with default H2 config"(){
        when:
        JdbcDiffRepositoryBuilder builder = jdbcDiffRepository()
        builder.build()

        then:
        builder.getContainerComponent(JdbcDiffRepository.class) != null
        builder.getContainerComponent(Dialect.class).getCode() == "H2"
        builder.getContainerComponent(BasicDataSource.class).getUrl() == "jdbc:h2:mem:test"
    }


    def "should create schema if not exists"() {
        when:
        JdbcDiffRepositoryBuilder builder = jdbcDiffRepository()
        builder.build()

        then:
        DataSource ds = builder.getContainerComponent(BasicDataSource.class)
        1 == queryForInt(ds,
                        "SELECT  count(*) \n" +
                        "FROM    INFORMATION_SCHEMA.TABLES\n"+
                        "WHERE   TABLE_NAME      = '" + FixedSchemaFactory.DIFF_TABLE_NAME.toUpperCase() + "'\n")
    }

    /**
     * some jdbc java boilerplate
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
 }
