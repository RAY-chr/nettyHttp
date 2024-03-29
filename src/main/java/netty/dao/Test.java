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
import java.util.Map;

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
        for (int i = 2; i < 1000; i++) {
            Book book = new Book(i).setDate(LocalDateTime.now());
            book.setBookNo("100" + i);
            book.setBookName("数学" + i);
            book.setBookState("1");
            list.add(book);
        }
        dao.saveBatch(list);
        List<Book> books = dao.select(new DefaultWrapper().lt("localdate", LocalDateTime.now()
                /*formatter.format(LocalDateTime.now())*/));
        //books.forEach(System.out::println);
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
        List<String> strings = dao.executeQuery(
                "select count(*) total,book_id,book_no from book where book_id = ?",
                new Object[]{1},
                resultSet -> {
                    List<String> list = new ArrayList<>();
                    while (resultSet.next()) {
                        long total = resultSet.getLong("total");
                        String book_id = resultSet.getString("book_id");
                        String book_no = resultSet.getString("book_no");
                        list.add(total + book_id + book_no);
                    }
                    return list;
                });
        System.out.println(strings);

        List<Map<String, Object>> list = dao.selectMaps(
                "select ( SELECT count(*) FROM book ) total,book_id,book_no from book", null);
        //list.forEach(System.out::println);
//        dao.selectList("select count(*) total,book_id,book_no from book where book_id = ?",
//                new Object[]{1});
        List<Map<String, Object>> maps = dao.selectMaps(buildSql(), null);
        maps.forEach(System.out::println);
    }

    private String buildSql() {
        return "\nSELECT\n" +
                "\tj.JOB_NAME,\n" +
                "\tj.DESCRIPTION,\n" +
                "\tt.TRIGGER_STATE 'job status',\n" +
                "\tj.JOB_CLASS_NAME,\n" +
                "\tc.CRON_EXPRESSION \n" +
                "FROM\n" +
                "\tqrtz_triggers t,\n" +
                "\tqrtz_job_details j,\n" +
                "\tqrtz_cron_triggers c \n" +
                "WHERE\n" +
                "\tt.JOB_NAME = j.JOB_NAME \n" +
                "\tAND t.TRIGGER_NAME = c.TRIGGER_NAME";
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
        Book id = bookDao.selectById(10);
        if (id != null) {
            bookDao.deleteById(id.getBookId());
        }
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
