package com.vrv.vap.admin.service.kafka;

import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.service.CollectorDataAccessService;
import com.vrv.vap.admin.util.LogSendUtil;
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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lilang
 * @date 2022/7/6
 * @description 离线数据汇聚监听服务
 */
@Component
public class CommonOfflineDataCollectService {

    private static final Logger log = LoggerFactory.getLogger(CommonOfflineDataCollectService.class);

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;

    @Autowired
    CollectorDataAccessService collectorDataAccessService;
    //标准字段通用接收任务
    private static final String CID = "b9e591b63518ea948e4562103845f4f9";

    @PostConstruct
    public void consumeAll() {

        FutureTask futureTask = new FutureTask<>(() -> {
            String address = System.getenv("LOCAL_SERVER_IP");
            String port = "";
            Optional<CollectorDataAccess> AccessOptional = collectorDataAccessService.findAll().stream().filter(item -> CID.equals(item.getCid())).findFirst();
            if (AccessOptional.isPresent()) {
                CollectorDataAccess collectorDataAccess = AccessOptional.get();
                port = collectorDataAccess.getPort();
            }
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-lx");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaUser +"\" password=\""+ kafkaPwd +"\";");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // 自动提交offset,每1s提交一次
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
            KafkaConsumer consumer = new KafkaConsumer(props);
            Set<String> set = new HashSet<>();
            set.add("offline-data-collect-common");
            consumer.subscribe(set);
            AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
            try {
                while (runing.get()) {
                    ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
                    for (Object record : records) {
                        ConsumerRecord consumerRecord = (ConsumerRecord)record;
                        log.info("消费到的数据：" + consumerRecord.value().toString());
                        String content = consumerRecord.value().toString();
                        LogSendUtil.sendLogByUdp(content,address + ":" + port);
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
}
