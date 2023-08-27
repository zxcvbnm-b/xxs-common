package xxs.common.module.codegenerate;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.DataSourceResourceLoader;
import org.apache.velocity.util.ExtProperties;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;


/**
 * Velocity代码生成模板引擎
 *
 * @author issuser
 */
public class VelocityTemplateEngine {
    private static VelocityEngine velocityEngine = null;
    private final static VelocityTemplateEngine velocityTemplateEngineInstance = new VelocityTemplateEngine();
    private final static String STORAGE_FORMAT_PROPERTIES_KEY = "storageFormat";
    private final static String DB_STORAGE_FORMAT_PROPERTIES_CREATE_TABLE_SQL_KEY = "ds.resource.createSql";
    private final static String DB_STORAGE_FORMAT_PROPERTIES_TABLE_KEY = "ds.resource.table";
    private final static String DB_STORAGE_FORMAT_PROPERTIES_KEY_COLUMN_KEY = "ds.resource.key_column";
    private final static String DB_STORAGE_FORMAT_PROPERTIES_TEMPLATE_COLUMN_KEY = "ds.resource.template_column";
    private final static String DB_STORAGE_FORMAT_PROPERTIES_TIMESTAMP_COLUMN_KEY = "ds.resource.timestamp_column";
    private final static String DB_STORAGE_FORMAT_DEFAULT_CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS tb_velocity_template (\n" +
            " id_template varchar (40) NOT NULL ,\n" +
            " template_definition text (16) NOT NULL ,\n" +
            " template_timestamp datetime NOT NULL\n" +
            " );";


    //模板存储类型-存储到数据库中
    private final static String STORAGE_FORMAT_DB = "db";

    {
        try {
            Class.forName("org.apache.velocity.util.DuckType");
        } catch (ClassNotFoundException e) {
            // velocity1.x的生成格式错乱 https://github.com/baomidou/generator/issues/5
            e.printStackTrace();
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
                VelocityTemplateEngine.createDbTemplateResourceTable(dataSource, props.getProperty(DB_STORAGE_FORMAT_PROPERTIES_CREATE_TABLE_SQL_KEY, DB_STORAGE_FORMAT_DEFAULT_CREATE_TABLE_SQL));
                DataSourceResourceLoader ds = new DataSourceResourceLoader();
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_DATASOURCE, props.getProperty("url", "url"));
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_TABLE, props.getProperty(DB_STORAGE_FORMAT_PROPERTIES_TABLE_KEY, "tb_velocity_template"));
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_KEY_COLUMN, props.getProperty(DB_STORAGE_FORMAT_PROPERTIES_KEY_COLUMN_KEY, "id_template"));
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_TEMPLATE_COLUMN, props.getProperty(DB_STORAGE_FORMAT_PROPERTIES_TEMPLATE_COLUMN_KEY, "template_definition"));
                velocityEngineProp.setProperty(RuntimeConstants.DS_RESOURCE_LOADER_TIMESTAMP_COLUMN, props.getProperty(DB_STORAGE_FORMAT_PROPERTIES_TIMESTAMP_COLUMN_KEY, "template_timestamp"));
                ds.setDataSource(dataSource);
                velocityEngineProp.put("resource.loader.ds.instance", ds);
            } else {
                velocityEngineProp.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建存储velocity渲染模板的表。
     *
     * @param dataSource
     * @param createTableSql
     */
    private static void createDbTemplateResourceTable(DataSource dataSource, String createTableSql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement(createTableSql).execute();
        }
    }

    public static VelocityTemplateEngine getVelocityTemplateEngineInstance() {
        return velocityTemplateEngineInstance;
    }

    /**
     * @param objectValueMap 参数，用于给模板设置值的
     * @param templatePath   模板文件位置
     * @param outputFile     文件输出位置
     * @param append         是否追加到文件
     * @param coverExistFile 当文件存在时是否覆盖
     * @throws Exception
     */
    public void generate(Map<String, Object> objectValueMap, String templatePath, String outputFile, boolean append, boolean coverExistFile) throws Exception {
        Template template = velocityEngine.getTemplate(templatePath, ConstVal.UTF8);
        try {
            // 这里的sw使用 OutputStreamWriter的时候生成的文件会错误 不知道为什么 换成StringWriter解决
            String realOutFilePathName = getRealOutFilePathName(outputFile, coverExistFile);
            StringWriter sw = new StringWriter();
            template.merge(new VelocityContext(objectValueMap), sw);
            //TODO 可以不是写到文件，而是输出到其他位置（比如压缩包）
            writeFile(new File(realOutFilePathName), sw.toString(), ConstVal.UTF8, append);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取真正的输出文件名称  如果文件存在，那么添加日期作为文件标识 （这样会导致java文件和类名不一样）
     */
    private String getRealOutFilePathName(String outFilePathName, boolean coverExistFile) {
        String realOutFilePathName = outFilePathName;
        if (!coverExistFile) {
            return realOutFilePathName;
        }
        File outFile = new File(realOutFilePathName);
        if (outFile.exists()) {
            String fileName = outFilePathName.substring(outFilePathName.lastIndexOf("\\") + 1, outFilePathName.lastIndexOf("."));
            String filePost = outFilePathName.substring(outFilePathName.lastIndexOf(".") + 1);
            String filePre = outFilePathName.substring(0, outFilePathName.lastIndexOf("\\") + 1);
            String newFileName = fileName + DateFormatUtils.format(new Date(), "yyyy-MM-dd HH-mm-ss");
            realOutFilePathName = filePre + newFileName + "." + filePost;
        }
        return realOutFilePathName;
    }

    private static void writeFile(File file, String content, String fileEncoding, boolean append) throws IOException {

        String path = file.getPath();
        path = path.substring(0, path.lastIndexOf("\\"));
        /*要有文件夹才能生成文件*/
        File file2 = new File(path);
        file2.mkdirs();
        // 如果文件不存在，那么追加的话会创建文件然后追加
        FileOutputStream fos = new FileOutputStream(file, append);
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
