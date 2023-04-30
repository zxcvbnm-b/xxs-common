package xxs.common.module.codegenerate.config;

import lombok.Data;
/**
 * nullCatalogMeansCurrent=true  mysql8.0的驱动，在5.5之前nullCatalogMeansCurrent属性默认为true,8.0中默认为false
 * 避免加载到其他的列信息或者其他表的信息在调用  metaData.getTables  metaData.getColumns时
 * @author xxs
 */
@Data
public class DataSourceConfig {
    private String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/world?serverTimezone=PRC&nullCatalogMeansCurrent=true";
    private String jdbcUsername = "root";
    private String jdbcPassword = "051515";
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
}
