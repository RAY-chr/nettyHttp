package netty.dao.dao;

import netty.dao.annotion.Transactional;
import netty.dao.entity.Book;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public interface BookDao extends BaseDao<Book> {

    @Transactional
    void testTran() throws Exception;

}
