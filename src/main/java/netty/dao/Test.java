package netty.dao;

import netty.dao.dao.BookDao;
import netty.dao.dao.ProxyDao;
import netty.dao.entity.Book;
import netty.dao.page.Page;

import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/9
 */
public class Test {

    static BookDao dao = ProxyDao.getDao(BookDao.class);

    public static void main(String[] args) throws Exception {
        dao.testTran();
        new Thread(()-> {
            try {
                List<Book> books = Test.dao.select(null);
                books.forEach(System.out::println);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 测试事务回滚
     *
     * @throws Exception
     */
    @org.junit.Test
    public void demo1() throws Exception {
        dao.testTran();
        new Thread(()-> {
            try {
                List<Book> books = dao.select(null);
                System.out.println(books.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @org.junit.Test
    public void demo2() throws Exception {
        Page<Book> page = dao.selectPage(new Page<>(1, 3),
                new DefaultWrapper().eq("book_state","1"));
        List<Book> records = page.getRecords();
        List<Book> books = dao.select(new DefaultWrapper().groupBy("book_state", "book_id")
                .orderBy("book_id").eq("book_state",1).limit(0,3));
        books.forEach(System.out::println);
        System.out.println(dao.selectById(4));
    }
}
