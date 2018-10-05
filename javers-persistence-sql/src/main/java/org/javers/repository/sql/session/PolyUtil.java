package org.javers.repository.sql.session;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.SelectQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author bartosz walacik
 */
@Deprecated
public class PolyUtil {
    public static List<Long> queryForLongList(SelectQuery query, PolyJDBC poly){
        return poly.queryRunner().queryList(query, resultSet -> resultSet.getLong(1));
    }

    static List<BigDecimal> queryForBigDecimalList(SelectQuery query, PolyJDBC poly){
        return poly.queryRunner().queryList(query, resultSet -> resultSet.getBigDecimal(1));
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
