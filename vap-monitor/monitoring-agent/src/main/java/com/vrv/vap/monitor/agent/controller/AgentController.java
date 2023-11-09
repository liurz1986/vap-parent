package com.vrv.vap.monitor.agent.controller;

import com.vrv.flume.cmd.FlumeTools;
import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.manager.BeatManager;
import com.vrv.vap.monitor.agent.manager.MonitorManager;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.ElasticMonitorTask;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.common.model.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/agent")
@Api(value = "代理Agent接收命令", tags = "代理Agent接收命令")
@Slf4j
public class AgentController  {
    @Resource
    BeatManager beatManager;
    @Resource
    MonitorManager monitorManager;
    @Resource
    ServerManager serverManager;
    @Resource
    RestTemplate restTemplate;


    //接收指令信息
    @PostMapping("/cmd")
    public Result agentCommand(@RequestBody CommandInfo commandInfo) {
        log.debug("[AGENT-COMMAND] command info :{}", JsonUtil.objToJson(commandInfo));
        switch (commandInfo.getCommandType()) {
            case BEAT:
                //主动获取心跳
                serverManager.sendBeat(beatManager.getBeatInfo());
                break;
            case CONFIG:
                //设置config，重启监控
                List<MonitorConfig> monitorConfigList = JsonUtil.jsonToEntityList(commandInfo.getCommandBody(), MonitorConfig.class);
                monitorManager.updateConfig(monitorConfigList,commandInfo.getOpen());
                break;
            case SHELL:
                //执行SHELL，暂时关闭
                break;
            case CUSTOM:
                //自定义，暂时关闭
                break;
            case SEND_LOG:
                //下载日志，异步下载，先将文件同步至SERVER
                try {
                    LogSendInfo logSendInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), LogSendInfo.class);
                    return monitorManager.sendLog(logSendInfo);
                }catch (Exception ex){
                    ex.printStackTrace();
                    log.error("下载组件日志失败，失败消息：{}",ex);
                    return Result.builder().code("-1").msg("下载组件日志失败").data(ex).build();
                }
            case RESTART:
                //重启服务，异步执行，无返回结果
                RestartInfo restartInfo = JsonUtil.jsonToEntity(commandInfo.getCommandBody(), RestartInfo.class);
                monitorManager.restartService(restartInfo);
            default:

                break;
        }


        return Result.builder().code("0").build();

    }
//    //接收指令信息
//    @PostMapping("/test")
//    public ServerStateInfo test() {
////        setServerManager(serverManager);
//        pushMetric(new MetricInfo());
//        return null;
//
//    }
    //接收指令信息
    @PostMapping("/test")
    public Object test() {
        KafkaProducer kafkaProducer = buildProducer();
        try {
            kafkaProducer.send(new ProducerRecord("test-connection", "success"),(recordMetadata, e)->{

            });
        } catch (Exception e) {
            log.error("kafka生产异常",e);
            kafkaProducer.close();
        }
        KafkaConsumer kafkaConsumer = buildConsumer();
        ConsumerRecords consumerRes=null;
        try {
            kafkaConsumer.subscribe(Collections.singleton("test-connection"));
             consumerRes = kafkaConsumer.poll(Duration.ofSeconds(1));
            if (consumerRes.isEmpty()) {
                System.out.println(consumerRes);
            }
        } catch (Exception e) {
            log.error("kafka消费异常", e);

        } finally {
            kafkaConsumer.close();
        }
        return consumerRes.isEmpty();
    }
    private KafkaProducer buildProducer() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.120.201:9092");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 10000);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 15000);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 1024 * 1000);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule " +
                "required username=\"" + "admin" + "\" password=\"" + "admin-2019" + "\";");
        KafkaProducer producer = new KafkaProducer<String, String>(props);
        return producer;
    }

    private KafkaConsumer buildConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.120.201:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-1");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule " +
                "required username=\"" + "admin" + "\" password=\"" + "admin-2019" + "\";");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
        KafkaConsumer consumer = new KafkaConsumer(props);
        return consumer;
    }
    //接收指令信息
    @PostMapping("/getbean")
    public Object getbean() {
//        setServerManager(serverManager);
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        Object es = applicationContext.getBean("es");
        Class<?> es1 = applicationContext.getType("es");
        System.out.println(es1== ElasticMonitorTask.class);
        System.out.println(es1);
        System.out.println(es);
        return es;

    }
    @PostMapping("cidStop")
    //@SysRequestLog(description="告警阈值修改", actionType = ActionType.UPDATE)
    @ApiOperation(value = "cid任务关闭", notes = "")
    public Result cidStop(String cid){
        String workDir = System.getenv("VAP_WORK_DIR");
        FlumeTools flumeTools = new FlumeTools(workDir + "/flume/flume");
        flumeTools.stop(cid,6000);
        return Result.builder().code("0").build();

    }
}
