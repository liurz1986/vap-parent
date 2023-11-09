package com.vrv.vap.syslog.common.utils;

import com.vrv.vap.syslog.common.constant.SyslogConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * @author wh1107066
 */
public class UserUtil {
    private static Logger logger = LoggerFactory.getLogger(UserUtil.class);

    public static Object getUser() {
        HttpSession session = SessionUtils.getSession();
        if(session != null) {
          Object user = session.getAttribute(SyslogConstant.USER);
          return user;
        } else {
            logger.error("user wei kong!!!");
        }
        return null;
    }
}