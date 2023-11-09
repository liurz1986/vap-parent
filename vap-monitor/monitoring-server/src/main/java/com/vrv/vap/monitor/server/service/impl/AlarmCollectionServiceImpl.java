package com.vrv.vap.monitor.server.service.impl;

import com.github.pagehelper.Page;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.monitor.server.common.util.TimeTools;
import com.vrv.vap.monitor.server.mapper.AlarmCollectionMapper;
import com.vrv.vap.monitor.server.model.AlarmItem;
import com.vrv.vap.monitor.server.model.AlarmItemGroup;
import com.vrv.vap.monitor.server.service.AlarmCollectionService;
import com.vrv.vap.monitor.server.service.AlarmItemGroupService;
import com.vrv.vap.monitor.server.vo.AlarmItemGroupVO;
import com.vrv.vap.monitor.server.vo.AlarmItemVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
public class AlarmCollectionServiceImpl extends BaseServiceImpl<AlarmItem> implements AlarmCollectionService {
    @Resource
    AlarmCollectionMapper alarmCollectionMapper;

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;

    private Map<String, String> alarmTypeDicMap;

    @Autowired
    private AlarmItemGroupService alarmItemGroupService;

    private final static Logger log = LoggerFactory.getLogger(AlarmCollectionServiceImpl.class);

    @Override
    public Page<AlarmItemGroup> getAlarmItemsByGroup(AlarmItemVO alarmItemVO) {
        if (alarmItemVO.getCount_() == 0) {
            alarmItemVO.setCount_(10);
        }
        List<AlarmItemGroup> groupList = alarmItemGroupService.findAll();
        if (CollectionUtils.isNotEmpty(groupList)) {
            for (AlarmItemGroup itemGroup : groupList) {
                alarmItemVO.setAlarmType(itemGroup.getAlarmType());
                alarmItemVO.setAlarmLevel(itemGroup.getAlarmLevel());
                alarmItemVO.setAlarmSource(itemGroup.getAlarmSource());
                alarmItemVO.setAlarmDesc(itemGroup.getAlarmDesc());
                AlarmItemGroup alarmItemGroup = alarmCollectionMapper.getAlarmItemsByGroup(alarmItemVO);
                itemGroup.setAlarmCount(alarmItemGroup.getAlarmCount());
                itemGroup.setFirstTime(alarmItemGroup.getFirstTime());
                itemGroup.setLastTime(alarmItemGroup.getLastTime());
            }
        }
        groupList = groupList.stream().filter(item -> item.getAlarmCount() > 0).collect(Collectors.toList());
        Page<AlarmItemGroup> alarmItemGroups = new Page<>();
        alarmItemGroups.addAll(groupList.subList(alarmItemVO.getStart_(),groupList.size() > alarmItemVO.getStart_() + alarmItemVO.getCount_() ? alarmItemVO.getStart_() + alarmItemVO.getCount_() : groupList.size()));
        alarmItemGroups.setTotal(groupList.size());
        return alarmItemGroups;
    }

    @Override
    public boolean updateAlarmItems(AlarmItemGroupVO alarmItemGroupVO) {
        if (StringUtils.isEmpty(alarmItemGroupVO.getIds())) {
            return false;
        }
        return alarmCollectionMapper.updateAlarmItems(alarmItemGroupVO) > 0;
    }

    @Override
    public List<Map> getAlarmTrend(AlarmItemVO alarmItemVO) {
        String _startTime = alarmItemVO.getStartTime();
        Date startTime = TimeTools.parseDate(_startTime,TimeTools.GMT_PTN);
        String _endTime = alarmItemVO.getEndTime();
        Date endTime = TimeTools.parseDate(_endTime,TimeTools.GMT_PTN);
        String formatType;
        int days = TimeTools.getDays(startTime,endTime);
        if (days == 1) {
            formatType = "1";
        } else {
            formatType = "2";
        }
        alarmItemVO.setFormatType(formatType);
        return alarmCollectionMapper.getAlarmTrend(alarmItemVO);
    }

    @Override
    public void pushAlarm(String alarmType, String desc) {
        AlarmItem alarmItem = new AlarmItem();
        alarmItem.setAlarmType(alarmType);
        alarmItem.setAlarmLevel(1);
        alarmItem.setAlarmStatus(0);
        alarmItem.setAlarmSource(System.getenv("LOCAL_SERVER_IP"));
        alarmItem.setAlarmDesc(desc);
        alarmItem.setAlarmTime(new Date());
        this.save(alarmItem);
        AlarmItemGroup alarmItemGroup = new AlarmItemGroup();
        alarmItemGroup.setAlarmType(alarmItem.getAlarmType());
        alarmItemGroup.setAlarmLevel(alarmItem.getAlarmLevel());
        alarmItemGroup.setAlarmSource(alarmItem.getAlarmSource());
        alarmItemGroup.setAlarmDesc(alarmItem.getAlarmDesc());
        AlarmItemGroup group = alarmItemGroupService.findOne(alarmItemGroup);
        if (group == null) {
            alarmItemGroupService.save(group);
        }
    }

    @Override
    public void pushAlarmToKafka(String alarmType, String desc) {

    }

//    @Override
//    public void pushAlarmToKafka(String alarmType, String desc) {
//        AlarmItem alarmItem = new AlarmItem();
//        alarmItem.setAlarmType(alarmType);
//        alarmItem.setAlarmLevel(1);
//        alarmItem.setAlarmStatus(0);
//        alarmItem.setAlarmSource(System.getenv("LOCAL_SERVER_IP"));
//        alarmItem.setAlarmDesc(desc);
//        alarmItem.setAlarmTime(new Date());
//        KafkaProducer kafkaProducer = buildProducer();
//        log.info("===kafkaServer==="+kafkaServer);
//        log.info("===kafkaUser==="+kafkaUser);
//        log.info("===kafkaPwd==="+kafkaPwd);
//        try {
//            kafkaProducer.send(new ProducerRecord("alarm-item-collection", JSON.toJSONString(alarmItem)),(recordMetadata, e)->{
//                if (e != null) {
//                    e.printStackTrace();
//                }
//            });
//            log.info("kafka消息发送成功"+alarmType);
//        } catch (Exception e) {
//            log.error("kafka生产异常",e);
//            e.printStackTrace();
//        } finally {
//            kafkaProducer.close();
//        }
//
//    }
//    private KafkaProducer buildProducer() {
//        Properties props = new Properties();
//        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
//        props.put(ProducerConfig.RETRIES_CONFIG, 0);
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10000);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, 15000);
//        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024*1000);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
//        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
//        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule " +
//                "required username=\"" + kafkaUser + "\" password=\"" + kafkaPwd + "\";");
//        KafkaProducer producer = new KafkaProducer<String, String>(props);
//        return producer;
//    }
//    @PostConstruct
//    public void consume() {
//        FutureTask futureTask = new FutureTask<>(() -> {
//            Properties props = new Properties();
//            log.info("===kafkaServer==="+kafkaServer);
//            log.info("===kafkaUser==="+kafkaUser);
//            log.info("===kafkaPwd==="+kafkaPwd);
//            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
//            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
//            props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
//            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
//            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
//            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaUser +"\" password=\""+ kafkaPwd +"\";");
//            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
//            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//            // 自动提交offset,每1s提交一次
//            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
//            KafkaConsumer consumer = new KafkaConsumer(props);
//            consumer.subscribe(Collections.singleton("alarm-item-collection"));
//            AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
//            try {
//                while (runing.get()) {
//                    Thread.sleep(100L);
//                    ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
//                    for (Object record : records) {
//                        ConsumerRecord consumerRecord = (ConsumerRecord)record;
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        AlarmItem alarmItem = objectMapper.readValue(consumerRecord.value().toString(), AlarmItem.class);
//                        log.info("alarmItem存入数据库"+alarmItem);
//                        if (this.save(alarmItem) != 1) {
//                            log.info("告警数据:" + record.toString());
//                            log.error("告警数据入库失败");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.error("consumer error : " + e.getMessage());
//            } finally {
//                consumer.close();
//            }
//            return null;
//        });
//        Thread thread = new Thread(futureTask);
//        thread.start();
//    }
}
