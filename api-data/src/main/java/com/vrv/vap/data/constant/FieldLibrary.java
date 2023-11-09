package com.vrv.vap.data.constant;

import java.util.*;

public class FieldLibrary {

    /**
     * 说明：内置一些常用的字段字典
     * 常用转换规则,
     */
    private static final Map<String, String> FILED_LABEL = new HashMap();

    // 日期类型
    private static Set<String> DATE_SET = new HashSet<String>(Arrays.asList(new String[]{"date", "time", "year", "datetime", "timestamp"}));
    // 长整型 "TINYINT", 布尔型 "BIT" 认为是 KEYWORD
    private static Set<String> LONG_SET = new HashSet<String>(Arrays.asList(new String[]{"smallint", "mediumint", "int", "integer", "bigint"}));
    // 浮点型
    private static Set<String> DOUBLE_SET = new HashSet<String>(Arrays.asList(new String[]{"float", "double", "decimal"}));
    // 长文本
    private static Set<String> TEXT_SET = new HashSet<String>(Arrays.asList(new String[]{"text", "tinytext", "mediumtext", "longtext"}));
    // 关键字
    private static Set<String> KEYWORD_SET = new HashSet<String>(Arrays.asList(new String[]{"varchar", "bit", "tinyint", "char", "linestring"}));

    static {
        //常用字段库 默认转换一些常用字段为标题
        FILED_LABEL.put("name", "名称");
        FILED_LABEL.put("title", "标题");
        FILED_LABEL.put("ip", "IP地址");
        FILED_LABEL.put("time", "时间");
        FILED_LABEL.put("start_time", "开始时间");
        FILED_LABEL.put("end_time", "结束时间");
        FILED_LABEL.put("indate", "入库时间");
        FILED_LABEL.put("log_type", "日志类型");


        FILED_LABEL.put("url", "URL");
        FILED_LABEL.put("user_id", "身份证号");
        FILED_LABEL.put("username", "姓名");
        FILED_LABEL.put("@timestamp", "时间");
        FILED_LABEL.put("app_id", "应用");
        FILED_LABEL.put("area_code", "地区");

        FILED_LABEL.put("src_area", "源区域");
        FILED_LABEL.put("src_ip", "源IP");
        FILED_LABEL.put("dst_ip", "目标IP");
        FILED_LABEL.put("dst_area", "目标区域");
        FILED_LABEL.put("src_port", "源端口");
        FILED_LABEL.put("dst_port", "目标端口");
        FILED_LABEL.put("threat_type", "威胁类型");
        FILED_LABEL.put("threat_name", "威胁名称");
        FILED_LABEL.put("threat_level", "威胁等级");
        FILED_LABEL.put("http_method", "HTTP请求方法");

        FILED_LABEL.put("terminal_id", "终端IP");
        FILED_LABEL.put("terminal_type", "终端类型");
        FILED_LABEL.put("DataType", "数据类型");
        FILED_LABEL.put("host", "主机IP");

        FILED_LABEL.put("operate_name", "操作名称");
        FILED_LABEL.put("operate_type", "操作类型");
        FILED_LABEL.put("operate_condition", "操作条件");
        FILED_LABEL.put("operate_result", "操作结果");
        FILED_LABEL.put("operattion_time", "操作时间");
        FILED_LABEL.put("organization", "机构名称");
        FILED_LABEL.put("organization_id", "机构名称");


        FILED_LABEL.put("transport_protocol", "协议");
        FILED_LABEL.put("protocol_type", "协议");
        FILED_LABEL.put("event_time", "发生时间");
        FILED_LABEL.put("safety_margin", "安全域");
        FILED_LABEL.put("security_level", "安全等级");

        FILED_LABEL.put("safety_margin_ip", "安全域IP");
        FILED_LABEL.put("report_ip", "上报IP");
        FILED_LABEL.put("report_ip_num", "上报IP个数");
        FILED_LABEL.put("report_msg", "上报消息");
        FILED_LABEL.put("msg_src", "消息来源");

        FILED_LABEL.put("all_pkg", "总包数");
        FILED_LABEL.put("upload_pkg", "上传包数");
        FILED_LABEL.put("download_pkg", "下载包数");
        FILED_LABEL.put("all_bytes", "总流量");
        FILED_LABEL.put("upload_bytes", "上传流量");
        FILED_LABEL.put("download_bytes", "下载流量");

    }

    public static String getFieldName(String field) {
        if (FILED_LABEL.containsKey(field)) {
            return FILED_LABEL.get(field);
        }
        return "";
    }

    public static String toElasticType(String type) {
        if (KEYWORD_SET.contains(type)) {
            return "keyword";
        }
        if (DATE_SET.contains(type)) {
            return "date";
        }
        if (LONG_SET.contains(type)) {
            return "long";
        }
        if (DOUBLE_SET.contains(type)) {
            return "double";
        }
        if (TEXT_SET.contains(type)) {
            return "text";
        }
        return "keyword";
    }

}
