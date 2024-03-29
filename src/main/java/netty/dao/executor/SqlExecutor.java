package netty.dao.executor;

import netty.dao.DefaultWrapper;
import netty.dao.page.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public interface SqlExecutor {
    long count(Class<?> clazz, DefaultWrapper wrapper) throws Exception;

    int save(Object entity) throws Exception;

    void beginTransaction() throws Exception;

    void commit() throws Exception;

    void rollback() throws Exception;

    int saveBatch(List<?> list) throws Exception;

    int deleteById(Class<?> clazz, Serializable id) throws Exception;

    int delete(Class<?> clazz, DefaultWrapper wrapper) throws Exception;

    int updateById(Object entity) throws Exception;

    Object selectById(Class<?> clazz, Serializable id) throws Exception;

    List<?> select(Class<?> clazz, DefaultWrapper wrapper) throws Exception;

    List<?> selectList(Class<?> clazz, String sql, Object[] params) throws Exception;

    List<Map<String, Object>> selectMaps(String sql, Object[] params) throws Exception;

    <T> T executeQuery(String sql, Object[] params, InvokeResultSet<T> invokeResultSet) throws Exception;

    Page<?> selectPage(Class<?> clazz, Page<?> page, DefaultWrapper wrapper) throws Exception;

}
