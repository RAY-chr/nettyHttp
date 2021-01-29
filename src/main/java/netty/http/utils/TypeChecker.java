package netty.http.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/3
 */
public class TypeChecker {
    private static final Set<Class<?>> checker = new HashSet<>();
    // 从数据库返回的时间是这种时间戳
    private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.0");


    /**
     * 判断类型是否是基本及其包装类类型
     *
     * @param type
     * @return
     */
    public static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || checker.contains(type);
    }

    /**
     * 判断类型是否是基本及其包装类类型
     *
     * @param type
     * @return
     */
    public static boolean isPrimitiveOrString(Class<?> type) {
        return isPrimitive(type) || type == String.class;
    }

    /**
     * 检验是否有特定注解
     *
     * @param method
     * @param paramIndex
     * @param type
     * @return
     */
    public static boolean checkAnnotation(Method method, int paramIndex, Class<?> type) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (Annotation annotation : annotations[paramIndex]) {
            if (annotation.annotationType().equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检验类型是否包含集合
     * @param type
     * @return
     */
    public static boolean checkHasCollection(Class<?> type) {
        if (Collection.class.isAssignableFrom(type)) {
            return true;
        }
        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (Collection.class.isAssignableFrom(fieldType)) {
                return true;
            }
        }
        return false;
    }

    /**
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
        }else if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(value, dateTimeFormatter);
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
        checker.add(Character.class);
        checker.add(Boolean.class);
    }
}
