package netty.dao.executor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author RAY
 * @descriptions
 * @since 2020/12/10
 */
public class DBElement {
    private ResultSet resultSet;
    private PreparedStatement preparedStatement;

    public DBElement(ResultSet resultSet, PreparedStatement preparedStatement) {
        this.resultSet = resultSet;
        this.preparedStatement = preparedStatement;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }
}
