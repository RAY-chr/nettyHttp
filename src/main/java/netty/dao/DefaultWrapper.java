package netty.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class DefaultWrapper {
    private StringBuffer sql = new StringBuffer();
    private StringBuffer whereSql = new StringBuffer(" where ");
    private StringBuffer groupBySql = new StringBuffer(" group by ");
    private StringBuffer orderBySql = new StringBuffer(" order by ");
    private StringBuffer limitSql = new StringBuffer(" limit ");
    private List<Object> whereList = new ArrayList<>();
    private boolean isSqlFinish = false;
    private boolean limit = false;

    public boolean isLimit() {
        return limit;
    }

    public DefaultWrapper eq(String column, Object val) {
        whereList.add(val);
        whereSql.append(column).append(" = ?").append(" and ");
        return this;
    }

    public DefaultWrapper like(String column, Object val) {
        whereList.add("%" + val + "%");
        whereSql.append(column).append(" like ?").append(" and ");
        return this;
    }

    public DefaultWrapper gt(String column, Object val) {
        whereList.add(val);
        whereSql.append(column).append(" > ?").append(" and ");
        return this;
    }

    public DefaultWrapper ge(String column, Object val) {
        whereList.add(val);
        whereSql.append(column).append(" >= ?").append(" and ");
        return this;
    }

    public DefaultWrapper or() {
        int index = whereSql.lastIndexOf(" and ");
        whereSql.replace(index, index + 5, " or ");
        return this;
    }

    public DefaultWrapper groupBy(String... column) {
        List<String> list = Arrays.asList(column);
        for (String s : list) {
            groupBySql.append(s).append(", ");
        }
        return this;
    }

    /**
     * 默认为升序
     *
     * @param column
     * @return
     */
    public DefaultWrapper orderBy(String... column) {
        List<String> list = Arrays.asList(column);
        for (String s : list) {
            orderBySql.append(s).append(" asc").append(", ");
        }
        return this;
    }

    public DefaultWrapper orderDesc(String... column) {
        List<String> list = Arrays.asList(column);
        for (String s : list) {
            orderBySql.append(s).append(" desc").append(", ");
        }
        return this;
    }

    /**
     *
     *
     * @param index
     * @param size
     * @return
     */
    public DefaultWrapper limit(long index, long size) {
        if (!limit) {
            whereList.add(index);
            whereList.add(size);
            limitSql.append("?").append(",").append("?").append(", ");
            limit = true;
        }
        return this;
    }

    public String getSqlString() {
        if (!isSqlFinish) {
            int and = whereSql.lastIndexOf(" and ");
            int group = groupBySql.lastIndexOf(",");
            int order = orderBySql.lastIndexOf(",");
            int limit = limitSql.lastIndexOf(",");
            sql.append(and > 0 ? whereSql.substring(0, and) : "");
            sql.append(group > 0 ? groupBySql.substring(0, group) : "");
            sql.append(order > 0 ? orderBySql.substring(0, order) : "");
            sql.append(limit > 0 ? limitSql.substring(0, limit) : "");
            isSqlFinish = true;
            whereSql = null;
            groupBySql = null;
            orderBySql = null;
            limitSql = null;
        }
        return sql.toString();
    }

    public List<Object> getValues() {
        return whereList;
    }

    public static void main(String[] args) {
        DefaultWrapper defaultWrapper = new DefaultWrapper();
        defaultWrapper.eq("book_id", 2).or().eq("book_name", "333")
                .gt("book_state", 0).groupBy("book_id", "book_name").orderBy("book_id", "book_no")
                .orderDesc("date").limit(0, 3);
        System.out.println(defaultWrapper.getSqlString());
    }


}
