package netty.dao;

import netty.dao.dao.BookDao;
import netty.dao.dao.ProxyDao;
import netty.dao.dao.impl.BookDaoImpl;
import netty.dao.entity.Book;

import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/9
 */
public class Test {

    BookDao dao = ProxyDao.getDao(BookDao.class, new BookDaoImpl());

    /**
     * 测试事务回滚
     *
     * @throws Exception
     */
    @org.junit.Test
    public void demo1() throws Exception {
        dao.testTran();
    }

    @org.junit.Test
    public void demo2() throws Exception {
        List<Book> books = dao.select(new DefaultWrapper().groupBy("book_state", "book_id")
                .orderBy("book_id").eq("book_state",1).limit(0,3));
        books.forEach(System.out::println);
    }
}
