package netty.dao.cache;

import netty.dao.CommonStr;
import netty.dao.annotion.TableField;
import netty.dao.annotion.TableId;
import netty.dao.annotion.TableName;
import netty.dao.connection.ConnectionPool;
import netty.http.utils.PackageScanner;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
        for (Class<?> aClass : classes) {
            String simpleName = aClass.getSimpleName();
            String tableName = simpleName;
            TableName annotation = aClass.getAnnotation(TableName.class);
            if (annotation != null) {
                tableName = annotation.value();
            }
            try {
                Connection connection = ConnectionPool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(select + tableName);
                ResultSet rs = preparedStatement.executeQuery();
                ResultSetMetaData metaData = rs.getMetaData();
                //表列数
                int size = metaData.getColumnCount();
                Map<String, String> map = new HashMap<>();
                map.put(CommonStr.TABLE, tableName);
                for (int i = 0; i < size; i++) {
                    String columnName = metaData.getColumnName(i + 1);
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
                put(simpleName, map);
                ConnectionPool.releaseConnection(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
}
