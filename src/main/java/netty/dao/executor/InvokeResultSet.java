package netty.dao.executor;

import java.sql.ResultSet;

/**
 * @author RAY
 * @descriptions 根据结果集返回自定义结果
 * @since 2021/9/6
 */
public interface InvokeResultSet<T> {

    /**
     * 回调方法
     *
     * @param resultSet 根据查询 sql 返回的结果集
     * @return T 自定义结果
     * @throws Exception Exception
     */
    T invoke(ResultSet resultSet) throws Exception;

}
