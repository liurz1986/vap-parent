package com.vrv.vap.netflow.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vrv.flume.cmd.FlumeTools;
import com.vrv.flume.cmd.model.AppState;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.netflow.common.enums.ErrorCode;
import com.vrv.vap.netflow.common.enums.SendStatusEnum;
import com.vrv.vap.netflow.common.enums.TemplateTypeEnum;
import com.vrv.vap.netflow.common.util.*;
import com.vrv.vap.netflow.model.CollectorDataAccess;
import com.vrv.vap.netflow.model.CollectorOfflineRecord;
import com.vrv.vap.netflow.model.CollectorOfflineTemplate;
import com.vrv.vap.netflow.service.CollectorOfflineImportService;
import com.vrv.vap.netflow.service.CollectorOfflineRecordService;
import com.vrv.vap.netflow.service.CollectorOfflineTemplateService;
import com.vrv.vap.netflow.service.feign.AdminFeign;
import com.vrv.vap.netflow.service.kafka.KafkaSenderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author lilang
 * @date 2022/3/29
 * @description
 */
@Service
@Transactional
public class CollectorOfflineImportServiceImpl implements CollectorOfflineImportService {

    private static final Logger log = LoggerFactory.getLogger(CollectorOfflineImportServiceImpl.class);

    @Autowired
    AdminFeign adminFeign;

    @Autowired
    CollectorOfflineTemplateService templateService;

    @Autowired
    CollectorOfflineRecordService recordService;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Autowired
    RedisTemplate<String,CollectorDataAccess> redisTemplate;

    @Autowired
    StringRedisTemplate redisTpl;

    private static final String EVENT_CHANGE_MESSAGE_TOPIC = "vap_event_change_message";

    private static final String EVENT_MESSAGE_KEY = "vap_event_message";

    private static final String offlineDataAccessPrefix = "OFFLINE_DATA_ACCESS_";


    @Override
    public ErrorCode importData(MultipartFile file, Integer type,Integer templateId) {
        // 导入excel文件
        if (TemplateTypeEnum.TYPE_XLS.getCode().equals(type)) {
            return this.importExcelData(file,templateId);
        }
        // 导入xml文件
        if (TemplateTypeEnum.TYPE_XML.getCode().equals(type)) {
            return this.importXmlData(file,templateId);
        }
        return null;
    }

    public ErrorCode importExcelData(MultipartFile file,Integer templateId) {
        String fileName = file.getOriginalFilename();
        InputStream in = null;
        List<List<Object>> listob;
        if (!file.isEmpty()) {
            try {
                in = file.getInputStream();
                listob = ImportExcelUtil.getListByExcel(in, fileName);
                List<String> contentList = new ArrayList<>();
                CollectorOfflineTemplate offlineTemplate = templateService.findById(templateId);
                if (offlineTemplate == null) {
                    return ErrorCode.OFFLINE_TEMPLATE_NOT_EXIST;
                }
                Integer accessId = offlineTemplate.getAccessId();
                if (accessId == null) {
                    return ErrorCode.OFFLINE_TEMPLATE_NO_DATA_ACCESS;
                }
                CollectorDataAccess dataAccess = this.getDataAccess(accessId);
                if (dataAccess == null) {
                    return ErrorCode.OFFLINE_NO_ACCESS;
                }
                if (!CollectionUtils.isEmpty(listob)) {
                    List<Object> fieldNames = listob.get(0);
                    Boolean result = this.validateField(fieldNames,templateId);
                    if (!result) {
                        return ErrorCode.OFFLINE_CONTENT_NOT_MATCH;
                    }
                    for (int i = 1; i < listob.size(); i++) {
                        List<Object> lo = listob.get(i);
                        Map map = new LinkedHashMap();
                        for (int j = 0; j < lo.size(); j++) {
                            map.put(fieldNames.get(j),lo.get(j) != null ? lo.get(j) : "");
                        }
                        Gson gson = new Gson();
                        String content = gson.toJson(map).toString();
                        contentList.add(content);
                    }
                } else {
                    return ErrorCode.OFFLINE_CONTENT_NOT_MATCH;
                }
                String cid = dataAccess.getCid();
                String port = dataAccess.getPort();
                // 记录导入日志
                CollectorOfflineRecord offlineRecord = this.saveRecord(fileName,dataAccess,contentList.size(),TemplateTypeEnum.TYPE_XLS.getCode(),templateId);
                // 校验采集流程状态
                if (!this.getAccessState(cid)) {
                    this.startFlume(cid);
                }
                // 发送日志
                this.sendLog(contentList,port,offlineRecord,cid);
                this.sendEventMessage(offlineTemplate.getSourceId());
            } catch (Exception e) {
                log.error("excel文件读取异常！",e);
                return ErrorCode.OFFLINE_CONTENT_PARSE_ERROR;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
            }
        }
        return null;
    }

    public ErrorCode importXmlData(MultipartFile file,Integer templateId) {
        String fileName = file.getOriginalFilename();
        List<Object> fieldList = new ArrayList<>();
        List<String> contentList = new ArrayList<>();
        ErrorCode errorCode = this.getXmlContent(file,fieldList,contentList);
        if (errorCode != null) {
            return errorCode;
        }
        CollectorOfflineTemplate offlineTemplate = templateService.findById(templateId);
        if (offlineTemplate == null) {
            return ErrorCode.OFFLINE_TEMPLATE_NOT_EXIST;
        }
        Integer accessId = offlineTemplate.getAccessId();
        if (accessId == null) {
            return ErrorCode.OFFLINE_TEMPLATE_NO_DATA_ACCESS;
        }
        CollectorDataAccess dataAccess = this.getDataAccess(accessId);
        if (dataAccess == null) {
            return ErrorCode.OFFLINE_NO_ACCESS;
        }
        Boolean result = this.validateField(fieldList,templateId);
        if (!result) {
            return ErrorCode.OFFLINE_CONTENT_NOT_MATCH;
        }
        String cid = dataAccess.getCid();
        String port = dataAccess.getPort();
        // 记录导入日志
        CollectorOfflineRecord offlineRecord = this.saveRecord(fileName,dataAccess,contentList.size(),TemplateTypeEnum.TYPE_XML.getCode(),templateId);
        // 校验采集流程状态
        if (!this.getAccessState(cid)) {
            this.startFlume(cid);
        }
        // 发送日志
        this.sendLog(contentList,port,offlineRecord,cid);
        this.sendEventMessage(offlineTemplate.getSourceId());
        return null;
    }

    /**
     * 获取采集流程状态
     * @param cid
     * @return
     */
    public boolean getAccessState(String cid) {
        // 采集任务是否启动
        String workingDir = CommonUtil.getBaseInfo("VAP_WORK_DIR");
        FlumeTools flumeTools = new FlumeTools(workingDir + File.separator + "flume" + File.separator + "flume");
        AppState appState = flumeTools.status(cid);
        if (appState == null || !appState.isRunning()) {
            return false;
        }
        return true;
    }

    /**
     * 开启采集器
     * @param cid
     */
    public void startFlume(String cid) {
        String workingDir = CommonUtil.getBaseInfo("VAP_WORK_DIR");
        FlumeTools flumeTools = new FlumeTools(workingDir + File.separator + "flume" + File.separator + "flume");
        String jvmOption = "-Djava.security.auth.login.config=" + workingDir + "/flume/file/00/kafka_client_jaas.conf";
        log.info(jvmOption);
        flumeTools.start(cid,512,jvmOption,60000);
    }

    /**
     * 发送变更消息
     * @param sourceId
     */
    public void sendEventMessage(Integer sourceId) {
        if (sourceId == null) {
            log.info("数据源ID为空");
            return;
        }
        Map<String,Object> result = new HashMap<>();
        result.put("time", TimeTools.getSecondTimestampTwo(new Date()));
        result.put("type", 3);
        List<Map<String,Object>> messageList = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("dataSourceId",sourceId);
        map.put("dataType",3);
        map.put("open_status",1);
        map.put("data_status",1);
        map.put("msg","");
        messageList.add(map);
        result.put("data",messageList);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(result);
            kafkaSenderService.send(EVENT_CHANGE_MESSAGE_TOPIC,null,content);
            String eventMessageContent = redisTpl.opsForValue().get(EVENT_MESSAGE_KEY);
            List<Map<String,Object>> messageContentList = objectMapper.readValue(eventMessageContent,List.class);
            if (CollectionUtils.isNotEmpty(messageContentList)) {
                for (Map messageContent : messageContentList) {
                    Integer dataSourceId = (Integer) messageContent.get("dataSourceId");
                    if (sourceId.equals(dataSourceId)) {
                        messageContent.put("open_status", 1);
                        messageContent.put("data_status", 1);
                    }
                }
            }
            redisTpl.opsForValue().set(EVENT_MESSAGE_KEY,objectMapper.writeValueAsString(messageContentList));
        } catch (Exception e) {
            log.error("",e);
        }
    }

    /**
     * 校验导入数据字段
     * @param fieldNames
     * @param templateId
     * @return
     */
    public boolean validateField(List<Object> fieldNames,Integer templateId) {
        if (CollectionUtils.isEmpty(fieldNames)) {
            return false;
        }
        CollectorOfflineTemplate offlineTemplate = templateService.findById(templateId);
        Integer type = offlineTemplate.getType();
        if (offlineTemplate == null) {
            log.info("未查询到模板");
            return false;
        }
        InputStream st = null;
        try {
            String path = offlineTemplate.getPath();
            String name = offlineTemplate.getName();

            if (StringUtils.isEmpty(path) || "null".equals(path)) {
                st = this.getClass().getResourceAsStream("/template/" + name);
            } else {
                File file = new File(path);
                st = new FileInputStream(file);
            }
            List<Object> fieldList = new ArrayList<>();
            if (TemplateTypeEnum.TYPE_XLS.getCode().equals(type)) {
                List<List<Object>> listob = ImportExcelUtil.getListByExcel(st, offlineTemplate.getName());
                if (!CollectionUtils.isEmpty(listob)) {
                    fieldList = listob.get(0);
                }
            }
            if (TemplateTypeEnum.TYPE_XML.getCode().equals(type))  {
                Document document;
                // 读取XML文件内容
                BufferedReader br = new BufferedReader(new InputStreamReader(st, "utf-8"));
                StringBuffer buffer = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                document = DocumentHelper.parseText(buffer.toString());
                document.setXMLEncoding("utf-8");
                Element root = document.getRootElement();
                Element title = root.element("title");
                if (title != null) {
                    List<Element> elements = title.elements();
                    for (Element item : elements) {
                        fieldList.add(item.getText());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(fieldList)) {
                fieldList.removeAll(fieldNames);
                if (CollectionUtils.isEmpty(fieldList)) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("",e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public CollectorOfflineRecord saveRecord(String fileName, CollectorDataAccess dataAccess, Integer totalCount, Integer type,Integer templateId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        CollectorOfflineRecord offlineRecord = new CollectorOfflineRecord();
        offlineRecord.setName(fileName);
        offlineRecord.setCreator(user != null ? user.getName() : "未知用户");
        offlineRecord.setCreateTime(new Date());
        offlineRecord.setStatus(SendStatusEnum.SENDING.getCode());
        offlineRecord.setTemplateId(templateId);
        CollectorOfflineTemplate template = templateService.findById(templateId);
        if (template != null) {
            offlineRecord.setTemplateName(template.getName());
        }
        offlineRecord.setCollectorId(dataAccess.getId());
        offlineRecord.setCollectorName(dataAccess.getName());
        offlineRecord.setTotalCount(totalCount);
        offlineRecord.setType(type);
        recordService.save(offlineRecord);
        return offlineRecord;
    }

    public ErrorCode getXmlContent(MultipartFile file,List<Object> fieldList,List<String> contentList) {
        Document document;
        try {
            // 读取XML文件内容
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "utf-8"));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            document = DocumentHelper.parseText(buffer.toString());
            document.setXMLEncoding("utf-8");
            Element root = document.getRootElement();
            Element title = root.element("title");
            if (title != null) {
                List<Element> elements = title.elements();
                for (Element item : elements) {
                    fieldList.add(item.getText());
                }
            }
            List<Element> logList = root.elements("log");
            if (CollectionUtils.isNotEmpty(logList)) {
                for (Element item : logList) {
                    Map map = new LinkedHashMap();
                    List<Element> valueItems = item.elements();
                    for (int j = 0; j < valueItems.size();j++) {
                        map.put(fieldList.get(j),valueItems.get(j).getText());
                    }
                    Gson gson = new Gson();
                    String content = gson.toJson(map).toString();
                    contentList.add(content);
                }
            }
        } catch (Exception e) {
            log.error("xml文件解析失败",e);
            return ErrorCode.OFFLINE_CONTENT_PARSE_ERROR;
        }
        return null;
    }

    public void sendLog(List<String> contentList, String port, CollectorOfflineRecord offlineRecord,String cid) {
        String workingDir = CommonUtil.getBaseInfo("VAP_WORK_DIR");
        Thread thread = new Thread(() -> {
            List<String> errorList = new ArrayList<>();
            Integer successCount = 0;
            Integer errorCount = 0;
            if (CollectionUtils.isNotEmpty(contentList)) {
                for (String content : contentList) {
                    String address = System.getenv("LOCAL_SERVER_IP");
                    Boolean result = LogSendUtil.sendLogByUdp(content, address + ":" + port);
                    boolean status = this.getAccessState(cid);
                    if (status && result) {
                        successCount++;
                    } else {
                        errorCount++;
                        errorList.add(content);
                    }
                }
                offlineRecord.setSuccessCount(successCount);
                offlineRecord.setErrorCount(errorCount);
                // 发送失败文件
                if (CollectionUtils.isNotEmpty(errorList)) {
                    String errorFilePath = workingDir + File.separator + "sendError" + File.separator + TimeTools.format3(new Date()) + ".txt";
                    FileUtils.createFile(errorFilePath);
                    FileUtils.writeFile(errorList, errorFilePath);
                    offlineRecord.setErrorFile(errorFilePath);
                    offlineRecord.setStatus(SendStatusEnum.SEND_ERROR.getCode());
                } else {
                    offlineRecord.setStatus(SendStatusEnum.SEND_SUCCESS.getCode());
                }
                recordService.updateSelective(offlineRecord);
            }
        });
        thread.start();
    }

    private CollectorDataAccess getDataAccess(Integer accessId) {
        CollectorDataAccess collectorDataAccess = redisTemplate.opsForValue().get(offlineDataAccessPrefix + accessId);
        if (collectorDataAccess == null) {
            VData vData = adminFeign.getDataAccess(accessId);
            collectorDataAccess = (CollectorDataAccess) vData.getData();
            if (collectorDataAccess != null) {
                redisTemplate.opsForValue().set(offlineDataAccessPrefix + accessId,collectorDataAccess,10, TimeUnit.MINUTES);
            }
        }
        return collectorDataAccess;
    }
}
