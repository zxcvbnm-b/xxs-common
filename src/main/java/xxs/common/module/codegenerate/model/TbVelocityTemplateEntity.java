package xxs.common.module.codegenerate.model;

import lombok.Data;

import java.util.Date;

@Data
public class TbVelocityTemplateEntity {
    //模板标识
    @TableColumn(fieldName = "id_template")
    public String idTemplate;
    //模板内容
    @TableColumn(fieldName = "template_definition")
    public String templateDefinition;
    @TableColumn(fieldName = "template_timestamp")
    public Date templateTimestamp;
    //是否是内置模板
    @TableColumn(fieldName = "built_in")
    public Boolean builtIn;
    //模板名称
    @TableColumn(fieldName = "template_name")
    public String templateName;
}
