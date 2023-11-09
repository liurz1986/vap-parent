package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.manager.ESClient;
import com.vrv.vap.monitor.agent.manager.MonitorManager;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.enums.AlarmTypeEnum;
import com.vrv.vap.monitor.common.model.MetricInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

@Slf4j
public class KafkaMonitorTask extends MonitorBaseTask {
    private BaseProperties baseProperties;
    ServerManager serverManager;


    private String kafkaServer;

    private String kafkaUser;

    private String kafkaPwd;

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        serverManager=applicationContext.getBean(ServerManager.class);
        baseProperties=applicationContext.getBean(BaseProperties.class);
        setServerManager(serverManager);
        //获取组件配置信息
        Map<String,MonitorConfig> monitorConfigMap=(Map<String,MonitorConfig>) jobDataMap.get("monitorConfig");
        MonitorConfig monitorConfig = monitorConfigMap.get("kafka");
        //获取组件名
        String name = monitorConfig.getName();
        log.debug("开始监控组件任务：{}",name);
        //获取组件连接信息
        Map<String, Object> connectConfig = monitorConfig.getConnectConfig();
        //本机ip
        String localIp = baseProperties.getLocalIp();
        MetricInfo metricInfo = buildBaseMetric(monitorConfig,localIp);
        metricInfo.setStatus(1);
        //连接配置
        Map<String, Object> kafka = (Map<String, Object>) connectConfig.get("kafka");
        Map<String, String> bootstrap = (Map<String, String>) kafka.get("bootstrap");
        kafkaServer = bootstrap.get("servers");
        kafkaUser = kafka.get("userName").toString();
        kafkaPwd = kafka.get("password").toString();
        KafkaProducer kafkaProducer = buildProducer();
        //查询kafka状态
        if (!ServiceCheckUtil.checkServiceStatus("kafka")) {
            metricInfo.setStatus(0);
            if (monitorConfig.getAlarm()) {
                pushAlarm(AlarmTypeEnum.ALARM_KAFKA_LINK.getCode(), AlarmTypeEnum.ALARM_KAFKA_LINK.getDesc(), localIp, name);
            }
            if (monitorConfig.getHandler()) {
                Boolean dealResult = dealKafkaAlarm("kafka", localIp,monitorConfig);
                metricInfo.setStatus(dealResult?1:0);
            }
        }
        //kafka生产测试
        try {
            kafkaProducer.send(new ProducerRecord("test-connection", "success"), (recordMetadata, e) -> {
                log.info("===========" + e);
                if (e != null) {
                    metricInfo.setStatus(0);
                    if (monitorConfig.getAlarm()) {
                        pushAlarm(AlarmTypeEnum.ALARM_KAFKA_PRODUCE.getCode(), AlarmTypeEnum.ALARM_KAFKA_PRODUCE.getDesc(), localIp, name);
                    }
                    if (monitorConfig.getHandler()) {
                        Boolean dealResult = dealKafkaAlarm("kafka", localIp,monitorConfig);
                        metricInfo.setStatus(dealResult?1:0);
                    }
                }
            });
        } catch (Exception e) {
            metricInfo.setStatus(0);
            log.error("kafka生产异常", e);
            if (monitorConfig.getAlarm()) {
                pushAlarm(AlarmTypeEnum.ALARM_KAFKA_PRODUCE.getCode(), AlarmTypeEnum.ALARM_KAFKA_PRODUCE.getDesc(), localIp, name);
            }
            if (monitorConfig.getHandler()) {
                Boolean dealResult = dealKafkaAlarm("kafka", localIp,monitorConfig);
                metricInfo.setStatus(dealResult?1:0);
            }
        } finally {
            kafkaProducer.close();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        KafkaConsumer kafkaConsumer = buildConsumer();
        try {
            kafkaConsumer.subscribe(Collections.singleton("test-connection"));
            ConsumerRecords consumerRes = kafkaConsumer.poll(Duration.ofSeconds(1));
            if (consumerRes.isEmpty()) {
                metricInfo.setStatus(0);
                if (monitorConfig.getAlarm()) {
                    pushAlarm(AlarmTypeEnum.ALARM_KAFKA_CONSUME.getCode(), AlarmTypeEnum.ALARM_KAFKA_CONSUME.getDesc(), localIp, name);
                }
                if (monitorConfig.getHandler()) {
                    Boolean dealResult = dealKafkaAlarm("kafka", localIp,monitorConfig);
                    metricInfo.setStatus(dealResult?1:0);
                }
            }
        } catch (Exception e) {
            metricInfo.setStatus(0);
            log.error("kafka消费异常", e);
            if (monitorConfig.getAlarm()) {
                pushAlarm(AlarmTypeEnum.ALARM_KAFKA_CONSUME.getCode(), AlarmTypeEnum.ALARM_KAFKA_CONSUME.getDesc(), localIp, name);
            }
            if (monitorConfig.getHandler()) {
                Boolean dealResult = dealKafkaAlarm("kafka", localIp,monitorConfig);
                metricInfo.setStatus(dealResult?1:0);
            }
        } finally {
            kafkaConsumer.close();
        }
        log.info("组件：{},状态：{}",monitorConfig.getName(),metricInfo.getStatus()==1?"正常":"异常");
        pushMetric(metricInfo);

    }

    private Boolean dealKafkaAlarm(String kafka, String localIp,MonitorConfig config) {
        boolean status = false;
        //查看进程是否存在
        boolean b = ServiceCheckUtil.checkServiceStatus(kafka);
        if (!b){
            //发送尝试处理的信息
            pushHandler(AlarmTypeEnum.ALARM_KAFKA_DEAL.getCode(), AlarmTypeEnum.ALARM_KAFKA_DEAL.getDesc(), localIp, "kafka",0);
            ServiceCheckUtil.restartService(kafka);
            try {
                Thread.sleep(40*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ServiceCheckUtil.checkServiceStatus(kafka)){
                KafkaProducer kafkaProducer = buildProducer();
                try {
                    kafkaProducer.send(new ProducerRecord("test-connection", "success"),(recordMetadata, e)->{
                        if (e != null) {
                            pushHandler(AlarmTypeEnum.ALARM_KAFKA_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_KAFKA_RESULT.getDesc(),"失败"),localIp, "kafka",0);
                        }
                    });
                } catch (Exception e) {
                    log.error("kafka生产异常",e);
                    pushHandler(AlarmTypeEnum.ALARM_KAFKA_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_KAFKA_RESULT.getDesc(),"失败"),localIp, "kafka",0);
                } finally {
                    kafkaProducer.close();
                }
                if(checkRestartStatus(config)){
                    pushHandler(AlarmTypeEnum.ALARM_KAFKA_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_KAFKA_RESULT.getDesc(),"成功"),localIp, "kafka",1);
                    status=true;
                }else {
                    pushHandler(AlarmTypeEnum.ALARM_KAFKA_RESULT.getCode(), String.format(AlarmTypeEnum.ALARM_KAFKA_RESULT.getDesc(),"失败"),localIp, "kafka",0);
                }

            }
        }
        return status;
    }

    @Override
    public void run(String jobName) {
       run(jobName,null);
    }

    private KafkaProducer buildProducer() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10000);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 15000);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024 * 1000);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule " +
                "required username=\"" + kafkaUser + "\" password=\"" + kafkaPwd + "\";");
        KafkaProducer producer = new KafkaProducer<String, String>(props);
        return producer;
    }

    private KafkaConsumer buildConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-1");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule " +
                "required username=\"" + kafkaUser + "\" password=\"" + kafkaPwd + "\";");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        KafkaConsumer consumer = new KafkaConsumer(props);
        return consumer;
    }



    @Override
    public Boolean restart(MonitorConfig config) {
        try {
            ServiceCheckUtil.restartService(config.getName());
        }catch (Exception exception){
            exception.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {

        try {
            Thread.sleep(1000*90);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //连接配置
        Map<String, Object> kafka = (Map<String, Object>) config.getConnectConfig().get("kafka");
        Map<String, String> bootstrap = (Map<String, String>) kafka.get("bootstrap");
        kafkaServer = bootstrap.get("servers");
        kafkaUser = kafka.get("userName").toString();
        kafkaPwd = kafka.get("password").toString();
        KafkaConsumer kafkaConsumer = buildConsumer();
        try {
            kafkaConsumer.subscribe(Collections.singleton("test-connection"));
            ConsumerRecords consumerRes = kafkaConsumer.poll(Duration.ofSeconds(1));
            if (consumerRes.isEmpty()) {
                return false;
            }else {
                return true;
            }
        } catch (Exception e) {
            log.error("kafka消费异常",e);
            return false;
        } finally {
            kafkaConsumer.close();
        }
    }



}
