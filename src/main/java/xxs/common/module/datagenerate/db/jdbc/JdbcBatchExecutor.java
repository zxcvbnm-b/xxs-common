package xxs.common.module.datagenerate.db.jdbc;


import com.alibaba.druid.DbType;
import xxs.common.module.datagenerate.db.dto.StatementSqlBuilderResult;
import xxs.common.module.datagenerate.db.dto.TableInfo;
import xxs.common.module.sql.CreateTableSQLParseUtils;

import java.sql.Connection;
//jdbc简单执行器,正在的执行的
public class JdbcBatchExecutor implements Executor {

    private String createTableSql;

    public JdbcBatchExecutor(String createTableSql) {
        this.createTableSql = createTableSql;
    }

    @Override
    public int executor() throws Exception {
        //事务控制问题
        JDBCDataSourceUtils jdbcDataSourceUtils = JDBCDataSourceUtils.getInstance();
        Connection connection = jdbcDataSourceUtils.getConnection();
        DbType druidDruidDatabaseType = jdbcDataSourceUtils.getDruidDruidDatabaseType(connection);
        TableInfo tableInfo = CreateTableSQLParseUtils.getTableInfo(druidDruidDatabaseType, createTableSql);
        //根据tableInfo得到 String preparedStatementSql, List<SQLParameterMapping> sqlParameterMappings,
        StatementSqlBuilder statementSqlBuilder=new StatementSqlBuilder(tableInfo);
        StatementSqlBuilderResult statementSqlBuilderResult = statementSqlBuilder.builder();
        JDBCBatchProcess jdbcBatchProcess = new JDBCBatchProcess(1000, statementSqlBuilderResult.getPreparedStatementSql(), statementSqlBuilderResult.getSqlParameterMappings(), connection);
        connection.setAutoCommit(false);
        try {
            jdbcBatchProcess.doExecute();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            connection.rollback();
        }finally {
            jdbcBatchProcess.close();
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
        String sql="CREATE TABLE `bus` (\n" +
                "  `bid` int(11) NOT NULL ,\n" +
                "  `bname` varchar(255) DEFAULT NULL,\n" +
                "  `buspath` longtext,\n" +
                "  `endDate` varchar(255) DEFAULT NULL,\n" +
                "  `startDate` varchar(255) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`bid`)\n" +
                ") ENGINE=InnoDB  DEFAULT CHARSET=utf8";
        JdbcBatchExecutor jdbcBatchExecutor=new JdbcBatchExecutor(sql);
        jdbcBatchExecutor.executor();

    }
}
