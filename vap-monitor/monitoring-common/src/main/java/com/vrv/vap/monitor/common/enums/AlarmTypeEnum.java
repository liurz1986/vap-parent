package com.vrv.vap.monitor.common.enums;

public enum AlarmTypeEnum {
    ALARM_CPU("501","操作系统告警：cpu负荷超过%s"),
    ALARM_MEMORY("501","操作系统告警：内存占用超过%s"),
    ALARM_DISK("501","操作系统告警：磁盘占用超过%s"),
    ALARM_DATABASE_READ("502","数据库监控告警：数据库查询异常"),
    ALARM_DATABASE_WRITE("502","数据库监控告警：数据库写入异常"),
    ALARM_DATABASE_LINK("502","数据库监控告警：数据库连接异常"),
    ALARM_DATABASE_DEAL("502","数据库监控告警尝试自动处理"),
    ALARM_DATABASE_RESULT("502","数据库监控告警尝试自动处理%s"),
    ALARM_KAFKA_PRODUCE("503","中间件告警：kafka生产异常"),
    ALARM_KAFKA_CONSUME("503","中间件告警：kafka消费异常"),
    ALARM_KAFKA_LINK("503","中间件告警：kafka连接异常"),
    ALARM_KAFKA_DEAL("503","中间件kafka告警尝试处理"),
    ALARM_KAFKA_RESULT("503","中间件kafka告警尝试自动处理%s"),
    ALARM_NETFLOW("504","接收模块告警：HTTP处理模块异常"),
    ALARM_FLUME("504","接收模块告警：%s采集任务异常"),
    ALARM_FLUME_DEAL("504","接收模块告警：%s采集任务异常尝试自动处理"),
    ALARM_FLUME_RESULT("504","接收模块告警：%s采集任务异常尝试自动处理%s"),
    ALARM_FLINK_SERVER("505","分析模块告警：flink告警"),
    ALARM_DEAL("505","分析模块告警：实时处理模块"),
    ALARM_AUDIT("505","分析模块告警：基线及离线处理模块"),
    ALARM_WEB_ZUUL("506","管理平台告警：网关异常"),
    ALARM_WEB_ZUUL_DEAL("506","管理平台告警：网关异常尝试自动处理"),
    ALARM_WEB_ZUUL_RESULT("506","管理平台告警：%s网关异常尝试自动处理%s"),
    ALARM_WEB_NACOS("506","管理平台告警：注册中心异常"),
    ALARM_WEB_NACOS_DEAL("506","管理平台告警：注册中心异常尝试自动处理"),
    ALARM_WEB_NACOS_RESULT("506","管理平台告警：注册中心异常尝试自动处理%s"),
    ALARM_WEB_ADMIN("506","管理平台告警：%s微服务异常"),
    ALARM_WEB_ADMIN_DEAL("506","管理平台告警：%s微服务异常尝试自动处理"),
    ALARM_WEB_ADMIN_RESULT("506","管理平台告警：%s微服务异常自动处理%s"),
    ALARM_MONITOR("507","流量采集器告警：设备ID：%s的监测器不在线"),
    ALARM_TRY_DEAL("508","HTTP处理模块异常尝试处理"),
    ALARM_DEAL_RESULT("508","HTTP处理模块异常尝试处理%s"),
    ALARM_REDIS_READ("509","redis监控告警：redis查询异常"),
    ALARM_REDIS_WRITE("509","redis监控告警：redis写入异常"),
    ALARM_REDIS_DEAL("509","redis异常尝试处理"),
    ALARM_REDIS_RESULT("509","redis异常尝试自动处理%s"),
    ALARM_REDIS_LINK("509","redis监控告警：redis连接异常"),
    ALARM_ES_READ("510","ES监控告警：ES查询异常"),
    ALARM_ES_WRITE("510","ES监控告警：ES写入异常"),
    ALARM_ES_DEAL("510","ES异常尝试处理"),
    ALARM_ES_RESULT("510","ES异常尝试自动处理%s"),
    ALARM_ES_LINK("510","Es监控告警：ES连接异常");
    private String code;
    private String desc;

    AlarmTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
