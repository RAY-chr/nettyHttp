package netty.dao.session;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class SqlSessionFactory {
    private static final ThreadLocal<SqlSession> session = new ThreadLocal<>();

    public static SqlSession openSession() {
        if (session.get() == null) {
            session.set(new DefaultSqlSession());
        }
        return session.get();
    }


}
