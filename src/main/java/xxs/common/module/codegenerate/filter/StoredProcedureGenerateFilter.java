package xxs.common.module.codegenerate.filter;

import com.google.auto.service.AutoService;
import xxs.common.module.codegenerate.CodeGenerateContext;
import xxs.common.module.codegenerate.model.TableInfo;
import xxs.common.module.codegenerate.template.AbstractTemplate;
import java.io.File;
import java.util.Map;

/**
 * 通用sql存储过程代码生成拦截
 *
 * @author xxs
 */
@AutoService(IGenerateFilter.class)
public class StoredProcedureGenerateFilter implements IGenerateFilter {
    @Override
    public void init(CodeGenerateContext generateContext) {
        generateContext.addTemplate(new AbstractTemplate(null) {
            @Override
            public String getTemplateFilePathName() {
                return "templates/storedProcedure.sql.vm";
            }

            @Override
            public String getOutFilePathName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
                String filePathName = codeGenerateContext.getAbsoluteDir() + AbstractTemplate.SRC_TEST_RESOURCES_PATH + "sql" + File.separator + "batch_add_" + tableInfo.getName() + SQL_FILE_POST;
                return filePathName;
            }

            @Override
            public String getFileSuffix() {
                return null;
            }

            @Override
            public Map<String, Object> customTemplateParamMap(Object pram) {
                return super.customTemplateParamMap(pram);
            }

            @Override
            public String getFileName(CodeGenerateContext codeGenerateContext, TableInfo tableInfo) {
                return "batch_add_" + tableInfo.getName() + SQL_FILE_POST;
            }
        });
    }
}
