
package xxs.common.module.utils.other;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxs.common.module.codegenerate.Constants;

import java.util.List;
import java.util.Properties;

/**
 * @author xxs
 */
public class XxsProperties extends Properties {
    private static final Logger logger = LoggerFactory.getLogger(LoadPropertyUtils.class);

    XxsProperties(Properties properties) {
        super(properties);
    }


    /**
     * get property value
     *
     * @param key property name
     * @return property value
     */
    public String getString(String key) {
        return this.getProperty(key.trim());
    }

    /**
     * get property value with upper case
     *
     * @param key property name
     * @return property value  with upper case
     */
    public String getUpperCaseString(String key) {
        String val = getString(key);
        return StringUtils.isEmpty(val) ? val : val.toUpperCase();
    }

    /**
     * get property value
     *
     * @param key        property name
     * @param defaultVal default value
     * @return property value
     */
    public String getString(String key, String defaultVal) {
        String val = getString(key);
        return StringUtils.isEmpty(val) ? defaultVal : val;
    }

    /**
     * get property value
     *
     * @param key property name
     * @return get property int value , if key == null, then return -1
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * @param key          key
     * @param defaultValue default value
     * @return property value
     */
    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(), e);
        }
        return defaultValue;
    }

    /**
     * get property value
     *
     * @param key property name
     * @return property value
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * get property value
     *
     * @param key          property name
     * @param defaultValue default value
     * @return property value
     */
    public Boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        return StringUtils.isEmpty(value) ? defaultValue : Boolean.parseBoolean(value);
    }

    /**
     * get property long value
     *
     * @param key          key
     * @param defaultValue default value
     * @return property value
     */
    public long getLong(String key, long defaultValue) {
        String value = getString(key);
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage(), e);
        }
        return defaultValue;
    }

    /**
     * @param key key
     * @return property value
     */
    public long getLong(String key) {
        return getLong(key, -1);
    }

    /**
     * set value
     *
     * @param key   key
     * @param value value
     */
    public void setValue(String key, String value) {
        this.setProperty(key, value);
    }

    public List<String> getStringList(String key) {
        return getStringList(key, null);
    }

    public List<String> getStringList(String key, List<String> defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        return StrUtil.splitTrim(value, Constants.COMMA_SEPARATOR);
    }
}
