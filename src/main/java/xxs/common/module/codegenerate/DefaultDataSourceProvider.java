package xxs.common.module.codegenerate;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 连接池工具
 * nullCatalogMeansCurrent=true  mysql8.0的驱动，在5.5之前nullCatalogMeansCurrent属性默认为true,8.0中默认为false
 * 避免加载到其他的列信息或者其他表的信息在调用  metaData.getTables  metaData.getColumns时
 * jdbc:mysql://127.0.0.1:3306/world?serverTimezone=PRC&nullCatalogMeansCurrent=true
 *
 * @author xxs
 */
public class DefaultDataSourceProvider {
    private static DataSource dataSource = null;

    static {
        try {
            InputStream is = DefaultDataSourceProvider.class.getClassLoader().getResourceAsStream("druid.properties");
            Properties props = new Properties();
            props.load(is);
            // 使用 DruidDataSourceFactory 创建数据源对象
            dataSource = DruidDataSourceFactory.createDataSource(props);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSourceInstance() {
        return dataSource;
    }
}
