package netty.dao.session;

import netty.dao.CommonStr;
import netty.dao.DBConfig;
import netty.dao.DefaultWrapper;
import netty.dao.annotion.TableId;
import netty.dao.cache.TableColumnCache;
import netty.dao.connection.ConnectionPool;
import netty.dao.dao.BookDao;
import netty.dao.dao.impl.BookDaoImpl;
import netty.dao.entity.Book;
import netty.dao.entity.Renter;
import netty.dao.executor.MysqlExecutor;
import netty.dao.executor.SqlExecutor;
import netty.dao.executor.SqlExecutorFactory;
import netty.http.HttpServerHandler;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.security.AlgorithmConstraints;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class DefaultSqlSession implements SqlSession {
    private static Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);
    private SqlExecutor sqlExecutor = getByType();

    /**
     * 增加操作
     *
     * @param entity
     * @throws Exception
     */
    public void save(Object entity) throws Exception {
        sqlExecutor.save(entity);
    }

    @Override
    public void saveBatch(List<?> list) throws Exception {
        sqlExecutor.saveBatch(list);
    }

    /**
     * 根据主键删除
     *
     * @param clazz
     * @param id
     * @throws Exception
     */
    public void deleteById(Class<?> clazz, Serializable id) throws Exception {
        sqlExecutor.deleteById(clazz, id);
    }

    /**
     * 根据主键更改
     *
     * @param entity
     * @throws Exception
     */
    @Override
    public void updateById(Object entity) throws Exception {
        sqlExecutor.updateById(entity);
    }

    /**
     * 根据条件删除
     *
     * @param clazz
     * @param wrapper
     * @throws Exception
     */
    public void delete(Class<?> clazz, DefaultWrapper wrapper) throws Exception {
        sqlExecutor.delete(clazz, wrapper);
    }

    public static SqlExecutor getByType() {
        return SqlExecutorFactory.getSqlExecutor(DBConfig.getString("dbType"));
    }

    public static void main(String[] args) throws Exception {
        SqlSession session = SqlSessionFactory.openSession();
        List<Book> list = new ArrayList<>();
        Book book = new Book();
        book.setBookId(5);
        book.setBookNo("1003");
        book.setBookName("fff22");
        book.setBookState("1");
        Book book2 = new Book();
        book2.setBookId(4);
        book2.setBookNo("1003");
        book2.setBookName("fff22");
        book2.setBookState("1");
        list.add(book);
        list.add(book2);
        session.saveBatch(list);
        //session.save(book);
        //Renter fggg = new Renter("3", "fggg");
        //session.save(fggg);
        //session.deleteById(Book.class, 5);
        //BookDao dao = new BookDaoImpl();
        //dao.save(book);
        //dao.deleteById(5);
        //dao.delete(new DefaultWrapper().eq("book_id", 5).eq("book_name", "fff"));
    }


}
