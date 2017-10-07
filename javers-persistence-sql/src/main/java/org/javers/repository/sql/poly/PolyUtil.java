package org.javers.repository.sql.poly;

import java.util.Optional;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author bartosz walacik
 */
public class PolyUtil {
    public static List<Long> queryForLongList(SelectQuery query, PolyJDBC poly){
        return poly.queryRunner().queryList(query, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(1);
            }
        });
    }

    public static List<BigDecimal> queryForBigDecimalList(SelectQuery query, PolyJDBC poly){
        return poly.queryRunner().queryList(query, new ObjectMapper<BigDecimal>() {
            @Override
            public BigDecimal createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getBigDecimal(1);
            }
        });
    }

    public static Optional<BigDecimal> queryForOptionalBigDecimal(SelectQuery query, PolyJDBC poly){
        List<BigDecimal> result = queryForBigDecimalList(query, poly);

        if (result.isEmpty() || (result.size() == 1 && result.get(0) == null)){
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    public static Optional<Long> queryForOptionalLong(SelectQuery query, PolyJDBC poly){
        List<Long> result = queryForLongList(query, poly);

        if (result.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }
}
