package netty.http.argument.impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import netty.http.annotion.RequestBody;
import netty.http.argument.ArgumentResolver;
import netty.http.utils.TypeChecker;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static netty.http.argument.impl.BasicReferenceResolver.injectField;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/4
 */
public class RequestBodyResolver implements ArgumentResolver {

    private static final String LIST = "list";
    private static final String MAP = "map";

    @Override
    public boolean handle(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception {
        return TypeChecker.checkAnnotation(method, paramIndex, RequestBody.class)
                && TypeChecker.checkHasCollection(type);
    }

    @Override
    public Object result(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception {
        // 直接一个List
        if (List.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            Parameter[] methodParameters = method.getParameters();
            Parameter parameter = methodParameters[paramIndex];
            List<String> list = parameters.get(parameter.getName());
            if (list != null) {
                String s = list.get(0);
                JSONObject object = new JSONObject();
                object.put(LIST, s);
                JSONArray array = (JSONArray) object.get(LIST);
                ParameterizedType parameterizedType = (ParameterizedType) parameter.getParameterizedType();
                Type[] arguments = parameterizedType.getActualTypeArguments();
                return handleList(type, array, (Class) arguments[0]);
            }
        } else { // 实体类 里面包含集合
            JSONObject object = new JSONObject();
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                object.put(entry.getKey(), entry.getValue().get(0));
            }
            return injectField(type, object);
        }
        throw new UnsupportedOperationException("not support to resolve collection excepet List and Map");
    }

    /**
     * 处理 List 集合类型
     * @param type
     * @param array
     * @param parameterizedType
     * @return
     * @throws Exception
     */
    public static Object handleList(Class<?> type, JSONArray array, Class parameterizedType) throws Exception {
        List list = new ArrayList();
        if (type == List.class) {
            Object[] objects = array.toArray();
            // 基本类型
            if (TypeChecker.isPrimitiveOrString(parameterizedType)) {
                for (Object o : objects) {
                    list.add(TypeChecker.parseValue(parameterizedType, o.toString()));
                }
            } else if (!TypeChecker.isPrimitiveOrString(parameterizedType) &&
                    !TypeChecker.checkHasCollection(parameterizedType)) {  // 基本引用，不包含集合
                for (Object o : objects) {
                    Object instance = injectField(parameterizedType, (JSONObject) o);
                    list.add(instance);
                }
            }else {
                throw new UnsupportedOperationException("not support to resolve this list");
            }
        }
        return list;
    }
}
