package com.vrv.vap.admin.web;

import com.vrv.vap.admin.vo.KafkaInfoVo;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xuda
 * @date 2021/4/2
 * @description kafka连接测试
 */
@RequestMapping(path = "/cascade")
@RestController
public class KafkaConnectTestController extends ApiController {
    // 测试连接结果
    private AtomicBoolean result = new AtomicBoolean();

    @ApiOperation("测试kafka连接")
    @PostMapping(path = "/test/connect")
    public Result testConect(@RequestBody KafkaInfoVo kafkaInfoVo) {
        result.set(true);
        Producer<String, String> producer = null;
        try {
            Properties props = new Properties();
            props.put("bootstrap.servers", kafkaInfoVo.getIp() + ":" + kafkaInfoVo.getPort());
            props.put("acks", "1");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaInfoVo.getUserName()
                            + "\" password=\"" + kafkaInfoVo.getPassWord() + "\";");
            producer = new KafkaProducer<String, String>(props);

            ProducerRecord record = new ProducerRecord<>("test", "key", "message");
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null){
                        result.set(false);
                    }
                }
            });
        } catch (Exception e) {
            result.set(false);
        } finally {
            if (producer != null) {
                producer.close();
            }
        }

        if (result.get()) {
            return this.result(true);
        }
        return new Result("-1", "测试连接失败");
    }

}
