package xxs.common.module.codegenerate.velocity;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import xxs.common.module.codegenerate.Constants;
import xxs.common.module.codegenerate.SimpleJavaClassMerge;
import xxs.common.module.codegenerate.config.AbstractTemplateConfig;
import xxs.common.module.codegenerate.enums.ClassCoverMode;
import xxs.common.module.codegenerate.template.AbstractTemplate;

import java.io.File;

/**
 * 生成到本地文件
 *
 * @author xiognsognxu
 */
public class LocalFileGenerateOutPut implements GenerateOutPut {
    private String outputFile;
    private boolean append;
    private boolean coverExistFile;
    private ClassCoverMode classCoverMode;

    public LocalFileGenerateOutPut(String outputFile, boolean append, boolean coverExistFile, ClassCoverMode classCoverMode) {
        this.outputFile = outputFile;
        this.append = append;
        this.coverExistFile = coverExistFile;
        this.classCoverMode = classCoverMode;
    }

    @Override
    public void output(String name, String stringWriterText) throws Exception {
        String content = stringWriterText;
        if (outputFile.contains(Constants.JAVA_FILE_POST)) {
            if (classCoverMode != null && !ClassCoverMode.NON.equals(classCoverMode)) {
                String old = FileUtil.readUtf8String(new File(outputFile));
                if (ClassCoverMode.MERGE_FROM_OLD.equals(classCoverMode)) {
                    content = SimpleJavaClassMerge.mergeJavaSrc(old, stringWriterText);
                } else if (ClassCoverMode.MERGE_FROM_NEW.equals(classCoverMode)) {
                    content = SimpleJavaClassMerge.mergeJavaSrc(stringWriterText, old);
                }
            }
        }
        String realOutFilePathName = FileGenerateUtils.getRealOutFilePathName(outputFile, coverExistFile);
        FileGenerateUtils.writeFile(new File(realOutFilePathName), content, ConstVal.UTF8, append);
    }
}
