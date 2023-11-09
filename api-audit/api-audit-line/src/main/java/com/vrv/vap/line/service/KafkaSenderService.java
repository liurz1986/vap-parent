package com.vrv.vap.line.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component
public class KafkaSenderService {
    private static Logger logger = Logger.getLogger(KafkaSenderService.class);

    //@Autowired
    //private KafkaTemplate<String, String> kafkaTemplate;


    @Value("${kafka.bootstrap.servers:192.168.120.201:9092}")
    private String bootstrapServers;

    @Value("${kafka.user:admin}")
    private String userName;

    @Value("${kafka.password:vrv@12345}")
    private String password;


    /**
     * 消息生产者
     */
    public void send(String topic, String data) {
//		ListenableFuture<SendResult<String,String>> listenableFuture = kafkaTemplate.send(topic, data);
//		listenableFuture.addCallback(success -> {},
//                fail -> logger.error("KafkaMessageProducer 发送消息失败！"));

        try {
            KafkaProducer<String, String> producer = buildProducer();
            producer.send(new ProducerRecord<>(topic, data));
            producer.close();

        } catch (Exception e) {
            logger.error("KafkaMessageProducer 发送消息失败！", e);
        }
    }

    public void batchSend(String topic, List<Map<String,Object>> datas) {
//		ListenableFuture<SendResult<String,String>> listenableFuture = kafkaTemplate.send(topic, data);
//		listenableFuture.addCallback(success -> {},
//                fail -> logger.error("KafkaMessageProducer 发送消息失败！"));

        try {
            KafkaProducer<String, String> producer = buildProducer();
            datas.forEach(d ->{
                logger.info("kafka消息发送 topic："+topic+";mesage:"+JSONObject.toJSONString(d));
                producer.send(new ProducerRecord<>(topic, JSONObject.toJSONString(d)));
            });
            producer.close();
        } catch (Exception e) {
            logger.error("KafkaMessageProducer 发送消息失败！", e);
        }
    }

    public KafkaConsumer buildConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-cascade-report");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"" + userName +"\" password=\""+ password +"\";");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 自动提交offset,每1s提交一次
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        KafkaConsumer consumer = new KafkaConsumer(props);
        return consumer;
    }

    public KafkaProducer buildProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + userName
                        + "\" password=\"" + password + "\";");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

}
