package xxs.common.module.codegenerate.method.enums;

/**
 * 逻辑运算符（and or）
 *
 * @author xxs
 */

public enum LogicOperator {
    OR("OR"), AND("AND");
    private String name;

    LogicOperator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
