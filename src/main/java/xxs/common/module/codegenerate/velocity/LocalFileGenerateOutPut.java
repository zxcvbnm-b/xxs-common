package xxs.common.module.codegenerate.velocity;

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
    public void output(String name, String stringWriterText) {

    }
}
