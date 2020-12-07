package netty.dao.executor;

import netty.dao.CommonStr;
import netty.dao.DefaultWrapper;
import netty.dao.annotion.TableId;
import netty.dao.cache.TableColumnCache;
import netty.dao.connection.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public abstract class AbstractSqlExecutor implements SqlExecutor {
    private static Logger logger = LoggerFactory.getLogger(AbstractSqlExecutor.class);

    /**
     * 增加操作
     *
     * @param entity
     * @throws Exception
     */
    public void save(Object entity) throws Exception {
        Field[] fields = entity.getClass().getDeclaredFields();
        Object[] params = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            params[i] = field.get(entity);
        }
        this.executeUpdate(insertSql(entity), params);
    }

    @Override
    public void saveBatch(List<?> list) throws Exception {
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
        this.executeBatch(insertSql(first), objects);
    }

    /**
     * 根据主键删除
     *
     * @param clazz
     * @param id
     * @throws Exception
     */
    public void deleteById(Class<?> clazz, Serializable id) throws Exception {
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        String primarykey = map.get(CommonStr.PRIMARYKEY);
        StringBuffer sql = new StringBuffer("delete from ");
        sql.append(map.get(CommonStr.TABLE)).append(" where ").append(primarykey);
        sql.append(" = ").append("?");
        this.executeUpdate(sql.toString(), new Object[]{id});
    }

    @Override
    public void updateById(Object entity) throws Exception {
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
        this.executeUpdate(sql.toString(), params);
    }

    /**
     * 根据条件删除
     *
     * @param clazz
     * @param wrapper
     * @throws Exception
     */
    public void delete(Class<?> clazz, DefaultWrapper wrapper) throws Exception {
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
        this.executeUpdate(sql.toString(), params);
    }

    /**
     * 执行增删改操作
     *
     * @param sql
     * @param params
     * @throws Exception
     */
    public void executeUpdate(String sql, Object[] params) throws Exception {
        logger.info("sql -> {}", sql);
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        injectParams(preparedStatement, params);
        preparedStatement.executeUpdate();
        ConnectionPool.releaseConnection(connection);
        preparedStatement.close();
    }

    public void executeBatch(String sql, List<Object[]> list) throws Exception {
        logger.info("sql -> {}", sql);
        Connection connection = ConnectionPool.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (Object[] params : list) {
            injectParams(preparedStatement, params);
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        ConnectionPool.releaseConnection(connection);
        preparedStatement.close();
    }

    /**
     * 注入参数
     *
     * @param preparedStatement
     * @param params
     * @throws SQLException
     */
    public void injectParams(PreparedStatement preparedStatement, Object[] params) throws SQLException {
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
