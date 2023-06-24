package xxs.common.module;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 内存数据源
 * @author xxs
 */
public class InMemoryDataSource {
    private static final String JDBC_URL = "jdbc:hsqldb:mem:xxs";
    private static final String JDBC_USERNAME = "sa";
    private static final String JDBC_PASSWORD = "";

    private static DataSource dataSource;

    static {
        try {
            InputStream is = InMemoryDataSource.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties props = new Properties();
            props.load(is);
            // 设置数据源的 JDBC URL、用户名和密码
            props.setProperty("url", JDBC_URL);
            props.setProperty("username", JDBC_USERNAME);
            props.setProperty("password", JDBC_PASSWORD);
            // 使用 DruidDataSourceFactory 创建数据源对象
            dataSource = DruidDataSourceFactory.createDataSource(props);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null) {
            ((DruidDataSource) dataSource).close();
        }
    }
}
