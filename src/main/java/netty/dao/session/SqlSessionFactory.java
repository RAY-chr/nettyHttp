package netty.dao.session;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class SqlSessionFactory {
    private static final ThreadLocal<SqlSession> session = new ThreadLocal<>();

    public static SqlSession openSession() {
        return new DefaultSqlSession();
    }

    /**
     * 这种获得session的方式需要及时清除
     *
     * @return SqlSession
     */
    public static SqlSession getCurrentSession() {
        if (session.get() == null) {
            session.set(new DefaultSqlSession());
        }
        return session.get();
    }

    public static void closeCurrentSession() {
        SqlSession sqlSession = session.get();
        if (sqlSession != null) {
            session.remove();
        }
    }


}
