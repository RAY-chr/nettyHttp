package netty.http.argument.impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import netty.http.argument.ArgumentResolver;
import netty.http.utils.TypeChecker;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static netty.http.argument.impl.RequestBodyResolver.handleList;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/2
 */
public class BasicReferenceResolver implements ArgumentResolver {
    private static final String LIST = "list";
    @Override
    public boolean handle(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception {
        if (!TypeChecker.isPrimitiveOrString(type) && !TypeChecker.checkHasCollection(type)) {
            JSONObject object = new JSONObject();
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                object.put(entry.getKey(), entry.getValue().get(0));
            }
            checkField(type, object);
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
            if (!TypeChecker.isPrimitiveOrString(fieldType)) {
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
        JSONObject object = new JSONObject();
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            object.put(entry.getKey(), entry.getValue().get(0));
        }
        return injectField(type, object);
    }

    /**
     * 递归注入字段值 可注入普通字段，基本引用，List类型
     *
     * @param type
     * @param object
     * @return
     * @throws Exception
     */
    public static Object injectField(Class<?> type, JSONObject object) throws Exception {
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
                // 基本引用，不包含集合
                if (!TypeChecker.isPrimitiveOrString(fieldType) &&
                        !TypeChecker.checkHasCollection(fieldType)) {
                    if (s != null) {
                        Object o = injectField(fieldType, (JSONObject) s);
                        field.set(instance, o);
                    }
                } else if (!TypeChecker.isPrimitiveOrString(fieldType)
                        && TypeChecker.checkHasCollection(fieldType)) {  // 包含集合
                    if (fieldType == List.class) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put(LIST, s);
                        JSONArray array = (JSONArray) jsonObject.get(LIST);
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Type[] arguments = parameterizedType.getActualTypeArguments();
                        Object o = handleList(fieldType, array, (Class) arguments[0]);
                        field.set(instance, o);
                    }
                } else {   // 普通字段
                    field.set(instance, TypeChecker.parseValue(fieldType, String.valueOf(s)));
                }
            }
        }
        return instance;

    }

}
