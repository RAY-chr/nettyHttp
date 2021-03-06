package netty.dao.dao;

import netty.dao.DefaultWrapper;
import netty.dao.page.Page;

import java.io.Serializable;
import java.util.List;

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

    Page<T> selectPage(Page<T> page, DefaultWrapper wrapper) throws Exception;
}
