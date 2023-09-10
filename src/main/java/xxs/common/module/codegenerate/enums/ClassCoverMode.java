package xxs.common.module.codegenerate.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * *java文件存在时的处理方式
 *
 * @author xiongsongxu
 */

public enum ClassCoverMode {
    NON("NON", "不处理 看coverExistFile属性的值"), MERGE_FROM_OLD("MERGE_FROM_OLD", "合并java文件，只添加旧类不存在的方法，字段"),
    MERGE_FROM_NEW("MERGE_FROM_NEW", "合并java文件，只添加新类不存在的方法，字段");
    private String name;
    private String desc;

    ClassCoverMode(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ClassCoverMode getClassCoverModeByName(String name, ClassCoverMode defaultMode) {
        if (StringUtils.isEmpty(name)) {
            return defaultMode;
        }
        for (ClassCoverMode value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return defaultMode;
    }
}
