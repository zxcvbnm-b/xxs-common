package xxs.common.module.codegenerate.velocity;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.util.JdbcUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import xxs.common.module.codegenerate.model.TableColumn;
import xxs.common.module.codegenerate.model.TbVelocityTemplateEntity;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author xxs
 */
@Slf4j
public class VelocityDbTemplateHelper {
    private DataSource dataSource;
    private Properties velocityProp;
    private final static String DEFAULT_TEMPLATE_PATH = "templates";
    public final static String DB_STORAGE_FORMAT_PROPERTIES_TABLE_NAME = "tb_velocity_template";
    public final static String DB_STORAGE_FORMAT_PROPERTIES_KEY_COLUMN_NAME = "id_template";
    public final static String DB_STORAGE_FORMAT_PROPERTIES_TEMPLATE_COLUMN_NAME = "template_definition";
    public final static String DB_STORAGE_FORMAT_PROPERTIES_TIMESTAMP_COLUMN_NAME = "template_timestamp";
    public final static String DB_STORAGE_FORMAT_DEFAULT_CREATE_TABLE_SQL = "   CREATE TABLE IF NOT EXISTS tb_velocity_template (\n" +
            "      id_template varchar (200) PRIMARY KEY ,\n" +//模板id
            "      template_definition longtext NOT NULL ,\n" +//模板内容
            "      template_timestamp datetime NOT NULL,\n" +
            "      built_in tinyint DEFAULT 0,\n" +//是否是内部模板 0不是，1是
            "      template_name varchar (200)\n" +//模板名称
            "    )charset=utf8mb4;";//不然可能会报错：mysql incorrect string value 是因为有些字符是占用四个字节的
    public final static String DB_STORAGE_FORMAT_DEFAULT_TRUNCATE_TABLE_SQL = "DELETE FROM tb_velocity_template where built_in = 1;";

    public VelocityDbTemplateHelper(DataSource dataSource, Properties velocityProp) {
        this.dataSource = dataSource;
        this.velocityProp = velocityProp;
    }

    public void initDefaultTemplateToDb() {
        // 扫描DEFAULT_TEMPLATE_PATH下的类到tb_velocity_template表中
        try {
            //1.每次启动时更新掉内置的模板
            this.truncateTemplateTable();
            //2.初始化内置所有模板到DB
            this.insertTemplates();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void truncateTemplateTable() throws SQLException {
        JdbcUtils.execute(dataSource, DB_STORAGE_FORMAT_DEFAULT_TRUNCATE_TABLE_SQL);
    }

    private void insertTemplates() throws SQLException, IOException {
        visitVelocityTemplateFile(DEFAULT_TEMPLATE_PATH, f -> {
            if (f.isFile() && f.getName().endsWith(".vm")) {
                TbVelocityTemplateEntity tbVelocityTemplateEntity = new TbVelocityTemplateEntity();
                tbVelocityTemplateEntity.setIdTemplate(f.getPath().substring(f.getPath().lastIndexOf(DEFAULT_TEMPLATE_PATH)).replace("\\", "/"));
                tbVelocityTemplateEntity.setTemplateName(f.getName().replace(".vm", ""));
                String templateDefinition = FileUtil.readUtf8String(f);
                tbVelocityTemplateEntity.setTemplateDefinition(templateDefinition);
                tbVelocityTemplateEntity.setBuiltIn(true);
                tbVelocityTemplateEntity.setTemplateTimestamp(new Date());
                try {
                    JdbcUtils.insertToTable(dataSource, DB_STORAGE_FORMAT_PROPERTIES_TABLE_NAME, getEntityBeanParamMap(tbVelocityTemplateEntity));
                } catch (SQLException e) {
                    log.error("insertTemplates error templateInfo = {}", JSONUtil.toJsonStr(tbVelocityTemplateEntity), e);
                }
            }
        });
    }

    /**
     * 尝试创建存储velocity渲染模板的表。
     */
    public void createDbTemplateResourceTable() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement(DB_STORAGE_FORMAT_DEFAULT_CREATE_TABLE_SQL).execute();
        } catch (Exception e) {
            log.error("createDbTemplateResourceTable error", e);
            throw e;
        }
    }

    /**
     * 访问classPath下的资源
     *
     * @param templateDirName 文件夹名称
     * @param consumer
     * @throws IOException
     */
    private static void visitVelocityTemplateFile(String templateDirName, Consumer<File> consumer) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(templateDirName);
        if (classPathResource.exists()) {
            visitVelocityTemplateFile(classPathResource.getFile(), consumer);

        }
    }

    private static void visitVelocityTemplateFile(File templateDir, Consumer<File> consumer) {
        if (templateDir.exists() && templateDir.isDirectory()) {
            File[] dirFiles = templateDir.listFiles();
            for (File dirFle : dirFiles) {
                if (dirFle.isDirectory()) {
                    visitVelocityTemplateFile(dirFle, consumer);
                }
                consumer.accept(dirFle);
            }
        }
    }

    public static Map<String, Object> getEntityBeanParamMap(Object source) {
        Map<String, Object> result = null;
        if (source == null) {
            return Collections.emptyMap();
        }
        result = new HashMap<>();
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(source);
                if (fieldValue != null) {
                    TableColumn tableColumn = field.getDeclaredAnnotation(TableColumn.class);
                    if (tableColumn != null) {
                        result.put(tableColumn.fieldName(), fieldValue);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
