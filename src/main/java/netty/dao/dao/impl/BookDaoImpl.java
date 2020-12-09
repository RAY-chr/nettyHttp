package netty.dao.dao.impl;

import netty.dao.annotion.Transactional;
import netty.dao.dao.BookDao;
import netty.dao.entity.Book;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class BookDaoImpl extends BaseDaoImpl<Book> implements BookDao {
    @Override
    @Transactional
    public void testTran() throws Exception {
        this.save(new Book(4));
        System.out.println(this.select(null));
        /*if (true) {
            throw new RuntimeException();
        }*/
        this.save(new Book(5));
    }
}
