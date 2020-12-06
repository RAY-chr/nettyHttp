package netty.dao.dao;

import netty.dao.DefaultWrapper;

import java.io.Serializable;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public interface BaseDao<T> {
    void save(T t) throws Exception;

    void deleteById(Serializable id) throws Exception;

    void delete(DefaultWrapper wrapper) throws Exception;
}
