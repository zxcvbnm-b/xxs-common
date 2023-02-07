package xxs.common.module.codegenerate.filter;

import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import xxs.common.module.codegenerate.template.ParamDTOTemplate;
import xxs.common.module.codegenerate.template.Template;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class StoredProcedureGenerateFilter implements IGenerateFilter {
    @Override
    public void init(CodeGenerateContext generateContext) {
        generateContext.addTemplate(new AbstractTemplate(generateContext) {
            @Override
            public String getTemplateFilePathName() {
                return "templates/storedProcedure.sql.vm";
            }

            @Override
            public String getOutFilePathName(TableInfo tableInfo) {
                String filePathName = codeGenerateContext.getAbsoluteDir()+AbstractTemplate.SRC_TEST_RESOURCES_PATH+ "sql" + File.separator+"batch_add_"+tableInfo.getName()+".sql";
                return filePathName;
            }

            @Override
            public Map<String, Object> getObjectValueMap() {
                return super.getObjectValueMap();
            }
        });
    }
}
