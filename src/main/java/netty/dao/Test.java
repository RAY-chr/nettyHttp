package netty.dao;

import netty.dao.dao.BookDao;
import netty.dao.dao.ProxyDao;
import netty.dao.dao.RenterDao;
import netty.dao.dao.impl.BookDaoImpl;
import netty.dao.dao.impl.RenterDaoImpl;
import netty.dao.entity.Book;
import netty.dao.entity.Renter;
import netty.dao.page.Page;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/9
 */
public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);

    static BookDao dao = ProxyDao.getDao(BookDao.class);

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws Exception {
        /*dao.testTran();
        new Thread(() -> {
            try {
                List<Book> books = Test.dao.select(null);
                books.forEach(System.out::println);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();*/
        demo3();
    }

    /**
     * 测试事务回滚
     *
     * @throws Exception
     */
    @org.junit.Test
    public void demo1() throws Exception {
        List<Book> list = new ArrayList<>();
        for (int i = 1; i < 1000; i++) {
            Book book = new Book(i).setDate(LocalDateTime.now());
            book.setBookNo("100" + i);
            book.setBookName("数学" + i);
            book.setBookState("1");
            list.add(book);
        }
        dao.saveBatch(list);
        List<Book> books = dao.select(new DefaultWrapper().lt("localdate", LocalDateTime.now()
                /*formatter.format(LocalDateTime.now())*/));
        books.forEach(System.out::println);
        //Book book = dao.selectById(1);
        //System.out.println(book);
        //dao.delete(new DefaultWrapper().gt("book_id",1));
        //dao.updateById(new Book(1).setDate(LocalDateTime.now()));
        /*SqlSession currentSession = SqlSessionFactory.getCurrentSession();
        try {
            currentSession.beginTransaction();
            dao.saveBatch(list);
            currentSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            currentSession.rollback();
        }*/
        //dao.delete(new DefaultWrapper().gt("book_id",3));
    }

    @org.junit.Test
    public void demo2() throws Exception {
        Page<Book> page = dao.selectPage(new Page<>(1, 3),
                new DefaultWrapper().eq("book_state", "1 or 1 = 1"));
        List<Book> records = page.getRecords();
        records.forEach(System.out::println);
        /*List<Book> books = dao.select(new DefaultWrapper().groupBy("book_state", "book_id")
                .orderBy("book_id").eq("book_state",1).limit(0,3));
        books.forEach(System.out::println);*/
        System.out.println(dao.selectById(4));
    }

    /**
     * 开启事务的时候，本线程新增数据之后，再查询是能查到的，但是其他线程在没提交之前是查不到的
     * 不要用 Junit 测试多线程的东西  因为 Junit 中主线程执行完就退出了
     *
     * @throws Exception
     */
    //@org.junit.Test
    public static void demo3() throws Exception {
        BookDao bookDao = new BookDaoImpl();
        RenterDao renterDao = new RenterDaoImpl();
        SqlSession currentSession = SqlSessionFactory.getCurrentSession();
        currentSession.beginTransaction();
        Book book = new Book(10).setDate(LocalDateTime.now());
        book.setBookNo("100" + 10);
        book.setBookName("数学" + 10);
        book.setBookState("1");
        new Thread(() -> {
            synchronized (Test.class) {
                try {
                    Test.class.wait();
                    logger.info("no commit before, sub thread ===>>> {}", bookDao.selectById(10));
                    Test.class.notify();
                    Test.class.wait();
                    logger.info("commit after, sub thread ===>>> {}", bookDao.selectById(10));
                    Test.class.notify();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            //System.out.println(bookDao.selectById(10));
            bookDao.save(book);
            synchronized (Test.class) {
                Test.class.notify();
                Test.class.wait();
            }
            logger.info("main thread ===>>> {}", bookDao.selectById(10));
            //int x = 1 / 0;
            //renterDao.save(new Renter("11", "22222"));
            //int x = 1 / 0;
            currentSession.commit();
            synchronized (Test.class) {
                Test.class.notify();
                Test.class.wait();
            }
        } catch (Exception e) {
            logger.error("occurs the mistake, rollback", e);
            currentSession.rollback();
        } finally {
            SqlSessionFactory.closeCurrentSession();
        }
    }


}
