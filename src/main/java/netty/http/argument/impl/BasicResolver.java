package netty.http.argument.impl;

import netty.http.argument.ArgumentResolver;
import netty.http.utils.TypeChecker;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/2
 */
public class BasicResolver implements ArgumentResolver {
    @Override
    public boolean handle(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) {
        if (TypeChecker.isPrimitiveOrString(type) && !TypeChecker.checkHasCollection(type)) {
            Parameter[] methodParameters = method.getParameters();
            Parameter parameter = methodParameters[paramIndex];
            return parameters.containsKey(parameter.getName());
        }
        return false;
    }

    @Override
    public Object result(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) {
        Parameter[] methodParameters = method.getParameters();
        Parameter parameter = methodParameters[paramIndex];
        String s = parameters.get(parameter.getName()).get(0);
        return TypeChecker.parseValue(type, s);
    }
}
