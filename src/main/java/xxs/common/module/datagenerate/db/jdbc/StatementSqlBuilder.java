package xxs.common.module.datagenerate.db.jdbc;

import com.alibaba.druid.DbType;
import xxs.common.module.datagenerate.db.dto.SQLParameterMapping;
import xxs.common.module.datagenerate.db.dto.StatementSqlBuilderResult;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.datagenerate.db.dto.TableInfo;
import xxs.common.module.datagenerate.db.jdbc.callback.DefaultFunctionCallBack;
import xxs.common.module.sql.CreateTableSQLParseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StatementSqlBuilder {
    private TableInfo tableInfo;

    public StatementSqlBuilder(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    /**
     *
     * @param defaultFunctionCallBack  设置值的回调 可以手动设置值 不需要随机生成无规律的值。
     * @return
     */
    public StatementSqlBuilderResult builder(DefaultFunctionCallBack defaultFunctionCallBack) {
        StringBuilder insertSqlValues = new StringBuilder();
        insertSqlValues.append("(");
        StringBuilder insertSqlPlaceholder = new StringBuilder();
        insertSqlPlaceholder.append("  values(");
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("insert into " + tableInfo.getTableName());
        StatementSqlBuilderResult statementSqlBuilderResult = new StatementSqlBuilderResult();
        List<SQLParameterMapping> sqlParameterMappings = new ArrayList<>();
        for (int i = 0; i < tableInfo.getTableColumnInfos().size(); i++) {
            TableColumnInfo tableColumnInfo = tableInfo.getTableColumnInfos().get(i);
            if (tableColumnInfo.isAutoincrement()) {
                continue;
            }
            SQLParameterMapping sqlParameterMapping = new SQLParameterMapping();
            //扩展 设置回调
            Function functionCallBack = defaultFunctionCallBack.getFunctionCallBack(tableColumnInfo);
            if (functionCallBack != null) {
                sqlParameterMapping.setParameterCallBack(functionCallBack);
            }
            sqlParameterMapping.setIndex(i + 1);
            sqlParameterMapping.setTableColumnInfo(tableColumnInfo);
            sqlParameterMappings.add(sqlParameterMapping);
            insertSqlValues.append(tableColumnInfo.getColumnName() + ",");
            insertSqlPlaceholder.append("?,");
        }
        insertSqlValues.deleteCharAt(insertSqlValues.length() - 1);
        insertSqlPlaceholder.deleteCharAt(insertSqlPlaceholder.length() - 1);
        insertSqlValues.append(")");
        insertSqlPlaceholder.append(")");
        insertSql.append(insertSqlValues).append("  ").append(insertSqlPlaceholder);
        statementSqlBuilderResult.setPreparedStatementSql(insertSql.toString());
        statementSqlBuilderResult.setSqlParameterMappings(sqlParameterMappings);
        return statementSqlBuilderResult;
    }
    public StatementSqlBuilderResult builder() {
        return this.builder(new DefaultFunctionCallBack());
    }
}
