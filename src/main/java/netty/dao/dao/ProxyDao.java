package netty.dao.dao;

import netty.dao.annotion.Dao;
import netty.dao.annotion.Transactional;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;
import netty.http.utils.PackageScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/9
 */
public class ProxyDao {

    private static Logger logger = LoggerFactory.getLogger(ProxyDao.class);
    private static final String daoImplPath = "netty.dao.dao.impl";
    private static Map<Class<?>, Object> beans = new HashMap<>();

    private static <T> T getDao(Class<T> clazz, Object object) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                (proxy, method, args) -> {
                    if (method.getAnnotation(Transactional.class) != null) {
                        SqlSession session = SqlSessionFactory.getCurrentSession();
                        session.beginTransaction();
                        Object invoke = null;
                        try {
                            invoke = method.invoke(object, args);
                            session.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                            session.rollback();
                            logger.error("[{}] occurs mistake, rollback", method);
                        } finally {
                            SqlSessionFactory.closeCurrentSession();
                        }
                        return invoke;
                    } else {
                        Object invoke = method.invoke(object, args);
                        SqlSessionFactory.closeCurrentSession();
                        return invoke;
                    }
                });
    }

    public static <T> T getDao(Class<T> clazz) {
        Object instance = beans.get(clazz);
        Objects.requireNonNull(instance, "not exist the bean belong " + clazz);
        return (T) getDao(clazz, instance);
    }

    static {
        Set<Class<?>> classes = PackageScanner.getClasses(daoImplPath);
        for (Class<?> aClass : classes) {
            if (aClass.getAnnotation(Dao.class) != null) {
                Class<?>[] interfaces = aClass.getInterfaces();
                try {
                    beans.put(interfaces[0], aClass.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
