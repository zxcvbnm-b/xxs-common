package xxs.common.module.codegenerate;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import xxs.common.module.datagenerate.db.jdbc.DatabaseMapping;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcUtils {
    DataSource dataSource;
    public final static String PROP_DRIVER_CLASS_NAME = "driverClassName";
    public final static String PROP_PASSWORD = "password";
    public final static String PROP_URL = "url";
    public final static String PROP_USERNAME = "username";


    /*
     * 读取配置文件
     * */
    public JdbcUtils(String driverClassName, String url, String username, String password) {
        //数据源配置
        Properties prop = new Properties();
        prop.put(PROP_DRIVER_CLASS_NAME, driverClassName);
        prop.put(PROP_PASSWORD, password);
        prop.put(PROP_URL, url);
        prop.put(PROP_USERNAME, username);
        try {
            //返回的是DataSource
            dataSource = DruidDataSourceFactory.createDataSource(prop);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
