package xxs.common.module.codegenerate.config;

import lombok.Data;
/**
 * nullCatalogMeansCurrent=true  mysql8.0的驱动，在5.5之前nullCatalogMeansCurrent属性默认为true,8.0中默认为false
 * 避免加载到其他的列信息或者其他表的信息在调用  metaData.getTables  metaData.getColumns时
 * @author xxs
 */
@Data
public class DataSourceConfig {
    private String jdbcUrl = "jdbc:mysql://10.19.20.111:3306/data_center_manager?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&nullCatalogMeansCurrent=true";
    private String jdbcUsername = "dbapp";
    private String jdbcPassword = "dbapp_321";
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
}
