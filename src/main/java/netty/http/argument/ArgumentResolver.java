package netty.http.argument;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/2
 */
public interface ArgumentResolver {

    /**
     * 判断是否能处理该类型
     * @param type
     * @param method
     * @param paramIndex
     * @return
     */
    boolean handle(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception;

    /**
     * 能处理的类型则返回结果
     * @param type
     * @param method
     * @param parameters
     * @param paramIndex
     * @return
     */
    Object result(Class<?> type, Method method, Map<String, List<String>> parameters, int paramIndex) throws Exception;
}
