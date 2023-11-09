package com.vrv.vap.toolkit.tools;

import com.google.gson.Gson;
import com.vrv.vap.common.model.User;
import com.vrv.vap.toolkit.config.EnviromentConfig;
import com.vrv.vap.toolkit.model.EnvironmentModel;
import com.vrv.vap.toolkit.model.UserInfoModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * session辅助工具
 *
 * @author xw
 * @date 2018年5月3日
 */
public final class SessionTools {
    private static final Log log = LogFactory.getLog(SessionTools.class);

    /**
     * 获取登录用户信息
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Optional<UserInfoModel> getUserInfo() {
        HttpSession session = getSession();
        if (session == null) {
            return Optional.empty();
        }
        User user = (User) session.getAttribute("_USER");
        if (user == null) {
            return Optional.empty();
        }
        Gson gson = new Gson();
        UserInfoModel userInfoModel = gson.fromJson(gson.toJson(user), UserInfoModel.class);

        // 获取用户的系统机构编码（用于级联过滤）
        String orgSubCode = (String) session.getAttribute("_SUB_CODE");
        userInfoModel.setOrgSubCode(orgSubCode);

        return Optional.of(userInfoModel);
    }

    /**
     * 获取session
     *
     * @return
     */
    public static HttpSession getSession() {
        EnvironmentModel environment = EnviromentConfig.getEnvironment();
        if (environment == null || environment.getRequest() == null) {
            return null;
        }
        return environment.getRequest().getSession();
    }

    /**
     * 从session中获取是否开启数据权限过滤
     *
     * @return
     */
    public static boolean isPermissionCheck() {
        HttpSession session = getSession();
        Map<String, Integer> userExtends = null;
        if (session != null) {
            userExtends = (Map<String, Integer>) session.getAttribute("_USER_EXTENDS");
            if (userExtends != null && userExtends.get("authorityType") != null && userExtends.get("authorityType") == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从session中获取数据权限过滤的安全域
     *
     * @return
     */
    public static Set<String> getSecurityDomains() {
        HttpSession session = getSession();
        Map<String, String> securityDomains = null;
        if (session != null) {
            securityDomains = (Map<String, String>) session.getAttribute("_DOMAIN");
            if (securityDomains != null && securityDomains.keySet().size() > 0) {
                return securityDomains.keySet();
            }
        }
        return new HashSet<>();
    }

    /**
     * 从session中获取数据权限过滤的安全域及其对应的ip段
     *
     * @return
     */
    public static Map<String, String> getSecurityDomainAndIpRange() {
        HttpSession session = getSession();
        Map<String, String> securityDomains = null;
        if (session != null) {
            securityDomains = (Map<String, String>) session.getAttribute("_DOMAIN");
        }
        return securityDomains;
    }

    /**
     * 从session中获取数据权限过滤安全域对应的ip段
     *
     * @return
     */
    public static Set<String> getIpRange() {
        Set<String> result = new HashSet<>();
        HttpSession session = getSession();
        Map<String, String> securityDomains = null;
        if (session != null) {
            securityDomains = (Map<String, String>) session.getAttribute("_DOMAIN");
            if (securityDomains != null) {
                securityDomains.forEach((k, v) -> {
                    //if (v.indexOf(",") > -1) {
                    String[] ipRanges = v.split(",");
                    if (ipRanges.length > 0) {
                        Arrays.asList(ipRanges).forEach(ipRange -> {
                            String[] ips = ipRange.split("-");
                            if (ips != null && ips.length == 2) {
                                result.add(IpTools.getIpConvertNum(ips[0]) + "," + IpTools.getIpConvertNum(ips[1]));
                            }
                        });
                    }
                    //}
                });
            }
        }
        return result;
    }

    /**
     * 获取访问ip
     *
     * @return
     */
    public static String getRemoteIp() {
        return EnviromentConfig.getEnvironment().getRequest().getRemoteAddr();
    }

    /**
     * 获取服务器ip
     *
     * @return
     */
    public static String getServerIp() {
        return EnviromentConfig.getEnvironment().getRequest().getLocalAddr();
    }
}
