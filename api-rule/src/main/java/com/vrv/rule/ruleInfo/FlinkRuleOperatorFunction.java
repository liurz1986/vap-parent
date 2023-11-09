package com.vrv.rule.ruleInfo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.logVO.StreamMidVO;
import com.vrv.rule.model.*;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.model.filter.OutFieldInfo;
import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.source.GetDataSourceStream;
import com.vrv.rule.source.datasourceparam.DataSourceInputParam;
import com.vrv.rule.util.*;
import com.vrv.rule.vo.FieldInfoVO;
import jodd.util.StringUtil;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * 告警规则2.0 编辑器规则启动类
 *
 * @author wd-pc
 */
public class FlinkRuleOperatorFunction {

    private static Logger logger = LoggerFactory.getLogger(FlinkRuleOperatorFunction.class);

    private static final String EXCEPTION_TYPE = "runnning";

    //private static final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static final Gson gson = DateUtil.parseGsonTime();
    //private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

//	static {
//		String path = YmlUtil.getValue("application.yml", "KAFKA_SSL_PATH").toString();
//        System.setProperty("java.security.auth.login.config", path);
//	}

    public static ConcurrentHashMap<String, CacheVo<Integer>> countCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, CacheVo<List<Map<String, Object>>>> dataCache = new ConcurrentHashMap<>();


    public static ConcurrentHashMap<String,String> tmpGuidCache = new ConcurrentHashMap<>();


    private static FlinkStartVO getFlinkStartVO() {
        String codes = "bef7692eaf8242c2aca49dcfea128198";  //d6b4496d7c35420b801a18b7f4eca0eb b17007441aec42dfab32a8dbc28c39a7
        FlinkStartVO flinkStartVO = new FlinkStartVO();
        Map<String, String> map = new HashMap<>();
        map.put("维表高级测试", codes);
        //List<FilterOperator> filterOperators = getFilterOperators();
        flinkStartVO.setFilterOperators(null);
        flinkStartVO.setCodeObj(map);
        flinkStartVO.setParallelism(2);
        flinkStartVO.setJobName("不在线");
        flinkStartVO.setType("category");
        return flinkStartVO;
    }


    public static void main(String[] args) {
//       Configuration configuration = new Configuration();
//       configuration.setInteger(RestOptions.PORT, 8089);
//       StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(configuration);
       StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime); //设置对应的时间类型(处理时间)
        env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES), Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）env.setRestartStrategy(RestartStrategies.failureRateRestart(10, Time.of(5, TimeUnit.MINUTES),Time.of(10, TimeUnit.SECONDS))); //故障率重启（每5分钟最大失败次数5次）
        FlinkStartVO flinkStartVO = new FlinkStartVO();
        String json = args[0];   //args[0]
        logger.info("json:" + json);
        try {
             //flinkStartVO = getFlinkStartVO(); //TODO 模拟数据
            flinkStartVO=gson.fromJson(json, FlinkStartVO.class);
        } catch (Exception e) {
            logger.info("进入异常处理流程:{}", e);
            flinkStartVO.setCodes(json);
            flinkStartVO.setCodeObj(null);
            flinkStartVO.setParallelism(1);
            flinkStartVO.setType("category");
            FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(json);
            flinkStartVO.setJobName(filterOperator.getName() + "-" + filterOperator.getCode());
            logger.info("异常构造数据：{}", new Gson().toJson(flinkStartVO));
        }
        String type = flinkStartVO.getType();
        String jobName = flinkStartVO.getJobName();
        Map<String, String> codeObj = flinkStartVO.getCodeObj();
        // Map<String, List<FilterOperator>> codeFilterObjects = flinkStartVO.getCodeFilterObjects();
        env.setParallelism(flinkStartVO.getParallelism());
        if (type.equals("datasource")) {  //数据源
            executeStrategyByDataSource(env, jobName, codeObj);
        } else { //分类启动
            executeStrategyByCategory(env, flinkStartVO, codeObj);
        }
        String eMessage = null;
        try {
            env.execute(jobName);
        } catch (Exception e) {
            logger.error("异常错误:{}", e);
            eMessage = String.valueOf(e);
            recordStrategyExceptionLog(jobName, eMessage, env);
        }
    }

    /**
     * 按照事件分类执行
     *
     * @param env
     * @param flinkStartVO
     * @param codeObj
     */
    private static void executeStrategyByCategory(StreamExecutionEnvironment env, FlinkStartVO flinkStartVO, Map<String, String> codeObj) {
        if (codeObj != null) { //codeObj和codes只能有一个是存在的
            for (Map.Entry<String, String> entry : codeObj.entrySet()) {
                String ruleCode = entry.getKey();    //策略Code
                String filterCodes = entry.getValue(); //规则Code
                executeFilterOperatorByCode(env, filterCodes, ruleCode);
            }
        } else {
            String ruleCode = "自定义_" + UUID.randomUUID().toString();
            String codes = flinkStartVO.getCodes();
            executeFilterOperatorByCode(env, codes, ruleCode);
        }
    }

    /**
     * 按照datasource的方式执行策略
     *
     * @param env
     * @param jobName
     * @param codeObj
     */
    private static void executeStrategyByDataSource(StreamExecutionEnvironment env, String jobName, Map<String, String> codeObj) {
        String groupId = jobName;
        String name = null;

        FilterOperator filterOperator = null;
        List<DataStreamSourceVO> dataStreamSourceVOS = new ArrayList<>();
        for (Map.Entry<String, String> entry : codeObj.entrySet()) {
            String filterCodes = entry.getValue(); //规则Code
            String ruleCode = entry.getKey();    //策略Code
            String[] codeArr = filterCodes.split(",");
            ruleCode = ruleCode + "-" + filterCodes;
            dataStreamSourceVOS = getKafkaStream(groupId, env, codeArr, ruleCode);
            break;
        }
        for (Map.Entry<String, String> entry : codeObj.entrySet()) {
            String ruleCode = entry.getKey();    //策略Code
            String filterCodes = entry.getValue(); //规则Code
            String[] codeArr = filterCodes.split(",");
            String tmpCode = ruleCode;
            // 遍历处理source id 数据流
            for (String code : codeArr) {
                filterOperator = FilterOperatorUtil.getFilterOperator(code);
                name = filterOperator.getLabel();
                ruleCode = ruleCode + "-" + code;
//					groupId  =  name+"-"+code;
                //List<DataStreamSourceVO> copyDataStreamSourceVOS = copyDataStream(dataStreamSourceVOS, name);
                try {
                    List<DataStreamSourceVO> dataStreamSourceVOs = FilterOperatorUtil.executeFilterOperator(env, filterOperator, name + "-" + code, dataStreamSourceVOS, ruleCode);
                    outPutInformation(filterOperator, dataStreamSourceVOs, ruleCode);
                } catch (Exception e) {
                    String ruleMessage = name + ":" + e.getMessage();
                    logger.error("规则{}出现错误，请检查错误:{}", name, e);
                    recordRuleExceptionLog(code, ruleMessage, env);  //记录规则名称
                }
                ruleCode = tmpCode;
            }
        }
    }


    /**
     * 获得kafkaStream流相关数据
     *
     * @param env
     * @param codeArr
     * @return
     */
    private static List<DataStreamSourceVO> getKafkaStream(String groupId, StreamExecutionEnvironment env, String[] codeArr, String ruleCode) {
        String oneCode = codeArr[0];
        FilterOperator filterOperator = FilterOperatorUtil.getFilterOperator(oneCode);
        String config = filterOperator.getConfig();
        String roomType = filterOperator.getRoomType();
        String tag = filterOperator.getTag();
        String startConfig = filterOperator.getStartConfig();
        FilterConfigObject filterConfigObject = gson.fromJson(config, FilterConfigObject.class);
        List<DataStreamSourceVO> kafkaDataStreams = getKafkaDataStream(env, filterConfigObject, groupId, roomType, ruleCode,tag,startConfig); //初始化数据
        return kafkaDataStreams;
    }

    private static List<DataStreamSourceVO> getKafkaDataStream(StreamExecutionEnvironment env, FilterConfigObject filterConfigObject, String groupId, String roomType, String ruleCode,String tag,String startConfig) {
        //TODO 这个地方还存在问题（还是希望通过这个sourceId获得）
        List<String> sources = getEventTableIds(filterConfigObject);

        DataSourceInputParam dataSourceInputParam = DataSourceInputParam.builder().sources(sources).tag(tag)
                .startConfig(startConfig).filterConfigObject(filterConfigObject)
                .groupId(groupId).roomType(roomType).build();
        List<DataStreamSourceVO> dataStreamSource = GetDataSourceStream.getDataStreamSource(env, dataSourceInputParam);
        return dataStreamSource;
    }


    /**
     * 获得相同数据源的事件表ID
     *
     * @param filterConfigObject
     * @return
     */
    private static List<String> getEventTableIds(FilterConfigObject filterConfigObject) {
        Set<String> collectionSets = new HashSet<>();
        Tables[][] tables = filterConfigObject.getTables();
        for (int i = 0; i < tables.length; i++) {
            for (int j = 0; j < tables[i].length; j++) {
                Tables table = tables[i][j];
                if (table != null) {  //TODO table可能为null
                    String type = table.getType();
                    if (type.equals("eventTable") || type.equals("filterSource") || type.equals("offlineTask")) {
                        collectionSets.add(table.getId());
                    }
                }
            }
        }
        List<String> list = new ArrayList(collectionSets);
        return list;
    }


    /**
     * 通过code执行对应的规则
     *
     * @param env
     * @param ruleCode
     */
    private static void executeFilterOperatorByCode(StreamExecutionEnvironment env, String codes, String ruleCode) {
        FilterOperator filterOperator = null;
        String groupId = "";
        String name = null;
        String eMessage = null;
        String tmpCode = ruleCode;
        String[] codeArr = codes.split(",");
        for (String code : codeArr) {
            ruleCode = ruleCode + "-" + code;
            String ruleMessage = null;
            filterOperator = FilterOperatorUtil.getFilterOperator(code);
            name = filterOperator.getLabel();
            groupId = name + "-" + code;
            try {
                List<DataStreamSourceVO> dataStreamSourceVOs = FilterOperatorUtil.executeFilterOperator(env, filterOperator, groupId, ruleCode);
                outPutInformation(filterOperator, dataStreamSourceVOs, ruleCode);
            } catch (Exception e) {
                ruleMessage = name + ":" + e.getMessage();
                 logger.error("规则{}出现错误，请检查错误:{}", name, e);
                recordRuleExceptionLog(code, ruleMessage, env);  //记录规则名称
            }
            ruleCode = tmpCode;
        }
    }


    /**
     * 记录对应的策略的异常日志
     *
     * @param strategyName
     * @param message
     * @param env
     */
    private static void recordStrategyExceptionLog(String strategyName, String message, StreamExecutionEnvironment env) {
        String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
        String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
        FlinkRunningTimeErrorLog flinkRunningTimeErrorLog = new FlinkRunningTimeErrorLog();
        flinkRunningTimeErrorLog.setGuid(UUID.randomUUID().toString());
        flinkRunningTimeErrorLog.setLogInfo(message);
        flinkRunningTimeErrorLog.setRuleName(strategyName);
        flinkRunningTimeErrorLog.setRuleLevel(null);
        flinkRunningTimeErrorLog.setExceptionType(EXCEPTION_TYPE);
        JdbcSingeConnectionUtil.getInstance().insertFlinkRunningTimeErrorLog(flinkRunningTimeErrorLog);
    }


    /**
     * 记录异常日志
     *
     * @param code
     * @param eMessage
     */
    private static void recordRuleExceptionLog(String code, String eMessage, StreamExecutionEnvironment env) {
        String topicName = "test1";   //TODO 确定后进行修改
        String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
        String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
        FlinkRunningTimeErrorLog flinkRunningTimeErrorLog = new FlinkRunningTimeErrorLog();
        RuleInfo ruleInfo = getRuleWeight(code);
        flinkRunningTimeErrorLog.setGuid(UUID.randomUUID().toString());
        flinkRunningTimeErrorLog.setLogInfo(eMessage);
        flinkRunningTimeErrorLog.setRuleName(ruleInfo.getName());
        flinkRunningTimeErrorLog.setRuleLevel(ruleInfo.getWeight());
        flinkRunningTimeErrorLog.setExceptionType(EXCEPTION_TYPE);
        try {
            JdbcSingeConnectionUtil.getInstance().insertFlinkRunningTimeErrorLog(flinkRunningTimeErrorLog);
        } catch (Exception e) {
            logger.error("插入数据异常，请检查！", e);
        }

    }


    /**
     * 输出对应的topic
     *
     * @param filterOperator
     * @return
     */
    private static void outPutInformation(FilterOperator filterOperator, List<DataStreamSourceVO> dataStreamSourceVOs, String ruleCode) {
        String name = filterOperator.getName();
        String outputs = filterOperator.getOutputs();
        String roomType = filterOperator.getRoomType();
        List<Outputs> outputList = gson.fromJson(outputs, new TypeToken<List<Outputs>>() {
        }.getType());
        for (Outputs output : outputList) {
            executeAlarmInfo(filterOperator, dataStreamSourceVOs, name, output, roomType, ruleCode);
        }

    }
    /**
     * 获得一个公共的resultguid
     * @param dataStreamSourceVOs
     * @return
     */



    /**
     * 处理告警信息
     *
     *
     * @param filterOperator
     * @param dataStreamSourceVOs
     * @param name
     * @param output
     */
    private static void executeAlarmInfo(FilterOperator filterOperator, List<DataStreamSourceVO> dataStreamSourceVOs, String name, Outputs output, String roomType, String ruleCode) {
        String type = output.getType();
        Configs config = output.getConfig();
        String url = YmlUtil.getValue("application.yml", "KAFKA_URL").toString();
        String port = YmlUtil.getValue("application.yml", "KAFKA_PORT").toString();
        switch (type) {
            case "alarmdeal":
                if (dataStreamSourceVOs.size() == 1) {
                    //RiskEventRule riskEventRule = output.getConfig().getAlarmObj().getRiskEventRule();
                    AlarmObj alarmObj = output.getConfig().getAlarmObj();

                    if (alarmObj != null) {
                        DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
                        List<FieldInfoVO> fieldInfoVOs = dataStreamSourceVO.getFieldInfoVOs();
                        DataStream<Row> dataStream = dataStreamSourceVO.getDataStreamSource();
                        String indexName = getindexName(filterOperator, output);
                        dealAlarmStream( indexName,ruleCode, "flink-wiki-demo", url, port, alarmObj, fieldInfoVOs, dataStream, roomType);
                    } else {
                        throw new RuntimeException("告警输出没有对应的规则请检查！");
                    }
                } else {
                    throw new RuntimeException("最终输出存在多个数据流,请检查！");
                }
                break;
            case "kafka":
                String topicName = config.getName();
                executeDataStreamJson(filterOperator, name, dataStreamSourceVOs, topicName, url, port);
                break;
            case "eventType":
                String indexName = config.getName(); //存储ES索引名称
                List<EventField> eventField = config.getEventField();//事件对象映射关系
                executeDataStreamByEventType(filterOperator, name, dataStreamSourceVOs, indexName, eventField, url, port);
                break;
            default:
                break;
        }
    }

    /**
     * 拿到indexName
     * @param filterOperator
     * @param output
     * @return
     */
    private static String getindexName(FilterOperator filterOperator, Outputs output) {
        String outputs = filterOperator.getOutputs();
        List<Outputs> outputList = gson.fromJson(outputs, new TypeToken<List<Outputs>>() {
        }.getType());
        List<Outputs> eventType = outputList.stream().filter(s -> s.getType().equals("eventType")).collect(Collectors.toList());
        String indexName = null;  //索引名称
        if(eventType.size()>0){
            indexName = eventType.get(0).getConfig().getName();
        }
        return indexName;
    }


    /**
     * 获得加密信息的Properties
     *
     * @return
     */
    private static Properties getSslProperties(String url, String port, String topicName) {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", url + ":" + port);
        properties.setProperty("group.id", topicName + UUID.randomUUID().toString());
        String kafkaUserName = YmlUtil.getValue("application.yml", "KAFKA_AUTH_USERNAME").toString();
        String kafkaPassword = YmlUtil.getValue("application.yml", "KAFKA_AUTH_PASSWORD").toString();

        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        properties.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=" + kafkaUserName + " password=" + kafkaPassword + ";");
        properties.put("request.timeout.ms", 120000);

        return properties;
    }

    /**
     * 处理告警信息流
     *
     * @param ruleCode
     * @param topicName
     * @param url
     * @param port
     * @param alarmObj
     * @param fieldInfoVOs
     * @param dataStream
     */
    private static void dealAlarmStream( String indexName,String ruleCode, String topicName, String url, String port, AlarmObj alarmObj,
                                        List<FieldInfoVO> fieldInfoVOs, DataStream<Row> dataStream, String roomType) {

        Properties sslProperties = getSslProperties(url, port, topicName);
        dataStream.map(new MapFunction<Row, String>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String map(Row row) throws Exception {
                String src_Ip = null;
                String dst_Ip = null;
                String src_port = null;
                String dst_port = null;
                String relate_ip = null;

                String srcIpField = alarmObj.getSrcIp();
                src_Ip = getFieldRowValue(srcIpField, fieldInfoVOs, row);

                String dstIpField = alarmObj.getDstIp();
                dst_Ip = getFieldRowValue(dstIpField, fieldInfoVOs, row);

                String relateIpField = alarmObj.getRelateIp();
                relate_ip = getFieldRowValue(relateIpField, fieldInfoVOs, row);

                String srcPortField = alarmObj.getSrcPort();
                src_port = getFieldRowValue(srcPortField, fieldInfoVOs, row);

                String dstPortField = alarmObj.getDstPort();
                dst_port = getFieldRowValue(dstPortField, fieldInfoVOs, row);

                List<ExtendParam> extendParams = alarmObj.getExtendParams();
                Map<String, Object> extendParamMap = new HashMap<>();
                if (extendParams != null) {
                    for (ExtendParam extendParam : extendParams) {
                        String fieldName = extendParam.getFieldName();
                        Object value = getFieldRowValueObj(fieldName, fieldInfoVOs, row);
                        extendParamMap.put(extendParam.getLabel(), value);
                    }
                }
                String resultGuid = UUID.randomUUID().toString();
                if(!StringUtil.isEmpty(indexName) && !tmpGuidCache.containsKey(indexName)){
                    tmpGuidCache.put(indexName,resultGuid);
                }
                String streamMidStr = getStreamMidInfo(resultGuid+"|"+indexName,ruleCode, fieldInfoVOs, roomType, row, src_Ip, dst_Ip, src_port,
                        dst_port, relate_ip, extendParamMap);
                return streamMidStr;

            }

        }).addSink(new FlinkKafkaProducer<>(topicName, new SimpleStringSchema(), sslProperties));

    }

    /**
     * 获得中间流数据
     *
     * @param ruleCode
     * @param fieldInfoVOs
     * @param roomType
     * @param row
     * @param src_Ip
     * @param dst_Ip
     * @param src_port
     * @param dst_port
     * @param relate_ip
     * @param extendParamMap
     * @return
     */
    private static String getStreamMidInfo(String resutlGuid, String ruleCode, List<FieldInfoVO> fieldInfoVOs, String roomType, Row row,
                                           String src_Ip, String dst_Ip, String src_port, String dst_port, String relate_ip,
                                           Map<String, Object> extendParamMap) {
        StreamMidVO streamMidVO = new StreamMidVO();
        switch (roomType) {
            case RoomInfoConstant.ID_ROOM_TYPE:
                Map<String, String[]> idRoom = getIdRoom(ruleCode, fieldInfoVOs, row);
                String timeRoomStrs = "";
                streamMidVO = new StreamMidVO(resutlGuid, ruleCode, new Date(), src_Ip, dst_Ip, relate_ip, src_port, dst_port, idRoom, timeRoomStrs, extendParamMap);
                break;
            case RoomInfoConstant.TIME_ROOM_TYPE:
                Map<String, Map<String, String>> timeRoom = getTimeRoom(fieldInfoVOs, row);
                Map<String, String[]> idRooms = new HashMap<>();
                String timeRoomStr = gson.toJson(timeRoom);
                streamMidVO = new StreamMidVO(resutlGuid, ruleCode, new Date(), src_Ip, dst_Ip, relate_ip, src_port, dst_port, idRooms, timeRoomStr, extendParamMap);
                break;
            default:
                break;
        }
        String streamMidStr = gson.toJson(streamMidVO);
        return streamMidStr;
    }

    /**
     * 获得成员变量对应的值
     *
     * @param alarmObj
     * @param field
     * @return
     * @throws IllegalAccessException
     */
    private static String getFieldValue(AlarmObj alarmObj, Field field) throws IllegalAccessException {
        Object fieldObj = field.get(alarmObj);
        if (fieldObj != null) {
            return fieldObj.toString();
        } else {
            return null;
        }
    }

    /**
     * 获得对应的row的值
     *
     * @param alarmFieldName
     * @param fieldInfoVOs
     * @param row
     * @return
     */
    private static String getFieldRowValue(String alarmFieldName, List<FieldInfoVO> fieldInfoVOs, Row row) {
        if (alarmFieldName != null) {
            String fieldValue = null;
            for (FieldInfoVO fieldInfoVO : fieldInfoVOs) {
                String fieldName = fieldInfoVO.getFieldName();
                if (alarmFieldName.equals(fieldName)) {
                    Integer order = fieldInfoVO.getOrder();
                    fieldValue = String.valueOf(row.getField(order));
                    break;
                }
            }
            return fieldValue;
        } else {
            return alarmFieldName;
        }
    }

    /**
     * 获得对应的row的值(object)
     *
     * @param alarmFieldName
     * @param fieldInfoVOs
     * @param row
     * @return
     */
    private static Object getFieldRowValueObj(String alarmFieldName, List<FieldInfoVO> fieldInfoVOs, Row row) {
        Object fieldValue = null;
        for (FieldInfoVO fieldInfoVO : fieldInfoVOs) {
            String fieldName = fieldInfoVO.getFieldName();
            if (alarmFieldName.equals(fieldName)) {
                Integer order = fieldInfoVO.getOrder();
                fieldValue = row.getField(order);
                break;
            }
        }
        return fieldValue;
    }


    /**
     * 获得IdRoom
     *
     * @param fieldInfoVOs
     * @param row
     * @return
     */
    private static Map<String, String[]> getIdRoom(String code, List<FieldInfoVO> fieldInfoVOs, Row row) {
        for (FieldInfoVO fieldInfoVO : fieldInfoVOs) {
            String fieldType = fieldInfoVO.getFieldType();
            if (fieldType.equals(RoomInfoConstant.MAP_ARRAY)) {
                Object field = row.getField(fieldInfoVO.getOrder());
                if (field instanceof Map<?, ?>) {
                    Map<String, String[]> map = (Map<String, String[]>) field;
                    return map;
                }
            }
        }
        logger.error("没有找到对应的输出idroom，请检查！" + code);
        Map<String, String[]> newmap = new HashMap<>();
        return newmap;
        //throw new RuntimeException("没有找到对应的输出idroom，请检查！");
    }

    /**
     * 获得timeRoom
     *
     * @param fieldInfoVOs
     * @param row
     * @return
     */
    private static Map<String, Map<String, String>> getTimeRoom(List<FieldInfoVO> fieldInfoVOs, Row row) {
        for (FieldInfoVO fieldInfoVO : fieldInfoVOs) {
            String fieldType = fieldInfoVO.getFieldType();
            if (fieldType.equals(RoomInfoConstant.MAP_MAP)) {
                Object field = row.getField(fieldInfoVO.getOrder());
                if (field instanceof Map<?, ?>) {
                    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) field;
                    return map;
                }
            }
        }
        throw new RuntimeException("没有找到对应的输出timeroom，请检查！");
    }


    /**
     * 执行数据按照事件类型输出
     *
     * @param filterOperator
     * @param name
     * @param dataStreamSourceVOs
     * @param indexName
     * @param url
     * @param port
     */
    private static void executeDataStreamByEventType( FilterOperator filterOperator, String name,
                                                     List<DataStreamSourceVO> dataStreamSourceVOs, String indexName, List<EventField> eventField, String url, String port) {

        String topicName = "event-type-topic";
        if (dataStreamSourceVOs.size() > 0) {
            Properties sslProperties = getSslProperties(url, port, topicName);
            DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
            String outputFields = filterOperator.getOutputFields();
            List<OutFieldInfo> outFieldInfos = gson.fromJson(outputFields, new TypeToken<List<OutFieldInfo>>() {
            }.getType());

            DataStream<Row> dataStreamSource = dataStreamSourceVO.getDataStreamSource();
            dataStreamSource.map(row -> {
                Map<String, Object> map = new HashMap<>();
                int arity = row.getArity();
                int size = outFieldInfos.size();
                if (arity == size) {
                    for (int i = 0; i < size; i++) {
                        Object value = row.getField(i);
                        OutFieldInfo outFieldInfo = outFieldInfos.get(i);
                        String fieldName = outFieldInfo.getFieldName();
                        map.put(fieldName, value);
                    }
                } else {
                    throw new RuntimeException(name + "最终输出结果和最终输出列不一致");
                }

                Map<String, String> eventFieldMap = getEventFieldMap(eventField);
                Map<String, Object> result = new HashMap<>();
                for (Map.Entry<String, String> entry : eventFieldMap.entrySet()) {
                    String logField = entry.getKey();
                    String outField = entry.getValue();
                    Object o = map.getOrDefault(logField, null);
                    result.put(outField, o);
                }
                String resultGuid = null;
                if(!StringUtil.isEmpty(indexName)&& tmpGuidCache.containsKey(indexName)){
                    String s = tmpGuidCache.get(indexName);
                    result.put("resultGuid", s);
                    tmpGuidCache.remove(indexName);
                }else {
                    result.put("resultGuid", UUID.randomUUID().toString());
                }
                String json = gson.toJson(result);
                EventTypeObject eventTypeObject = new EventTypeObject();
                eventTypeObject.setContent(json);
                eventTypeObject.setIndexName(indexName);
                String lastResult = gson.toJson(eventTypeObject);

                return lastResult;
            }).addSink(new FlinkKafkaProducer<>(topicName, new SimpleStringSchema(), sslProperties));
        }

    }


    /**
     * 将EventField的list转换成Map对象
     * @param list
     * @return
     */
    private static Map<String,String> getEventFieldMap(List<EventField> list){
          Map<String,String> map = new HashMap<>();
            for(EventField eventField:list){
                String logField = eventField.getLogField();
                String outField = eventField.getOutputField();
                map.put(logField,outField);
            }
            return  map;
    }

    /**
     * kafka类型数据流输出
     *
     * @param filterOperator
     * @param name
     * @param dataStreamSourceVOs
     * @param topicName
     * @param url
     * @param port
     */
    private static void executeDataStreamJson(FilterOperator filterOperator, String name,
                                              List<DataStreamSourceVO> dataStreamSourceVOs, String topicName, String url, String port) {
        if (dataStreamSourceVOs.size() > 0) {
            Properties sslProperties = getSslProperties(url, port, topicName);
            String outputFields = filterOperator.getOutputFields();
            List<OutFieldInfo> outFieldInfos = gson.fromJson(outputFields, new TypeToken<List<OutFieldInfo>>() {
            }.getType());
            DataStreamSourceVO dataStreamSourceVO = dataStreamSourceVOs.get(0);
            DataStream<Row> dataStreamSource = dataStreamSourceVO.getDataStreamSource();
            dataStreamSource.map(new MapFunction<Row, String>() {

                private static final long serialVersionUID = 1L;

                @Override
                public String map(Row row) throws Exception {
                    Map<String, Object> map = new HashMap<>();
                    int arity = row.getArity();
                    int size = outFieldInfos.size();
                    if (arity == size) {
                        for (int i = 0; i < size; i++) {
                            Object value = row.getField(i);
                            OutFieldInfo outFieldInfo = outFieldInfos.get(i);
                            String fieldName = outFieldInfo.getFieldName();
                            map.put(fieldName, value);
                        }
                    } else {
                        throw new RuntimeException(name + "最终输出结果和最终输出列不一致");
                    }
                    String json = gson.toJson(map);
                    return json;
                }
            }).addSink(new FlinkKafkaProducer<>(topicName, new SimpleStringSchema(), sslProperties));
        }
    }


    /**
     * 获得对应的规则的ruleCode
     *
     * @param guid
     * @return
     */
    private static String getRuleCode(String guid) {
        List<String> list = new ArrayList<>();
        list.add(guid);
        String sql = JdbcSqlConstant.RISK_CODE_SQL;
        Map<String, Object> map = JdbcSingeConnectionUtil.getInstance().querySqlForMap(sql, list);
        String ruleCode = map.get("rule_code").toString();
        return ruleCode;
    }


    /**
     * 根据code查询对应的规则的等级
     *
     * @param code
     * @return
     */
    private static RuleInfo getRuleWeight(String code) {
        RuleInfo ruleInfo = new RuleInfo();
        List<String> list = new ArrayList<>();
        list.add(code);
        String sql = JdbcSqlConstant.FILTER_NAME_WEIGHT_SQL;
        List<Map<String, Object>> result = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql, list);
        if (result.size() > 0) {
            Map<String, Object> map = result.get(0);
            String riskEventName = map.getOrDefault("label", "").toString();
            ruleInfo.setName(riskEventName);
            ruleInfo.setWeight("0");
        }
        return ruleInfo;
    }


}
