package xxs.common.module.codegenerate.config;

import lombok.Data;

@Data
public class DataSourceConfig {
    private String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/bus?serverTimezone=PRC";
    private String jdbcUsername = "root";
    private String jdbcPassword = "051515";
    private String driverClassName = "com.mysql.cj.jdbc.Driver";
}
