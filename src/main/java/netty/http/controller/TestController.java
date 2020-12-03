package netty.http.controller;

import netty.http.annotion.RequestMapping;
import netty.http.annotion.RequestParam;
import netty.http.annotion.ResponseBody;
import netty.http.entity.Book;


/**
 * @author RAY
 * @descriptions
 * @since 2020/12/1
 */
public class TestController {

    @RequestMapping("/test")
    public String test(@RequestParam("code") String code2, long x, @RequestParam("info") String info2) {
        return "hello " + code2 + " " + (x == 0 ? "" : x) + " " + info2;
    }

    @RequestMapping("/model")
    @ResponseBody
    public Book test(Book book) {
        return book;
    }

    @RequestMapping("/json")
    @ResponseBody
    public String test(String json) {
        return json;
    }
}
