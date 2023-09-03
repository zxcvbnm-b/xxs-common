package xxs.common.module.codegenerate.velocity;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;


/**
 * Velocity代码生成模板引擎
 *
 * @author xiognsongxu
 */
@Slf4j
public class VelocityTemplateEngine {
    private static VelocityEngine velocityEngine = null;
    private final static VelocityTemplateEngine velocityTemplateEngineInstance = new VelocityTemplateEngine();
    private final static String STORAGE_FORMAT_PROPERTIES_KEY = "storageFormat";


    //模板存储类型-存储到数据库中
    private final static String STORAGE_FORMAT_DB = "db";

    {
        try {
            Class.forName("org.apache.velocity.util.DuckType");
        } catch (ClassNotFoundException e) {
            // velocity1.x的生成格式错乱 https://github.com/baomidou/generator/issues/5
            log.error(" Class DuckType Not Found Exception", e);
        }
    }

    static {
        //resource.loader.ds.
        Properties velocityEngineProp = new Properties();
        VelocityTemplateEngine.init(velocityEngineProp);
        velocityEngine = new VelocityEngine(velocityEngineProp);
    }

    private static void init(Properties velocityEngineProp) {
        InputStream velocityProp = VelocityTemplateEngine.class.getClassLoader().getResourceAsStream("velocity.properties");
        Properties props = new Properties();
        try {
            props.load(velocityProp);
            if (STORAGE_FORMAT_DB.equals(props.getProperty(STORAGE_FORMAT_PROPERTIES_KEY))) {
                velocityEngineProp.put(RuntimeConstants.RESOURCE_LOADER, "ds");
                DataSource dataSource = DruidDataSourceFactory.createDataSource(props);
                VelocityDbTemplateHelper velocityDbTemplateHelper = new VelocityDbTemplateHelper(dataSource, props);
                velocityDbTemplateHelper.createDbTemplateResourceTable();
                velocityDbTemplateHelper.initDefaultTemplateToDb();
                DataSourceResourceLoader ds = new DataSourceResourceLoader();
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_DATASOURCE, "default");
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_TABLE, VelocityDbTemplateHelper.DB_STORAGE_FORMAT_PROPERTIES_TABLE_NAME);
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_KEY_COLUMN, VelocityDbTemplateHelper.DB_STORAGE_FORMAT_PROPERTIES_KEY_COLUMN_NAME);
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_TEMPLATE_COLUMN, VelocityDbTemplateHelper.DB_STORAGE_FORMAT_PROPERTIES_TEMPLATE_COLUMN_NAME);
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_TIMESTAMP_COLUMN, VelocityDbTemplateHelper.DB_STORAGE_FORMAT_PROPERTIES_TIMESTAMP_COLUMN_NAME);
                ds.setDataSource(dataSource);
                velocityEngineProp.put("resource.loader.ds.instance", ds);
            } else {
                velocityEngineProp.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            }
        } catch (Exception e) {
            log.error("xxs.common.module.codegenerate.velocity.VelocityTemplateEngine.generate error ", e);
        }
    }

    public static VelocityTemplateEngine getVelocityTemplateEngineInstance() {
        return velocityTemplateEngineInstance;
    }

    /**
     * @param generate       数据输出
     * @param objectValueMap 参数，用于给模板设置值的
     * @param templatePath   模板文件位置(或者模板名称)
     * @param name           文件名称
     * @throws Exception
     */
    public void generate(GenerateOutPut generate, Map<String, Object> objectValueMap, String templatePath, String name) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        try {
            // 这里的sw使用 OutputStreamWriter的时候生成的文件会错误 不知道为什么 换成StringWriter解决
            StringWriter sw = new StringWriter();
            template.merge(new VelocityContext(objectValueMap), sw);
            String stringWriterText = sw.toString();
            generate.output(name, stringWriterText);
        } catch (Exception e) {
            log.error("xxs.common.module.codegenerate.velocity.VelocityTemplateEngine.generate error ", e);
        }
    }

}
