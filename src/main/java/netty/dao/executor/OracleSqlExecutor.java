package netty.dao.executor;

import netty.dao.CommonStr;
import netty.dao.DefaultWrapper;
import netty.dao.cache.TableColumnCache;
import netty.dao.page.Page;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author RAY
 * @descriptions
 * @since 2021/1/12
 */
public class OracleSqlExecutor extends AbstractSqlExecutor {

    @Override
    public Page<?> selectPage(Class<?> clazz, Page<?> page, DefaultWrapper wrapper) throws Exception {
        long current = page.getCurrent();
        long size = page.getSize();
        Objects.requireNonNull(current == 0 ? null : current, "current can't be 0");
        Objects.requireNonNull(size == 0 ? null : size, "size can't be 0");
        if (wrapper == null) {
            wrapper = new DefaultWrapper();
        }
        Map<String, String> map = TableColumnCache.get(clazz.getSimpleName());
        String sql = pageSql(map.get(CommonStr.TABLE), wrapper);
        Object[] params = null;
        int paramSize = wrapper.getValues().size();
        params = paramSize > 0 ? new Object[paramSize + 2] : new Object[2];
        int length = params.length;
        for (int i = 0; i < length - 2; i++) {
            params[i] = wrapper.getValues().get(i);
        }
        params[length - 1] = (current - 1) * size;
        params[length - 2] = current * size + 1;
        List<?> list = this.selectList(clazz, sql, params);
        Page<?> result = new Page<>();
        result.setCurrent(current);
        result.setSize(size);
        long count = this.count(clazz, wrapper);
        long total = count % size == 0 ? count / size : count / size + 1;
        result.setTotal(total);
        result.setRecords(list);
        return result;
    }


    /**
     * 生成oracle的分页sql
     *
     * @param tableName
     * @param wrapper
     * @return
     */
    private String pageSql(String tableName, DefaultWrapper wrapper) {
        StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT a.*, ROWNUM rn FROM ( ");
        sql.append("SELECT * FROM ");
        sql.append(tableName);
        sql.append(wrapper.getSqlString());
        sql.append(" ) a WHERE ROWNUM < ?) WHERE rn > ?");
        return sql.toString();
    }

}
