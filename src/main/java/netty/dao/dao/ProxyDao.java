package netty.dao.dao;

import netty.dao.annotion.Transactional;
import netty.dao.executor.AbstractSqlExecutor;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/9
 */
public class ProxyDao {

    private static Logger logger = LoggerFactory.getLogger(ProxyDao.class);

    public static  <T> T getDao(Class<T> clazz, Object object) {
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
                        }finally {
                            SqlSessionFactory.closeCurrentSession();
                        }
                        return invoke;
                    }else {
                        Object invoke = method.invoke(object, args);
                        SqlSessionFactory.closeCurrentSession();
                        return invoke;
                    }
                });
    }
}
