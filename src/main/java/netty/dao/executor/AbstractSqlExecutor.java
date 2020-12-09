package netty.dao.executor;

import netty.dao.CommonStr;
import netty.dao.DefaultWrapper;
import netty.dao.annotion.TableId;
import netty.dao.cache.TableColumnCache;
import netty.dao.connection.ConnectionPool;
import netty.http.utils.TypeChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public abstract class AbstractSqlExecutor implements SqlExecutor {
    private static Logger logger = LoggerFactory.getLogger(AbstractSqlExecutor.class);
    private Connection transaction;
    private boolean isBeginTransaction = false;

    /**
     * 增加操作
     *
     * @param entity
     * @throws Exception
     */
    public int save(Object entity) throws Exception {
        Objects.requireNonNull(entity, "the entity cat't be null");
        Field[] fields = entity.getClass().getDeclaredFields();
        Object[] params = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            params[i] = field.get(entity);
        }
        return this.executeUpdate(insertSql(entity), params);
    }

    @Override
    public void beginTransaction() throws Exception {
        isBeginTransaction = true;
        transaction = ConnectionPool.getConnection();
        transaction.setAutoCommit(false);
    }

    @Override
    public void commit() throws Exception {
        transaction.commit();
        isBeginTransaction = false;
        transaction.setAutoCommit(true);
        ConnectionPool.releaseConnection(transaction);
    }

    @Override
    public void rollback() throws Exception {
        transaction.rollback();
    }

    @Override
    public int saveBatch(List<?> list) throws Exception {
        Objects.requireNonNull(list, "the list cat't be null");
        Objects.requireNonNull(list.get(0), "the list'size cat't be null");
        Object first = list.get(0);
        Field[] fields = first.getClass().getDeclaredFields();
        List<Object[]> objects = new ArrayList<>();
        for (Object o : list) {
            Object[] params = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                params[i] = field.get(o);
            }
            objects.add(params);
        }
        return this.executeBatch(insertSql(first), objects);
    }

    /**
     * 根据主键删除
     *
     * @param clazz
     * @param id
     * @throws Exception
     */
    public int deleteById(Class<?> clazz, Serializable id) throws Exception {
        Objects.requireNonNull(id, "the id cat't be null");
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        String primarykey = map.get(CommonStr.PRIMARYKEY);
        StringBuffer sql = new StringBuffer("delete from ");
        sql.append(map.get(CommonStr.TABLE)).append(" where ").append(primarykey);
        sql.append(" = ").append("?");
        return this.executeUpdate(sql.toString(), new Object[]{id});
    }

    @Override
    public int updateById(Object entity) throws Exception {
        Objects.requireNonNull(entity, "the entity cat't be null");
        Class<?> aClass = entity.getClass();
        Map<String, String> map = TableColumnCache.get(aClass.getSimpleName());
        StringBuffer sql = new StringBuffer("update ");
        sql.append(map.get(CommonStr.TABLE)).append(" set ");
        Field[] fields = aClass.getDeclaredFields();
        Object[] params = new Object[fields.length];
        int index = 0;
        Field primaryField = null;
        for (Field field : fields) {
            if (field.getAnnotation(TableId.class) == null) {
                sql.append(map.get(field.getName())).append(" = ?, ");
                field.setAccessible(true);
                params[index++] = field.get(entity);
            } else {
                primaryField = field;
            }
        }
        sql.setCharAt(sql.lastIndexOf(","), ' ');
        sql.append("where ").append(map.get(CommonStr.PRIMARYKEY)).append(" = ?");
        primaryField.setAccessible(true);
        params[fields.length - 1] = primaryField.get(entity);
        return this.executeUpdate(sql.toString(), params);
    }

    /**
     * 根据条件删除
     *
     * @param clazz
     * @param wrapper
     * @throws Exception
     */
    public int delete(Class<?> clazz, DefaultWrapper wrapper) throws Exception {
        Objects.requireNonNull(wrapper, "the wrapper cat't be null");
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        String primarykey = map.get(CommonStr.PRIMARYKEY);
        StringBuffer sql = new StringBuffer("delete from ");
        sql.append(map.get(CommonStr.TABLE));
        sql.append(wrapper.getSqlString());
        int size = wrapper.getValues().size();
        Object[] params = new Object[size];
        for (int i = 0; i < wrapper.getValues().size(); i++) {
            params[i] = wrapper.getValues().get(i);
        }
        return this.executeUpdate(sql.toString(), params);
    }

    @Override
    public Object selectById(Class<?> clazz, Serializable id) throws Exception {
        Objects.requireNonNull(id, "the id cat't be null");
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        return this.select(clazz, new DefaultWrapper().eq(map.get(CommonStr.PRIMARYKEY), id)).get(0);
    }

    @Override
    public List<?> select(Class<?> clazz, DefaultWrapper wrapper) throws Exception {
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        StringBuffer sql = new StringBuffer("select * from ");
        Object[] params = null;
        sql.append(map.get(CommonStr.TABLE));
        if (wrapper != null) {
            sql.append(wrapper.getSqlString());
        }
        if (wrapper != null && wrapper.getValues().size() > 0) {
            int size = wrapper.getValues().size();
            params = new Object[size];
            for (int i = 0; i < wrapper.getValues().size(); i++) {
                params[i] = wrapper.getValues().get(i);
            }
        }
        ResultSet resultSet = this.executeQuery(sql.toString(), params);
        List<Object> list = new ArrayList<>();
        while (resultSet.next()) {
            Field[] fields = clazz.getDeclaredFields();
            Object instance = clazz.newInstance();
            for (Field field : fields) {
                Object object;
                if (field.getAnnotation(TableId.class) == null) {
                    object = resultSet.getObject(map.get(field.getName()));
                } else {
                    object = resultSet.getObject(map.get(CommonStr.PRIMARYKEY));
                }
                field.setAccessible(true);
                field.set(instance, TypeChecker.parseValue(field.getType(),
                        object == null ? null : object.toString()));
            }
            list.add(instance);
        }
        return list;
    }

    @Override
    public List<?> selectByIds(Class<?> clazz, Collection<? extends Serializable> idList) throws Exception {
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        String primarykey = map.get(CommonStr.PRIMARYKEY);
        StringBuffer sql = new StringBuffer("select * from ");
        sql.append(map.get(CommonStr.TABLE)).append(" where ").append(primarykey);
        sql.append(" =? ");
        return null;

    }

    /**
     * 执行查询
     *
     * @param sql
     * @param params
     * @return
     * @throws Exception
     */
    public ResultSet executeQuery(String sql, Object[] params) throws Exception {
        logger.info("sql -> {}", sql);
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        injectParams(preparedStatement, params);
        return preparedStatement.executeQuery();
    }

    /**
     * 执行增删改操作
     *
     * @param sql
     * @param params
     * @throws Exception
     */
    public int executeUpdate(String sql, Object[] params) throws Exception {
        logger.info("sql -> {}", sql);
        if (isBeginTransaction) {
            PreparedStatement preparedStatement = transaction.prepareStatement(sql);
            injectParams(preparedStatement, params);
            int update = preparedStatement.executeUpdate();
            preparedStatement.close();
            return update;
        } else {
            Connection connection = ConnectionPool.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            injectParams(preparedStatement, params);
            int update = preparedStatement.executeUpdate();
            ConnectionPool.releaseConnection(connection);
            preparedStatement.close();
            return update;
        }
    }

    /**
     * 执行批量增删改操作
     *
     * @param sql
     * @param list
     * @return
     * @throws Exception
     */
    public int executeBatch(String sql, List<Object[]> list) throws Exception {
        logger.info("sql -> {}", sql);
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (Object[] params : list) {
            injectParams(preparedStatement, params);
            preparedStatement.addBatch();
        }
        int[] updates = preparedStatement.executeBatch();
        ConnectionPool.releaseConnection(connection);
        preparedStatement.close();
        return updates.length;
    }

    /**
     * 注入参数
     *
     * @param preparedStatement
     * @param params
     * @throws SQLException
     */
    public void injectParams(PreparedStatement preparedStatement, Object[] params) throws SQLException {
        if (params != null) {
            StringBuffer buffer = new StringBuffer();
            for (Object param : params) {
                buffer.append(String.valueOf(param)).append(", ");
            }
            buffer.setCharAt(buffer.lastIndexOf(","), ' ');
            logger.info("params -> {}", buffer.toString());
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
        }
    }

    /**
     * 生成插入 sql
     *
     * @param entity
     * @return
     * @throws Exception
     */
    private String insertSql(Object entity) throws Exception {
        Class<?> aClass = entity.getClass();
        Field[] fields = aClass.getDeclaredFields();
        String name = aClass.getSimpleName();
        Map<String, String> map = TableColumnCache.get(name);
        StringBuffer sql = new StringBuffer("insert into " + map.get(CommonStr.TABLE) + " ( ");
        for (Field field : fields) {
            if (field.getAnnotation(TableId.class) != null) {
                sql.append(map.get(CommonStr.PRIMARYKEY)).append(", ");
            } else {
                sql.append(map.get(field.getName())).append(", ");
            }
        }
        sql.setCharAt(sql.lastIndexOf(","), ' ');
        sql.append(") values ( ");
        for (Field field : fields) {
            sql.append(" ?, ");
        }
        sql.setCharAt(sql.lastIndexOf(","), ' ');
        sql.append(")");
        return sql.toString();
    }

}
