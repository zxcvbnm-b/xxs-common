package xxs.common.module.utils.sql;

import lombok.extern.slf4j.Slf4j;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql注入处理工具类  对于一些大数据项目或者是在线的报表不能使用到占位符的方式都会存在一些sql注入的漏洞的
 * 还有的方式比如可以将'转义成''？ 或者说是使用\转义 比如'转义成\'  select * from sys_oss where url='${aa}' aa = a' or 1=1# 也可以预防（#是注释）
 * 但是如下这种方式 如果输入的字符串中有这些字眼，也会报错，那么是不合适的，如果可以使用转义那么还是使用转义的方式比较好
 */
@Slf4j
public class SqlInjectionUtil {
    private final static String XSS_STR = "and |extractvalue|updatexml|geohash|gtid_subset|gtid_subtract|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |;|or |+|user()";

    /**
     * 正则 user() 匹配更严谨
     */
    private final static String REGULAR_EXPRE_USER = "user[\\s]*\\([\\s]*\\)";
    /**正则 show tables*/
    private final static String SHOW_TABLES = "show\\s+tables";

    /**
     * sql注释的正则
     */
    private final static Pattern SQL_ANNOTATION = Pattern.compile("/\\*[\\s\\S]*\\*/");

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     * @param value
     */
    public static void filterContent(String value) {
        filterContent(value, null);
    }

    /**
     * sql注入过滤处理，遇到注入关键字抛异常
     *customXssString :可以传入其他的一些  XSS_STR 比如单引号
     * @param value
     * @return
     */
    public static void filterContent(String value, String customXssString) {
        if (value == null || "".equals(value)) {
            return;
        }
        // 校验sql注释 不允许有sql注释
        checkSqlAnnotation(value);
        // 统一转为小写
        value = value.toLowerCase();
        //SQL注入检测存在绕过风险 https://gitee.com/jeecg/jeecg-boot/issues/I4NZGE
        //value = value.replaceAll("/\\*.*\\*/","");

        String[] xssArr = XSS_STR.split("\\|");
        for (int i = 0; i < xssArr.length; i++) {
            if (value.indexOf(xssArr[i]) > -1) {
                log.error("请注意，存在SQL注入关键词---> {}", xssArr[i]);
                log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
            }
        }
        //update-begin-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如 单引号
        if (customXssString != null) {
            String[] xssArr2 = customXssString.split("\\|");
            for (int i = 0; i < xssArr2.length; i++) {
                if (value.indexOf(xssArr2[i]) > -1) {
                    log.error("请注意，存在SQL注入关键词---> {}", xssArr2[i]);
                    log.error("请注意，值可能存在SQL注入风险!---> {}", value);
                    throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
                }
            }
        }
        //update-end-author:taoyan date:2022-7-13 for: 除了XSS_STR这些提前设置好的，还需要额外的校验比如 单引号
        if(Pattern.matches(SHOW_TABLES, value) || Pattern.matches(REGULAR_EXPRE_USER, value)){
            throw new RuntimeException("请注意，值可能存在SQL注入风险!--->" + value);
        }
        return;
    }
    /**
     * 校验是否有sql注释
     * @return
     */
    public static void checkSqlAnnotation(String str){
        Matcher matcher = SQL_ANNOTATION.matcher(str);
        if(matcher.find()){
            String error = "请注意，值可能存在SQL注入风险---> \\*.*\\";
            log.error(error);
            throw new RuntimeException(error);
        }
    }
}
