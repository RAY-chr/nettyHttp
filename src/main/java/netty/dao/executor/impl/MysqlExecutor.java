package netty.dao.executor.impl;

import netty.dao.DefaultWrapper;
import netty.dao.executor.AbstractSqlExecutor;
import netty.dao.page.Page;

import java.util.List;
import java.util.Objects;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/7
 */
public class MysqlExecutor extends AbstractSqlExecutor {

    @Override
    public Page<?> selectPage(Class<?> clazz, Page<?> page, DefaultWrapper wrapper) throws Exception {
        long current = page.getCurrent();
        long size = page.getSize();
        Objects.requireNonNull(current == 0 ? null : current, "current can't be 0");
        Objects.requireNonNull(size == 0 ? null : size, "size can't be 0");
        if (wrapper == null) {
            wrapper = new DefaultWrapper();
        }
        if (wrapper.isLimit()) {
            throw new IllegalArgumentException(" wrapper can't contains limit");
        }
        wrapper.limit((current - 1) * size, size);
        List<?> list = this.select(clazz, wrapper);
        Page<?> result = new Page<>();
        result.setCurrent(current);
        result.setSize(size);
        long count = this.count(clazz, wrapper);
        long total = count % size == 0 ? count / size : count / size + 1;
        result.setTotal(total);
        result.setRecords(list);
        return result;
    }

}
