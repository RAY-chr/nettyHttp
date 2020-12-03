package netty.http.argument.impl;

import net.sf.json.JSONObject;
import netty.http.argument.ArgumentResolver;
import netty.http.utils.BasicTypeChecker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/2
 */
public class BasicReferenceResolver implements ArgumentResolver {
    @Override
    public boolean handle(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception {
        if (!BasicTypeChecker.isPrimitive(type) && type != String.class) {
            Field[] fields = type.getDeclaredFields();
            if (fields.length < parameters.size()) {
                for (Field field : fields) {
                    parameters.keySet().remove(field.getName());
                }
                throw new IllegalArgumentException("amount of arguments is more -> " + parameters.keySet());
            }
            Set<String> set = new HashSet<>();
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                if (fieldType == type) {
                    throw new IllegalArgumentException("member can't be itself");
                }
                if (!BasicTypeChecker.isPrimitive(fieldType) && fieldType != String.class) {
                    List<String> list = parameters.get(field.getName());
                    if (list != null) {
                        String s = list.get(0);
                        if (s != null) {
                            checkField(fieldType, JSONObject.fromObject(s));
                        }
                    }
                }
                String name = field.getName();
                set.add(name);
            }
            for (String s : parameters.keySet()) {
                if (!set.contains(s)) {
                    throw new IllegalArgumentException("arguments: [" + s + "] not exist");
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 递归校验字段
     *
     * @param type
     * @param object
     */
    public void checkField(Class<?> type, JSONObject object) {
        Field[] fields = type.getDeclaredFields();
        if (fields.length < object.keySet().size()) {
            Set set = object.keySet();
            Object[] objects = set.toArray();
            Set<Object> objectSet = new HashSet<>();
            for (Object o : objects) {
                objectSet.add(o);
            }
            for (Field field : fields) {
                if (objectSet.contains(field.getName())) {
                    objectSet.remove(field.getName());
                }
            }
            throw new IllegalArgumentException("amount of arguments is more -> " + objectSet);
        }
        Set<Object> set = new HashSet<>();
        for (Field field : fields) {
            Class<?> fieldType = field.getType();
            if (fieldType == type) {
                throw new IllegalArgumentException("member can't be itself");
            }
            if (!BasicTypeChecker.isPrimitive(fieldType) && fieldType != String.class) {
                Object s = object.get(field.getName());
                if (s != null) {
                    checkField(fieldType, JSONObject.fromObject(s));
                }
            }
            String name = field.getName();
            set.add(name);
        }
        for (Object s : object.keySet()) {
            if (!set.contains(s)) {
                throw new IllegalArgumentException("arguments: [" + s + "] not exist");
            }
        }

    }

    @Override
    public Object result(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception {
        Field[] fields = type.getDeclaredFields();
        if (fields.length < parameters.size()) {
            throw new IllegalArgumentException("amount of arguments is more");
        }
        Object instance = type.newInstance();
        for (Field field : fields) {
            String name = field.getName();
            if (parameters.containsKey(name)) {
                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                List<String> list = parameters.get(name);
                if (list != null) {
                    String s = list.get(0);
                    if (!BasicTypeChecker.isPrimitive(fieldType) && fieldType != String.class) {
                        if (s != null) {
                            Object o = injectField(fieldType, JSONObject.fromObject(s));
                            field.set(instance, o);
                        }
                    } else {
                        field.set(instance, BasicTypeChecker.parseValue(field.getType(), s));
                    }
                }
            }
        }
        return instance;
    }

    /**
     * 递归注入字段值
     *
     * @param type
     * @param object
     * @return
     * @throws Exception
     */
    public Object injectField(Class<?> type, JSONObject object) throws Exception {
        Field[] fields = type.getDeclaredFields();
        if (fields.length < object.keySet().size()) {
            throw new IllegalArgumentException("amount of arguments is more");
        }
        Object instance = type.newInstance();
        for (Field field : fields) {
            String name = field.getName();
            if (object.containsKey(name)) {
                Class<?> fieldType = field.getType();
                field.setAccessible(true);
                Object s = object.get(name);
                if (!BasicTypeChecker.isPrimitive(fieldType) && fieldType != String.class) {
                    if (s != null) {
                        Object o = injectField(fieldType, JSONObject.fromObject(s));
                        field.set(instance, o);
                    }
                } else {
                    field.set(instance, BasicTypeChecker.parseValue(fieldType, String.valueOf(s)));
                }
            }
        }
        return instance;

    }

}
