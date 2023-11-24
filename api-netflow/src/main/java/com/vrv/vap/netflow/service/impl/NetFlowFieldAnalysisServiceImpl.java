package com.vrv.vap.netflow.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.netflow.common.config.BatchQueueProperties;
import com.vrv.vap.netflow.common.config.TypeDictionaryProperties;
import com.vrv.vap.netflow.common.enums.AppProtocolEnum;
import com.vrv.vap.netflow.common.enums.NetworkProtocolEnum;
import com.vrv.vap.netflow.common.enums.SessionProtocolEnum;
import com.vrv.vap.netflow.common.enums.TransportProtocolEnum;
import com.vrv.vap.netflow.model.AppSysManager;
import com.vrv.vap.netflow.model.BaseKoalOrg;
import com.vrv.vap.netflow.model.BasePersonZjg;
import com.vrv.vap.netflow.model.BaseSecurityDomain;
import com.vrv.vap.netflow.service.NetFlowFieldAnalysisService;
import com.vrv.vap.netflow.service.NetflowBaseDataService;
import com.vrv.vap.netflow.vo.AssetVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 流量数据发送flume的原始日志数据清洗及转换
 *
 * @author wh1107066
 */

@Service
public class NetFlowFieldAnalysisServiceImpl implements InitializingBean, NetFlowFieldAnalysisService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String DEFAULT_UNKNOWN_VALUE = "未知";
    @Resource
    private NetflowBaseDataService netflowBaseDataService;
    @Resource
    private TypeDictionaryProperties typeDictionaryProperties;
    @Resource
    private BatchQueueProperties batchQueueProperties;
    private List<String> ignoreFilterIpsList = new ArrayList<>();


    /**
     * 初始化方法，初始化的时候，将配置文件中的忽略过滤的ip地址进行初始化
     */
    @Override
    public void afterPropertiesSet() {
        String ips = batchQueueProperties.getIgnoreFilterIps();
        if (!StringUtils.isEmpty(ips)) {
            ignoreFilterIpsList = Arrays.stream(ips.split(",")).map(String::trim).collect(Collectors.toList());
        }
    }

    /**
     * 探测器的传输的字段与字段类型  与  zjg中的flume解析的标准不一样。
     * 中间转化一次，成为 flume 的标准字段类型数据解析。
     *
     * @param targetMap map
     */
    @Override
    public void swapFieldValues(Map targetMap) {
        try {
            // TODO flume 的解析字段与 流量探针 字段不一样的数据转化，转化成vrv特有的标准数据类型。
            setMonitorFieldToStanderField("username", "pri_username", targetMap);
            setMonitorFieldToStanderField("login_username", "username", targetMap);
            setMonitorFieldToStanderField("res_code", "pri_res_code", targetMap);
            setMonitorFieldToStanderField("login_res_code", "res_code", targetMap);
            setMonitorFieldToStanderField("transport_protocol", "transport", targetMap, TransportProtocolEnum.UNKNOWN_TRANSPORT_PROTOCOL.getType());
            setMonitorFieldToStanderField("network_protocol", "network_protocol", targetMap, NetworkProtocolEnum.IP_NETWORK_PROTOCOL.getType());
            // 流入数据总字节
            setMonitorFieldToStanderField("total_in_bytes", "client_total_byte", targetMap);
            setMonitorFieldToStanderField("total_in_pkts", "client_total_pkt", targetMap);
            setMonitorFieldToStanderField("total_out_bytes", "server_total_byte", targetMap);
            setMonitorFieldToStanderField("total_out_pkts", "server_total_pkt", targetMap);
            setMonitorFieldToStanderField("session_protocol", "session_protocol", targetMap, SessionProtocolEnum.TLS_SESSION_PROTOCOL.getType());
            setMonitorFieldToStanderField("app_protocol ", "app_protocol ", targetMap, AppProtocolEnum.UNKNOWN_APP_PROTOCOL.getType());

        } catch (Exception e) {
            String jsonString = JSONObject.toJSONString(targetMap);
            logger.error(String.format("swapFieldValues解析异常, %s", jsonString), e);
        }
    }

    /**
     * flume与流量探针的原始数据的标准化数据转化过程
     *
     * @param monitorField 监视器字段
     * @param flumeField   flume的标准字段
     * @param targetMap    监视器中的原始数据
     */
    private void setMonitorFieldToStanderField(String monitorField, String flumeField, Map targetMap) {
        if (StringUtils.isEmpty(monitorField)) {
            String jsonString = JSONObject.toJSONString(targetMap);
            throw new RuntimeException(String.format("monitorField为空, %s", jsonString));
        }
        if (StringUtils.isEmpty(flumeField)) {
            String jsonString = JSONObject.toJSONString(targetMap);
            throw new RuntimeException(String.format("FlumeField为空, %s", jsonString));
        }
        if (targetMap.containsKey(monitorField)) {
            targetMap.put(flumeField, targetMap.get(monitorField));
        }
    }

    /**
     * 带默认值的携带与设置操作
     *
     * @param monitorField monitorField
     * @param flumeField   flumeField
     * @param targetMap    targetMap
     * @param defaultValue defaultValue
     */
    private void setMonitorFieldToStanderField(String monitorField, String flumeField, Map targetMap, Object defaultValue) {
        this.setMonitorFieldToStanderField(monitorField, flumeField, targetMap);
        Object targetValue = targetMap.get(flumeField);
        if (targetValue == null || StringUtils.isEmpty(String.valueOf(targetValue))) {
            targetMap.put(flumeField, defaultValue);
        }
    }

    /**
     * log_type标准化vrv的标准字段，， 流量探针的log_type  转化成  zjg 的标准 log_type 类型
     */
    @Override
    public void standMonitorLogTypeToFlumeLogType(Map<String, Object> logMap) {

    }

    @Override
    public void handleSessionId(Map targetMap) {
        try {
            String sessionId = "";
            if (targetMap.containsKey("cookie")) {
                String cookie = (String) targetMap.get("cookie");
                if (StringUtils.isNotEmpty(cookie)) {
                    String[] cookieArr = cookie.split(";");
                    for (String coo : cookieArr) {
                        if (coo.toUpperCase().indexOf("SESSION") >= 0) {
                            String[] cos = coo.split("=");
                            if (cos.length > 0) {
                                sessionId = cos[1];
                            }
                        }
                    }
                }
            }
            if (StringUtils.isEmpty(sessionId)) {
                if (targetMap.containsKey("http_req_header")) {
                    String reqHeader = (String) targetMap.get("http_req_header");
                    if (StringUtils.isNotEmpty(reqHeader)) {
                        String[] headerArr = reqHeader.split(";");
                        for (String header : headerArr) {
                            if (header.toUpperCase().indexOf("SESSION") >= 0) {
                                String[] head = header.split("=");
                                if (head.length >=2 ) {
                                    sessionId = head[1].replaceAll("\r", "").replaceAll("\n", "");
                                }
                            }
                        }
                    }
                }
            }
            // TODO 新增session_id字段的解析数据
            if(StringUtils.isEmpty(sessionId)) {
                if (targetMap.containsKey("sess_id")) {
                    sessionId = String.valueOf(targetMap.get("sess_id"));
                }
            }
            targetMap.put("session_id", sessionId);
        } catch (Exception e) {
            targetMap.put("session_id", "");
            String jsonString = JSONObject.toJSONString(targetMap);
            logger.error(String.format("handleSessionId解析异常, %s", jsonString), e);
        }
    }


    @Override
    public void handlerApp(Map targetMap) {
        //应用系统编号
        targetMap.put("src_std_sys_id", DEFAULT_UNKNOWN_VALUE);
        //应用系统名称
        targetMap.put("src_std_sys_name", DEFAULT_UNKNOWN_VALUE);

        targetMap.put("dst_std_sys_id", DEFAULT_UNKNOWN_VALUE);
        //应用系统名称
        targetMap.put("dst_std_sys_name", DEFAULT_UNKNOWN_VALUE);
        try {
            //源机构，目标机构
            String srcIp = targetMap.containsKey("sip") ? (String) targetMap.get("sip") : "";
            String srcCode = "";
            String disCode = "";
            // TODO 补全数据，通过uri获取应用的携带参数信息,可之定义排查一些非法的ip，在配置文件中定义
            String param = findUriParams(targetMap);
            if (StringUtils.isNotEmpty(srcIp) && !ignoreFilterIpsList.contains(srcIp)) {
                AppSysManager appSysManager = netflowBaseDataService.fixAppIpCache(srcIp);
                if (appSysManager != null) {
                    //应用系统编号
                    targetMap.put("src_std_sys_id", appSysManager.getAppNo());
                    //应用系统名称
                    targetMap.put("src_std_sys_name", appSysManager.getAppName());
                } else {
                    logger.debug("应用系统srcIp未找到对应的应用！ srcIp: {}, appSysManager获取的值为空！", srcIp);
                }
            } else {
                logger.debug("应用系统编号补全srcIp为空！srcIp {}", srcIp);
            }

            String url = (String) targetMap.get("url");
            if (StringUtils.isNotEmpty(url)) {
                int index = url.indexOf("?");
                String miniUrl = url;
                if (index > 0) {
                    miniUrl = url.substring(0, index);
                }
                AppSysManager appSysManager = netflowBaseDataService.fixAppUrlCache(miniUrl);
                if (appSysManager != null) {
                    disCode = appSysManager.getAppNo();
                    //应用系统编号
                    targetMap.put("dst_std_sys_id", appSysManager.getAppNo());
                    //应用系统名称
                    targetMap.put("dst_std_sys_name", appSysManager.getAppName());

                }
            }
            String dip = (String) targetMap.get("dip");
            if (StringUtils.isEmpty(url) && StringUtils.isNotEmpty(dip)) {
                AppSysManager appSysManager = netflowBaseDataService.fixAppIpCache(dip);
                if (appSysManager != null) {
                    srcCode = appSysManager.getAppNo();
                    //应用系统编号
                    targetMap.put("dst_std_sys_id", appSysManager.getAppNo());
                    //应用系统名称
                    targetMap.put("dst_std_sys_name", appSysManager.getAppName());
                }
            }

            targetMap.put("std_communication_type", 5);
            if (StringUtils.isNotEmpty(srcCode) && StringUtils.isNotEmpty(disCode)) {
                if (srcCode.equals(disCode)) {
                    targetMap.put("std_communication_type", 2);
                } else {
                    targetMap.put("std_communication_type", 1);
                }
            }
            if (StringUtils.isNotEmpty(srcCode) && StringUtils.isEmpty(disCode)) {
                targetMap.put("std_communication_type", 3);
            }
            if (StringUtils.isEmpty(srcCode) && StringUtils.isNotEmpty(disCode)) {
                targetMap.put("std_communication_type", 4);
            }

            //通信参数
            targetMap.put("src_sys_parameter", param);
            targetMap.put("param", param);
            targetMap.put("param_length", param == null ? 0 : param.length());
        } catch (Exception exception) {
            String jsonString = JSONObject.toJSONString(targetMap);
            logger.error(String.format("handlerApp解析异常, %s", jsonString), exception);
        }

    }

    /**
     * GET 的uri参数是有参数的，post请求无参数
     *
     * @param map
     * @return
     */
    public String findUriParams(Map map) {
        String url = Optional.ofNullable((String) map.get("uri")).orElse("");
        if (url.isEmpty()) {
            return "";
        }
        if (map.containsKey("method") && "GET".equalsIgnoreCase((String) map.get("method"))) {
            int index = url.indexOf("?");
            if (index > 0 && index < (url.length() - 1)) {
                logger.debug(String.format("解析uri的参数：uri: %s, method: %s", url, map.get("method")));
                String substring = url.substring(index, url.length() - 1);
                return substring;
            }
        }
        return "";
    }

    @Override
    public void handlerOrg(Map targetMap) {
        targetMap.put("src_std_org_code", DEFAULT_UNKNOWN_VALUE);
        targetMap.put("dst_std_org_code", DEFAULT_UNKNOWN_VALUE);
        targetMap.put("src_std_secret_level", "-1");
        targetMap.put("classification_level_code", 4);
        if (targetMap.containsKey("classification_level") &&
                typeDictionaryProperties.getLevelTypeDic().containsKey((String) targetMap.get("classification_level"))) {
            targetMap.put("classification_level_code",
                    typeDictionaryProperties.getLevelTypeDic().get((String) targetMap.get("classification_level")));
        }

        try {
            //源机构，目标机构
            String srcIp = (String) targetMap.get("sip");
            String srcCode = "";
            String disCode = "";
            if (StringUtils.isNotEmpty(srcIp)) {
                BaseKoalOrg baseKoalOrg = netflowBaseDataService.fixOrgIpCache(srcIp);
                if (baseKoalOrg != null) {
                    srcCode = baseKoalOrg.getCode();
                    targetMap.put("src_std_org_code", baseKoalOrg.getCode());
                    targetMap.put("src_std_secret_level", baseKoalOrg.getSecretLevel());
                }
            }

            String distIp = (String) targetMap.get("dip");
            if (StringUtils.isNotEmpty(distIp)) {

                BaseKoalOrg baseKoalOrg = netflowBaseDataService.fixOrgIpCache(distIp);
                if (baseKoalOrg != null) {
                    disCode = baseKoalOrg.getCode();
                    targetMap.put("dst_std_org_code", baseKoalOrg.getCode());
                }
            }
            targetMap.put("std_is_same_unit", 0);
            if (StringUtils.isNotEmpty(srcCode) && StringUtils.isNotEmpty(disCode) && srcCode.equals(disCode)) {
                targetMap.put("std_is_same_unit", 1);
            }
            //1-单位内部访问  2-外部访问本单位 3-本单位访问外部 4-外部访问
            Integer visitType = 4;
            if (StringUtils.isNotEmpty(srcCode)) {
                visitType -= 1;
            }
            if (StringUtils.isNotEmpty(disCode)) {
                visitType -= 2;
            }
            targetMap.put("visit_type", visitType);
        } catch (Exception exception) {
            String jsonString = JSONObject.toJSONString(targetMap);
            logger.error(String.format("handlerOrg 解析异常, %s", jsonString), exception);
        }
    }

    @Override
    public void handlerDev(Map log) {
        try {
            //设备类型-二级名称
            log.put("src_std_dev_type", DEFAULT_UNKNOWN_VALUE);
            //设备类型-一级code
            log.put("src_std_dev_type_group", "-1");
            //设备密级
            log.put("src_std_dev_level", "4");
            //所属安全域
            log.put("src_std_dev_safety_marign", DEFAULT_UNKNOWN_VALUE);
            log.put("src_std_dev_safety_marign_name", DEFAULT_UNKNOWN_VALUE);
            // 源设备标识
            log.put("src_std_terminal_type", "-1");
            // 目的设备标识
            log.put("dst_std_terminal_type", "-1");
            //设备类型-二级名称
            log.put("dst_std_dev_type", DEFAULT_UNKNOWN_VALUE);
            //设备类型-一级code
            log.put("dst_std_dev_type_group", "-1");

            //设备密级
            log.put("dst_std_dev_level", "4");
            //IP
            log.put("dst_std_dev_ip", DEFAULT_UNKNOWN_VALUE);
            //所属安全域
            log.put("dst_std_dev_safety_marign", DEFAULT_UNKNOWN_VALUE);
            log.put("dst_std_dev_safety_marign_name", DEFAULT_UNKNOWN_VALUE);
            String srcIp = (String) log.get("sip");
            if (StringUtils.isNotEmpty(srcIp)) {
                // terminalType做了单独处理
                AssetVo asset = netflowBaseDataService.fixAssetIpCache(StringUtils.strip(srcIp));
                if (asset != null) {
                    //设备类型
                    log.put("src_std_dev_type", asset.getTypeName());
                    if (typeDictionaryProperties.getDevTypeDic().isEmpty()) {
                        logger.error("logTypeDic设备类型初始化字典为空！");
                    } else if (typeDictionaryProperties.getDevTypeDic().containsKey(asset.getGroupName())) {
                        String srcGroupName = typeDictionaryProperties.getDevTypeDic().get(asset.getGroupName());
                        log.put("src_std_dev_type_group", srcGroupName);
                    }
                    //设备密级
                    log.put("src_std_dev_level", asset.getEquipmentIntensive());
                    //MAC
                    // 源设备标识typeGuid
                    log.put("src_std_terminal_type", asset.getTerminalType());
                    //所属安全域
                    log.put("src_std_dev_safety_marign", asset.getSecurityguid());
                    // TODO fixed bug! redis中的id是0,1,  而不是guid
                    BaseSecurityDomain baseSecurityDomainInfo = netflowBaseDataService.fixSecCodeCache(asset.getSecurityguid());
                    if (baseSecurityDomainInfo != null) {
                        log.put("src_std_dev_safety_marign_name", baseSecurityDomainInfo.getDomainName());
                    }
                } else {
                    // TODO 如果ip不在资产表中，那么直接去安全域下的ip关联安全域
                    logger.debug("设备srcIp未找到对应的资产！ srcIp: {}, map中 key: {} ,asset获取的值为空！", srcIp, NetflowBaseDataService.CACHE_ASSET_IP_KEY);
                    BaseSecurityDomain baseSecurityDomain = netflowBaseDataService.fixSecIpCache(srcIp);
                    if (baseSecurityDomain != null) {
                        log.put("src_std_dev_safety_marign", baseSecurityDomain.getCode());
                        log.put("src_std_dev_safety_marign_name", baseSecurityDomain.getDomainName());
                        logger.debug("设备srcIp未找到对应的资产！ src_std_dev_safety_marign: {}, src_std_dev_safety_marign_name: {} ！", baseSecurityDomain.getCode(), baseSecurityDomain.getDomainName());
                    }
                    logger.debug("设备srcIp未找到对应的资产！ sip: {} ， 未关联到安全域下的的code和domainName！", srcIp);
                }
            }


            String distIp = (String) log.get("dip");
            if (StringUtils.isNotEmpty(distIp)) {
                AssetVo asset = netflowBaseDataService.fixAssetIpCache(distIp);
                if (asset != null) {
                    //设备类型
                    log.put("dst_std_dev_type", asset.getTypeName());
                    log.put("dst_std_dev_type_group", typeDictionaryProperties.getDevTypeDic().containsKey(asset.getGroupName()) ?
                            typeDictionaryProperties.getDevTypeDic().get(asset.getGroupName()) : "-1");
                    //设备密级
                    log.put("dst_std_dev_level", asset.getEquipmentIntensive());
                    //IP
                    log.put("dst_std_dev_ip", asset.getIp());
                    //MAC
                    log.put("dst_std_terminal_type", asset.getTerminalType());
                    //所属安全域
                    log.put("dst_std_dev_safety_marign", asset.getSecurityguid());
                    BaseSecurityDomain baseSecurityDomainInfo = netflowBaseDataService.fixSecCodeCache(asset.getSecurityguid());
                    if (baseSecurityDomainInfo != null) {
                        log.put("dst_std_dev_safety_marign_name", baseSecurityDomainInfo.getDomainName());
                    }
                } else {
                    logger.debug("设备distIp未找到对应的资产！ distIp: {}, map中 key: {} asset获取的值为空！", distIp, NetflowBaseDataService.CACHE_ASSET_IP_KEY);
                    BaseSecurityDomain baseSecurityDomain = netflowBaseDataService.fixSecIpCache(distIp);
                    if (baseSecurityDomain != null) {
                        log.put("dst_std_dev_safety_marign", baseSecurityDomain.getCode());
                        log.put("dst_std_dev_safety_marign_name", baseSecurityDomain.getDomainName());
                    }
                }
            }
        } catch (Exception exception) {
            String jsonString = JSONObject.toJSONString(log);
            logger.error(String.format("handlerDev 解析异常, %s", jsonString), exception);
        }


    }

    @Override
    public void handlerPerson(Map targetMap) {
        try {
            //源设备责任人编号
            targetMap.put("src_std_user_no", DEFAULT_UNKNOWN_VALUE);
            //用户类型
            targetMap.put("src_std_user_type", "-1");
            //源设备责任人编号
            targetMap.put("dst_std_user_no", DEFAULT_UNKNOWN_VALUE);
            //用户类型
            targetMap.put("dst_std_user_type", "-1");
            //源资产负责人，目标资产负责人
            String srcIp = (String) targetMap.get("sip");
            if (StringUtils.isNotEmpty(srcIp)) {
                // TODO 此处每次都是循环遍历，需要优化。 存入map通过ip的key进行获取复杂度 o(1) 算法复杂度
                BasePersonZjg basePersonZjg = netflowBaseDataService.fixPersonIpCache(srcIp);
                if (basePersonZjg != null) {
                    //源设备责任人编号
                    targetMap.put("src_std_user_no", basePersonZjg.getUserNo());
                    //用户类型
                    targetMap.put("src_std_user_type", basePersonZjg.getPersonType());
                } else {
                    logger.debug("人员srcIp未找到对应的资产！ srcIp: {}, map中 key: {} ,asset获取的值为空！", srcIp, NetflowBaseDataService.CACHE_PERSON_ZJG_KEY);
                }
            }

            String distIp = (String) targetMap.get("dip");
            if (StringUtils.isNotEmpty(distIp)) {
                // TODO 同上处理方法
                BasePersonZjg basePersonZjg = netflowBaseDataService.fixPersonIpCache(distIp);
                if (basePersonZjg != null) {
                    //源设备责任人编号
                    targetMap.put("dst_std_user_no", basePersonZjg.getUserNo());
                    //用户类型
                    targetMap.put("dst_std_user_type", basePersonZjg.getPersonType());
                }
            } else {
                logger.debug("人员distIp未找到对应的资产！ distIp: {}, map key: {} asset获取的值为空！", distIp, NetflowBaseDataService.CACHE_PERSON_ZJG_KEY);
            }
        } catch (Exception exception) {
            String jsonString = JSONObject.toJSONString(targetMap);
            logger.error(String.format("handlerPerson 解析异常, %s", jsonString), exception);
        }
    }


}
