package netty.http.utils;

import com.alibaba.druid.sql.visitor.functions.Char;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/3
 */
public class BasicTypeChecker {
    private static final Set<Class<?>> checker = new HashSet<>();


    /**
     * 判断类型是否是基本及其包装类类型
     * @param type
     * @return
     */
    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || checker.contains(type);
    }

    /**
     *
     * @param type
     * @param value
     * @return
     */
    public static Object parseValue(Class<?> type, String value) {
        if (value == null) {
            return null;
        }
        if ("".equals(value)) {
            boolean base = type.equals(int.class) || type.equals(double.class) ||
                    type.equals(short.class) || type.equals(long.class) ||
                    type.equals(byte.class) || type.equals(float.class);
            if (base) {
                return 0;
            }
        }
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (type.equals(String.class)) {
            return value;
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(value);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return Short.parseShort(value);
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        }

        return null;
    }


    static {
        checker.add(Byte.class);
        checker.add(Short.class);
        checker.add(Integer.class);
        checker.add(Long.class);
        checker.add(Float.class);
        checker.add(Double.class);
        checker.add(Char.class);
        checker.add(Boolean.class);
    }
}
