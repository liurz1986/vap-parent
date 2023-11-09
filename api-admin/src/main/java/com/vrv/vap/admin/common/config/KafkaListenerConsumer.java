package com.vrv.vap.admin.common.config;

import com.google.gson.*;
import com.vrv.vap.admin.common.enums.SuperviseDataSubmitStatusEnum;
import com.vrv.vap.admin.common.enums.SuperviseDataTypeEnum;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.config.PutField;
import com.vrv.vap.admin.model.DataSubmitInfo;
import com.vrv.vap.admin.model.SuperviseDataSubmit;
import com.vrv.vap.admin.model.SuperviseStatusSubmit;
import com.vrv.vap.admin.service.SuperviseDataSubmitService;
import com.vrv.vap.admin.service.SuperviseService;
import com.vrv.vap.admin.service.SuperviseStatusSubmitService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.vo.supervise.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;


@Component
public class KafkaListenerConsumer implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(KafkaListenerConsumer.class);

    private static final Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER).setDateFormat("yyyy-MM-dd HH:mm:ss").create();


    @Autowired
    SuperviseDataSubmitService superviseDataSubmitService;
    @Autowired
    SuperviseService superviseService;
    @Autowired
    SuperviseStatusSubmitService superviseStatusSubmitService;

    @Autowired
    private SystemConfigService systemConfigService;


    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;


    private static Properties props = null;

    public Properties kafkaTestConsumerProperties() {
        if (props == null) {
            synchronized (this) {
                props = new Properties();
                props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
                props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
                props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-sj");
                props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
                props.put(ConsumerConfig.CLIENT_ID_CONFIG, "201");
                props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
                props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaUser + "\" password=\"" + kafkaPwd + "\";");
                props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
                props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
                // 自动提交offset,每1s提交一次
                props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
            }
        }
        return props;
    }


    @Override
    public void run(String... args) throws Exception {
        logger.info("向业务平台-上报接口线程");
        consume("SuperviseDataSubmit", (message) -> superviseDataSubmit(message));
    }

    public void consume(String topic, Consumer<Object> fun) {
        try {
            logger.info("开始消费任务:" + topic);
            FutureTask futureTask = new FutureTask<>(() -> {
                logger.info("开始消费tipic:" + topic);
                KafkaConsumer consumer = new KafkaConsumer(kafkaTestConsumerProperties());
                consumer.subscribe(Collections.singleton(topic));
                AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
                try {
                    while (runing.get()) {
                        ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
                        for (Object record : records) {
                            ConsumerRecord consumerRecord = (ConsumerRecord) record;
                            try {
                                fun.accept(consumerRecord.value());
                            } catch (Exception es) {
                                logger.error("消息消费失败", es);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("consumer error : ", e);
                } finally {
                    consumer.close();
                }
                return null;
            });
            Thread thread = new Thread(futureTask);
            thread.start();
        } catch (Exception e) {
            logger.error("消费任务失败 : ", e);
        }
    }

    private static Type getParamterTypeClasszByList(Field field) {

        try {
            Type genericType = field.getGenericType();
            ParameterizedType pt = (ParameterizedType) genericType;
            Type ts = pt.getActualTypeArguments()[0];
            return ts;
        } catch (Exception e) {
            logger.info("泛型实例化异常", e);
            throw new RuntimeException(e);
        }

    }

    public static List<Field> getAllFields(Object object) {

        if (object == null) {
            return new ArrayList<Field>();
        }
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }

        return fieldList;
    }

    private static ServerData getServerData(SuperviseDataSubmit data, DataSubmitInfo dataSubmitInfo, String clientId) {
        try {
            ServerData putData = new ServerData();
            putData.setType(data.getDataType());
            String objJson = data.getData();
            Map objMap = gson.fromJson(objJson, Map.class);
            SuperviseDataTypeEnum dataTypeEnum = SuperviseDataTypeEnum.getByCode(data.getDataType());
            if (dataTypeEnum == null) {
                throw new RuntimeException(String.format("ServerData获取的type类型是未实现的类型，%s", data.getDataType()));
            }
            //1-监管事件信息、2-事件处置信息、3-事件线索信息、4-协查请求、5-协办结果、6-预警响应信息、7-策略变更信息、8-对象刻画信息
            switch (dataTypeEnum) {
                case ONE:
                    putData.setData(objJson);
                    break;
                case TWO:
                    putData.setData(objMap.get("data"));
                    break;
                case THREE:
                    putData.setData(objMap.get("data"));
                    break;
                case FOUR:
                    putData.setData(objMap.get("data"));
                    putData.setNoticeId(dataSubmitInfo.getNotice_id());
                    putData.setCoFile(gson.toJson(dataSubmitInfo.getCo_file()));
                    break;
                case FIVE:
                    putData.setData(objMap.get("data"));
                    putData.setNoticeId(dataSubmitInfo.getNotice_id());
                    putData.setCoFile(gson.toJson(dataSubmitInfo.getCo_file()));
                    break;
                case SIX:
                    putData.setData(objMap.get("data"));
                    putData.setNoticeId(dataSubmitInfo.getNotice_id());
                    putData.setWarnFile(gson.toJson(dataSubmitInfo.getWarn_file()));
                    break;
                case SEVEN:
                    putData.setData(objMap.get("data"));
                    break;
                case EIGHT:
                    putData.setData(objMap.get("data"));
                    break;
                case RUN_STATE:
                    putData.setData(objMap.get("data"));
                    break;
                default:
                    logger.error(String.format("未匹配的type类型, %s", data.getDataType()));
                    break;
            }
            return putData;
        } catch (Exception e) {
            logger.error("数据转换异常", e);
            return null;
        }
    }

    private static String setClientId(String objJson, String clientId) {
        Map objData = gson.fromJson(objJson, Map.class);
        if (objData.containsKey("alert_info")) {
            Map alertInfo = (Map) objData.get("alert_info");
            if (alertInfo != null && alertInfo.containsKey("alert")) {
                Map alert = (Map) alertInfo.get("alert");
                alert.put("clientId", clientId);
                alertInfo.put("alert", alert);
            }
        }
        return gson.toJson(objData);
    }


    private static Map<String, Object> getPutData(Object _obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        List<Field> declaredFields = getAllFields(_obj);
        for (Field field : declaredFields) {

            boolean annotationPresent = field.isAnnotationPresent(PutField.class);
            if (annotationPresent) {

                PutField annon = field.getAnnotation(PutField.class);
                if (annon != null) {
                    // 获得配置的推送字段名称
                    String fieldname = annon.value();
                    ReflectionUtils.makeAccessible(field);
                    //field.setAccessible(true);//设置data属性为可访问的
                    // 获取属性值
                    Object value = field.get(_obj);
                    if (value == null) {
                        continue;
                    }
                    String classname = field.getType().getName();
                    if (field.getClass().isPrimitive() || classname.startsWith("java.lang.")
                            || "java.util.Date".equals(classname)) {
                        map.put(fieldname, value);
                    } else if (field.getType().isAssignableFrom(List.class)) {
                        Type ctype = getParamterTypeClasszByList(field);

                        if (ctype.getClass().isPrimitive() || ctype.getTypeName().startsWith("java.lang.") || "java.util.Date".equals(ctype.getTypeName())) {
                            map.put(fieldname, value);
                        } else {
                            List<Object> child = new ArrayList<>();
                            for (Object t : (List) value) {
                                child.add(getPutData(t));
                            }
                            map.put(fieldname, child);
                        }
                    } else // 其他自定义类型
                    {
                        map.put(fieldname, getPutData(value));
                    }

                }
            }
        }

        return map;
    }

    private void superviseDataSubmit(Object message) {
        if (message != null) {
            String json = message.toString();
            logger.info("消费到的信息是：" + json);
            if (!StringUtils.isEmpty(json)) {
                DataSubmitInfo dataSubmitInfo = this.getSumbitInfo(json);
                SuperviseDataSubmit superviseDataSubmit = new SuperviseDataSubmit();
                superviseDataSubmit.setGuid(Uuid.uuid());
                superviseDataSubmit.setDataType(dataSubmitInfo.getType());
                superviseDataSubmit.setCreateTime(dataSubmitInfo.getUpdate_time());
                superviseDataSubmit.setData(json);
                superviseDataSubmit.setSubmitStatus(SuperviseDataSubmitStatusEnum.NO_SUBMIT.getCode());
                String serverObj = (String) systemConfigService.getStaticConfig("ServerInfo");
                if (StringUtils.isEmpty(serverObj)) {
                    logger.error("无注册信息!");
                    throw new RuntimeException("无注册信息，请先注册业务平台级联与安全审计平台");
                }
                ServerInfo serverInfo = gson.fromJson(serverObj, ServerInfo.class);
                if (!serverInfo.getIsRegister()) {
                    logger.error("注册失败!");
                    throw new RuntimeException("系统注册失败，请先注册业务平台级联与安全审计平台");
                }

                // 在线注册
                if (serverInfo.getRegisterType() == 1) {
                    OAuth2ClientKey clientKey = superviseService.getClientKey();
                    String clientId = clientKey.getClientId();
                    // 上报数据
                    ServerData serverData = getServerData(superviseDataSubmit, dataSubmitInfo, clientId);
                    if (SuperviseDataTypeEnum.RUN_STATE.getCode().equals(superviseDataSubmit.getDataType())) {
                        // 系统运行状态上报
                        Map map = gson.fromJson(json, Map.class);
                        ServerStatus status = new ServerStatus();
                        try {
                            status.setUpdateTime(DateUtil.parseDate((String) map.get("update_time"), DateUtil.DEFAULT_DATE_PATTERN));
                        } catch (ParseException e) {
                            logger.error("时间转化异常!", e);
                        }
                        status.setSsaRunState(Integer.valueOf((String) map.get("ssa_run_state")));
                        // TODO 上报监管平台状态信息
                        PutServerStatusResult putStatusData = superviseService.reportServerStatus(status);
                        if (putStatusData != null) {
                            SuperviseStatusSubmit superviseStatusSubmit = new SuperviseStatusSubmit();
                            superviseStatusSubmit.setGuid(Uuid.uuid());
                            superviseStatusSubmit.setSubmitTime(new Date());
                            superviseStatusSubmit.setRunState(1);
                            if ("200".equals(putStatusData.getCode())) {
                                superviseStatusSubmit.setSubmitStatus(SuperviseDataSubmitStatusEnum.ONLINE_SUBMIT_SUCCESS.getCode());
                            } else {
                                superviseStatusSubmit.setSubmitStatus(SuperviseDataSubmitStatusEnum.ONLINE_SUBMIT_FAIL.getCode());
                            }
                            superviseStatusSubmitService.save(superviseStatusSubmit);
                        }
                    } else {
                        // TODO 上报监管平台，协查、协办、监管事件、处置信息等上报信息。
                        PutServerDataResult putServerData = superviseService.reportBusinessEventData(serverData);
                        if (putServerData != null) {
                            superviseDataSubmit.setSubmitTime(new Date());
                            if ("200".equals(putServerData.getCode())) {
                                superviseDataSubmit.setSubmitStatus(SuperviseDataSubmitStatusEnum.ONLINE_SUBMIT_SUCCESS.getCode());
                            } else {
                                superviseDataSubmit.setSubmitStatus(SuperviseDataSubmitStatusEnum.ONLINE_SUBMIT_FAIL.getCode());
                            }
                            superviseDataSubmit.setOnlineSubmitResult(gson.toJson(putServerData));
                        }
                        superviseDataSubmitService.save(superviseDataSubmit);
                    }
                }


            } else {
                logger.info("消费json为空！");
            }

        } else {
            logger.info("message信息是null");
        }
    }

    private DataSubmitInfo getSumbitInfo(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        DataSubmitInfo dataSubmitInfo = gson.fromJson(json, DataSubmitInfo.class);
        if (dataSubmitInfo.getData() == null) {
            Map map = gson.fromJson(json, Map.class);
            if (map.containsKey("alert_info")) {
                Map alertInfo = (Map) map.get("alert_info");
                if (alertInfo.containsKey("alert")) {
                    Map alert = (Map) alertInfo.get("alert");
                    dataSubmitInfo.setType(Integer.valueOf(alert.get("type").toString()));
                    try {
                        dataSubmitInfo.setUpdate_time(DateUtil.parseDate((String) alert.get("update_time"), DateUtil.DEFAULT_DATE_PATTERN));
                    } catch (ParseException e) {
                        logger.error("", e);
                    }
                }
            }
        }
        return dataSubmitInfo;
    }

}
