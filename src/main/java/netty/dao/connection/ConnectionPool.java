package netty.dao.connection;

import netty.dao.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class ConnectionPool {
    private static String driverClass = DBConfig.getString("driverClass");
    private static String url = DBConfig.getString("url");
    private static String userName = DBConfig.getString("userName");
    private static String password = DBConfig.getString("password");
    private static final LinkedList<Connection> CONNECTIONS = new LinkedList<>();
    private static boolean init = false;

    public synchronized static Connection getConnection() throws Exception {
        Connection connection = null;
        try {
            connection = CONNECTIONS.removeFirst();
        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                for (int i = 0; i < 10; i++) {
                    CONNECTIONS.add(produce());
                }
            }
            return CONNECTIONS.removeFirst();
        }
        return connection;
    }

    public synchronized static void releaseConnection(Connection connection) {
        CONNECTIONS.add(connection);
    }

    public static Connection produce() throws Exception {
        if (!init) {
            Class.forName(driverClass);
            init = true;
        }
        Connection conn = DriverManager.getConnection(url, userName, password);
        return conn;
    }

    public synchronized static void reSet() throws Exception {
        CONNECTIONS.clear();
        for (int i = 0; i < 10; i++) {
            CONNECTIONS.add(produce());
        }
    }

    static {
        try {
            for (int i = 0; i < 10; i++) {
                CONNECTIONS.add(produce());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
