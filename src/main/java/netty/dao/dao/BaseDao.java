package netty.dao.dao;

import netty.dao.DefaultWrapper;
import netty.dao.executor.InvokeResultSet;
import netty.dao.page.Page;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public interface BaseDao<T> {
    boolean save(T t) throws Exception;

    boolean saveBatch(List<T> list) throws Exception;

    boolean deleteById(Serializable id) throws Exception;

    boolean delete(DefaultWrapper wrapper) throws Exception;

    boolean updateById(T t) throws Exception;

    T selectById(Serializable id) throws Exception;

    List<T> select(DefaultWrapper wrapper) throws Exception;

    List<T> selectList(String sql, Object[] params) throws Exception;

    List<Map<String, Object>> selectMaps(String sql, Object[] params) throws Exception;

    <E> E executeQuery(String sql, Object[] params,
                              InvokeResultSet<E> invokeResultSet) throws Exception;

    Page<T> selectPage(Page<T> page, DefaultWrapper wrapper) throws Exception;
}
