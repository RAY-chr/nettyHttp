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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public static SqlExecutor getByType() {
        return SqlExecutorFactory.getSqlExecutor(DBConfig.getString("dbType"));
    }

    public static void main(String[] args) throws Exception {
        BookDao bookDao = new BookDaoImpl();
        System.out.println(bookDao.selectById(1));
        //ExecutorService executorService = Executors.newFixedThreadPool(200);
        SqlSession session = SqlSessionFactory.openSession();
        try {
            //session.beginTransaction();
            List<Book> list = new ArrayList<>();
            for (int i = 3; i < 800; i++) {
                Book book = new Book();
                book.setBookId(i);
                book.setBookNo("100"+i);
                list.add(book);
            }
            //System.out.println(session.saveBatch(list));
            //executorService.shutdown();
            List<?> book_id = session.select(Book.class,
                    new DefaultWrapper().eq("book_state", 1)
                                        .like("book_name", "数"));
            System.out.println(book_id);
            System.out.println(session.selectById(Book.class, 1));

            //session.commit();
        } catch (Exception e) {
            //session.rollback();
            //e.printStackTrace();
        }
        //Renter fggg = new Renter("3", "fggg");
        //session.save(fggg);
        //session.deleteById(Book.class, 5);
        //BookDao dao = new BookDaoImpl();
        //dao.save(book);
        //dao.deleteById(5);
        //dao.delete(new DefaultWrapper().eq("book_id", 5).eq("book_name", "fff"));
    }


}
