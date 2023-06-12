package xxs.common.module.codegenerate.method.enums;

/**
 * where参数操作类型
 *
 * @author xxs
 */

public enum WhereParamOperationType {
    BETWEEN("BETWEEN"), EQ("="), IN("IN"), GE(">="), GT(">"), LE("<="), LIKE("LIKE"), LT("<"), OR("OR"), AND("AND"), NEQ("!=");
    private String name;

    WhereParamOperationType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
