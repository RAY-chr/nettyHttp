package netty.dao.executor;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class SqlExecutorFactory {

    public static SqlExecutor getSqlExecutor(String type) {
        if (type.equals("MYSQL")) {
            return new MysqlExecutor();
        }
        return null;
    }
}
