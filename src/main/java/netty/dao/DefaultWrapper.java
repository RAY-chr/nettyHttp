package netty.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/6
 */
public class DefaultWrapper {
    private StringBuffer sql = new StringBuffer(" where ");
    private List<Object> list = new ArrayList<>();

    public DefaultWrapper eq(String column, Object val) {
        list.add(val);
        sql.append(column).append(" = ?").append(" and ");
        return this;
    }

    public DefaultWrapper like(String column, Object val) {
        list.add("%" + val + "%");
        sql.append(column).append(" like ?").append(" and ");
        return this;
    }

    public String getSqlString() {
        int and = sql.lastIndexOf(" and ");
        return sql.substring(0, and);
    }

    public List<Object> getValues() {
        return list;
    }

    public static void main(String[] args) {
        DefaultWrapper defaultWrapper = new DefaultWrapper();
        defaultWrapper.eq("book_id", 2).eq("book_name", "333");
        System.out.println(defaultWrapper.getSqlString());
    }


}
