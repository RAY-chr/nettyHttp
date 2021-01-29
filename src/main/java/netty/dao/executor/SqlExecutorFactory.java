package netty.dao.executor;

import netty.http.utils.PackageScanner;

import java.util.Set;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class SqlExecutorFactory {
    private static final String executorPath = "netty.dao.executor.impl";
    private static final Set<Class<?>> executors = PackageScanner.getClasses(executorPath);

    /*public static SqlExecutor getSqlExecutor(String type) {
        if (type.equals(DBType.MYSQL)) {
            return new MysqlExecutor();
        } else if (type.equals(DBType.ORACLE)) {
            return new OracleSqlExecutor();
        }
        return null;
    }*/

    public static SqlExecutor getSqlExecutor(String type) {
        for (Class<?> executor : executors) {
            String simpleName = executor.getSimpleName();
            if (simpleName.toUpperCase().startsWith(type)) {
                try {
                    Object instance = executor.newInstance();
                    if (instance instanceof SqlExecutor) {
                        return (SqlExecutor) instance;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException("missing the type: "+type);
    }
}
