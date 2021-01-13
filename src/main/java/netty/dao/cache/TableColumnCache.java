package netty.dao.cache;

import netty.dao.CommonStr;
import netty.dao.DBConfig;
import netty.dao.DBType;
import netty.dao.annotion.TableField;
import netty.dao.annotion.TableId;
import netty.dao.annotion.TableName;
import netty.dao.connection.ConnectionPool;
import netty.http.utils.PackageScanner;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class TableColumnCache {
    private static final String select = "select * from ";
    private static final String entityPath = "netty.dao.entity";
    // 实体类与表的映射关系
    private static final Map<String, Map<String, String>> cache = new ConcurrentHashMap<>();

    public static void put(String entityName, Map<String, String> map) {
        cache.put(entityName, map);
    }

    public static Map<String, String> get(String entityName) {
        return cache.get(entityName);
    }

    static {
        Set<Class<?>> classes = PackageScanner.getClasses(entityPath);
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Class<?> aClass : classes) {
            String simpleName = aClass.getSimpleName();
            String tableName = simpleName;
            TableName annotation = aClass.getAnnotation(TableName.class);
            if (annotation != null) {
                tableName = annotation.value();
            }
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        sql(DBConfig.getString("dbType"), tableName));
                ResultSet rs = null;
                try {
                    rs = preparedStatement.executeQuery();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("tableName not exist in the database");
                }
                ResultSetMetaData metaData = rs.getMetaData();
                //表列数
                int size = metaData.getColumnCount();
                Map<String, String> map = new HashMap<>();
                map.put(CommonStr.TABLE, tableName);
                for (int i = 0; i < size; i++) {
                    String columnName = metaData.getColumnName(i + 1);
                    if (DBConfig.getString("dbType").equals(DBType.ORACLE)) {
                        columnName = columnName.toLowerCase();
                        if (columnName.equals("rn")) {
                            continue;
                        }
                    }
                    Field field = null;
                    try {
                        field = aClass.getDeclaredField(columnName);
                    } catch (NoSuchFieldException e) {
                        field = getField(aClass, columnName);
                    }
                    assert field != null;
                    if (field.getAnnotation(TableId.class) != null) {
                        map.put(CommonStr.PRIMARYKEY, columnName);
                    } else {
                        map.put(field.getName(), columnName);
                    }
                }
                rs.close();
                preparedStatement.close();
                put(simpleName, map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ConnectionPool.releaseConnection(connection);
    }

    public static Field getField(Class<?> aClass, String columnName) {
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            TableField annotation = field.getAnnotation(TableField.class);
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                if (tableId.value().equals(columnName)) {
                    return field;
                }
            }
            if (annotation != null) {
                if (annotation.value().equals(columnName)) {
                    return field;
                }
            }
        }
        throw new RuntimeException("{ " + columnName + " }" + " can't be mapping the Bean : " + aClass.getName());
    }

    public static String sql(String type, String tableName) {
        if (type.equals(DBType.MYSQL)) {
            return select + tableName + " limit 0,1";
        } else if (type.equals(DBType.ORACLE)) {
            StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT a.*, ROWNUM rn FROM ( ");
            sql.append("SELECT * FROM ");
            sql.append(tableName.toUpperCase());
            sql.append(" ) a WHERE ROWNUM < 2) WHERE rn > 0");
            return sql.toString();
        }
        throw new IllegalArgumentException(" not support the type ");
    }
}
