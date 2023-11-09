package com.vrv.vap.server.zuul.common;

/**
 * @Author: liujinhui
 * @Date: 2019/8/14 15:08
 */
public class ZuulConstant {

    /**
     *  授权导入，认证之后存放在Session中的key
     */
    public static final String LICENSEAUTHENTICATION_KEY = "licenseAuthentication";

    /**
     *  授权认证成功之后的key
     */
    public static final String LICENSEAUTHENTICATIONSUCCESS_KEY = "licenseAuthenticationSuccess";

    public static final String LICENSEAUTHENTICATION_DEFALUT_VALUE = "false";

    public static final String LICENSEVALIDATEURL = "/hardware/?__hide=true#/configurationWizard";


    public static final String LOGIN_PAGE = "_LOGIN_PAGE";

    public static final String AUTHTYPE_CEMS = "3";


    /**
     *  国网
     */
    //登录
    public static final int STATUS_LOGIN = 201;

    //无权限
    public static final int STATUS_FORBIDDEN = 403;

    //主页
    public static final int STATUS_HOME = 202;

    /**
     * 默认密码
     */
    public static final Integer PWD_STATUS_DEFAULT =  0;

    /**
     * 修改密码页面
     */
    public static final String MODIFY_PWD_URL = "/modify_password/";

    /**
     * 修改密码接口-登录后
     */
    public static final String UPD_PWD_URL = "/api-common/user/upd";

    public static final String AUTH_STRATEGY_KEY = "_AUTH_STRATEGY";
    public static final String API_ALL_KEY = "_API_ALL";
    public static final String ROLE_API = "_ROLE_API";

    // 垂直越权开关
    public static final String AUTHORITY_EXCEED_STRATEGY_SWITCH = "authority_exceed_strategy_switch";
    // 未维护接口开关
    public static final String AUTHORITY_EXCEED_STRATEGY_UNKNOWN = "authority_exceed_strategy_unknown";
    // 白名单列表
    public static final String AUTHORITY_EXCEED_STRATEGY_WHITELIST = "authority_exceed_strategy_whitelist";

    public final static  String CLIENT_HEADER_AUTH="Authorization_App_Token";

    public final static  String CLIENT_AUTH_API="/api-common/auth/app/token";
}
