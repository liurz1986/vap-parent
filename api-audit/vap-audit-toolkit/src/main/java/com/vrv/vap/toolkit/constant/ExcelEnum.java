package com.vrv.vap.toolkit.constant;

/**
 * 描述每个导入导出的excel信息
 *
 * @author xw
 * @date 2018年4月3日
 */
public enum ExcelEnum {

    /**
     * 应用系统信息
     */
    SYSINFO("应用系统信息", new String[]{"name", "sysLevel", "sysState", "sysType", "areaCode", "appTypeId", "infoScope",
        "developLanguage", "sysFramework", "otherPlugin", "externalDevice", "extDeviceInfo",
        "browserInfo", "accountType", "userLoginType", "isAuditModel", "isSecurityPlatform", "isQueryModel",
        "databaseType", "databaseName", "userName", "tel", "infoOfficePolice",
        "buildCompany", "buildContact", "buildTel", "createModel", "completeTime", "isFontSoftware",
        "isFontCompatible", "webType", "isOverPkiOrPmi", "isRankRecord", "rankLevel", "systemId"},
        new String[]{"应用/网站名称", "系统等级", "系统状态", "系统类别", "应用/网站区域", "业务分类", "信息应用域", "开发语言", "系统架构",
            "所使用的第三方插件", "所需要的外接设备", "外接设备型号", "运行浏览器及版本号", "账户类型", "个人用户登录方式", "是否有日志审计模块", "是否已对接安审平台",
            "是否有查询模块", "数据库类型 ", "数据库实例名 ", "业务联系人", "业务联系人电话", "科通部门对接民警",
            "承建公司", "承建公司联系人", "承建公司联系人电话", "建库模式", "开发完成时间", "是否使用文字处理软件", "与国产文字处理软件是否兼容", "注册类别",
            "是否完成PK/PMI改造", "是否定级备案", "定级情况", "巨龙系统编号"}),

    /**
     * 应用系统信息(简版)
     */
    SYSINFO3("应用系统信息", new String[]{"name", "systemId", "webType", "domain", "ip", "areaCode", "orgCode", "userName",
        "tel", "remark"},
        new String[]{"系统名称", "系统编号", "系统类型", "域名", "ip地址", "区域", "部门", "负责人", "负责人电话", "备注"}),

    /**
     * 新疆应用系统信息-导入
     */
    SYSINFO2_IMPORT("应用系统信息", new String[]{"name", "systemId", "domain", "areaCode", "orgCode", "userName", "tel",
        "ip", "isRankRecord", "rankLevel",
        "webType", "netType", "isImportant", "buildCompany", "buildContact", "buildTel", "isFontCompatible",
        "isCopyShare", "copyShareDesc", "enableStatus", "downTime", "isCrossNetwork", "crossNetworkDesc",
        "isInInternet", "dataInternetDesc", "isSecurityBaseline", "serverCount","isSecurityPlatform",
        "noLogDesc", "isOverPkiOrPmi", "noSingleTrackDesc", "multifactorial", "noMultifactorialDesc",
        "buildModel", "isLocalSave", "remark"
    },
        new String[]{"系统名称", "系统编号", "URL地址", "区域", "部门", "责任民警", "责任民警电话",
            "ip地址", "是否等保定级备案", "定级情况",
            "系统标识", "网络类型", "是否为重要应用系统", "厂商", "工程师", "工程师联系方式", "对接方式",
            "是否拷贝共享", "拷贝共享情况说明", "是否停用", "停用时间", "是否跨网络", "跨网络情况",
            "是否有数据落地公共互联网", "数据落地情况", "是否建立安全基线", "系统所属服务器数", "日志对接情况",
            "无需日志对接说明", "单轨制改造情况", "无需单轨制改造说明", "多因子改造情况", "无需多因子改造说明",
            "建设方式", "本地是否存储数据", "备注"
        }),

    /**
     * 新疆应用系统信息-导出
     */
    SYSINFO2_EXPORT("应用系统信息", new String[]{"name", "systemId", "domain", "areaCode", "orgCode", "userName", "tel",
        "ip", "isRankRecord", "rankLevel",
        "webType", "netType", "isImportant",
        "buildCompany", "buildContact", "buildTel", "isFontCompatible",
        "isCopyShare", "copyShareDesc", "enableStatus", "downTime", "isCrossNetwork", "crossNetworkDesc",
        "isInInternet", "dataInternetDesc", "isSecurityBaseline", "serverCount","isSecurityPlatform",
        "noLogDesc", "isOverPkiOrPmi", "noSingleTrackDesc", "multifactorial", "noMultifactorialDesc",
        "buildModel", "isLocalSave", "remark"},
        new String[]{"系统名称", "系统编号", "URL地址", "区域", "部门", "责任民警", "责任民警电话",
            "ip地址", "是否等保定级备案", "定级情况",
            "系统标识", "网络类型", "是否为重要应用系统", "承建厂商", "工程师", "工程师联系方式", "对接方式",
            "是否拷贝共享", "拷贝共享情况说明", "是否停用", "停用时间", "是否跨网络", "跨网络情况",
            "是否有数据落地公共互联网", "数据落地情况", "是否建立安全基线", "系统所属服务器数", "日志对接情况",
            "无需日志对接说明", "单轨制改造情况", "无需单轨制改造说明", "多因子改造情况", "无需多因子改造说明",
            "建设方式", "本地是否存储数据", "备注"}),

    /**
     * 人员信息
     */
    PERSONINFO("人员信息",
        new String[]{"userName", "personType", "policeCode", "userIdnEx", "policeTypeName",
            "stationName", "orgCode", "orgName", "status"},
        new String[]{"姓名", "人员类别", "警号", "身份证号", "警种", "岗位", "机构号", "机构", "维护状态"}),

    /**
     * 证书信息
     */
    BASE_PKI_INFO("证书信息",
        new String[]{"pkiId", "username", "userIdNumex", "policeType", "station", "certSn", "certNotBefore",
            "certNotAfter", "orgName"},
        new String[]{"PKI-ID", "姓名", "身份证号18位", "警种", "岗位", "证书序列号", "生效时间", "过期时间", "单位名称"}),

    /**
     * 联网设备信息
     */
    BASE_DEVICE_INFO("联网设备信息",
        new String[]{"ip", "mac", "areaCode", "deviceName", "deviceTypeId", "pkiId", "userName", "deptName",
            "officeName", "isProtect", "isLock", "registered", "isForceOut", "lastTime"},
        new String[]{"设备IP", "MAC地址", "区域", "设备名称", "设备类型", "注册人身份证", "注册人姓名", "单位名称", "部门名称", "保护", "信任", "注册",
            "阻断", "最后访问时间"}),

    /**
     * 证书信息
     */
    BASE_PKI_INFO_ABNORMAL("证书信息",
        new String[]{"pkiId", "username", "userIdNumex", "policeType", "station", "certSn", "certNotBefore",
            "certNotAfter", "certRole", "orgName", "dutyLevel", "charge", "id"},
        new String[]{"PKI-ID", "姓名", "身份证号18位", "警种", "岗位", "证书序列号", "生效时间", "过期时间", "证书类别", "单位名称", "职级", "任职",
            "异常类型"}),

    /**
     * 违规统计信息
     */
    WARN_STATISTICS_INFO("违规统计信息",
        new String[]{"username", "userId", "warnType", "state", "ip", "warnTime", "count"},
        new String[]{"姓名", "身份证", "报警类型", "状态", "ip地址", "报警日期", "次数"}),

    /**
     * 违规信息
     */
    //WARN_INFO("违规信息", new String[] { "username", "userId", "areaCode", "ip", "content", "warnTime", "operateTime" },
    //new String[] { "姓名", "身份证", "区域", "ip地址", "操作内容", "报警时间", "操作时间" }),
    WARN_INFO("违规信息", new String[]{"areaCode", "username", "userId", "riskEventCode", "statusEnum", "relatedIps", "triggerTime"},
        new String[]{"区域", "姓名", "身份证", "报警类型", "状态", "设备IP", "报警日期"}),

    GAME_PROCESS_INFO("游戏进程数据", new String[]{"areaCode", "ip", "pId", "signedName", "process", "version", "dataTime"},
        new String[]{"区域", "终端IP", "PID", "游戏名称", "进程名称", "版本", "时间"}),

    DANGER_PROCESS_INFO("危险进程数据", new String[]{"areaCode", "ip", "pId", "signedName", "process", "version", "time"},
        new String[]{"区域", "终端IP", "PID", "危险进程名称", "危险进程", "版本", "时间"}),

    GAME_WHITELIST_INFO("游戏进程白名单", new String[]{"process", "describ", "lastUpdateTime", "state"},
        new String[]{"游戏进程", "描述", "最近更新时间", "进程状态"}),

    DANGER_WHITELIST_INFO("危险进程白名单", new String[]{"process", "describ", "lastUpdateTime", "state"},
        new String[]{"危险进程", "描述", "最近更新时间", "进程状态"}),

    RED_LIST_WARN("红名单告警数据", new String[]{"ip", "idCard", "userName", "areaCode", "eventTime", "operateCondition", "count"},
        new String[]{"终端IP", "身份证", "姓名", "地区", "时间", "操作", "数量"}),

    HOT_LIST_WARN("热点库告警数据", new String[]{"ip", "idCard", "userName", "areaCode", "eventTime", "operateCondition", "count"},
        new String[]{"终端IP", "身份证", "姓名", "地区", "时间", "操作", "数量"}),
    GAME_PORT("游戏端口数据", new String[]{"gameName", "gamePort", "gameDescription"},
        new String[]{"游戏名称", "游戏端口", "描述"}),
    ACCOUNT_VISIT("账号访问系统预警 ", new String[]{"organization", "userId", "userName", "triggerTime", "repeatCount"},
        new String[]{"单位", "账号", "用户名", "预警时间", "查询次数"}),
    ACCOUNT_VISIT_DETAIL("账号访问系统预警详情 ", new String[]{"areaCode", "organization", "userName", "userId",
        "relatedIps", "triggerTime", "sysId", "triggerTime", "operateCondition"},
        new String[]{"区域", "单位", "用户名", "身份证", "终端ip", "系统名称", "操作时间", "操作条件"}),

    /**
     * 中海油2期-源ip Top分析
     */
    NETFLOW_SRCIP_TOP("流量态势-源IP的TOP分析",
        new String[]{"srcIp", "visitTimes", "ipCount", "portCount", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName", "hasKvs",
            "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"源ip", "访问次数", "ip次数", "端口数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 中海油2期-目标ip Top分析
     */
    NETFLOW_DSTIP_TOP("流量态势-目标IP的TOP分析",
        new String[]{"dstIp", "visitTimes", "ipCount", "portCount", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName", "hasKvs",
            "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"目标ip", "访问次数", "ip次数", "端口数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 中海油2期-端口Top分析
     */
    NETFLOW_DSTPORT_TOP("流量态势-端口TOP分析",
        new String[]{"srcIp", "dstPort", "visitTimes", "ipCount", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName",
            "hasKvs", "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"目标ip", "目标端口", "访问次数", "目标ip数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 中海油2期-应用安全态势-攻击设备排行
     */
    SYS_SECURITY_ATTACK_TOP("应用安全态势-攻击设备排行",
        new String[]{"srcIp", "total", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName",
            "hasKvs", "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"攻击者ip", "攻击次数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 中海油2期-应用访问统计-非工作时间访问排行
     */
    SYS_VISIT_UNWORK_TOP("应用访问统计-非工作时间访问排行",
        new String[]{"ip", "total", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName",
            "hasKvs", "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"设备ip", "访问次数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 中海油2期-资产应用态势-设备风险排行
     */
    DEVICE_RISK_TOP("资产应用态势-设备风险排行",
        new String[]{"ip", "score", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName", "hasKvs",
            "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"设备ip", "风险指数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 中海油2期-(综合态势)-攻击设备Top
     */
    THREAD_ATTACK_TOP("攻击设备Top",
        new String[]{"srcIp", "total", "areaCode", "deptName", "officeName",
            "ip", "mac", "registered", "userName", "tel", "deviceName", "domainName", "logonUserName", "hasKvs",
            "kvsCompany", "kvsVersion", "regIp", "clientVersion", "osType", "reserved1", "isProtect"},
        new String[]{"设备ip", "攻击次数", "所属区域", "二级单位", "三级单位", "IP地址", "MAC地址", "注册", "使用人", "联系电话", "设备名称", "登录域名称",
            "登录用户", "杀毒软件安装", "杀毒厂商", "杀毒软件版本", "管理器IP", "客户端版本号", "操作系统", "卸载", "保护"}),

    /**
     * 被访问人离散度概览
     */
    BEVISITED_PEOPLE_INFO("被访问人离散度概览", new String[]{"areaName", "userName", "idCard", "policeType", "nums", "total"},
        new String[]{"区域名称", "姓名", "身份证", "警种", "查询的地区个数", "查询次数"}),

    /**
     * 被访问人离散度详情
     */
    BEVISITED_PEOPLE_DETAIL_INFO("被访问人离散度详情",
        new String[]{"areaName", "userName", "idCard", "ip", "sysId", "operTime", "organ", "operation",
            "policeType"},
        new String[]{"区域名称", "姓名", "身份证", "ip地址", "系统名称", "操作时间", "机构", "操作条件", "警种"}),

    /**
     * 热点人物区域统计
     */
    HOT_PEOPLE_AREA_INFO("热点对象统计", new String[]{"areaName", "peopleCount", "allCount"},
        new String[]{"区域名称", "访问人数", "访问次数"}),

    /**
     * 热点人物分析概览
     */
    HOT_PEOPLE_SUMMARY_INFO("热点对象分析概览",
        new String[]{"areaName", "userName", "idCard", "ip", "peopleCount", "allCount"},
        new String[]{"区域名称", "姓名", "身份证", "ip地址", "访问人数", "访问次数"}),

    /**
     * 热点人物分析详情
     */
    HOT_PEOPLE_DETAIL_INFO("热点对象分析详情",
        new String[]{"areaName", "userName", "idCard", "ip", "sysId", "org", "people", "operTime", "operation"},
        new String[]{"区域名称", "姓名", "身份证", "ip地址", "系统名称", "机构", "操作对象", "操作时间", "操作条件"}),

    /**
     * 同警种访问概览
     */
    SAME_PLOCE_ACCESS_INFO("同警种访问概览",
        new String[]{"areaCode", "idCard", "username", "policeType", "avgCount", "count"},
        new String[]{"区域", "身份证", "姓名", "警种", "警种平均查询次数", "查询次数"}),

    /**
     * 同机构访问概览
     */
    SAME_ORGAN_ACCESS_INFO("同机构访问概览",
        new String[]{"areaCode", "idCard", "username", "orgCode", "orgname", "avgCount", "count"},
        new String[]{"区域", "身份证", "姓名", "机构编码", "机构", "警种平均查询次数", "查询次数"}),
    /**
     * 同机构同警种访问详情
     */
    SAME_ACCESS_DETAIL_INFO("访问详情",
        new String[]{"area_code", "user_id", "user_name", "org_code", "police_type", "ip", "sys_id", "operate_condition", "time"},
        new String[]{"区域", "身份证", "姓名", "机构", "警种", "IP", "应用名称", "操作条件", "时间"}),
    /**
     * 访问对象分析概览
     */
    VISIT_OBJECT_SUMMARY_INFO("访问对象分析概览",
        new String[]{"areaName", "organ", "userName", "idCard", "policeTypeName", "personCount", "searchCount"},
        new String[]{"区域", "机构", "姓名", "身份证", "警种", "查询个数", "查询次数"}),

    /**
     * 访问对象分析详情
     */
    VISIT_OBJECT_DETAIL_INFO("访问对象分析详情",
        new String[]{"areaName", "organ", "userName", "idCard", "policeTypeName", "ip",
            "sysId", "objectValue", "operateCondition", "operateTime"},
        new String[]{"区域", "机构", "姓名", "身份证", "警种", "终端ip", "系统名称", "操作对象", "操作条件", "操作时间"}),

    /**
     * 信息泄露对象分析详情
     */
    INFORMATION_OBJECT_DETAIL("信息泄露对象分析详情",
        new String[]{"idCard", "userName", "sysId", "ip", "pliceType", "objectId",
            "operateCondition", "organization", "time", "operateTime"},
        new String[]{"身份证", "姓名", "系统名称", "设备ip", "警种", "对象", "操作条件", "机构", "操作时间"}),

    /**
     * 多人查询分析概览
     */
    MORE_PEOPLE_VISIT_SUMMARY_INFO("多人查询分析概览", new String[]{"idCard", "sysNum", "peopleNum"},
        new String[]{"操作对象", "查询系统数", "查询人数"}),

    /**
     * 多人查询分析详情
     */
    MORE_PEOPLE_VISIT_DETAIL_INFO("多人查询分析详情",
        new String[]{"areaName", "organ", "userName", "idCard", "ip", "sysId", "objectPeople", "operTime",
            "operation"},
        new String[]{"区域名称", "机构", "姓名", "身份证", "ip地址", "系统名称", "操作对象", "操作时间", "操作条件"}),

    /**
     * 协同办案分析概览
     */
    SUSPECT_VISIT_SUMMARY_INFO("协同办案分析概览",
        new String[]{"userName", "organ", "policeTypeName", "idCard", "sysId", "searchObject", "searchCount"},
        new String[]{"姓名", "单位", "警种", "身份证", "查询应用", "查询对象", "查询次数"}),

    /**
     * 协同办案分析详情
     */
    SUSPECT_VISIT_DETAIL_INFO(
        "协同办案分析详情", new String[]{"userName", "areaName", "organ", "idCard", "ip", "sysId", "objectValue", "operateCondition", "operateTime"},
        new String[]{"姓名", "区域名称", "单位", "身份证号", "IP", "查询应用", "操作对象", "操作条件", "操作时间"}),

    /**
     * 高频次访问分析概览
     */
    HIGH_FREQUENCY_VISIT_SUMMARY_INFO(
        "高频次访问分析概览", new String[]{"areaName", "idCard", "userName", "policeType", "content", "total"},
        new String[]{"区域名", "身份证", "姓名", "警种", "查询对象", "查询次数"}),

    /**
     * 高频次访问分析详情
     */
    HIGH_FREQUENCY_VISIT_DETAIL_INFO(
        "高频次访问分析详情", new String[]{"areaName", "idCard", "userName", "policeType", "ip", "content", "sysId", "operTime", "organ", "operation"},
        new String[]{"区域名", "身份证", "姓名", "警种", "IP", "查询内容（身份证）",
            "系统编号", "操作时间", "组织机构", "操作条件"}),

    /**
     * 应用访问分析区域统计
     */
    APP_VISIT_AREA_INFO("应用访问分析区域统计", new String[]{"areaName", "allCount"},
        new String[]{"区域名称", "查询次数"}),

    /**
     * 应用访问分析概览
     */
    APP_VISIT_SUMMARY_INFO("应用访问分析概览",
        new String[]{"areaName", "organ", "policeTypeName", "userName", "userId", "count"},
        new String[]{"区域", "机构", "警种", "姓名", "身份证", "查询次数"}),
    /**
     * 应用访问分析详情
     */
    APP_VISIT_DETAIL_INFO("应用访问分析详情",
        new String[]{"areaName", "userName", "userId", "sysId", "organ", "policeTypeName", "ip", "operateCondition", "operateTime"},
        new String[]{"区域", "姓名", "身份证", "系统名称", "机构", "警种", "ip地址", "操作条件", "操作时间"}),
    /**
     * 终端行为top排行
     */
    DEVICE_BEHAVIOR_COPY("终端行为复制粘贴",
        new String[]{"ip", "count"},
        new String[]{"设备IP", "次数"}),

    DEVICE_BEHAVIOR_PKI("终端行为Pki插拔",
        new String[]{"ip", "count"},
        new String[]{"设备IP", "次数"}),

    DEVICE_BEHAVIOR_SCREENSHOTS("终端行为截屏详情",
        new String[]{"ip", "count"},
        new String[]{"设备IP", "次数"}),

    DEVICE_BEHAVIOR_PKI_MOREKEY("终端行为一机多key",
        new String[]{"ip", "count"},
        new String[]{"设备IP", "PKI个数"}),

    DEVICE_BEHAVIOR_PKI_ONEKEY("终端行为一key多机",
        new String[]{"ip", "count"},
        new String[]{"身份证", "设备个数"}),

    DEVICE_BEHAVIOR_MOVEABLESTORAGE_COPYIN("终端行为拷入",
        new String[]{"ip", "count"},
        new String[]{"设备IP", "次数"}),

    DEVICE_BEHAVIOR_MOVEABLESTORAGE_COPYOUT("终端行为拷出",
        new String[]{"ip", "count"},
        new String[]{"设备IP", "次数"}),

    /**
     * 终端行为top分析详情
     */
    DEVICE_BEHAVIOR_COPY_DETAIL("终端行为top-复制粘贴详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "DataType", "EXE", "WinF"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "数据类型", "进程名(EXE)", "进程名(WinF)"}),
    DEVICE_BEHAVIOR_PKI_DETAIL("终端行为top-Pki插拔详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "UserName", "PID"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "使用人", "身份证"}),
    DEVICE_BEHAVIOR_SCREENSHOTS_DETAIL("终端行为top-截屏详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "DataType", "EXE", "WinF"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "数据类型", "进程名(EXE)", "进程名(WinF)"}),
    DEVICE_BEHAVIOR_PKI_ONEKEY_DETAIL("终端行为top_一key多机详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "UserName", "PID"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "使用人", "身份证"}),
    DEVICE_BEHAVIOR_PKI_MOREKEY_DETAIL("终端行为top_一机多key详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "UserName", "PID"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "使用人", "身份证"}),
    DEVICE_BEHAVIOR_MOVEABLESTORAGE_COPYIN_DETAIL("终端行为top_拷入详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "SrcP", "DstP", "FSize"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "拷贝文件的源路径", "拷贝文件的目标路径", "拷贝文件大小"}),
    DEVICE_BEHAVIOR_MOVEABLESTORAGE_COPYOUT_DETAIL("终端行为top_拷出详情",
        new String[]{"ni", "nm", "oi", "time", "Mode", "SrcP", "DstP", "FSize"},
        new String[]{"终端IP", "MAC地址", "区域", "时间", "类别", "拷贝文件的源路径", "拷贝文件的目标路径", "拷贝文件大小"}),

    /**
     * 区域top
     */
    DEVICE_BEHAVIOR_SYS_AREA_TOP("系统区域访问",
        new String[]{"regId", "oi", "ipcount", "visitcount"},
        new String[]{"系统", "区域", "IP访问个数", "访问量"}),

    /**
     * 数据监控-IP统计详情导出
     */
    DATA_MONITOR_TODAY_IP_COUNT_DETAIL("今日入库终端",
        new String[]{"ip", "todayTotal", "yesterdayTotal", "lastReportDate"},
        new String[]{"ip", "今日入库量", "昨日入库量", "最后入库日期"}),
    DATA_MONITOR_YESTERDAY_IP_COUNT_DETAIL("昨日入库终端",
        new String[]{"ip", "todayTotal", "yesterdayTotal", "lastReportDate"},
        new String[]{"ip", "今日入库量", "昨日入库量", "最后入库日期"}),
    DATA_MONITOR_YESTERDAY_UNREPORT_IP_COUNT_DETAIL("昨日未入库终端",
        new String[]{"ip", "todayTotal", "yesterdayTotal", "lastReportDate"},
        new String[]{"ip", "今日入库量", "昨日入库量", "最后入库日期"}),
    /**
     * 数据监控- 今日数据导出
     */
    DATA_MONITOR_TODAY_SYS_COUNT("今日应用数据入库",
        new String[]{"areaCode", "sysId", "todaySysCount", "yesterdaySysCount", "sysTotal", "sysLastDate"},
        new String[]{"区域", "应用", "今日入库量", "昨日入库量", "数据总量", "最后入库日期"}),
    DATA_MONITOR_TODAY_DEVICE_COUNT("今日设备数据入库",
        new String[]{"areaCode", "index", "msgSrcName", "todayTotal", "yesterdayTotal", "lastReportDate"},
        new String[]{"区域", "数据类型", "厂商", "今日入库量", "昨日入库量", "最后入库日期"}),
    DATA_MONITOR_TODAY_TERMINAL_COUNT("今日终端日志数据入库",
        new String[]{"areaCode", "index", "todayTotal", "yesterdayTotal", "todayIpCount", "yesterdayIpCount"},
        new String[]{"区域", "数据类型", "今日入库量", "昨日入库量", "今日入库终端数", "昨日入库终端数"}),
    /**
     * 关注对象分析概览
     */
    CONCERN_PEOPLE_SUMMARY("关注对象分析概览", new String[]{"content", "count"}, new String[]{"对象", "访问次数"}),
    /**
     * 关注对象分析详情
     */
    CONCERN_PEOPLE_DETAIL("关注对象分析详情",
        new String[]{"areaCode", "policeTypeName", "organ", "userName", "idCard", "ip", "sysId", "operateTime", "operateCondition"},
        new String[]{"区域", "警种", "机构", "姓名", "身份证", "终端IP", "系统名称", "操作时间", "操作内容"}
    ),

    /**
     * 被查询警员分析详情
     */
    CONCERN_POLICE_DETAIL("被查询警员分析详情",
        new String[]{"areaCode", "policeTypeName", "organ", "userName", "idCard", "ip", "sysId", "operateTime", "operateCondition"},
        new String[]{"区域", "警种", "机构", "姓名", "身份证", "终端IP", "系统名称", "操作时间", "操作内容"}
    ),
    /**
     * 关联人员导出
     */
    LABEL_PERSON("关联人员导出",
        new String[]{"objName", "objKey", "labelInfos"},
        new String[]{"姓名", "身份证", "标签"}),
    /**
     * 关联设备导出
     */
    LABEL_DEVICE("关联设备导出",
        new String[]{"objName", "objKey", "labelInfos"},
        new String[]{"MAC地址", "设备IP", "标签"}),
    /**
     * 关联应用导出
     */
    LABEL_SYS("关联应用导出",
        new String[]{"objName", "objKey", "labelInfos"},
        new String[]{"应用名称", "应用编号", "标签"}),

    /**
     * 人员信息
     */
    JD_PERSONINFO("人员信息",
        new String[]{"pCode", "name", "formerName", "sex", "nation", "idCard", "birthday", "position", "houseHold",
            "address", "joinAddress", "joinDate", "unit", "dnaStatus", "palmStatus", "voiceStatus", "fingerStatus",
            "photoStatus", "dnaUrl", "palmUrl", "voiceUrl", "fingerUrl", "photoUrl", "delStatus", "background", "createTime"},
        new String[]{"人员编码", "姓名", "曾用名", "性别0：男，1：女", "民族", "身份证", "出生日期", "人员类别",
            "户籍地", "家庭住址", "入伍地", "入伍时间", "所在单位", "dna录入状态0：未开始，1：不符合，2：已完成",
            "掌纹状态0：未开始，1：不符合，2：已完成", "声纹状态0：未开始，1：不符合，2：已完成",
            "指纹状态0：未开始，1：不符合，2：已完成", "人像状态0：未开始，1：不符合，2：已完成", "dna文件路径", "掌纹文件路径",
            "声纹文件路径", "指纹文件路径", "图像文件路径", "删除状态0：正常，1：已删除", "0：合格，1：不合格", "创建时间"}),

    /**
     * 情报信息
     */
    INDICATOR_INFO("情报信息",
        new String[]{"value", "sourceType", "description", "reportStartTime", "reportEndTime", "createdTime",
            "updateTime"},
        new String[]{"情报值", "数据来源", "描述", "情报开始时间", "情报结束时间", "创建时间", "最后更新时间"}),

    /**
     * 车牌号离散度详情
     */
    OLT_BEVUSUTED_CAR_AREA_DETAIL("车牌号离散度详情",
        new String[]{"areaName", "idCard", "userName", "ip", "provinceCode", "cityCode", "countArea", "sysId",
            "operTime", "organ", "operation", "policeType", "areaCode"},
        new String[]{"区域名称", "身份证", "姓名", "ip", "省级地区编码", "市级地区编码", "统计的地区编码", "系统名称",
            "操作时间", "组织机构", "操作条件", "警种", "区域编码"}
    ),
    /**
     * 车牌号离散度详情
     */
    OLT_BEVUSUTED_CAR_AREA("车牌号离散度",
        new String[]{"areaName", "idCard", "userName", "policeType", "nums", "total", "areaCode"},
        new String[]{"区域名称", "身份证", "姓名", "警种", "查询的地区个数", "查询次数", "区域编码"}
    ),
    /**
     * 警种访问应用系统排行详情
     */
    OLT_VISITEDSYSINFO_POLICE_DETAIL("警种访问应用系统排行详情",
        new String[]{"areaName", "idCard", "userName", "ip", "areaName", "areaCode", "sysId", "operTime", "organ", "operation", "policeType"},
        new String[]{"区域名称", "身份证", "姓名", "ip", "区域名称", "区域编码", "系统名称", "操作时间", "组织机构", "操作条件", "警种"}
    ),
    /**
     * 警种访问应用系统排行
     */
    OLT_VISITEDSYSINFO_POLICE1("按应用系统",
        new String[]{"sysId", "count"},
        new String[]{"系统名称", "访问次数"}
    ),
    OLT_VISITEDSYSINFO_POLICE2("按警种",
        new String[]{"policeType", "count"},
        new String[]{"警种", "访问次数"}
    ),
    /**
     * 跨区域访问应用
     */
    OLT_CROSSAREA_APP("跨区域访问应用",
        new String[]{"areaCode", "areaName", "idCard", "userName", "sysId", "total", "policeType"},
        new String[]{"区域编码", "区域名称", "身份证", "姓名", "系统名称", "访问次数", "警种"}
    ),
    /**
     * 跨区域访问应用
     */
    OLT_VISIT_COUNT_CONTRAST("上月应用对比",
        new String[]{"areaCode", "areaName", "idCard", "userName", "organ", "beforeCount", "afterCount", "diffCount"},
        new String[]{"区域编码", "区域名称", "身份证", "姓名", "组织机构", "前时间段查询次数", "本时间段查询次数", "次数差值"}
    ),
    /**
     * 重点关注 进程
     */
    IMPORTANT_PRO("重点关注",
        new String[]{"pid", "name", "path"},
        new String[]{"进程ID", "名称", "地址"}),
    /**
     * 重点关注 流量
     */
    IMPORTANT_NET("重点关注",
        new String[]{"pid", "name", "uploadspeed", "downloadspeed"},
        new String[]{"进程ID", "名称", "上传速度", "下载速度"}),
    /**
     * 重点关注 软件
     */
    IMPORTANT_SOFTWARE("重点关注",
        new String[]{"name", "company", "version", "insDate", "insPath"},
        new String[]{"软件名称", "公司", "版本号", "安装时间", "安装地址"}),
    /**
     * 个人异常时段访问统计
     */
    OLT_USER_ABNORMAL_TOP("个人异常时段访问统计",
        new String[]{"userId", "userName", "visitTotal", "variance"},
        new String[]{"身份证号", "姓名", "查询总次数", "查询次数方差"}
    ),

    /**
     * 知识库 进程库
     */
    REPOSITORY_PROCESS("进程库",
        new String[]{"md5", "areaCode", "areaName", "processName", "processDesc", "signName",
            "softwareName", "version", "classId", "className", "appClassId", "appClassName", "company", "type",
            "insertTime", "lastUpdateTime"},
        new String[]{"MD5值", "地区编码", "地区名称", "进程名", "进程描述", "签名", "软件名称", "版本", "进程分类编码", "进程分类描述", "业务分类编码",
            "业务分类描述", "公司名", "类别", "入库时间", "最后更新时间"}
    ),
    /**
     * 基础资源 软件进程维护
     */
    BASE_PROCESS("软件进程维护",
        new String[]{"name", "softwareName", "company", "signedName", "isDisplay", "isSuspicious", "state"},
        new String[]{"进程名称", "软件名称", "公司名称", "签名", "是否显示运行", "是否可疑", "维护状态"}),
    /**
     * 查询对象分析概览
     */
    HBSW_QUERY_OBJECT_SUMMARY("查询对象分析概览",
        new String[]{"areaCode", "ip", "userName", "organ", "personCount", "searchCount"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "查询对象个数", "查询次数"}),
    /**
     * 查询对象分析详情
     */
    HBSW_QUERY_OBJECT_DETAIL("查询对象分析详情",
        new String[]{"areaCode", "ip", "userName", "organ", "sysId", "objectValue", "operateCondition", "operateTime"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "系统名称", "被查对象", "操作条件", "时间"}),
    /**
     * 关注对象分析概览
     */
    HBSW_CONCERN_OBJECT_SUMMARY("关注对象分析概览",
        new String[]{"areaCode", "ip", "userName", "organ", "visitCount"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "访问次数"}),
    /**
     * 设备行为预警:注册不规范
     */
    DEVICE_UNNORM_VISIT("设备注册不规范预警",
        new String[]{"ip", "areaCode", "warnTime", "sysId", "total", "operateCondition"},
        new String[]{"设备IP", "设备区域", "预警时间", "系统", "访问次数", "操作条件"}),
    /**
     * 设备行为预警:未注册访问
     */
    DEVICE_UNREG_VISIT("设备未注册访问预警",
        new String[]{"ip", "areaCode", "organization", "warnTime", "operateCondition", "username", "idCard", "total"},
        new String[]{"设备IP", "设备区域", "单位", "预警时间", "运行条件", "用户名", "身份证号", "访问次数"}),
    /**
     * 应用访问分析概览
     */
    HBSW_APP_VISITED_SUMMARY("应用访问分析概览",
        new String[]{"areaCode", "ip", "userName", "organ", "count"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "访问次数"}),
    /**
     * 应用访问分析详情
     */
    HBSW_APP_VISITED_DETAIL("应用访问分析详情",
        new String[]{"areaCode", "ip", "userName", "organ", "sysId", "operateCondition", "operateTime"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "系统名称", "操作条件", "时间"}),
    /**
     * 上下班异常访问分析概览
     */
    HBSW_WORK_APP_VISIT_SUMMARY("上下班异常访问分析概览",
        new String[]{"areaCode", "ip", "userName", "organ", "sysDetail", "total", "warnType", "warnTime"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "各应用访问详情", "访问次数", "预警名称", "预警时间"}),
    /**
     * 上下班异常访问分析详情
     */
    HBSW_WORK_APP_VISIT_DETAIL("上下班异常访问分析详情",
        new String[]{"areaCode", "ip", "userName", "organ", "warnType", "sysId", "operateCondition", "operateTime", "warnTime"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属机构", "预警名称", "应用名称", "操作条件", "操作时间", "预警时间"}),
    /**
     * 单设备多账号登陆导出 概况
     */
    ONE_DEVICE_MANY_ACCOUNT("单设备多账号登陆",
        new String[]{"areaCode", "ip", "userName", "organ", "sysId", "personCount", "searchCount"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属单位", "登陆系统名称", "登陆账号个数", "登陆次数"}),
    /**
     * 单设备多账号登陆导出 概况
     */
    ONE_DEVICE_MANY_ACCOUNT_COUNT("单设备多账号登陆概况",
        new String[]{"areaCode", "ip", "userName", "organ", "sysId", "personCount", "searchCount"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属单位", "登陆系统名称", "登陆账号", "登陆次数"}),
    /**
     * 单设备多账号登陆导出 详情
     */
    ONE_DEVICE_MANY_ACCOUNT_DETAIL("单设备多账号登陆详情",
        new String[]{"areaCode", "ip", "userName", "organ", "sysId", "personCount", "searchCount"},
        new String[]{"设备区域", "设备IP", "设备责任人", "设备所属单位", "登陆系统名称", "登陆账号", "登陆时间"}),
    /**
     * 单账号多设备登陆导出
     */
    ONE_ACCOUNT_MANY_DEVICE("单账号多设备登陆",
        new String[]{"sysId", "account", "deviceCount", "areaCount", "searchCount"},
        new String[]{"登陆系统名称", "登陆账号", "登录设备个数", "登陆区域数", "登陆次数"}),
    /**
     * 单账号多设备登陆导出 概况
     */
    ONE_ACCOUNT_MANY_DEVICE_COUNT("单账号多设备登陆概况",
        new String[]{"sysId", "account", "areaCode", "ip", "userName", "organ", "searchCount"},
        new String[]{"登陆系统名称", "登陆账号", "登录设备区域", "登录设备IP", "设备责任人", "设备所属单位", "登陆次数"}),
    /**
     * 单账号多设备登陆导出 详情
     */
    ONE_ACCOUNT_MANY_DEVICE_DETAIL("单账号多设备登陆详情",
        new String[]{"sysId", "account", "areaCode", "ip", "userName", "organ", "time"},
        new String[]{"登陆系统名称", "登陆账号", "登录设备区域", "登录设备IP", "设备责任人", "设备所属单位", "登陆时间"}),
    /**
     * 单账号多区域登陆导出
     */
    ONE_ACCOUNT_MANY_AREA("单账号多区域登陆",
        new String[]{"sysId", "account", "deviceCount", "areaCount", "searchCount"},
        new String[]{"登陆系统名称", "登陆账号", "登录设备个数", "登陆区域数", "登陆次数"}),
    /**
     * 单账号多区域登陆导出 概况
     */
    ONE_ACCOUNT_MANY_AREA_COUNT("单账号多区域登陆概况",
        new String[]{"sysId", "account", "areaCode", "searchCount"},
        new String[]{"登陆系统名称", "登陆账号", "登录设备区域", "登陆次数"}),
    /**
     * 单账号多区域登陆导出 详情
     */
    ONE_ACCOUNT_MANY_AREA_DETAIL("单账号多区域登陆详情",
        new String[]{"sysId", "account", "areaCode", "ip", "userName", "organ", "time"},
        new String[]{"登陆系统名称", "登陆账号", "登录设备区域", "登录设备IP", "设备责任人", "设备所属单位", "登陆时间"}),
    /**
     * 密码复杂度概览 概况
     */
    HBSW_USER_COMPLEXITY_SUMMARY("概览",
        new String[]{"appName", "totalCount", "rate"},
        new String[]{"系统名称", "弱密码数/总数", "弱密码占比"}),
    /**
     * 密码复杂度弱密码导出 详情
     */
    HBSW_USER_COMPLEXITY_DETAIL("弱密码列表",
        new String[]{"appId", "account", "passwordLength", "containUpperCase", "containLowerCase",
            "containNumber", "containSpecialChar", "ip", "areaCode", "username", "organization", "lastUpdateTime"},
        new String[]{"系统名称", "账号", "密码长度", "密码含大写字母", "密码含小写字母", "密码含数字", "密码含特殊字符",
            "登录IP", "登录设备区域", "设备责任人", "设备所属单位", "登陆时间"}),

    /**
     * 密码变更概览 概况
     */
    HBSW_USER_UNCHANGE_SUMMARY("概览",
        new String[]{"appName", "totalCount", "rate"},
        new String[]{"系统名称", "未变更密码数/总数", "未变更占比"}),
    /**
     * 密码周期内未变更账号信息导出 详情
     */
    HBSW_USER_UNCHANGE_DETAIL("未变更账号",
        new String[]{"appId", "account", "lastUpdateTime", "ip", "areaCode", "username", "organization"},
        new String[]{"系统名称", "账号", "最近登陆时间", "登录设备IP", "登录设备区域", "设备责任人", "设备所属单位"}),
    /**
     * 被查询警员分析概览
     */
    CONCERN_POLICE_SUMMARY("被查询警员分析概览", new String[]{"content", "policeType", "count"},
        new String[]{"对象", "警种", "访问次数"}),

    /**
     * URL链接备案
     */
    URL("URL链接备案",
        new String[]{"url", "sid", "areaCode", "orgName", "serverIp", "isAlive", "state"},
        new String[]{"URL", "网站/应用名称", "区域", "负责单位", "服务器IP", "URL有效性", "状态"}),
    /**
     * URL链接备案
     */
    URL_DEVICE("URL链接备案-关联设备",
        new String[]{"url", "sid", "areaCode", "orgName", "belong", "tel", "serverIp", "isAlive", "state", "protect"},
        new String[]{"URL", "应用名称", "区域", "负责单位", "所属人", "联系电话", "服务器IP", "URL有效性", "备案状态", "保护状态"}),
    /**
     * 新疆企业人员导出
     */
    XJ_PERSON("人员",
        new String[]{"name", "idCard", "sex", "position", "politicsStatus", "nation", "tel", "resident", "companyId", "projectId", "operatorOrg"},
        new String[]{"姓名", "身份证号", "性别", "职位", "政治面貌", "民族", "联系方式", "是否驻场", "公司名称", "项目名称", "机构"}),
    /**
     * 新疆项目导出
     */
    XJ_PROJECT("项目",
        new String[]{"contractName", "acceptance", "responsiblePoliceName", "responsiblePoliceTel", "capitalPayed", "sysNetType1",
            "residentNum", "companyId", "operator", "operatorOrg"},
        new String[]{"项目名称", "项目状态", "责任民警", "责任民警电话", "合同金额", "部署网络", "驻场人数", "公司名称", "填写人员", "机构"}
    ),
    /**
     * 新疆项目产品公司信息及转包公司信息导出
     */
    XJ_PROJECT_PROD_COM("三方公司",
        new String[]{"name", "orgCode", "address", "companyId", "tel", "securityQualification", "projectPersonNum",
            "operator", "remark"},
        new String[]{"企业名称", "公司注册码", "通信地址", "上级合作企业", "联系人及电话", "资质情况", "项目参与人数",
            "填写人员", "机构"}
    ),
    /**
     * 新疆企业导出
     */
    XJ_ENTERPRISE("企业",
        new String[]{"name", "orgCode", "tel", "contacts", "securityQualification", "hasSecurityQualification",
            "projectNum", "personNum", "operator", "operatorOrg"},
        new String[]{"企业名称", "注册号", "企业联系电话", "联系人", "资质列表", "是否有涉密资质", "项目数", "人员数", "填写人员", "机构"}),

    /**
     * 新疆企业导入
     */
    XJ_ENTERPRISE_I("企业",
        new String[]{"name", "orgCode", "contacts", "tel", "address", "foundTime", "registeredFund",
            "securityQualification", "signedSecurity", "cooperationDuration", "inSecurityProject",
            "hasSecurityQualification", "badnessRecord", "badnessRecordDesc", "coCase", "coPolice", "coType", "description", "remark"},
        new String[]{"企业名称", "注册号", "联系人", "联系电话", "通信地址", "成立时间", "注册资金", "资质列表", "是否签订保密协议", "与公安机关累计合作年限",
            "是否参与涉密项目", "目前是否具有涉密资质", "三年内存在不良记录情况", "不良记录描述", "合作事项", "合作警种", "合作类型", "简介", "备注"},
        new int[]{60, 60, 20, 100, 180, 20, 18, 500, 8, 10, 8, 8, 10, 180, 100, 200, 200, 800, 500}
    ),
    /**
     * 新疆项目导入
     */
    XJ_PROJECT_I("项目",
        new String[]{"contractName", "signDate", "capital", "completeDate", "acceptance", "maintenanceTerm", "residentNum",
            "responsibleOrg", "responsiblePoliceName", "responsiblePoliceTel", "responsiblePoliceCode", "responsiblePoliceIdcard",
            "projectType", "taskContent", "projectProcess", "existThirdPro", "existSubCom", "existSubType",
            "sysName1", "sysUrl1", "sysNetType1",
            "sysName2", "sysUrl2", "sysNetType2",
            "sysName3", "sysUrl3", "sysNetType3",
            "sysName4", "sysUrl4", "sysNetType4",
            "sysName5", "sysUrl5", "sysNetType5",
            "remark"},
        new String[]{"合同名称", "签订日期", "合同资金", "竣工日期", "验收情况", "维保期限", "驻场人数", "公安责任单位", "项目责任民警", "责任民警电话",
            "责任民警警号", "责任民警身份证号", "项目类型", "任务内容", "项目进展", "是否有第三方产品", "是否存在项目外包", "转包类型",
            "应用(1)名称", "应用(1)URL", "应用(1)部署网络",
            "应用(2)名称", "应用(2)URL", "应用(2)部署网络",
            "应用(3)名称", "应用(3)URL", "应用(3)部署网络",
            "应用(4)名称", "应用(4)URL", "应用(4)部署网络",
            "应用(5)名称", "应用(5)URL", "应用(5)部署网络",
            "备注"},
        new int[]{50, 10, 10, 10, 80, 20, 11, 80, 20, 18, 50, 36, 50, 500, 180, 8, 8, 18, 100, 200, 100,
            100, 200, 100, 100, 200, 100, 100, 200, 100, 100, 200, 100, 500}
    ),
    /**
     * 新疆项目产品公司信息及转包公司信息导入
     */
    XJ_PROJECT_PROD_COM_I("项目产品公司或转包公司",
        new String[]{"name", "companyType", "orgCode", "address", "email", "projectPersonNum", "tel",
            "signedSecurity", "maintenanceTerm", "securityQualification", "description", "remark"},
        new String[]{"企业名称", "公司类型", "注册号", "通信地址", "电子邮箱", "项目参与人数", "联系人及电话",
            "是否签订保密协议", "维保期限", "资质情况", "简介", "备注"},
        new int[]{80, 10, 80, 180, 80, 11, 80, 10, 20, 500, 800, 500}
    ),
    /**
     * 新疆项目服务器信息导入
     */
    XJ_PROJECT_SERVER_I("项目服务器信息",
        new String[]{"serverType", "category", "networkEnv", "ip", "mac", "sn", "system",
            "position", "maintainPolice", "maintainTechnicist", "hasBastion", "mainFeature"},
        new String[]{"服务器类型", "型号", "网络环境", "ip(网段)", "mac", "序列号", "服务器系统",
            "服务器具体位置", "运维民警", "运维技术人员", "是否接入堡垒机", "主要功能"},
        new int[]{80, 50, 50, 100, 32, 80, 50, 80, 20, 20, 10, 180}
    ),
    /**
     * 新疆项目服务器信息导入
     */
    XJ_PROJECT_DB_I("项目数据库信息",
        new String[]{"dbName", "dbType", "dbStorageType", "dbContent", "dbAccessType", "dbBackupType", "maintainPolice",
            "maintainTechnicist", "responsibleOrg", "responsibleCharge", "useOrg", "useOrgCharge",
            "buildOrg", "buildOrgCharge", "dbOperateRecord", "inAudit"},
        new String[]{"数据库名称", "数据库类型", "数据库存储类型", "数据库内容", "数据库对接方式", "数据库备份方式", "运维民警",
            "运维技术人员", "责任单位", "主要负责人", "使用单位", "使用单位负责人", "承建单位", "承建单位负责人", "数据操作日志是否留存及期限", "是否介入数据库审计设备"},
        new int[]{50, 30, 30, 50, 80, 50, 32, 32, 50, 50, 50, 50, 50, 50, 50, 10}
    ),
    /**
     * 新疆项目边界信息表导入
     */
    XJ_PROJECT_PLAT_CHANNEL_I("项目边界信息表",
        new String[]{"channelName", "businessName", "businessType", "dataFlowTo", "ip", "description"},
        new String[]{"涉及边界通道名称", "业务名称", "业务类型", "数据网络流向", "前置机IP/端口", "业务描述"},
        new int[]{80, 80, 50, 80, 80, 200}
    ),
    /**
     * 新疆项目人员基础信息导入
     */
    XJ_PROJECT_PERSON_I("人员基础信息",
        new String[]{"name", "idCard", "sex", "position", "graduatedCollege", "profession", "education",
            "politicsStatus", "nation", "tel", "register", "residentialAddress", "driverNum", "companyType",
            "email", "qq", "weixin", "dingding", "shortVideo", "otherChat", "resident",
            "workSite", "workTime", "securityTrain", "illegalCase", "signedSecurity", "duty", "servicePolice",
            "workDomain", "workNature", "workType"},
        new String[]{"姓名", "身份证", "性别", "职位", "毕业院校", "专业", "最高学历",
            "政治面貌", "民族", "联系方式", "户籍", "居住地址", "驾照备案号码", "厂家类型",
            "电子邮箱", "qq", "微信", "钉钉", "短视频", "其他工具", "是否驻场",
            "工作地点", "工作时间", "是否经过保密安全教育培训", "近三年违法犯罪情况", "是否签订保密协议", "工作内容", "服务警种",
            "工作领域", "工作性质", "工作类型"},
        new int[]{30, 20, 8, 20, 50, 50, 20, 10, 20, 30, 80, 80, 20, 50, 80, 20, 30, 30, 30, 50, 10, 80, 20, 10, 80, 10, 50, 50, 80, 50, 50}
    ),
    /**
     * 新疆项目人员车辆信息导入
     */
    XJ_PROJECT_PERSON_CAR_I("人员车辆信息",
        new String[]{"name", "idCard", "carBrand", "carNum"},
        new String[]{"姓名", "身份证", "汽车品牌", "车牌号"},
        new int[]{30, 20, 50, 80}
    ),
    /**
     * 新疆项目人员证件信息导入
     */
    XJ_PROJECT_PERSON_CARD_I("人员证件信息（选填）",
        new String[]{"name", "idCard", "identification", "expiryDate", "alreadyTake"},
        new String[]{"姓名", "身份证", "证件名称", "有效期", "是否办理"},
        new int[]{30, 20, 20, 20, 10}
    ),
    /**
     * 新疆项目人员出入境信息导入
     */
    XJ_PROJECT_PERSON_ENTRYEXIT_I("人员出入境信息（选填）",
        new String[]{"name", "idCard", "outTime", "inTime", "record", "reason"},
        new String[]{"姓名", "身份证", "出境时间", "入境时间", "目的城市", "出入境原因"},
        new int[]{30, 20, 50, 50, 80, 80}
    ),
    /**
     * 新疆项目人员工作履历信息导入
     */
    XJ_PROJECT_PERSON_WORKHISTORY_I("人员工作履历信息",
        new String[]{"name", "idCard", "title", "name", "position", "signedWorkerContract", "workerContractPeriod", "remark"},
        new String[]{"姓名", "身份证", "就职公司顺序", "企业名称", "岗位名称", "是否签订劳动合同", "劳动合同起止时间", "备注"},
        new int[]{30, 20, 30, 30, 30, 11, 80, 50}
    ),
    /**
     * 新疆项目人员操作终端信息导入
     */
    XJ_PROJECT_PERSON_PC_I("人员操作终端信息",
        new String[]{"name", "idCard", "ip", "mac", "type", "category", "sn",
            "networkEnv", "multiNic", "position", "mainFeature"},
        new String[]{"姓名", "身份证", "ip(网段)", "mac", "终端类型", "型号", "序列号",
            "网络环境", "是否多网卡", "终端具体位置", "主要功能"},
        new int[]{30, 20, 30, 30, 20, 30, 50, 50, 10, 80, 200}
    ),
    /**
     * 新疆项目人员维护服务器信息导入
     */
    XJ_PROJECT_PERSON_MAINTAINER_I("人员维护服务器信息",
        new String[]{"name", "idCard", "serverType", "networkEnv", "multiNic", "system", "hasBastion",
            "position", "mainFeature", "ip", "mac", "category", "sn", "maintainDate"},
        new String[]{"姓名", "身份证", "服务器类型", "网络环境", "是否多网卡", "服务器系统", "是否接入堡垒机运维",
            "服务器具体位置", "主要功能", "ip(网段)", "mac", "型号", "序列号", "维护日期"},
        new int[]{30, 20, 50, 50, 10, 50, 10, 80, 200, 80, 30, 30, 50, 20}
    ),


    /**
     * 数据源信息
     */
    DATA_SOURCE_MANAGER("数据源信息",
        new String[]{"sourceName", "ip", "port", "protocol", "description", "createTime", "lastUpdateTime"},
        new String[]{"数据源名称", "设备地址", "端口", "采集协议", "描述", "创建时间", "最后修改时间"}),
    /**
     * 数据源信息2
     */
    DATA_SOURCE_MANAGER2("数据源信息",
        new String[]{"devid", "company", "number", "name", "status", "insertTime",},
        new String[]{"数据源唯一标识", "厂家", "产品类型", "产品名称", "状态", "创建时间"}),

    QUERY_OBJECT_SUMMARY("挖掘分析报表概览",
        new String[]{"areaName", "userName", "idCard", "org", "total", "regName", "regTotal", "firstTime", "lastTime"},
        new String[]{"区域", "姓名", "身份证号码", "单位", "查询总量", "应用系统", "系统查询次数", "第一次查询时间", "最后一次查询时间"}),
    QUERY_OBJECT_DETAIL("挖掘分析报表详情",
        new String[]{"areaName", "userName", "idCard", "org", "sysId", "operTime", "ip", "operateName", "operation"},
        new String[]{"区域", "姓名", "身份证号码", "单位", "应用系统", "查询时间", "终端IP", "查询对象", "详细条件"}),
    /**
     * 新疆-应用日志导出
     */
    AUDIT_LOG_DETAIL("应用日志",
        new String[]{"organization", "username", "user_id", "terminal_id", "app_id", "operate_type", "event_time", "operate_condition"},
        new String[]{"单位", "用户名", "身份证号", "终端IP", "系统名称", "操作类型", "操作时间", "操作条件"}),

    ORGAN_APP_QUERY("单位人员应用查询统计",
        new String[]{"userName", "userId", "orgCode", "orgName", "sysNum", "peopleNum", "carNum", "visitCount", "cronTime"},
        new String[]{"姓名", "身份证号", "单位编号", "单位", "查询系统个数", "查询人员个数", "查询车牌个数", "查询次数", "任务周期"}),
    /**
     * 新疆-病毒导出
     */
    VIRUS_LOG_DETAIL("病毒详情",
        new String[]{"event_time", "msg_src", "virus_type", "virus_name", "virus_act", "threat_file_path", "threat_file_md5", "threat_file_name",
            "threat_file_type", "event_level", "handle_result", "handle_method"},
        new String[]{"发现时间", "厂商名称", "病毒类型", "病毒名称", "病毒行为", "文件路径", "文件md5", "文件名称", "文件类型", "威胁等级", "处理结果", "处理方式"}),
    UKN_ASSET("未知资产",
        new String[]{"ip", "areaCode", "url"},
        new String[]{"IP", "区域", "URL"}
    ),
    UKN_ASSET2("新发现资产",
        new String[]{"ip", "areaCode", "url", "assetType"},
        new String[]{"IP", "区域", "URL", "状态"}
    ),
    VUL_INFO("漏洞详情",
        new String[]{"vulName", "areaCode", "vulLevel", "ip"},
        new String[]{"漏洞名称", "区域", "漏洞等级", "IP"}
    ),
    BE_VISITED_IMPORT("被访问人导入信息", new String[]{"idCard", "userName"},
        new String[]{"身份证号", "姓名"}),
    /**
     * 反诈骗分析
     */
    BEVISITED_PEOPLE_IMPORT("反诈骗分析", new String[]{"areaName", "userName", "idCard", "policeType", "nums", "peopleNum", "total"},
        new String[]{"区域名称", "姓名", "身份证", "警种", "查询的地区个数", "查询人数", "查询次数"}),

    /**
     * 反诈骗分析详情
     */
    BEVISITED_PEOPLE_DETAIL_IMPORT("反诈骗分析详情",
        new String[]{"areaName", "userName", "idCard", "ip", "sysId", "operTime", "organ", "operation",
            "policeType"},
        new String[]{"区域名称", "姓名", "身份证", "ip地址", "系统名称", "操作时间", "机构", "操作条件", "警种"}),


    PRINT_DETAIL("打印详情",
        new String[]{"file_level", "data_source", "username", "dev_ip", "dev_name", "op_type",
                "op_result", "file_name", "business_list", "md5", "file_size", "file_type", "event_time",
                "op_hour", "std_user_no", "std_user_type", "std_org_name", "std_dev_type_group", "std_dev_type", "std_dev_safety_marign", "std_terminal_type"},
        new String[]{"文件密级", "数据来源", "用户", "设备IP", "设备名称", "操作类型",
                "操作结果", "文件名称", "业务类别", "MD5", "文件大小", "文件类别", "发生时间",
                "时段", "责任人编号", "用户类型", "组织机构", "设备一级类型", "设备二级类型", "安全域", "设备标识"}),

    LOGIN_DETAIL("登录次数详情",
            new String[]{"num", "username", "login_time"},
            new String[]{"序号", "用户名", "登录时间"}),
    DEDICATED_MEDIA_DETAIL("专用介质使用详情",
        new String[]{"num", "std_dyperiph_vid", "std_dyperiph_pid", "std_dyperiph_sn", "u_level", "media_user", "event_time"},
        new String[]{"序号", "vid", "pid", "sn", "密级", "责任人", "接入时间"}),

    DEV_PRINT_DETAIL("打印详情",
            new String[]{"num", "event_time", "file_name", "md5", "file_type", "file_size",
                    "file_level", "file_num", "file_page", "dev_name", "", "username", "", "op_result"},
            new String[]{"序号", "打印时间", "文件名", "文件MD5", "文件类型", "文件大小", "文件密级", "打印份数", "打印页数",
                    "打印机名称", "打印机类型", "打印用户", "打印任务编号", "打印结果"}),
    DEV_BURN_DETAIL("刻录详情",
            new String[]{"num", "event_time", "file_name", "md5", "file_type", "file_size", "file_level", "file_num",
                    "dev_name", "", "username", "op_result"},
            new String[]{"序号", "刻录时间", "文件名", "文件MD5", "文件类型", "文件大小", "文件密级", "刻录文件数量",
                    "刻录机名称", "刻录机类型", "刻录用户", "刻录结果"}),
    DEV_VISIT_DETAIL("网络通联详情",
        new String[]{"num","event_time", "dst_std_dev_type_group", "dip", "dport", "sport", "app_protocol", "total_byte"},
        new String[]{"序号","通联时间", "通联对象类型", "通联对象IP", "通联目的端口", "通联源端口", "通联协议", "流量大小"}),
    SYS_VISIT_DETAIL("应用访问详情",
            new String[]{"num", "event_time", "dst_std_sys_name", "dip", "operation_url", "app_url", "username", "", "url"},
            new String[]{"序号","访问时间", "访问系统名称", "所属服务器ip","管理入口URL","业务入口URL", "访问账户","访问时长","应用URL"}),
    BUSINESS_VISIT_DETAIL("业务访问详情",
            new String[]{"num", "event_time", "", "url", "sip", "src_std_dev_type_group", ""},
            new String[]{"序号", "访问时间", "访问账户", "URL", "源ip", "源对象类型", "访问操作指令"}),
    FILE_TRANS_DETAIL("文件传输详情",
          new String[]{"num", "event_time", "file_dir", "dst_std_dev_type_group", "dip", "file_name", "file_md5",
                    "classification_level", "file_type", "file_size"},
          new String[]{"序号","传输时间", "传输方向", "传输对象类型", "传输对象IP","文件名", "文件MD5", "文件密级", "文件类型","文件大小"}),
    OPERATION_DETAIL("运维详情",
            new String[]{"num","event_time", "client_ip","std_dev_type_group","user_account", "conn_type", "conn_port", "opt_detail"},
            new String[]{"序号","运维时间", "运维源对象IP","运维源对象类型","运维账号", "运维协议", "运维端口", "运维指令"}),
    OPERATION_DETAIL2("运维详情",
            new String[]{"num","event_time", "resource_ip","resource_type_group","user_account", "conn_type", "conn_port", "opt_detail"},
            new String[]{"序号","运维时间", "运维对象IP","运维对象类型","运维账号", "运维协议", "运维端口", "运维指令"}),
    NETWORK_DETAIL("运维详情",
            new String[]{"num","user_account", "resource_ip","source_device_type","event_time", "conn_type", "operation_port", "operation_record"},
            new String[]{"序号","账户名", "源ip","源ip所属设备类型", "登录时间", "运维协议", "运维端口", "操作指令"}),
    INTERCONN_NET_DETAIL("互联边界流量情况",
            new String[]{"sip", "dip","total_byte","total_pkt"},
            new String[]{"源设备ip","目的设备ip", "总字节数统计", "总包数统计"}),
    APP_NETWORK_DETAIL("运维管理列表详情",
            new String[]{"event_time", "logout_time","app_name","user_account", "resource_ip", "operation_port", "operation_record"},
            new String[]{"登录时间", "退出时间","应用服务名称", "账户名", "源ip", "源ip所属设备类型", "运维URL列表", "运维协议", "运维端口", "运维操作指令"}),
    APP_FILE_DETAIL("文件传输列表详情",
            new String[]{"event_time", "file_name","file_hash","classification_level", "file_type", "file_size", "file_dir", "dst_std_dev_ip"},
            new String[]{"传输时间", "文件名","文件hash", "文件密级", "文件类型", "文件大小", "传输方向", "传输对象"}),
    INTETIVE_DETAIL("交互列表详情",
            new String[]{"event_time", "dip","dmac","dport", "dst_std_dev_type", "dst_std_username", "sport", "app_protocol", "username", "total_byte"},
            new String[]{"访问时间", "目的端ip","目的端MAC", "目的端口", "目的端设备类型", "目的端责任人", "源端口", "协议名称", "登录用户名", "流量大小"}),
    SECU_DETAIL("安全服务列表",
            new String[]{"num","event_time", "dip","dst_std_dev_type_group", "app_protocol", "sport", "dport"},
            new String[]{"序号","服务时间", "客户端ip","客户端类型", "服务协议", "源端口", "目的端口"}),

    NET_WORK_BOUNDARY_DETAIL("边界流量详情",
            new String[]{"num", "event_time", "sip", "dip", "sport", "dport", "app_protocol", "client_total_byte", "server_total_byte", "client_total_pkt", "server_total_pkt"},
            new String[]{"序号", "访问时间", "源ip", "目的ip", "源端口", "目的端口", "协议", "流入数据总字节数（MB）", "流出数据总字节数（MB）", "流入总包数（个）","流出总包数（个）"}),
    ;

    /**
     * 文件名
     */
    private String filename;

    /**
     * sheet页名称
     */
    private String sheet;

    /**
     * 英文字段名
     */
    private String[] fields;

    /**
     * 中文字段名
     */
    private String[] fieldsCn;

    /**
     * 导入时校验最大长度
     */
    private int[] maxlength;

    ExcelEnum(String filename, String[] fields, String[] fieldsCn) {
        this(filename, filename, fields, fieldsCn);
    }

    ExcelEnum(String filename, String[] fields, String[] fieldsCn, int[] maxlength) {
        this.filename = filename;
        this.sheet = filename;
        this.fields = fields;
        this.fieldsCn = fieldsCn;
        this.maxlength = maxlength;
    }

    ExcelEnum(String filename, String sheet, String[] fields, String[] fieldsCn) {
        this.filename = filename;
        this.sheet = sheet;
        this.fields = fields;
        this.fieldsCn = fieldsCn;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String[] getFieldsCn() {
        return fieldsCn;
    }

    public void setFieldsCn(String[] fieldsCn) {
        this.fieldsCn = fieldsCn;
    }

    public int[] getMaxlength() {
        return maxlength;
    }
}
