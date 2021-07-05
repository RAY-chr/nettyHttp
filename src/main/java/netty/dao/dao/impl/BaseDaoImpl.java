package netty.dao.dao.impl;

import netty.dao.DefaultWrapper;
import netty.dao.dao.BaseDao;
import netty.dao.page.Page;
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
    //private SqlSession selectSession = SqlSessionFactory.openSession();

    public BaseDaoImpl() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pa = (ParameterizedType) type;
            Type argument = pa.getActualTypeArguments()[0];
            this.clazz = (Class) argument;
        }
    }

    @Override
    public boolean save(T t) throws Exception {
        return this.getSession().save(t);
    }

    @Override
    public boolean saveBatch(List<T> list) throws Exception {
        return this.getSession().saveBatch(list);
    }

    @Override
    public boolean deleteById(Serializable id) throws Exception {
        return this.getSession().deleteById(clazz, id);
    }

    @Override
    public boolean delete(DefaultWrapper wrapper) throws Exception {
        return this.getSession().delete(clazz, wrapper);
    }

    @Override
    public boolean updateById(T t) throws Exception {
        return this.getSession().updateById(t);
    }

    @Override
    public T selectById(Serializable id) throws Exception {
        return (T) this.getSession().selectById(clazz, id);
    }

    @Override
    public List<T> select(DefaultWrapper wrapper) throws Exception {
        return (List<T>) this.getSession().select(clazz, wrapper);
    }

    @Override
    public Page<T> selectPage(Page<T> page, DefaultWrapper wrapper) throws Exception {
        Page<?> result = this.getSession().selectPage(clazz, page, wrapper);
        return (Page<T>) result;
    }

    public SqlSession getSession() {
        return SqlSessionFactory.getCurrentSession();
    }
}
