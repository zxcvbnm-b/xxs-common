package xxs.common.module.datagenerate.db.dto;

/**
 * 列的约束
 *
 * @author xxs
 */
public enum ColumnConstraint {
    NOT_NULL("NOT_NULL", "非空"), PRIMARY_KEY("PRIMARY_KEY", "主键约束"), UNIQUE_KEY("UNIQUE_KEY", "唯一约束");
    private String value;
    private String text;

    ColumnConstraint(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
