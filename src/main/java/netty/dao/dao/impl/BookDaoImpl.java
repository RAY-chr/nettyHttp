package netty.dao.dao.impl;

import netty.dao.annotion.Transactional;
import netty.dao.dao.BookDao;
import netty.dao.dao.RenterDao;
import netty.dao.entity.Book;
import netty.dao.entity.Renter;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;

import java.time.LocalDateTime;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class BookDaoImpl extends BaseDaoImpl<Book> implements BookDao {

    RenterDao renterDao = new RenterDaoImpl();
    @Override
    @Transactional
    public void testTran() throws Exception {
        System.out.println(this.save(new Book(4).setDate(LocalDateTime.now())));
        System.out.println(this.select(null));
        if (true) {
            throw new RuntimeException();
        }
        renterDao.save(new Renter("2","3333"));
        this.save(new Book(5).setDate(LocalDateTime.now()));
    }
}
