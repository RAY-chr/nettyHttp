package netty.dao.executor;

import java.sql.ResultSet;

/**
 * @author RAY
 * @descriptions
 * @since 2021/9/6
 */
public interface InvokeResultSet<T> {

    T invoke(ResultSet resultSet) throws Exception;

}
