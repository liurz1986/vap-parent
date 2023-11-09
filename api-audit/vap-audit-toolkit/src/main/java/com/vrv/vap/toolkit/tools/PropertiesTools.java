package com.vrv.vap.toolkit.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获取属性文件内容工具类，属性文件放在conf包目录下
 *
 * @author xw
 * @date 2015年10月9日
 */
public final class PropertiesTools {
    private static Log log = LogFactory.getLog(CommonTools.class);

    private static Properties prop = null;

    static {
        init();
    }

    public static void init() {
        prop = loadProp("toolkit-settings.properties");
    }

    private static Properties loadProp(String filename) {
        Properties prop = new Properties();
        InputStream in = PropertiesTools.class.getClassLoader().getResourceAsStream(filename);

        try {
            prop.load(in);
            in.close();
        } catch (FileNotFoundException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }
        return prop;
    }

    public static String getValue(String key, String def, Properties prop) {
        String res = prop.getProperty(key);
        return StringUtils.isEmpty(res) ? def : res;
    }

    /**
     * 获取系统配置
     *
     * @param key
     * @param def
     * @return
     */
    public static String getSysProperties(String key, String def) {
        return PropertiesTools.getValue(key, def, prop).trim();
    }

    /**
     * 获取系统配置
     *
     * @param key
     * @param def
     * @return
     */
    public static int getSysPropertiesIntVal(String key, int def) {
        return Integer.parseInt(getSysProperties(key, String.valueOf(def).toString()));
    }

}
