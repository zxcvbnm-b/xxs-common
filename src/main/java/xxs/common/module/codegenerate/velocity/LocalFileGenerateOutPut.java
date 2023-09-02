package xxs.common.module.codegenerate.velocity;

import com.baomidou.mybatisplus.generator.config.ConstVal;

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

    public LocalFileGenerateOutPut(String outputFile, boolean append, boolean coverExistFile) {
        this.outputFile = outputFile;
        this.append = append;
        this.coverExistFile = coverExistFile;
    }

    @Override
    public void output(String name, String stringWriterText) throws Exception {
        String realOutFilePathName = FileGenerateUtils.getRealOutFilePathName(outputFile, coverExistFile);
        FileGenerateUtils.writeFile(new File(realOutFilePathName), stringWriterText, ConstVal.UTF8, append);
    }
}
