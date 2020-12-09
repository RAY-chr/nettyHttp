package netty.http.controller;

import netty.dao.dao.BookDao;
import netty.dao.dao.impl.BookDaoImpl;
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

    /**
     * 普通字段接收
     * @param code2
     * @param x
     * @param info2
     * @return
     */
    @RequestMapping("/test")
    public String test(@RequestParam("code") String code2, @RequestParam("x")long x, @RequestParam("info") String info2) throws Exception {
        long l = System.currentTimeMillis();
        BookDaoImpl bookDao = new BookDaoImpl();
        List<netty.dao.entity.Book> books = bookDao.select(null);
        System.out.println(books);
        System.out.println(System.currentTimeMillis()-l);
        return "hello " + code2 + " " + (x == 0 ? "" : x) + " " + info2;
    }

    /**
     * 基本引用类型，字段不包含集合
     * @param book
     * @return
     */
    @RequestMapping("/model")
    @ResponseBody
    public Book test(Book book) {
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
    public List<Book> test(@RequestBody List<Book> list) {
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
