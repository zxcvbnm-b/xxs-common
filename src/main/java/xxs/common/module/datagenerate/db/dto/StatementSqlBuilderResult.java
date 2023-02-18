package xxs.common.module.datagenerate.db.dto;

import java.util.List;

/**
 * @author xxs
 */
public class StatementSqlBuilderResult {
    /**
     * 占位符sql
     */
    private String preparedStatementSql;
    /**
     * sql参数映射，（占位符映射）
     */
    private List<SQLParameterMapping> sqlParameterMappings;

    public String getPreparedStatementSql() {
        return preparedStatementSql;
    }

    public void setPreparedStatementSql(String preparedStatementSql) {
        this.preparedStatementSql = preparedStatementSql;
    }

    public List<SQLParameterMapping> getSqlParameterMappings() {
        return sqlParameterMappings;
    }

    public void setSqlParameterMappings(List<SQLParameterMapping> sqlParameterMappings) {
        this.sqlParameterMappings = sqlParameterMappings;
    }
}
