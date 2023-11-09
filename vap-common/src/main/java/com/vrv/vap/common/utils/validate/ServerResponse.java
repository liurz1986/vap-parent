package com.vrv.vap.common.utils.validate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author wh1107066
 * @date 2021/7/10 10:36
 */
public class ServerResponse {
    private static final Logger logger = LoggerFactory.getLogger(ServerResponse.class);

    public static String illegalArgument(String defaultMessage) {
        logger.info("错误消息{}", defaultMessage);
        return defaultMessage;
    }
}
