package xxs.common.module.codegenerate;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Map;
import java.util.Properties;


/**
 * Velocity代码生成模板引擎
 *
 * @author issuser
 */
public class VelocityTemplateEngine {
    private static VelocityEngine velocityEngine = null;

    {
        try {
            Class.forName("org.apache.velocity.util.DuckType");
        } catch (ClassNotFoundException e) {
            // velocity1.x的生成格式错乱 https://github.com/baomidou/generator/issues/5
            e.printStackTrace();
        }
    }

    static {
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        velocityEngine = new VelocityEngine(prop);
    }


    /**
     * @param objectValueMap 参数，用于给模板设置值的
     * @param templatePath   模板文件位置
     * @param outputFile     文件输出位置
     * @throws Exception
     */
    public void generate(Map<String, Object> objectValueMap, String templatePath, File outputFile) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        String path = outputFile.getPath();
        String mkdirsPath = path.substring(0, path.lastIndexOf("\\"));
        /*要有文件夹才能生成文件*/
        File mkdirs = new File(mkdirsPath);
        mkdirs.mkdirs();
        try {
            //TODO 这里的sw使用 OutputStreamWriter的时候生成的文件会错误 不知道为什么 换成StringWriter解决
            StringWriter sw = new StringWriter();
            template.merge(new VelocityContext(objectValueMap), sw);
            writeFile(outputFile, sw.toString(), ConstVal.UTF8);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeFile(File file, String content, String fileEncoding) throws IOException {

        String path = file.getPath();
        path = path.substring(0, path.lastIndexOf("\\"));
        /*要有文件夹才能生成文件*/
        File file2 = new File(path);
        file2.mkdirs();
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        try (BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(content);
            bw.flush();
        } finally {
            osw.close();
            fos.close();
        }

    }
}
