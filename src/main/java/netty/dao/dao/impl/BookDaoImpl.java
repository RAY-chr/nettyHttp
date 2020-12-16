package netty.dao.dao.impl;

import netty.dao.annotion.Dao;
import netty.dao.annotion.Transactional;
import netty.dao.dao.BookDao;
import netty.dao.entity.Book;

import java.time.LocalDateTime;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
@Dao
public class BookDaoImpl extends BaseDaoImpl<Book> implements BookDao {
    @Override
    @Transactional
    public void testTran() throws Exception {
        System.out.println(this.save(new Book(4).setDate(LocalDateTime.now())));
        System.out.println(this.select(null));
        if (true) {
            throw new RuntimeException();
        }
        this.save(new Book(5).setDate(LocalDateTime.now()));
    }
}
