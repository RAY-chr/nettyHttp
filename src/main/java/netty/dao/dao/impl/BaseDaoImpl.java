package netty.dao.dao.impl;

import netty.dao.DefaultWrapper;
import netty.dao.dao.BaseDao;
import netty.dao.session.DefaultSqlSession;
import netty.dao.session.SqlSession;
import netty.dao.session.SqlSessionFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

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
        this.getSession().save(t);
    }

    @Override
    public void deleteById(Serializable id) throws Exception {
        this.getSession().deleteById(clazz, id);
    }

    @Override
    public void delete(DefaultWrapper wrapper) throws Exception {
        this.getSession().delete(clazz, wrapper);
    }

    @Override
    public T selectById(Serializable id) throws Exception {
        return (T) this.getSession().selectById(clazz, id);
    }

    @Override
    public List<T> select(DefaultWrapper wrapper) throws Exception {
        return (List<T>) this.getSession().select(clazz, wrapper);
    }

    public SqlSession getSession() {
        return SqlSessionFactory.getCurrentSession();
    }
}
