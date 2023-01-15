package xxs.common.module.datagenerate.db.jdbc;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCDataSourceUtils {

    private static JDBCDataSourceUtils instance;

    DataSource dataSource;


    /**
     * 获取当前类的实例对象
     *
     * @return
     */
    public static JDBCDataSourceUtils getInstance() {
        if (null == instance) {
            instance = new JDBCDataSourceUtils();
        }
        return instance;
    }

    /*
     * 读取配置文件
     * */
    private JDBCDataSourceUtils() {
        //数据源配置
        Properties prop = new Properties();
        //读取配置文件
        InputStream is = JdbcUtils.class.getResourceAsStream("/datagenerateDB.properties");
        try {
            prop.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //返回的是DataSource
            dataSource = DruidDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeResource(PreparedStatement preparedStatement, Connection connection) throws SQLException {
        preparedStatement.close();
        connection.close();
    }

    /**
     * 获取连接
     *
     * @return
     */
    public Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭连接
     *
     * @return
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取和druid的映射数据源枚举类型
     *
     * @return
     */
    public DbType getDruidDruidDatabaseType(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        String databaseProductName = metaData.getDatabaseProductName();
        DbType druidDruidDatabaseType = DatabaseMapping.getDruidDruidDatabaseType(databaseProductName);
        return druidDruidDatabaseType;
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCDataSourceUtils.getInstance().getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(null, null, null, null);
        String string = tables.getString(1);


    }
}
