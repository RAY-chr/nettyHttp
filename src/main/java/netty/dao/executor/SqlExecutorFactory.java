package netty.dao.executor;

import netty.dao.DBType;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class SqlExecutorFactory {

    public static SqlExecutor getSqlExecutor(String type) {
        if (type.equals(DBType.MYSQL)) {
            return new MysqlExecutor();
        } else if (type.equals(DBType.ORACLE)) {
            return new OracleSqlExecutor();
        }
        return null;
    }
}
