package com.vrv.vap.monitor.server.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ServiceCheckUtil {

    private static Logger logger = LoggerFactory.getLogger(ServiceCheckUtil.class);

    public static boolean checkService(String url) {
        boolean result = false;
        try {
            //String url = "http://" + nacosAddr + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + namespace;
            String res = HTTPUtil.GET(url,null);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            List<Map<String, Object>> hosts = (List<Map<String, Object>>) resMap.get("hosts");
            if (CollectionUtils.isEmpty(hosts)) {
                return result;
            }

            for (Map<String, Object> host : hosts) {
                if ((Boolean) host.get("healthy")) {
                    result = true;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("服务状态查询异常", e);
        }
        return result;
    }

    public static boolean checkNetflow(String url) {
        try {
            //String url = "http://" + nacosAddr + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + namespace;
            String res = HTTPUtil.GET(url,null);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            if (!"0".equals(resMap.get("code").toString())) {
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("服务状态查询异常", e);
        }
        return false;
    }

    public static boolean  checkServiceStatus(String serviceName) {
        String cmd = "systemctl status " + serviceName.toLowerCase();
        logger.info("将要执行命令：" + LogForgingUtil.validLog(cmd));
        List<String> queryExecuteCmd = ShellExecuteScript.querySuccessExecuteCmd(cmd);
        for(String result : queryExecuteCmd) {
//            if (StringUtils.isNoneEmpty(result)&&(result.contains("PID:")||result.contains("pid:")|| result.contains("active (running)"))) {
//                return true;
//            }
            if (StringUtils.isNoneEmpty(result)&&(result.contains("active (running)"))) {
                return true;
            }
        }
        return false;
    }
    //重启服务
    public static void  restartService(String serviceName) {
        String cmd = "systemctl restart " + serviceName.toLowerCase();
        logger.info("将要执行命令：" + LogForgingUtil.validLog(cmd));
        ShellExecuteScript.querySuccessExecuteCmd(cmd);
        logger.info("重启服务指令完成");
    }
}
