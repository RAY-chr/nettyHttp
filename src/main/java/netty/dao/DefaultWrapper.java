package netty.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class DefaultWrapper {
    private StringBuilder sql = new StringBuilder();
    private StringBuilder whereSql = new StringBuilder(" where ");
    private StringBuilder groupBySql = new StringBuilder(" group by ");
    private StringBuilder orderBySql = new StringBuilder(" order by ");
    private StringBuilder limitSql = new StringBuilder(" limit ");
    private List<String> columns = new ArrayList<>();
    private List<Object> whereList = new ArrayList<>();
    private boolean isSqlFinish = false;
    private boolean limit = false;

    public boolean isLimit() {
        return limit;
    }

    public DefaultWrapper eq(String column, Object val) {
        columns.add(column);
        whereList.add(val);
        whereSql.append(column).append(" = ?").append(" and ");
        return this;
    }

    public DefaultWrapper like(String column, Object val) {
        columns.add(column);
        whereList.add("%" + val + "%");
        whereSql.append(column).append(" like ?").append(" and ");
        return this;
    }

    public DefaultWrapper gt(String column, Object val) {
        columns.add(column);
        whereList.add(val);
        whereSql.append(column).append(" > ?").append(" and ");
        return this;
    }

    public DefaultWrapper lt(String column, Object val) {
        columns.add(column);
        whereList.add(val);
        whereSql.append(column).append(" < ?").append(" and ");
        return this;
    }

    public DefaultWrapper le(String column, Object val) {
        columns.add(column);
        whereList.add(val);
        whereSql.append(column).append(" <= ?").append(" and ");
        return this;
    }

    public DefaultWrapper ge(String column, Object val) {
        columns.add(column);
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
        columns.addAll(list);
        for (String s : list) {
            groupBySql.append(s).append(", ");
        }
        return this;
    }

    /**
     * 默认为升序
     *
     * @param column 多个字段
     * @return DefaultWrapper
     */
    public DefaultWrapper orderBy(String... column) {
        List<String> list = Arrays.asList(column);
        columns.addAll(list);
        for (String s : list) {
            orderBySql.append(s).append(" asc").append(", ");
        }
        return this;
    }

    public DefaultWrapper orderDesc(String... column) {
        List<String> list = Arrays.asList(column);
        columns.addAll(list);
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

    public List<String> getColumns() {
        return columns;
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
