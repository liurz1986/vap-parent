package com.vrv.vap.xc.constants;

import java.util.*;

public class FieldLibrary {

    private static Set<String> DATE_SET = new HashSet<String>(Arrays.asList(new String[]{"date", "time", "year", "datetime", "timestamp"}));
    // 长整型 "TINYINT", 布尔型 "BIT" 认为是 KEYWORD
    private static Set<String> LONG_SET = new HashSet<String>(Arrays.asList(new String[]{"smallint", "mediumint", "int", "integer", "bigint"}));
    // 浮点型
    private static Set<String> DOUBLE_SET = new HashSet<String>(Arrays.asList(new String[]{"float", "double", "decimal"}));
    // 长文本
    private static Set<String> TEXT_SET = new HashSet<String>(Arrays.asList(new String[]{"text", "tinytext", "mediumtext", "longtext"}));
    // 关键字
    private static Set<String> KEYWORD_SET = new HashSet<String>(Arrays.asList(new String[]{"varchar", "bit", "tinyint", "char", "linestring"}));

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

    public static Map<String,String> configMap(){
        Map<String,String> map = new HashMap<>();
        map.put("BaseInfo","基础信息");
        map.put("BaseAbnormal","异常行为信息");
        map.put("AssetInfo","资产清单");
        map.put("AssetSafety","保密产品安装情况");
        map.put("AssetLogin","终端登录情况");
        map.put("AppVisit","访问应用系统行为");
        map.put("AppConn","访问互联单位应用行为");
        map.put("DeviceVisit","终端设备访问");
        map.put("DeviceSafety","安全保密设备访问");
        map.put("DeviceOther","其它设备访问");
        map.put("FileExec","本地文件处理情况");
        map.put("FileImport","文件导入情况");
        map.put("FileOutput","文件输出情况");
        map.put("NetworkDevice","网络设备运维");
        map.put("NetworkApp","应用系统运维");
        map.put("NetworkSafety","安全保密设备运维");
        map.put("NetworkMaintain","终端设备运维");
        map.put("BasicServer","服务器信息");
        map.put("BasicResource","资源信息");
        map.put("BasicRole","角色信息");
        map.put("BasicUser","账户信息");
        map.put("ServerLogin","用户登录情况");
        map.put("ServerConn","后台连接情况");
        map.put("ServerVisit","其它应用访问情况");
        map.put("DBExec","文件处理情况");
        map.put("DBDownload","文件上传、下载情况");
        map.put("BaseVisit","互联访问情况");
        map.put("BaseTransfer","文件传输情况");
        map.put("BaseInternet","互联单位信息");
        map.put("BaseUser","用户数据分布");
        map.put("BaseApp","应用数据分布");
        return map;
    }

    public static String transConfig(String value){
        StringBuffer sb = new StringBuffer("");
        for(String s : value.split(",")){
            if(sb.length() > 0){
                sb.append(",");
            }
            sb.append(configMap().get(s));
        }
        return sb.toString();
    }
}
