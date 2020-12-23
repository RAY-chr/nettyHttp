package netty.dao.connection;

import netty.dao.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

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
    private static final LinkedList<Connection> connections = new LinkedList<>();
    private static final Map<Connection, Long> expired = new HashMap<>();
    private static boolean init = false;
    private static long expiredTime = Long.parseLong(DBConfig.getString("expiredTime"));

    /**
     * 获取连接，如果连接失效，获取有效连接
     * @return
     * @throws Exception
     */
    public synchronized static Connection getConnection() throws Exception {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = connections.removeFirst();
            statement = connection.createStatement();
        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                System.err.println("---------------->>>>>>>> no such <<<<<<<<-------------------");
                for (int i = 0; i < 30; i++) {
                    connections.add(produce());
                }
            } else if (e instanceof SQLException) {
                reSet();
            }
            return connections.removeFirst();
        }
        statement.close();
        if (expired(connection)) {
            connection = getValidConnection();
        }
        expired.put(connection, System.currentTimeMillis() + expiredTime * 1000);
        return connection;
    }

    /**
     * 保证有效连接
     * @return
     * @throws Exception
     */
    private static Connection getValidConnection() throws Exception {
        Iterator<Connection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            Connection next = iterator.next();
            if (expired(next)) {
                iterator.remove();
                System.err.println(" >>>clear the connection ["+next+"]<<< ");
                expired.remove(next);
            }
        }
        Connection removeFirst = null;
        try {
            removeFirst = connections.removeFirst();
        } catch (Exception e) {
            reSet();
            return connections.removeFirst();
        }
        return removeFirst;
    }

    private static boolean expired(Connection connection) {
        long futureTime = expired.get(connection);
        return System.currentTimeMillis() > futureTime;
    }

    public synchronized static void releaseConnection(Connection connection) {
        connections.add(connection);
    }

    public static Connection produce() throws Exception {
        if (!init) {
            Class.forName(driverClass);
            init = true;
        }
        Connection conn = DriverManager.getConnection(url, userName, password);
        expired.put(conn, System.currentTimeMillis() + expiredTime * 1000);
        return conn;
    }

    public synchronized static void reSet() throws Exception {
        System.err.println(" >>>reSet the connection<<< ");
        connections.clear();
        expired.clear();
        for (int i = 0; i < 10; i++) {
            connections.add(produce());
        }
    }

    static {
        try {
            for (int i = 0; i < 10; i++) {
                connections.add(produce());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
