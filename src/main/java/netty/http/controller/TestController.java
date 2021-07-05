package netty.http.controller;

import netty.dao.DefaultWrapper;
import netty.dao.dao.BookDao;
import netty.dao.dao.ProxyDao;
import netty.dao.dao.RenterDao;
import netty.dao.dao.impl.BookDaoImpl;
import netty.dao.entity.Renter;
import netty.dao.page.Page;
import netty.http.annotion.RequestBody;
import netty.http.annotion.RequestMapping;
import netty.http.annotion.RequestParam;
import netty.http.annotion.ResponseBody;
import netty.http.entity.Book;
import netty.http.entity.Person;

import java.util.List;


/**
 * @author RAY
 * @descriptions
 * @since 2020/12/1
 */
public class TestController {

    static BookDao dao = ProxyDao.getDao(BookDao.class);

    static RenterDao renterDao = ProxyDao.getDao(RenterDao.class);
    /**
     * 普通字段接收
     * @param code2
     * @param x
     * @param info2
     * @return
     */
    @RequestMapping("/test")
    public String test(@RequestParam("code") String code2, @RequestParam("x")long x, @RequestParam("info") String info2) throws Exception {
        BookDaoImpl bookDao = new BookDaoImpl();
        long start = System.currentTimeMillis();
        //List<netty.dao.entity.Book> books = bookDao.select(null);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        //System.out.println(books);
        return "hello " + code2 + " " + (x == 0 ? "" : x) + " " + info2;
    }

    @RequestMapping("/page")
    @ResponseBody
    public Page<netty.dao.entity.Book> page(@RequestParam("curr") long curr,
                                            @RequestParam("size") long size) throws Exception {
        long start = System.currentTimeMillis();
        Page<netty.dao.entity.Book> page = dao.selectPage(new Page<netty.dao.entity.Book>(curr, size),
                new DefaultWrapper().eq("book_state","1").orderBy("book_id"));
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        renterDao.selectPage(new Page<Renter>(1,2),null);
        return page;
    }

    /**
     * 基本引用类型，字段不包含集合
     * @param book
     * @return
     */
    @RequestMapping("/model")
    @ResponseBody
    public Book test(Book book) {
        book.setBookName("1111111");
        return book;
    }

    /**
     * json 传入一段json字符串 用String接收 最前面的json字符串key要么等于 RequestParam的值
     *      要么等于形参
     * @param ff
     * @return
     */
    @RequestMapping("/json")
    @ResponseBody
    public String test(@RequestParam("json") String ff) {
        return ff;
    }

    /**
     * 接收一个list 必须加 @RequestBody注解
     * @param list
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public List<Book> test(@RequestBody("list") List<Book> list) {
        return list;
    }

    /**
     * 接收一个包含集合的引用类型  必须加 @RequestBody注解
     * @param person
     * @return
     */
    @RequestMapping("/person")
    @ResponseBody
    public Person test(@RequestBody Person person) {
        return person;
    }
}
