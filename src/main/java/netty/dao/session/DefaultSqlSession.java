package netty.dao.session;

import netty.dao.CommonStr;
import netty.dao.DefaultWrapper;
import netty.dao.annotion.TableId;
import netty.dao.cache.TableColumnCache;
import netty.dao.connection.ConnectionPool;
import netty.dao.dao.BookDao;
import netty.dao.dao.impl.BookDaoImpl;
import netty.dao.entity.Book;
import netty.dao.entity.Renter;
import netty.http.HttpServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class DefaultSqlSession implements SqlSession {
    private static Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);

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
     * 执行增删改操作
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
        StringBuffer sql = new StringBuffer("insert into " + map.get("TABLE") + " ( ");
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

    public static void main(String[] args) throws Exception {
        DefaultSqlSession session = new DefaultSqlSession();
        Book book = new Book();
        book.setBookId(5);
        book.setBookName("fff");
        //session.save(book);
       /* Renter fggg = new Renter("3", "fggg");
        session.save(fggg);*/
        //session.deleteById(Book.class, 5);
        BookDao dao = new BookDaoImpl();
        dao.save(book);
        dao.deleteById(5);
        //dao.delete(new DefaultWrapper().eq("book_id", 5).eq("book_name", "fff"));
    }


}
