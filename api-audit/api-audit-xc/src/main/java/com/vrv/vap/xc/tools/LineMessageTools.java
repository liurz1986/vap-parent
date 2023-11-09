package com.vrv.vap.xc.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.client.RedisCacheTools;
import com.vrv.vap.xc.config.MessageConfig;
import com.vrv.vap.xc.constants.LineConstants;
import com.vrv.vap.xc.fegin.ApiDataClient;
import com.vrv.vap.xc.mapper.BaseLineMapper;
import com.vrv.vap.xc.mapper.BaseLineResultMapper;
import com.vrv.vap.xc.mapper.BaseLineSpecialMapper;
import com.vrv.vap.xc.pojo.BaseLine;
import com.vrv.vap.xc.model.Source;
import com.vrv.vap.xc.model.SourceQuery;
import com.vrv.vap.xc.service.CommonService;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.service.kafka.KafkaSenderService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class LineMessageTools {
    public static final Logger log = Logger.getLogger(LineMessageTools.class);

    private BaseLineMapper baseLineMapper = VapXcApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private CommonService commonService = VapXcApplication.getApplicationContext().getBean(CommonService.class);
    private KafkaSenderService kafkaSenderService = VapXcApplication.getApplicationContext().getBean(KafkaSenderService.class);
    private RedisCacheTools redisCacheTools = VapXcApplication.getApplicationContext().getBean(RedisCacheTools.class);
    private BaseLineResultMapper baseLineResultMapper = VapXcApplication.getApplicationContext().getBean(BaseLineResultMapper.class);
    private static ApiDataClient apiDataClient = VapXcApplication.getApplicationContext().getBean(ApiDataClient.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapXcApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private MessageConfig messageConfig = VapXcApplication.getApplicationContext().getBean(MessageConfig.class);
    private String PRE_CN = "_";
    private String PRE_SM = LineConstants.NAME_PRE.PRE_SM;
    private String PRE_LINE = LineConstants.NAME_PRE.PRE_LINE;
    private String SOURCE_NAME_END = "-*";

    private static Map<String,Source> sourceNameMap = new HashMap<>();

    static {
        VData<List<Source>> listVData = apiDataClient.queryAllSource();
        List<Source> sourceVList = listVData.getData();
        if(CollectionUtils.isNotEmpty(sourceVList)){
            List<Source> collect = sourceVList.stream().collect(
                    Collectors.collectingAndThen(
                            Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Source::getName))), ArrayList::new)
            );
            sourceNameMap = collect.stream().collect(Collectors.toMap(r -> r.getName(), r-> r));
        }
    }

    public static Map<String,Source> getSourceNameMap(){
        return sourceNameMap;
    }

    private int getDataSourceIdByName(String name){
        String key = name.replaceAll("-","_");
        int id = 0;
        if(sourceNameMap != null && sourceNameMap.containsKey(key)){
            id = sourceNameMap.get(key).getId();
        }
        return id;
    }

    public void sendMessage(BaseLine line, Date currentTime){
        log.info("基线通知推送开始："+line.getId());
        int sourceId = 0;
        String endIndex = PRE_LINE + line.getSaveIndex();
        if(LineConstants.LINE_TYPE.TS.equals(line.getType())) {
            //内置模型
            sourceId = messageConfig.getSourceIdMap().get(line.getSpecialId());
        }else{
            sourceId = getDataSourceIdByName(endIndex);
        }

        int summarySourceId = 0;
        String summaryIndex = PRE_SM + line.getSaveIndex();
        if(!LineConstants.LINE_TYPE.TS.equals(line.getType())) {
            summarySourceId = getDataSourceIdByName(summaryIndex);
        }
        List<Map<String,Object>> messageData = new ArrayList<>();
        List<Map<String,Object>> summaryData = new ArrayList<>();
        int oldnum = line.getSummaryNum() == null ? 0 : line.getSummaryNum();
//        line.setSummaryNum(oldnum+1);
        line.setSummaryNum(oldnum);
        int open_status = line.getStatus().equals(LineConstants.LINE_STATUS.ENABLE) ? 1 : 0;
        int data_status = line.getDays() <= line.getSummaryNum() ? 1 : 0;
        String msg = line.getDays() > line.getSummaryNum() ? line.getDays()+"天基线只有"+line.getSummaryNum()+"天数据" : "";
        Map<String,Object> message = new HashMap<>();
        Map<String,Object> messageSumarrry = new HashMap<>();
        message.put("dataSourceId",sourceId);
        message.put("dataType",2);
        message.put("id",line.getId());
        message.put("open_status",open_status);
        message.put("data_status",data_status);
        if(currentTime != null){
            message.put("insertTime", TimeTools.format2(currentTime));
        }
        message.put("msg",msg);
        messageData.add(message);

        messageSumarrry.put("dataSourceId",summarySourceId);
        messageSumarrry.put("dataType",2);
        messageSumarrry.put("id",line.getId());
        messageSumarrry.put("open_status",open_status);
        messageSumarrry.put("data_status",data_status);
        if(currentTime != null){
            messageSumarrry.put("insertTime", TimeTools.format2(currentTime));
        }
        messageSumarrry.put("msg",msg);
        summaryData.add(messageSumarrry);

        //推送redis
        String redisMsg = this.redisCacheTools.get(this.messageConfig.getVapBaseLineKey());
        List<JSONObject> jsonObjects = new ArrayList<>();
        if(StringUtils.isNotEmpty(redisMsg)){
            jsonObjects = JSONArray.parseArray(redisMsg, JSONObject.class);
        }
        Map<Integer, JSONObject> redisDatas = jsonObjects.stream().collect(Collectors.toMap(j -> j.getIntValue("id"), j -> j));
        redisDatas.put(line.getId(),JSONObject.parseObject(JSONObject.toJSONString(message)));
        redisDatas.put(line.getId(),JSONObject.parseObject(JSONObject.toJSONString(messageSumarrry)));
        redisCacheTools.add(this.messageConfig.getVapBaseLineKey(),JSONObject.toJSONString(redisDatas.values()));
        //推送kafka
        Map<String,Object> kafkaMsg = new HashMap<>();
        kafkaMsg.put("type",2);
        kafkaMsg.put("data",messageData);
        kafkaMsg.put("time",System.currentTimeMillis());

        Map<String,Object> kafkaMsgSummary = new HashMap<>();
        kafkaMsgSummary.put("type",2);
        kafkaMsgSummary.put("data",summaryData);
        kafkaMsgSummary.put("time",System.currentTimeMillis());
        log.info("kafka推送："+JSONObject.toJSONString(kafkaMsg));
        log.info("kafka推送："+JSONObject.toJSONString(kafkaMsgSummary));
        kafkaSenderService.send(messageConfig.getBaseLineTopic(),JSONObject.toJSONString(kafkaMsg));
        kafkaSenderService.send(messageConfig.getBaseLineTopic(),JSONObject.toJSONString(kafkaMsgSummary));
        log.info("基线通知推送结束："+line.getId());
    }

    public void batchSendMessage(List<BaseLine> lines){
        try{
            log.info("基线批量推送开始："+lines.size());
            List<Map<String,Object>> kafkaDatas = new ArrayList<>();
            List<Map<String,Object>> redisDatas = new ArrayList<>();
            for(BaseLine line : lines){
                int sourceId = 0;
                String endIndex = PRE_LINE + line.getSaveIndex();
                if(LineConstants.LINE_TYPE.TS.equals(line.getType())) {
                    //内置模型
                    sourceId = messageConfig.getSourceIdMap().get(line.getSpecialId());
                }else{
                    sourceId = getDataSourceIdByName(endIndex);
                }
                List<Map<String,Object>> messageData = new ArrayList<>();
                int oldnum = line.getSummaryNum() == null ? 0 : line.getSummaryNum();
                int open_status = line.getStatus().equals(LineConstants.LINE_STATUS.ENABLE) ? 1 : 0;
                int data_status = line.getDays() <= oldnum ? 1 : 0;
                String msg = line.getDays() > oldnum ? line.getDays()+"天基线只有"+oldnum+"天数据" : "";
                Map<String,Object> message = new HashMap<>();
                message.put("dataSourceId",sourceId);
                message.put("id",line.getId());
                message.put("dataType",2);
                message.put("open_status",open_status);
                message.put("data_status",data_status);
                message.put("msg",msg);
                messageData.add(message);
                redisDatas.add(message);
                Map<String,Object> kafkaMsg = new HashMap<>();
                kafkaMsg.put("type",2);
                kafkaMsg.put("data",messageData);
                kafkaMsg.put("time",System.currentTimeMillis());
                kafkaDatas.add(kafkaMsg);
            }
            kafkaSenderService.batchSend(messageConfig.getBaseLineTopic(),kafkaDatas);
            redisCacheTools.add(this.messageConfig.getVapBaseLineKey(),JSONObject.toJSONString(redisDatas));
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
}
