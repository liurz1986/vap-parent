package com.vrv.vap.admin.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BasePersonZjgService;
import com.vrv.vap.admin.vo.BaseKoalOrgVO;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lilang
 * @date 2022/6/13
 * @description 监听基础数据同步完成消息，更新缓存
 */
@Component
public class BaseDataCompleteConsumerService {

    private static final Logger log = LoggerFactory.getLogger(BaseDataCompleteConsumerService.class);

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;

    @Resource
    BasePersonZjgService basePersonZjgService;

    @Resource
    BaseKoalOrgService baseKoalOrgService;

    @Autowired
    Cache<String, List<BaseKoalOrgVO>> koalOrgCache;

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
            set.add("vap_base_data_complete_message");
            consumer.subscribe(set);
            AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
            try {
                while (runing.get()) {
                    ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
                    for (Object record : records) {
                        ConsumerRecord consumerRecord = (ConsumerRecord)record;
                        ObjectMapper objectMapper = new ObjectMapper();
                        log.info("消费到的数据：" + consumerRecord.value().toString());
                        Map<String,Object> map = objectMapper.readValue(consumerRecord.value().toString(),Map.class);
                        this.updateCache(map);
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

    private void updateCache(Map<String,Object> map) {
        if (map != null) {
            String item = (String) map.get("item");
            if ("person".equals(item)) {
                basePersonZjgService.cachePerson();
                basePersonZjgService.sendChangeMessage();
            }
            if ("org".equals(item)) {
                koalOrgCache.clear();
                baseKoalOrgService.cacheOrg();
                baseKoalOrgService.sendChangeMessage();
            }
        }
    }
}
