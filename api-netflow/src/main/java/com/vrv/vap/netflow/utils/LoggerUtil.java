package com.vrv.vap.netflow.utils;

import org.slf4j.Logger;

/**
 * @author wh1107066
 */
public class LoggerUtil {

    protected static Logger logger;

    public void debug(String message) {
        logger.debug(message);
    }


    public void debug(String message, String... args) {
        logger.debug(message, args);
    }

    public static LoggerUtil getLogger(Class clazz) {
        logger = org.slf4j.LoggerFactory.getLogger(clazz);
        return new LoggerUtil();
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }


    public void info(String message) {
        logger.info(message);
    }

    public void info(String message, String... args) {
        logger.info(message, args);
    }

    public void info(String message, LoggerPrintStrategy strategy) {
        if (strategy != null && strategy.whetherPrint()) {
            logger.info(message + "【第" + strategy.getIndex() + "次重复" + strategy.getKey() + "】");
        }
    }

    public void debug(String message, LoggerPrintStrategy strategy) {
        if (strategy.whetherPrint()) {
            logger.info(message + "【第" + strategy.getIndex() + "次重复" + strategy.getKey() + "】");
        }
    }

    public void error(String message, LoggerPrintStrategy strategy) {
        if (strategy.whetherPrint()) {
            logger.error(message + "【第" + strategy.getIndex() + "次重复" + strategy.getKey() + "】");
        }
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }


    public void debug(String message, Throwable e) {
        logger.debug(message, e);
    }


    public void info(String message, Throwable e) {
        logger.info(message, e);

    }

    public void info(String message, Throwable e, LoggerPrintStrategy strategy) {
        if (strategy.whetherPrint()) {
            logger.info(message + "【第" + strategy.getIndex() + "次重复" + strategy.getKey() + "】", e);
        }
    }

    public void warn(String message, Throwable e) {
        logger.warn(message, e);
    }

    public void error(String message, Throwable e) {
        logger.error(message, e);
    }

    public void error(String message, Throwable e, LoggerPrintStrategy strategy) {
        if (strategy.whetherPrint()) {
            logger.error(message + "【第" + strategy.getIndex() + "次重复" + strategy.getKey() + "】", e);
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
