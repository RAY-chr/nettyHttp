package netty.http.argument.impl;

import netty.http.annotion.RequestParam;
import netty.http.argument.ArgumentResolver;
import netty.http.utils.BasicTypeChecker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/2
 */
public class RequestParamResolver implements ArgumentResolver {
    @Override
    public boolean handle(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (Annotation annotation : annotations[paramIndex]) {
            if (annotation.annotationType().equals(RequestParam.class)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object result(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (Annotation annotation : annotations[paramIndex]) {
            if (annotation.annotationType().equals(RequestParam.class)) {
                String value = ((RequestParam) annotation).value();
                List<String> list = parameters.get(value);
                Objects.requireNonNull(list, "request binding the method missing required argument: " + value);
                String s = list.get(0);
                return BasicTypeChecker.parseValue(type, s);
            }
        }
        throw new RuntimeException("method named result must be used after handle");
    }
}
