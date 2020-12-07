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

    void save(Object entity) throws Exception;

    void saveBatch(List<?> list) throws Exception;

    void deleteById(Class<?> clazz, Serializable id) throws Exception;

    void delete(Class<?> clazz, DefaultWrapper wrapper) throws Exception;

    void updateById(Object entity) throws Exception;
}
