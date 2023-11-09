package com.vrv.vap.admin.common.constant;


public class Const {

    /**
     * 系统管理员角色，本系统中固定为 1
     * */
//    public static final int SYSTEM_ROLE = 1;

    /**
     * 组织机构 - 部
     * */
    public static final int HIERARCHY_GAB = 0;
    /**
     * 组织机构 - 省
     * */
    public static final int HIERARCHY_PROVINCE = 1;
    /**
     * 组织机构 - 市
     * */
    public static final int HIERARCHY_CITY = 2;
    /**
     * 组织机构 - 区
     * */
    public static final int HIERARCHY_TWON = 3;
    /**
     * 开启三权
     */
    public static final Byte THREE_POWER_ON = 1;
    /**
     * 三权配置
     */
    public static final String THREE_POWER_CONF = "THREE_POWER_ON";

    /**
     * 超级管理员的三权编号
     */
    public static final Integer ADMIN_THREE_POWER = 8;


    /**
     * 管理员账号
     */
    public static final String USER_ADMIN = "admin";

    /**
     * 安全保密管理员
     */
    public static final String SEC_ADMIN = "secadm";

    /**
     * 默认密码状态
     */
    public static final Byte PWD_STATUS_DEFAULT =  0;

    /**
     * 密码已修改
     */
    public static final Byte PWD_STATUS_CHANGED =  1;

    /**
     * 用户启用
     */
    public static final Byte USER_STATUS_ENABLE =  0;

    /**
     * 用户禁用
     */
    public static final Byte USER_STATUS_DISABLE =  1;

    /**
     * 用户已删除
     */
    public static final Byte USER_STATUS_DEL =  2;

    /**
     * 默认密码
     */
    public static final String PWD_DEFAULT_FIELD = "pw_default";

    /**
     * 默认密码
     */
//  public static final String PWD_DEFAULT = "e10adc3949ba59abbe56e057f20f883e";
    public static final String PPP_DEFAULT = "9898410d7f5045bc673db80c1a49b74f088fd7440037d8ce25c7d272a505bce5";

    /**
     * 密码盐
     */
    public static final String DEFAULT_SALT = "salt";

    /**
     * 登录错误次数默认为0
     */
    public static final int LOGIN_ERROR_TIMES =  0;


    /**
     * 是否领导0
     */
    public static final Byte IS_LEADER_STATUS =  0;

    /**
     * 执行失败0
     */
    public static final int LOGIN_FAIL =  0;
    /**
     * 执行成功1
     */
    public static final int LOGIN_SUCCESS =  1;

    /**
     * 开启
     */
    public static final Byte CONF_ENABLED = 1;

    /**
     * 关闭
     */
    public static final Byte CONF_DISABLED = 0;

    public static final String USER_SYSCONTROLLER = "sysController";

    public static final String USER_SAFETER = "safeter";

    public static final String USER_AUDITER = "auditer";

    /**
     * 200-成功
     */
    public final static String CODE_SUCCESS = "200";
    /**
     * 0-success
     */
    public final static String CODE_NEW_SUCCESS = "0";

    /**
     *	301-代表永久性转移
     */
    public final static String CODE_PERMANENTLY_MOVED = "301";

    /**
     * 500-业务逻辑错误
     */
    public final static String CODE_ERROR_SERVICE = "500";

    /**
     * 501-功能不完善，无对应方法
     */
    public final static String CODE_ERROR_FUNCTION = "501";

    /**
     * 502-网络异常
     */
    public final static String CODE_ERROR_WEB = "502";
    /**
     * 503-未知其它
     */
    public final static String CODE_ERROR_OTHER = "503";

    public final static String RETURN_SUCCESS = "SUCCESS";

    public final static String TOKEN_VALIDATE_SUCCESS = "001";

    public final static String SECRET_MGR = "secretMgr";

    public final static String BUSINESS_MGR = "businessMgr";

    public final static String OPERATION_MGR = "operationMgr";
}
