package xxs.common.module.utils.other;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 占位符字符串解析器 解析占位符，比如#{aaa}中的aaa
 *
 * @author xxs
 */
public class PlaceholderStringResolver {
    private static String DEFAULT_PLACEHOLDER_REGX = "\\#\\{(.*?)\\}";

    /**
     * 获取占位符字符串值
     *
     * @param content
     * @return
     */
    public static List<String> getPlaceholderStrings(String content) {
        return getPlaceholderStrings(DEFAULT_PLACEHOLDER_REGX, content);
    }

    /**
     * 获取占位符字符串值
     *
     * @param placeholderRgex
     * @param content
     * @return
     */
    public static List<String> getPlaceholderStrings(String placeholderRgex, String content) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(placeholderRgex);
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }

    /**
     * 获取占位符字符串值
     *
     * @param content
     * @return
     */
    public static boolean hasPlaceholderString(String content) {
        return hasPlaceholderString(DEFAULT_PLACEHOLDER_REGX, content);
    }

    /**
     * 获取占位符字符串值
     *
     * @param placeholderRgex
     * @param content
     * @return
     */
    public static boolean hasPlaceholderString(String placeholderRgex, String content) {
        Pattern pattern = Pattern.compile(placeholderRgex);
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            return true;
        }
        return false;
    }

}
