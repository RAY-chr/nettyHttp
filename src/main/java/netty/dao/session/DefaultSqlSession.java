package netty.dao.session;

import netty.dao.DBConfig;
import netty.dao.DefaultWrapper;
import netty.dao.executor.InvokeResultSet;
import netty.dao.executor.SqlExecutor;
import netty.dao.executor.SqlExecutorFactory;
import netty.dao.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class DefaultSqlSession implements SqlSession {

    private SqlExecutor sqlExecutor = getByType();

    /**
     * 增加操作
     *
     * @param entity
     * @throws Exception
     */
    public boolean save(Object entity) throws Exception {
        return sqlExecutor.save(entity) == 1;
    }

    @Override
    public void beginTransaction() throws Exception {
        sqlExecutor.beginTransaction();
    }

    @Override
    public void commit() throws Exception {
        sqlExecutor.commit();
    }

    @Override
    public void rollback() throws Exception {
        sqlExecutor.rollback();
    }

    @Override
    public boolean saveBatch(List<?> list) throws Exception {
        return sqlExecutor.saveBatch(list) == list.size();
    }

    /**
     * 根据主键删除
     *
     * @param clazz
     * @param id
     * @throws Exception
     */
    public boolean deleteById(Class<?> clazz, Serializable id) throws Exception {
        return sqlExecutor.deleteById(clazz, id) == 1;
    }

    /**
     * 根据主键更改
     *
     * @param entity
     * @throws Exception
     */
    @Override
    public boolean updateById(Object entity) throws Exception {
        return sqlExecutor.updateById(entity) == 1;
    }

    /**
     * 根据条件删除
     *
     * @param clazz
     * @param wrapper
     * @throws Exception
     */
    public boolean delete(Class<?> clazz, DefaultWrapper wrapper) throws Exception {
        return sqlExecutor.delete(clazz, wrapper) > 0;
    }

    @Override
    public Object selectById(Class<?> clazz, Serializable id) throws Exception {
        return sqlExecutor.selectById(clazz, id);
    }

    @Override
    public List<?> select(Class<?> clazz, DefaultWrapper wrapper) throws Exception {
        return sqlExecutor.select(clazz, wrapper);
    }

    @Override
    public List<?> selectList(Class<?> clazz, String sql, Object[] params) throws Exception {
        return sqlExecutor.selectList(clazz, sql, params);
    }

    @Override
    public List<Map<String, Object>> selectMaps(String sql, Object[] params) throws Exception {
        return sqlExecutor.selectMaps(sql, params);
    }

    @Override
    public <T> T executeQuery(String sql, Object[] params, InvokeResultSet<T> invokeResultSet) throws Exception {
        return sqlExecutor.executeQuery(sql, params, invokeResultSet);
    }

    @Override
    public Page<?> selectPage(Class<?> clazz, Page<?> page, DefaultWrapper wrapper) throws Exception {
        return sqlExecutor.selectPage(clazz, page, wrapper);
    }

    public static SqlExecutor getByType() {
        return SqlExecutorFactory.getSqlExecutor(DBConfig.getString("dbType"));
    }

}
