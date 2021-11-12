package netty.http.route;

import netty.http.annotion.RequestMapping;
import netty.http.argument.ArgumentResolver;
import netty.http.utils.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/1
 */
public class RouteMethod {
    private static final Map<String, Method> ROUTE = new ConcurrentHashMap<>();
    private static final Map<String, Object> BEAN = new ConcurrentHashMap<>();
    private static final List<ArgumentResolver> RESOLVERS = new ArrayList<>();
    private static final String controllerPath = "netty.http.controller";
    private static final String argumentResolverPath = "netty.http.argument.impl";
    private static Logger logger = LoggerFactory.getLogger(RouteMethod.class);

    public static Method route(String path) {
        return ROUTE.get(path);
    }

    public static Object bean(String name) {
        return BEAN.get(name);
    }

    /**
     * 初始化 url和 method的对应关系
     */
    public static void init() {
        logger.info("start init route -> method");
        if (ROUTE.size() == 0) {
            Set<Class<?>> classes = PackageScanner.getClasses(controllerPath);
            for (Class<?> aClass : classes) {
                try {
                    BEAN.put(aClass.getName(), aClass.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Method[] methods = aClass.getMethods();
                for (Method method : methods) {
                    RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                    if (annotation != null) {
                        ROUTE.put(annotation.value(), method);
                        logger.info("path [{}] -> method [{}]", annotation.value(), method);
                    }
                }
            }
        }
        logger.info("start init resolvers");
        resolvers();
    }

    /**
     * 反射时解析并设置参数
     *
     * @param method 路径匹配到的方法
     * @param parameters 参数对
     * @return 解析后的方法的参数
     * @throws Exception Exception
     */
    public static Object[] parseRouteParameter(Method method, Map<String, List<String>> parameters) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            return null;
        }
        Object[] instances = new Object[parameterTypes.length];
        for (int i = 0; i < instances.length; i++) {
            boolean support = false;
            for (ArgumentResolver resolver : RESOLVERS) {
                if (resolver.handle(parameterTypes[i], method, parameters, i)) {
                    instances[i] = resolver.result(parameterTypes[i], method, parameters, i);
                    support = true;
                    break;
                }
            }
            if (!support) {
                throw new UnsupportedOperationException("not support to resolve the arguments");
            }
        }
        return instances;
    }

    public static void resolvers() {
        Set<Class<?>> classes = PackageScanner.getClasses(argumentResolverPath);
        for (Class<?> aClass : classes) {
            try {
                Object instance = aClass.newInstance();
                logger.info("resolver -> {}", instance);
                RESOLVERS.add((ArgumentResolver) instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
