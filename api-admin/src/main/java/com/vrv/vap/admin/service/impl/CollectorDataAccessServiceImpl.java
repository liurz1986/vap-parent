package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.flume.cmd.FlumeTools;
import com.vrv.flume.cmd.model.AppState;
import com.vrv.vap.admin.common.util.CommonUtil;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.vo.CollectorDataAccessVO;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.vo.Result;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
@Service
@Transactional
public class CollectorDataAccessServiceImpl extends BaseServiceImpl<CollectorDataAccess> implements CollectorDataAccessService {

    private static final Logger logger = LoggerFactory.getLogger(CollectorDataAccessServiceImpl.class);

    @Autowired
    private CollectorRuleCollectionService ruleCollectionService;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Autowired
    private CollectorRuleService collectorRuleService;

    @Autowired
    private CollectorIndexService collectorIndexService;

    @Autowired
    private SearchService searchService;

    //规则集-最新
    private static final Integer COLLECTION_STATUS_NEW = 1;

    //规则集-可更新
    private static final Integer COLLECTION_STATUS_UPDATE = 2;

    //采集器状态-运行中
    private static final Integer STATUS_RUNNING = 1;

    //采集器状态-停止
    private static final Integer STATUS_STOP = 2;

    //协议类型-UDP
    private static final int PROTOCOL_TYPE_UDP = 1;
    // 流量日志入kafka
    private static final String NETFLOW_KAFKA_CID = "a2f22a9a557e235301d0331240d6b2aa";
    // 流量日志kafka入es
    private static final String NETFLOW_CID = "34e8627173686d9960832aa144498a3d";
    // 安全产品cid
    private static final String SAFE_PRODUCT_CID = "0f50466728270d9754ba532a09444630";
    // 离线导入-标准字段通用接收
    private static final String STANDARD_FIELD_CID = "b9e591b63518ea948e4562103845f4f9";

    String workingDir = CommonUtil.getBaseInfo("VAP_WORK_DIR");

    String flumeDir = workingDir + File.separator + "flume" + File.separator;

    @Value("${vap.flume.log-path:/var/log/vap-flume}")
    private String logPath;

    // 自定义
    private static final Integer BUILD_TYPE_ADD = 0;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String EVENT_CHANGE_MESSAGE_TOPIC = "vap_event_change_message";

    private static final String EVENT_MESSAGE_KEY = "vap_event_message";

    @Override
    public List<CollectorDataAccessVO> transformDataAccess(List<CollectorDataAccess> accessList) {
        List<CollectorDataAccessVO> accessVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(accessList)) {
            return accessVOList;
        }
        logger.info("参数accessList值{}", ReflectionToStringBuilder.toString(accessList, ToStringStyle.MULTI_LINE_STYLE));
        accessList.stream().forEach(item -> {
            CollectorDataAccessVO accessVO = new CollectorDataAccessVO();
            BeanUtils.copyProperties(item,accessVO);
            Integer collectionId = item.getCollectionId();
            if (collectionId != null) {
                CollectorRuleCollection ruleCollection = ruleCollectionService.findById(collectionId);
                accessVO.setCollectionName(Optional.ofNullable(ruleCollection)
                        .map(CollectorRuleCollection::getName)
                        .orElseThrow(() -> new RuntimeException("规则集不存在:" + collectionId))
                );
                // 是否最新
                String newVersion = ruleCollection.getVersion();
                String oldVersion = item.getVersion();
                if (StringUtils.isNotEmpty(oldVersion) && oldVersion.equals(newVersion)) {
                    accessVO.setCollectionStatus(COLLECTION_STATUS_NEW);
                } else {
                    accessVO.setCollectionStatus(COLLECTION_STATUS_UPDATE);
                }
            }

            // 运行状态
            String cId = item.getCid();
            FlumeTools flumeTools = new FlumeTools(flumeDir + "flume");
            logger.info("flume工作目录：" + flumeDir + "flume");
            AppState appState = flumeTools.status(cId);
            if (appState != null && appState.isRunning()) {
                accessVO.setStatus(STATUS_RUNNING);
            } else {
                accessVO.setStatus(STATUS_STOP);
            }
            accessVOList.add(accessVO);
        });
        return accessVOList;
    }

    @Override
    public void restartFlume() {
        new Thread(() -> {
            List<CollectorDataAccess> accessList = this.findAll();
            List<CollectorDataAccessVO> accessVOList = this.transformDataAccess(accessList);
            accessVOList = accessVOList.stream().filter(item -> (!NETFLOW_CID.equals(item.getCid()) &&!SAFE_PRODUCT_CID.equals(item.getCid()) &&!NETFLOW_KAFKA_CID.equals(item.getCid()) && STATUS_RUNNING.equals(item.getStatus()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(accessVOList)) {
                FlumeTools flumeTools = this.getFlumeTool();
                for (CollectorDataAccessVO accessVO : accessVOList) {
                    String cid = accessVO.getCid();
                    Integer initMemory = accessVO.getInitMemory();
                    AppState appState = flumeTools.stop(cid,30000);
                    String jvmOption = "-Djava.security.auth.login.config=" + workingDir + "/flume/file/00/kafka_client_jaas.conf";
                    if (!appState.isRunning()) {
                        flumeTools.start(cid,initMemory,jvmOption,60000);
                    }
                }
            }
        }).start();
    }

    @Override
    public FlumeTools getFlumeTool() {
        FlumeTools flumeTools = new FlumeTools(flumeDir + "flume");
        return flumeTools;
    }

    @Override
    public String generateFlumeConf(CollectorDataAccess collectorDataAccess, String ruleJson, String filePath, String jsContent) {
        StringBuffer sb = new StringBuffer("");
        if (PROTOCOL_TYPE_UDP == collectorDataAccess.getType()) {
            // agent组装
            sb.append(this.buildAgent());
            // source组装
            sb.append(this.buildSource(collectorDataAccess));
            // 拦截器组装
            sb.append("#r1拦截器配置" + "\r\n");
            sb.append(ruleJson);
            if (StringUtils.isNotEmpty(filePath)) {
                sb.append("a1.sources.r1.interceptors.i1.rules = " + filePath + "\r\n"  + "\r\n");
            }
            // 组装Js拦截器
            sb.append(this.buildJsPrepare(collectorDataAccess,jsContent));
            // 组装kafka通道
            sb.append(this.buildKafkaChannel(collectorDataAccess.getId()));
            // 组装kafka sink
            sb.append(this.buildKafkaSink());

            sb.append("#source 与 channel 配置信息" + "\r\n");
            sb.append("a1.sources.r1.channels = c1" + "\r\n");
        }
        return sb.toString();
    }

    public String buildAgent() {
        StringBuffer sb = new StringBuffer("");
        sb.append("#agent配置信息" + "\r\n");
        sb.append("a1.sources = r1" + "\r\n");
        sb.append("a1.sinks = k1 k2 k3" + "\r\n");
        sb.append("a1.channels = c1" + "\r\n" + "\r\n");
        return sb.toString();
    }

    public String buildSource(CollectorDataAccess collectorDataAccess) {
        StringBuffer sb = new StringBuffer("");
        sb.append("#source配置信息"  + "\r\n");
        sb.append("#UDP服务端接收组件" + "\r\n");
        sb.append("a1.sources.r1.type = com.vrv.vap.dysource.UdpServerSource" + "\r\n");
        sb.append("a1.sources.r1.batch = 200" + "\r\n");
        String encoding = collectorDataAccess.getEncoding();
        if (StringUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        sb.append("a1.sources.r1.charsetIn = " + encoding + "\r\n");
        sb.append("a1.sources.r1.charsetOut = UTF-8" + "\r\n");
        sb.append("a1.sources.r1.jsPath = " + this.generateSourceIpFilter(collectorDataAccess.getSrcIp(),collectorDataAccess.getId()) + "\r\n");
        sb.append("a1.sources.r1.port = " + collectorDataAccess.getPort() + "\r\n");
        sb.append("a1.sources.r1.flushInterval = 10" + "\r\n");
        sb.append("a1.sources.r1.assetsFilter = false" + "\r\n");
        return sb.toString();
    }

    public String buildJsPrepare(CollectorDataAccess collectorDataAccess,String jsContent) {
        StringBuffer sb = new StringBuffer("");
        sb.append("#JS 脚本数据处理" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i2.type = com.vrv.vap.interceptor.FunctionInterceptor$Builder" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i2.jsPath = " + this.generateJsContent(jsContent,collectorDataAccess.getId(),collectorDataAccess.getUpdateNew()) + "\r\n");
        sb.append("a1.sources.r1.interceptors.i2.charset = UTF-8" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i2.useHeader = true" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i2.useData = false" + "\r\n" + "\r\n");
        return sb.toString();
    }

    public String buildKafkaChannel(Integer accessId) {
        StringBuffer sb = new StringBuffer("");
        sb.append("#channel for kafka配置信息" + "\r\n");
        sb.append("a1.channels.c1.type = SPILLABLEMEMORY" + "\r\n");
        sb.append("a1.channels.c1.memoryCapacity = 20000"  + "\r\n");
        sb.append("a1.channels.c1.overflowCapacity = 100000000"  + "\r\n");
        sb.append("a1.channels.c1.overflowTimeout = 3"  + "\r\n");
        sb.append("a1.channels.c1.byteCapacityBufferPercentage = 20"  + "\r\n");
        sb.append("a1.channels.c1.avgEventSize = 800"  + "\r\n");
        sb.append("a1.channels.c1.checkpointDir =/data/flume-conf/" + accessId + "/checkPoint1"  + "\r\n");
        sb.append("a1.channels.c1.dataDirs = /data/flume-conf/" + accessId + "/data1,/data/flume-conf/" + accessId + "/data2,/data/flume-conf/" + accessId + "/data3"  + "\r\n");
        sb.append("a1.channels.c1.capacity = 100000000" + "\r\n" + "\r\n");
        return sb.toString();
    }

    public String buildKafkaSink() {
        String kafkaPort = CommonUtil.getBaseInfo("KAFKA_PORT");
        StringBuffer sb = new StringBuffer("");
        sb.append("#sink for kafka配置信息" + "\r\n");
        sb.append("a1.sinks.k1.type = com.vrv.vap.kafkasink.KafkaSink2" + "\r\n");
        sb.append("a1.sinks.k1.dataFix = " + flumeDir + "file/00/datafix-kafka-zjg.yml" + "\r\n");
        sb.append("a1.sinks.k1.kafka.bootstrap.servers = vap:" + kafkaPort + "\r\n");
        sb.append("a1.sinks.k1.inputType=FLATJSON" + "\r\n");
        sb.append("a1.sinks.k1.allowTopicOverride = true" + "\r\n");
        sb.append("a1.sinks.k1.openVapFix=true" + "\r\n");
        sb.append("a1.sinks.k1.kafka.flumeBatchSize = 1000" + "\r\n");
        sb.append("a1.sinks.k1.tables = " + flumeDir + "file/default/baseinfo_dev.csv," + flumeDir + "file/default/baseinfo_user.csv," + flumeDir + "file/default/baseinfo_sys.csv,"  + flumeDir + "file/default/baseinfo_server.csv" +"\r\n");
        sb.append("a1.sinks.k1.kafka.producer.acks = 1" + "\r\n");
        sb.append("a1.sinks.k1.kafka.producer.linger.ms = 1" + "\r\n");
        sb.append("a1.sinks.k1.kafka.producer.security.protocol = SASL_PLAINTEXT" + "\r\n");
        sb.append("a1.sinks.k1.kafka.producer.sasl.mechanism = PLAIN" + "\r\n");
        sb.append("a1.sinks.k1.channel = c1" + "\r\n" + "\r\n");

        sb.append("#sink for kafka配置信息" + "\r\n");
        sb.append("a1.sinks.k2.type = com.vrv.vap.kafkasink.KafkaSink2" + "\r\n");
        sb.append("a1.sinks.k2.dataFix = " + flumeDir + "file/00/datafix-kafka-zjg.yml" + "\r\n");
        sb.append("a1.sinks.k2.kafka.bootstrap.servers = vap:" + kafkaPort + "\r\n");
        sb.append("a1.sinks.k2.inputType=FLATJSON" + "\r\n");
        sb.append("a1.sinks.k2.allowTopicOverride = true" + "\r\n");
        sb.append("a1.sinks.k2.openVapFix=true" + "\r\n");
        sb.append("a1.sinks.k2.kafka.flumeBatchSize = 1000" + "\r\n");
        sb.append("a1.sinks.k2.tables = " + flumeDir + "file/default/baseinfo_dev.csv," + flumeDir + "file/default/baseinfo_user.csv," + flumeDir + "file/default/baseinfo_sys.csv," + flumeDir + "file/default/baseinfo_server.csv" +"\r\n");
        sb.append("a1.sinks.k2.kafka.producer.acks = 1" + "\r\n");
        sb.append("a1.sinks.k2.kafka.producer.linger.ms = 1" + "\r\n");
        sb.append("a1.sinks.k2.kafka.producer.security.protocol = SASL_PLAINTEXT" + "\r\n");
        sb.append("a1.sinks.k2.kafka.producer.sasl.mechanism = PLAIN" + "\r\n");
        sb.append("a1.sinks.k2.channel = c1" + "\r\n" + "\r\n");

        sb.append("#sink for kafka配置信息" + "\r\n");
        sb.append("a1.sinks.k3.type = com.vrv.vap.kafkasink.KafkaSink2" + "\r\n");
        sb.append("a1.sinks.k3.dataFix = " + flumeDir + "file/00/datafix-kafka-zjg.yml" + "\r\n");
        sb.append("a1.sinks.k3.kafka.bootstrap.servers = vap:" + kafkaPort + "\r\n");
        sb.append("a1.sinks.k3.inputType=FLATJSON" + "\r\n");
        sb.append("a1.sinks.k3.allowTopicOverride = true" + "\r\n");
        sb.append("a1.sinks.k3.openVapFix=true" + "\r\n");
        sb.append("a1.sinks.k3.kafka.flumeBatchSize = 1000" + "\r\n");
        sb.append("a1.sinks.k3.tables = " + flumeDir + "file/default/baseinfo_dev.csv," + flumeDir + "file/default/baseinfo_user.csv," + flumeDir + "file/default/baseinfo_sys.csv," + flumeDir + "file/default/baseinfo_server.csv" +"\r\n");
        sb.append("a1.sinks.k3.kafka.producer.acks = 1" + "\r\n");
        sb.append("a1.sinks.k3.kafka.producer.linger.ms = 1" + "\r\n");
        sb.append("a1.sinks.k3.kafka.producer.security.protocol = SASL_PLAINTEXT" + "\r\n");
        sb.append("a1.sinks.k3.kafka.producer.sasl.mechanism = PLAIN" + "\r\n");
        sb.append("a1.sinks.k3.channel = c1" + "\r\n" + "\r\n");
        return sb.toString();
    }

    @Override
    public String getRuleContent(CollectorDataAccess collectorDataAccess) {
        String content = "";
        Integer collectionId = collectorDataAccess.getCollectionId();
        if (collectionId == null) {
            return content;
        }
        List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",collectionId);
        if (CollectionUtils.isEmpty(ruleList)) {
            return content;
        }
        String ruleJson;
        String jsContent;
        Boolean updateNew = collectorDataAccess.getUpdateNew();
        // 单条规则
        if (ruleList.size() == 1) {
            ruleJson = ruleList.get(0).getRuleJson();
            jsContent = ruleList.get(0).getJsContent();
            content = this.generateFlumeConf(collectorDataAccess,ruleJson,null,jsContent);
        } else {
            ruleJson = this.generateMultiRuleJson();
            jsContent = ruleList.get(0).getJsContent();
            String rulePath = collectorRuleService.generateRules(ruleList,collectorDataAccess.getId(),collectorDataAccess.getUpdateNew());
            content = this.generateFlumeConf(collectorDataAccess,ruleJson,rulePath,jsContent);
        }
        // 新增或更新为最新
        if (updateNew || StringUtils.isEmpty(collectorDataAccess.getRuleJson())) {
            collectorDataAccess.setRuleJson(ruleJson);
        }
        return content;
    }

    public String generateJsContent(List<CollectorRule> ruleList) {
        String jsContent = "";
        if (CollectionUtils.isNotEmpty(ruleList)) {
            for (CollectorRule rule : ruleList) {
                if (StringUtils.isNotEmpty(rule.getJsContent())) {
                    jsContent += rule.getJsContent() + "\r\n";
                }
            }
        }
        return jsContent;
    }

    public String generateMultiRuleJson() {
        StringBuffer sb = new StringBuffer("");
        sb.append("#数据预处理" + "\r\n");
        sb.append("a1.sources.r1.interceptors = i1 i2" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.type = com.vrv.vap.interceptor.DataPrepareInterceptor$Builder" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.charset  = UTF-8" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.errorType  = error" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.errorDataKey  = source" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.handler  = TEST" + "\r\n");
        sb.append("a1.sources.r1.interceptors.i1.ignoreError  = true" + "\r\n");
        return sb.toString();
    }

    public String generateJsContent(String jsContent,Integer accessId,Boolean updateNew) {
        String jsPath = flumeDir + "file" + File.separator + "00" + File.separator + "dataFilter" + accessId + ".js";
        if (updateNew) {
            FileUtils.writeFile(jsContent,jsPath);
        }
        return jsPath;
    }

    public String generateSourceIpFilter(String sourceIp,Integer accessId) {
        // 从环境变量中获取
        String localIp = System.getenv("LOCAL_SERVER_IP");
        if (StringUtils.isEmpty(localIp)) {
            localIp = "127.0.0.1";
        }
        StringBuffer sb = new StringBuffer("");
        sb.append("var sourceIp = \"" + sourceIp + "\";" + "\r\n");
        sb.append("function parse(event){" + "\r\n");
        sb.append("  var srcIp = event.getHeaders().get(\"_SRC_IP\");" + "\r\n");
        sb.append("  if (srcIp != sourceIp && srcIp != '" + localIp + "') {" + "\r\n");
        sb.append("    return null;" + "\r\n");
        sb.append("  }" + "\r\n");
        sb.append("  event.setUseSource(true);" + "\r\n");
        sb.append("  return event;" + "\r\n");
        sb.append("}" + "\r\n");
        String jsContent = sb.toString();
        String jsPath = flumeDir + "file" + File.separator + "00" + File.separator + "ipFilter" + accessId + ".js";
        FileUtils.writeFile(jsContent,jsPath);
        return jsPath;
    }

    @Override
    public Long getRunningCount() {
        // 授权点数校验
        List<CollectorDataAccess> accessList = this.findAll();
        if (CollectionUtils.isEmpty(accessList)) {
            return 0L;
        }
        FlumeTools flumeTools = this.getFlumeTool();
        Long runCount = accessList.stream().filter(item -> {
            AppState appState = flumeTools.status(item.getCid());
            if (BUILD_TYPE_ADD.equals(item.getBuildType()) && appState != null && appState.isRunning()) {
                return true;
            }
            return false;
        }).count();
        return runCount;
    }

    @Override
    public Result downloadLog(HttpServletResponse response, String cid) {
        Result result = new Result();
        String distPath = logPath + File.separator + cid + File.separator + "flume-" + cid + ".log";
        File logFile = new File(distPath);
        if(logFile.exists()) {
            FileUtils.downloadFile(distPath, response);
            return null;
        }
        logger.info("日志文件路径：" + distPath);
        result.setCode("-1");
        result.setMessage("日志下载失败");
        return result;
    }

    @Override
    public void changeStatus(String cid, Boolean openStatus) {
        // 离线导入标准字段任务不通知
        if (STANDARD_FIELD_CID.equals(cid)) {
            return;
        }
        Map<String,Object> result = new HashMap<>();
        result.put("time", TimeTools.getSecondTimestampTwo(new Date()));
        result.put("type", 1);
        List<CollectorDataAccess> accessList = this.findByProperty(CollectorDataAccess.class,"cid",cid);
        if (CollectionUtils.isNotEmpty(accessList)) {
            CollectorDataAccess dataAccess = accessList.get(0);
            Integer collectionId = dataAccess.getCollectionId();
            List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",collectionId);
            if (CollectionUtils.isEmpty(ruleList)) {
                return;
            }
            Set<Integer> sourceIds = new HashSet<>();
            Map<Integer,Boolean> statusMap = new HashMap<>();
            List<Map<String,Object>> messageList = new ArrayList<>();
            for (CollectorRule rule : ruleList) {
                String relateIndex = rule.getRelateIndex();
                Boolean status = this.processOpenStatus(relateIndex,openStatus,cid);
                if (openStatus) {
                    logger.info("规则ID：" + rule.getId() + "存在运行中的采集器" );
                }
                List<CollectorIndex> collectorIndexList = collectorIndexService.findByProperty(CollectorIndex.class,"type",relateIndex);
                if (CollectionUtils.isNotEmpty(collectorIndexList)) {
                    CollectorIndex collectorIndex = collectorIndexList.get(0);
                    Integer sourceId = collectorIndex.getSourceId();
                    statusMap.put(sourceId,status);
                    if (sourceId != null) {
                        sourceIds.add(sourceId);
                        Map<String,Object> map = new HashMap<>();
                        map.put("dataSourceId",sourceId);
                        map.put("dataType",1);
                        map.put("open_status",status ? 1 : 0);
                        map.put("data_status",status ? 1 : 0);
                        map.put("msg","");
                        messageList.add(map);
                    }
                }
            }

            result.put("data",messageList);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String content = objectMapper.writeValueAsString(result);
                kafkaSenderService.send(EVENT_CHANGE_MESSAGE_TOPIC,null,content);
                String eventMessageContent = redisTemplate.opsForValue().get(EVENT_MESSAGE_KEY);
                List<Map<String,Object>> messageContentList = objectMapper.readValue(eventMessageContent,List.class);
                if (CollectionUtils.isNotEmpty(messageContentList)) {
                    for (Map messageContent : messageContentList) {
                        Integer dataSourceId = (Integer) messageContent.get("dataSourceId");
                        for (Integer sourceId : sourceIds) {
                            if (sourceId.equals(dataSourceId)) {
                                messageContent.put("open_status",statusMap.get(sourceId) ? 1 : 0);
                                messageContent.put("data_status",statusMap.get(sourceId) ? 1 : 0);
                            }
                        }
                    }
                }
                redisTemplate.opsForValue().set(EVENT_MESSAGE_KEY,objectMapper.writeValueAsString(messageContentList));
            } catch (Exception e) {
                logger.error("",e);
            }
        }
    }

    private boolean processOpenStatus(String relateIndex,Boolean openStatus,String cid) {
        Boolean status = openStatus;
        if (!openStatus) {
            List<CollectorRule> collectorRules = collectorRuleService.findByProperty(CollectorRule.class,"relateIndex",relateIndex);
            Set<Integer> collectionIdSet = new HashSet<>();
            if (CollectionUtils.isNotEmpty(collectorRules)) {
                for (CollectorRule collectorRule : collectorRules) {
                    Integer ruleCollectionId = collectorRule.getCollectionId();
                    collectionIdSet.add(ruleCollectionId);
                }
            }
            if (CollectionUtils.isNotEmpty(collectionIdSet)) {
                for (Integer ruleCollectionId : collectionIdSet) {
                    List<CollectorDataAccess> accesses = this.findByProperty(CollectorDataAccess.class,"collectionId",ruleCollectionId);
                    if (CollectionUtils.isNotEmpty(accesses)) {
                        for (CollectorDataAccess access : accesses) {
                            String ccid = access.getCid();
                            FlumeTools flumeTools = this.getFlumeTool();
                            AppState appState = flumeTools.status(ccid);
                            // 当前事件接收器关闭，但是其他关联该数据源的事件接收器为开启状态
                            if (appState.isRunning() && !cid.equals(ccid)) {
                                status = true;
                                if (status) {
                                    logger.info("采集器ID：" + ccid + "正在运行中" );
                                }
                                return status;
                            }
                        }
                    }
                }
            }
        }
        return status;
    }

    @Override
    public List<Map<String, Object>> getAccessReport() {
        List<Map<String,Object>> resultList = new ArrayList<>();
        List<CollectorDataAccess> accessList = this.findAll();
        List<CollectorDataAccessVO> accessVOList = this.transformDataAccess(accessList);
        if (CollectionUtils.isNotEmpty(accessVOList)) {
            // 只查询运行状态数据
            List<CollectorDataAccessVO> runList = accessVOList.stream().filter(p -> p.getStatus() != null && p.getStatus().equals(1)).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(runList)) {
                for (CollectorDataAccessVO accessVO : runList) {
                    Map<String,Object> resultMap = new HashMap<>();
                    Integer collectionId = accessVO.getCollectionId();
                    String cid = accessVO.getCid();
                    CollectorRuleCollection ruleCollection = ruleCollectionService.findById(collectionId);
                    if (ruleCollection == null) {
                        logger.info("规则集不存在");
                        continue;
                    }
                    List<String> indexList = new ArrayList<>();
                    // 流量日志
                    if (NETFLOW_CID.equals(cid)) {
                        indexList = Arrays.asList("netflow-*");
                    }
                    // 安全产品日志
                    else if (SAFE_PRODUCT_CID.equals(cid)) {
                        indexList = Arrays.asList("client-offline-*,omms-log-*,device-startorshut-*,net-virus-*,sem-log-*,violationoutreach-log-*,weblogin-audit-*,client-logininout-*,terminal-login-*,adm-operate-*,specialudisk-use-*,file-audit-*,mb-log-*,process-audit-*,service-audit-*,software-audit-*,share-audit-*,sa-log-*,changestrategy-audit-*,print-audit-*,hardware-audit-*,performance-audit-*,attack-audit-*,operation-audit-*");
                    }
                    else {
                        List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",ruleCollection.getId());
                        if (CollectionUtils.isNotEmpty(ruleList)) {
                            for (CollectorRule rule : ruleList) {
                                indexList.add(rule.getRelateIndex() + "-*");
                            }
                        }
                    }
                    if (CollectionUtils.isEmpty(indexList)) {
                        logger.info("索引为空");
                        continue;
                    }
                    String queryJsonStr = "{\"size\":0,\"aggs\":{\"max_val\":{\"max\":{\"field\":\"@timestamp\"}}}}";
                    String res = searchService.searchGlobalContent(indexList, queryJsonStr);
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
                        Map<String, Object> maxMap = (Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("max_val");
                        Double maxValue = (Double) maxMap.get("value");
                        resultMap.put(ruleCollection.getName(),maxValue.longValue());
                        resultList.add(resultMap);
                    } catch (Exception e){
                        logger.error("", e);
                    }
                }
            }
        }
        return resultList;
    }

}
