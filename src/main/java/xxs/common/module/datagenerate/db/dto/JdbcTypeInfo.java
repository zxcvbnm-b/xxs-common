package xxs.common.module.datagenerate.db.dto;

import xxs.common.module.datagenerate.db.jdbc.type.JdbcType;

import java.util.List;

/*字段的类型信息*/
public class JdbcTypeInfo {
    private JdbcType jdbcType;//jdbc类型 ，来源：java.sql.Types
    private List<Object> arguments;// 字段的参数信息，比如 int(10)中的10等

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }
}
