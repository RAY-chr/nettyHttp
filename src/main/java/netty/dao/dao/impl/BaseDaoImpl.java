package netty.dao.dao.impl;

import netty.dao.DefaultWrapper;
import netty.dao.dao.BaseDao;
import netty.dao.session.DefaultSqlSession;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class BaseDaoImpl<T> implements BaseDao<T> {

    private Class clazz;

    public BaseDaoImpl() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pa = (ParameterizedType) type;
            Type argument = pa.getActualTypeArguments()[0];
            this.clazz = (Class) argument;
        }
    }

    @Override
    public void save(T t) throws Exception {
        SqlSession session = SqlSessionFactory.openSession();
        session.save(t);
    }

    @Override
    public void deleteById(Serializable id) throws Exception {
        SqlSession session = SqlSessionFactory.openSession();
        session.deleteById(clazz, id);
    }

    @Override
    public void delete(DefaultWrapper wrapper) throws Exception {
        SqlSession session = SqlSessionFactory.openSession();
        session.delete(clazz, wrapper);
    }
}
