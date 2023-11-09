package com.vrv.vap.admin.common.enums;

import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.Result;

public enum  ErrorCode implements ResultAble{


    INVALIDATE("1000","用户名或者密码错误"),
    USER_IS_LOGIN("1001","用户已经登录"),
    USER_NAME_EMPTY("1002","用户帐号不能为空"),
    USER_PASS_EMPTY("1003","用户密码不能为空"),
    USER_NOT_EXIST("1004","用户不存在"),
    USER_FREEZE("1005","该帐号号已被冻结，请联系管理员"),
    USER_NO_ROLE("1006","帐号还没有分配角色，请联系管理员"),
    USER_OLD_INVALIDATE("1007","原始密码有误"),
    USER_TOOMANY_CODE("1008","一个证书绑定多个用户,请联系管理员"),
    USER_UNBIND_CODE("1009","证书未绑定用户,请联系管理员"),
    USER_TOOMANY_CODE_IRIS("1010","一个身份证绑定多个用户,请联系管理员"),
    LOGIN_LOCK("1011","连续s%次登录错误，账号将锁定s%分钟"),
    SIGN_VALID_ERROR("1012","请求签名验证错误"),
    TOKEN_INVALIDATE("1101","无效的TOKEN"),
    TOKEN_OUT_DATE("1102","TOKEN 已经失效"),
    TOKEN_USED("1103","TOKEN 已经被使用过了"),
    TOKEN_USER_ERROR("1104","用户信息已经变更，请重新登录"),
    CASCADE_LOGIN_CHECK_FAILE("1105","级联登录TOKEN验证失败"),
    CASCADE_CALL_INTERFACE_FAILE("1106","级联登录验证接口调用失败"),
    CASCADE_MAPREGION_NOT("1108","下级设备信息未找到"),
    CASCADE_HASNOT_USER("1109","未配置登录用户信息"),
    USER_HASNOT_SALT("1107","用户账号信息异常"),
    CASCADE_HASNOT_GUID("1110","参数错误"),
    CASCADE_LOGIN_TIMEOUT("1111","登录用户信息失效"),
    USER_IP_NOT_CONFIG("1112","当前登录IP不在配置范围内,请联系管理员"),
    USER_MAC_NOT_CONFIG("1113","当前登录MAC不在配置范围内,请联系管理员"),
    HAS_SAME_ACCOUNT("2001","已经存在相同帐号"),
    HAS_SAME_NAME("2002","已经存在同名用户"),
    OTHER_SAME_NAME("2003","请勿修改他人密码"),
    USER_PASS_ERROR("2004","用户密码不能为空"),
    USER_PASS_TIME_DIFF("2005","密码校验失败，请检查客户端与服务器时间差"),

    ROLE_HAS_USER("2101","请先删除角色下的所有用户，再删除该角色"),
    ROLE_CODE_EMPTY("2102","无效的角色编码"),
    ROLE_CODE_USED("2103","该角色编码已经被使用"),
    ROLE_USER_IN_USE("2104","请先取消角色下的所有用户，再禁用该角色"),

    RESOURCE_HAS_ROLE("2201","请先删除资源下所有的角色，再删除该资源"),
    RESOURCE_CODE_EMPTY("2202","无效的资源编码"),

    REGISTER_INTERFACE_ERROR("10001", "服务注册失败，接口访问错误"),
    REGISTER_PARAM_ERROR("10002", "服务注册失败，参数错误"),
    REGISTER_OTHER_ERROR("10003", "服务注册失败，其它错误"),
    USER_CODE_INVALIDATE("1088", "获取验证码为空！"),
    USER_MESSAGE_CODE_INVALIDATE("1089", "验证码错误，请重新输入！"),
    USER_MESSAGE_CODE_EXPIRE("1090","验证码已过期，请刷新验证码！"),


    ORG_IP_NULL("3001","IP参数为空"),
    ORG_IP_INVALIDATE("3002","IP地址无效"),
    ORG_IP_NOT_RANGE("3003","无此范围的IP机构"),
    ORG_IP_RANGE_VALIDATE("3004","结束IP必须大于起始IP"),
    ORG_CODE_NULL("3005","code参数为空"),
    ORG_IP_NOT_IN_PARENT("3006","IP段超出父机构所属范围"),
    ORG_IP_IN_SELF("3007","IP段与已经添加的IP段存在冲突"),
    ORG_USER_NULL("3008","userId参数为空"),
    ORG_USER_NOT_HAS_ORG("3009","该用户还未加入组织"),
    ORG_CONDITION_EMPTY("3010","code 与 userId 不能同时为空"),
    ORG_IS_SUPPER("3011","该用户和机构已经是最高级别了"),
    ORG_CAN_NOT_FIND("3012","无法找到该用户的组织"),
    ORG_ARGMENT_EMPTY("3012","code 与 ip 不能同时为空"),
    ORG_CODE_INVALIDATE("3013","没有找到该组织"),
    ORG_UP_NOT_FIND("3015","没有找到上级组织机构"),
    ORG_USER_NOT_FIND("3014","没有找到该用户"),
    ORG_HAS_SUB("3016","含有下级组织，请先删除下级组织"),
    ORG_HAS_IP("3017","含有IP段信息，请先删除IP段信息"),
    ORG_HAS_USER("3018","含有用户，请先删除用户"),
    AREA_NAME_NOT_FIND("3100","没有找到该IP对应的地区"),
    ORG_HAS_CODE("3102","组织机构编码重复"),
    ORG_SAVE_FAIL_SUBCODE("3103","机构层级关系维护码生成失败"),
    ORG_CODE_EMPTY("3104","组织机构编码不能为空"),
    ORG_CODE_NOT_MATCH("3105","组织机构编码仅由数字与字母组成"),

    PERSON_EXIST("3120","员工编号已存在"),
    PERSON_NO_INVALID("3121","员工编号只能由数字和字母和部分字符组成"),


    AREA_IS_NULL("3101","无区域数据"),
    SEGMENT_IP_IS_NULL("3201","无IP段区域数据"),

    DOMAIN_HAS_SUB("3216","含有下级安全域，请先删除下级"),
    DOMAIN_HAS_IP("3217","含有IP段信息，请先删除IP段信息"),
    DOMAIN_HAS_NAME("3218","安全域名称重复"),
    DOMAIN_NAME_NOT_FIND("3219","没有找到该IP对应的安全域"),
    DOMAIN_HAS_USER("3220","请先解除所有用户在该安全域下的权限，再删除安全域"),

    RESOURCE_NULL("4001","无效的 Parent Resource"),

    BACKUP_CONNECT_ERROR("4101","数据库连接失败"),
    BACKUP_QUERY_ERROR("4102","查询数据失败"),
    BACKUP_FILE_ERROR("4101","文件生成失败"),

    RECOVERY_PARAM_ERROR("4201","参数错误"),
    RECOVERY_FILE_FIND_ERROR("4202","文件不存在"),
    RECOVERY_VALID_ERROR("4203","文件校验失败"),
    RECOVERY_PARSE_ERROR("4204","文件解析错误"),

    SERVICE_NOT_FOUND("5001", "未查询到该服务"),
    CMD_NOT_FOUND("5002", "不合法的命令"),

    USER_LOCK("6000", "登录失败，错误登录次数太多，暂时禁止登录"),

    FIEL_NOT_EXIST("67001","文件不存在"),
    FIEL_RORR("67002","文件解析错误"),
    FIEL_TRANSFER_RORR("67003","文件解析错误"),
    ORGANZATION_CODE_NULL("67004","录入数据存在关键信息为空，请检查"),

    PERSON_CODE_NULL("67009","录入数据存在关键信息为空，请检查"),

    PERSON_ORG_CODE_NULL("67009","组织机构编码不存在"),

    ORGANZATION_CODE_REPEAT("67005","文档组织机构编码有重复"),

    PERSONCODE_REPEAT("67105","员工编码有重复"),

    ORGANZATION_PCODE_REPEAT("67006","请输入唯一跟节点"),
    ORGANZATION_TYPE_NULL("67008","机构类型不能为空"),
    ORGANZATION_TYPE_ORG("67009","机构类型有且仅有一个单位"),

    USER_PASS_OUTTIME("7000", "管理员密码已经失效"),

    THREE_POWER_OFF("8000", "三权未开启，当前用户无法登陆"),
    THREE_POWER_NOT_CHOOSE("9001","三权已开启，请勾选对应的三权"),
    THREE_POWER_CHOOSE_WRONG("9002","勾选的三权与当前用户的三权不一致，请重新选择"),
    THREE_POWER_ROLE_CREATE_WRONG("9003","三权开启，超级管理员不可创建角色"),
    THREE_POWER_USER_CREATE_WRONG("9004","三权开启，超级管理员不可赋予用户超级管理员角色权限"),

    PWD_MIN("8001", "密码长度低于最小长度限制"),
    PWD_MAX("8002", "密码长度大于最大长度限制"),
    PWD_UPPER("8003", "密码中必须包含大写字母"),
    PWD_LOWER("8004", "密码中必须包含小写字母"),
    PWD_SPECIAL("8005", "密码中必须包含特殊字符或下划线"),
    PWD_NUMBER("8006", "密码中必须包含数字"),
    PWD_TWO_OR_MORE("8007","密码中必须包含数字、字母、特殊字符两者及以上"),

    LICENSE_INVALID("9001","授权校验失败"),

    MESSAGE_DELETE_WRONG("9100","内置短信模板禁止删除"),

    UIAS_UIASLOGIN_CALLBACK_FAIL("9201","回调接口调用失败，未携带参数"),
    UIAS_UIASLOGIN_NO_RESULT("9202","调用UIAS系统，返回结果为空"),
    UIAS_UIASLOGIN_DECODE_FAIL("9203","人员信息，解密结果不正确"),
    UIAS_LOGOUT_FAIL("9203","平台单点登出失败"),

    CEMS_DATA_SYSN_NO_PARA("9301","数据同步回调失败，未携带相关参数"),

    APP_NOT("9501","未配置该应用信息！"),

    UPLOAD_FILE_TYPE_ERROR("9601","上传文件格式不正确"),

    FLUME_DATA_ANALYSIS_ERROR("11001","数据格式解析失败"),
    FLUME_COLLECTION_UPDATE_ERROR("11002","预定义规则集不可修改"),
    FLUME_COLLECTION_DELETE_ERROR("11003","预定义规则集不可删除"),
    FLUME_RULE_SOURCE_ERROR("11004","请输入日志样例"),
    FLUME_COLLECTION_IN_USE("11005","规则集使用中，不可删除"),
    FLUME_ACCESS_PORT_IN_USE("11006","此日志源设备下接收端口已被占用，请更换端口再重新启动"),
    FLUME_OFFLINE_COLLECTION_ERROR("11007","离线导入时，规则集只能有一条规则"),
    FLUME_MONITOR_TRANS_ERROR("11008","监测器转发接收器已存在，不可重复配置"),
    FLUME_DUPLICATE_PORT("11009","端口重复，请重新选择端口"),
    FLUME_ACCESS_TOO_MANY_ERROR("11010","产品授权使用数量:s%个,当前运行接入任务数已超过限制"),

    RESOURCE_API_SYNC_ERROR("12001","同步失败，请检查同步地址及服务状态"),

    BUILD_IN_DATA_DELETE_ERROR("13001","内置数据不可删除"),

    OFFLINE_TEMPLATE_TYPE_ERROR("14001", "文件导入失败，失败原因：当前导入文件格式不对。"),
    OFFLINE_CONTENT_NOT_MATCH("14002","文件导入失败，失败原因：当前导入文件内容格式与模板不匹配。"),
    OFFLINE_CONTENT_PARSE_ERROR("14003","文件导入失败，失败原因：数据格式异常。"),
    ;




    private Result result;

    @Override
    public Result getResult() {
        return this.result;
    }

    ErrorCode(String code, String message){
        this.result = new Result(code,message);
    }
}
