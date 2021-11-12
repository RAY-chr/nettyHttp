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
    private static long max = 40;
    private static int count = 0;

    /**
     * 获取连接，如果连接失效，获取有效连接
     * 池子的连接最大加到 max 就会等待，等连接释放的时候，会判断继续等待还是返回连接
     *
     * @return Connection
     * @throws Exception Exception
     */
    public synchronized static Connection getConnection() throws Exception {
        Connection connection;
        Statement statement;
        try {
            connection = connections.removeFirst();
            statement = connection.createStatement();
        } catch (Exception e) {
            if (e instanceof NoSuchElementException) {
                if (count >= max) {
                    while (count >= max && connections.size() == 0) {
                        ConnectionPool.class.wait();
                    }
                    return removeFirst();
                }
                System.err.println("---------------->>>>>>>> no such <<<<<<<<-------------------");
                for (int i = 0; i < 30; i++) {
                    connections.add(produce());
                }
                count += 30;
            } else if (e instanceof SQLException) {
                reSet();
            }
            return removeFirst();
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
     *
     * @return Connection
     * @throws Exception Exception
     */
    private static Connection getValidConnection() throws Exception {
        Iterator<Connection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            Connection next = iterator.next();
            if (expired(next)) {
                iterator.remove();
                System.err.println(" >>>clear the connection [" + next + "]<<< ");
                expired.remove(next);
            }
        }
        Connection removeFirst;
        try {
            removeFirst = connections.removeFirst();
        } catch (Exception e) {
            reSet();
            return removeFirst();
        }
        expired.put(removeFirst, System.currentTimeMillis() + expiredTime * 1000);
        return removeFirst;
    }

    private static boolean expired(Connection connection) {
        long futureTime = expired.get(connection);
        return System.currentTimeMillis() > futureTime;
    }

    /**
     * 返回连接前修改其时间戳
     *
     * @return Connection
     */
    private static Connection removeFirst() {
        Connection connection = connections.removeFirst();
        expired.put(connection, System.currentTimeMillis() + expiredTime * 1000);
        return connection;
    }

    public synchronized static void releaseConnection(Connection connection) {
        connections.add(connection);
        ConnectionPool.class.notifyAll();
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
        count = 10;
    }

    static {
        try {
            for (int i = 0; i < 10; i++) {
                connections.add(produce());
            }
            count += 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
