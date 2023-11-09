package com.vrv.vap.data.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.data.model.Source;
import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2023/3/22
 * @description 事件、基线消息汇总
 */
@Component
public class SourceChangeConsumerService {

    private static final Logger log = LoggerFactory.getLogger(SourceChangeConsumerService.class);

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Autowired
    SourceService sourceService;

    public static final String VAP_SOURCE_MESSAGE = "vap_source_message";

    public static final String VAP_SOURCE_CHANGE_MESSAGE = "vap_source_change_message";

    public static final String VAP_EVENT_CHANGE_MESSAGE = "vap_event_change_message";

    // 离线导入
    private static final Integer TYPE_OFFLINE = 3;

    @PostConstruct
    public void consumeAll() {
        FutureTask futureTask = new FutureTask<>(() -> {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-ll");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaUser +"\" password=\""+ kafkaPwd +"\";");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // 自动提交offset,每1s提交一次
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
            KafkaConsumer consumer = new KafkaConsumer(props);
            Set<String> set = new HashSet<>();
            set.add(VAP_EVENT_CHANGE_MESSAGE);
            consumer.subscribe(set);
            AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
            try {
                while (runing.get()) {
                    ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
                    for (Object record : records) {
                        ConsumerRecord consumerRecord = (ConsumerRecord)record;
                        ObjectMapper objectMapper = new ObjectMapper();
                        log.info("消费到的数据：" + consumerRecord.value().toString());
                        Map<String,Object> changeMessage = objectMapper.readValue(consumerRecord.value().toString(), Map.class);
                        this.initDefaultStartMessage();
                        this.sendSourceChangeMessage(changeMessage);
                    }
                }
            } catch (Exception e) {
                log.error("consumer error : " + e.getMessage());
            }
            return null;
        });
        Thread thread = new Thread(futureTask);
        thread.start();
    }

    public void sendSourceChangeMessage(Map<String,Object> changeMessage) {
        List<Map> dataList = (List<Map>) changeMessage.get("data");
        Integer type = (Integer) changeMessage.get("type");
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map map : dataList) {
                Integer dataSourceId = (Integer) map.get("dataSourceId");
                if (dataSourceId != null) {
                    Source source = sourceService.findById(dataSourceId);
                    if (source != null) {
                        String index = source.getName();
                        String dataTopicName = source.getTopicName();
                        String dataTopicAlias = source.getTopicAlias();
                        map.put("index",index);
                        map.put("dataTopicName",dataTopicName);
                        map.put("dataTopicAlias",dataTopicAlias);
                        Integer openStatus = (Integer) map.get("open_status");
                        if (openStatus != null && openStatus.equals(0)) {
                            map.put("msg","未开启接收");
                        }
                        String content = JSON.toJSONString(map);
                        redisTemplate.opsForHash().delete(VAP_SOURCE_MESSAGE,dataSourceId + "");
                        redisTemplate.opsForHash().put(VAP_SOURCE_MESSAGE,dataSourceId + "", content);
                    }
                    // 离线导入时，修改数据源为默认开启
                    if (type.equals(TYPE_OFFLINE)) {
                        source.setChangeInform(1);
                        sourceService.updateSelective(source);
                    }
                }
            }
            changeMessage.put("data",dataList);
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                log.info("发送数据源变更消息：" + objectMapper.writeValueAsString(changeMessage));
                kafkaSenderService.send(VAP_SOURCE_CHANGE_MESSAGE,null,objectMapper.writeValueAsString(changeMessage));
            } catch (JsonProcessingException e) {
                log.error("",e);
            }
        }
    }

    public void initDefaultStartMessage() {
        List<Source> sourceList = sourceService.findAll();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(sourceList)) {
            List<Source> sources = sourceList.stream().filter(p -> p.getChangeInform() != null && p.getChangeInform() == 1).collect(Collectors.toList());
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(sources)) {
                for (Source source : sources) {
                    Map map = new HashMap<>();
                    String index = source.getName();
                    String dataTopicName = source.getTopicName();
                    String dataTopicAlias = source.getTopicAlias();
                    map.put("index",index);
                    map.put("dataTopicName",dataTopicName);
                    map.put("dataTopicAlias",dataTopicAlias);
                    map.put("dataSourceId",source.getId());
                    map.put("dataType",1);
                    map.put("open_status",1);
                    map.put("data_status",1);
                    map.put("msg","");
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        redisTemplate.opsForHash().delete(VAP_SOURCE_MESSAGE,source.getId() + "");
                        redisTemplate.opsForHash().put(VAP_SOURCE_MESSAGE,source.getId() + "",objectMapper.writeValueAsString(map));
                    } catch (JsonProcessingException e) {
                        log.error("",e);
                    }
                }
            }
        }
    }
}
