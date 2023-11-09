package com.vrv.vap.xc.tools;

import com.vrv.vap.toolkit.constant.Common;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.vo.Query;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 查询及分页处理
 *
 * @author xw
 * @date 2018年4月19日
 */
public class PageTools {
    private static final Log log = LogFactory.getLog(PageTools.class);

    private static final String START = "setMyStart";
    private static final String COUNT = "setMyCount";
    private static final String ORDER_BY = "setOrderByClause";

    private static String[] errChars = {"(", ")", ","};

    /**
     * 设置分页和排序
     *
     * @param example
     * @param pm
     */
    public static void setAll(Object example, Query pm) {
        setPage(example, pm);
        setOrderBy(example, pm);
    }

    /**
     * 设置分页
     *
     * @param example
     * @param pm
     */
    public static void setPage(Object example, Query pm) {
        Class<?> clazz = example.getClass();
        try {
            clazz.getDeclaredMethod(START, Integer.class).invoke(example, pm.getMyStart());
            clazz.getDeclaredMethod(COUNT, Integer.class).invoke(example, pm.getMyCount());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            log.error("", e);
        }
    }

    /**
     * 设置排序
     *
     * @param example
     * @param pm
     */
    public static void setOrderBy(Object example, Query pm) {
        Class<?> clazz = example.getClass();
        try {
            if (StringUtils.isEmpty(pm.getOrder()) || safeCheck(pm.getOrder())) {
                return;
            }
            clazz.getDeclaredMethod(ORDER_BY, String.class).invoke(example,
                    new StringBuilder().append(CommonTools.camelToUnderLine(pm.getOrder())).append(" ").append(pm.getBy() != null ? pm.getBy() : "asc").toString());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            log.error("", e);
        }
    }

    /**
     * 特殊字符校验
     *
     * @param order
     * @return
     */
    private static boolean safeCheck(String order) {

        for (String errChar : errChars) {
            if (order.indexOf(errChar) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 当分页参数未设置时自动添加
     *
     * @param param
     */
    public static void setPageIfEmpty(Map<String, Object> param) {
        Object start = param.get(Common.START);
        Object count = param.get(Common.COUNT);

        Object start_ = param.get(Common.START_);
        Object count_ = param.get(Common.COUNT_);

        if (null == start && null == start_) {
            param.put(Common.START, Common.START_DEF);
            param.put(Common.START_, Common.START_DEF);
        } else if (null != start && null == start_) {
            param.put(Common.START_, start);
        } else if (null == start && null != start_) {
            param.put(Common.START, start_);
        }

        if (null == count && null == count_) {
            param.put(Common.COUNT, Common.COUNT_DEF);
            param.put(Common.COUNT_, Common.COUNT_DEF);
        } else if (null == count && null != count_) {
            param.put(Common.COUNT, count_);
        } else if (null != count && null == count_) {
            param.put(Common.COUNT_, count);
        }
    }

    /**
     * 校验
     *
     * @param param
     */
    public static void paramCheck(Query param) {
        if (safeCheck(param.getOrder())) {
            param.setOrder(null);
            param.setBy(null);
        }
    }
}
