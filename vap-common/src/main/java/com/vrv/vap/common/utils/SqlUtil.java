package com.vrv.vap.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * sql操作工具类
 *
 * @author wh1107066
 */
public class SqlUtil {
    private static Logger logger = LoggerFactory.getLogger(SqlUtil.class);
    /**
     * 仅支持字母、数字、下划线、空格、逗号（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,]+";

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value)) {
            logger.info("参数不符合规范，不能进行查询");
            return StringUtils.EMPTY;
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) {
        return value.matches(SQL_PATTERN);
    }
}
