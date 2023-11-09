package com.vrv.vap.syslog.common.utils;

import com.vrv.vap.syslog.model.SystemLog;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wh1107066
 * @date 2021/7/2 17:36
 */
public class SyslogContextHolder {

    public static final Logger logger = LoggerFactory.getLogger(SyslogContextHolder.class);

    /**
     * 使用ThreadLocal维护变量，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
    private static final ThreadLocal<SystemLog> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源的变量
     */
    public static void setSyslog(SystemLog systemLog) {
        if (logger.isDebugEnabled()) {
            logger.debug("AOP的SysRequestLog值{}", ReflectionToStringBuilder.toString(systemLog, ToStringStyle.MULTI_LINE_STYLE));
        }
        CONTEXT_HOLDER.set(systemLog);
    }

    /**
     * 获得SystemLog的变量
     */
    public static SystemLog getSyslog() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清空SystemLog变量
     */
    public static void clearSyslog() {
        CONTEXT_HOLDER.remove();
    }
}
