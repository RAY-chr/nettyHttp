package netty.dao.dao;

import netty.dao.DefaultWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public interface BaseDao<T> {
    void save(T t) throws Exception;

    void deleteById(Serializable id) throws Exception;

    void delete(DefaultWrapper wrapper) throws Exception;

    T selectById(Serializable id) throws Exception;

    List<T> select(DefaultWrapper wrapper) throws Exception;
}
