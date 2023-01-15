package xxs.common.module.datagenerate.db.jdbc;

import org.springframework.util.StopWatch;
import xxs.common.module.datagenerate.db.dto.SQLParameterMapping;
import xxs.common.module.datagenerate.db.dto.TableColumnInfo;
import xxs.common.module.datagenerate.db.jdbc.type.TypeHandler;
import xxs.common.module.datagenerate.db.jdbc.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;

/*使用JDBC进行数据批量进行插入,执行批处理，需要控制事务*/
public class JDBCBatchProcess {
    private Integer batchSize = 1000;//批大小。
    //预处理的 preparedStatementSql insert into admin1 values(null,?,?)
    private String preparedStatementSql;
    //数据库连接
    private Connection connection;

    private PreparedStatement preparedStatement;

    private List<SQLParameterMapping> sqlParameterMappings;

    public JDBCBatchProcess(Integer batchSize, String preparedStatementSql, List<SQLParameterMapping> sqlParameterMappings, Connection connection) throws SQLException {
        this.batchSize = batchSize;
        this.preparedStatementSql = preparedStatementSql;
        this.connection = connection;
        this.preparedStatement = connection.prepareStatement(preparedStatementSql);
        this.sqlParameterMappings = sqlParameterMappings;

    }

    //入参为需要生成的参数 批量执行一次
    public void doExecute() throws SQLException {
        TypeHandlerRegistry typeHandlerRegistry = JdbcTypeHandler.getTypeHandlerRegistry();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < batchSize; i++) {
            //参数设置
            for (SQLParameterMapping sqlParameterMapping : sqlParameterMappings) {
                int index = sqlParameterMapping.getIndex();
                TypeHandler typeHandler = typeHandlerRegistry.getTypeHandler(sqlParameterMapping.getTableColumnInfo().getJdbcTypeInfo().getJdbcType());
                if(typeHandler==null){
                    throw new RuntimeException("获取不到类型处理器");
                }
                Object parameter = null;
                //如果有回调，那么调用回调返回参数
                Function<TableColumnInfo, Object> parameterCallBack = sqlParameterMapping.getParameterCallBack();
                if(parameterCallBack!=null){
                     parameter = parameterCallBack.apply(sqlParameterMapping.getTableColumnInfo());
                }
                typeHandler.setParameter(preparedStatement,index,parameter,sqlParameterMapping.getTableColumnInfo());
            }
            preparedStatement.addBatch();
        }
        //批量执行，清除批量状态
        preparedStatement.executeBatch();
        preparedStatement.clearBatch();
        //输出执行时间
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }

    /*关闭资源*/
    public void close() throws SQLException {
        JDBCDataSourceUtils.closeResource(preparedStatement, connection);
    }
}

