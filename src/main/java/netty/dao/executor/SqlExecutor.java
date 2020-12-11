package netty.dao.executor;

import netty.dao.DefaultWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public interface SqlExecutor {

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

}
