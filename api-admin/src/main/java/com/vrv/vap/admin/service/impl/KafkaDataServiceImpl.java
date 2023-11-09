package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.enums.KafkaMetricsEnum;
import com.vrv.vap.admin.model.KafkaDataInfo;
import com.vrv.vap.admin.service.KafkaDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

/**
 * 获取kafka运行数据
 */
@Service
public class KafkaDataServiceImpl implements KafkaDataService {
    private static Logger logger = LoggerFactory.getLogger(KafkaDataServiceImpl.class);

    @Value("${kafka.jmx.url:127.0.0.1:9999}")
    private String jmxUrl;

    /**
     * 获取kafka运行数据
     *
     * @return kafkaDataInfo
     */
    @Override
    public KafkaDataInfo extractKafkaData() {
        KafkaDataInfo kafkaDataInfo = null;
        try {
            MBeanServerConnection jmxConnection = getMBeanServerConnection(jmxUrl);
            ObjectName messageCountObj = new ObjectName(KafkaMetricsEnum.MESSAGE_IN_PER_SEC.getMetric());
            ObjectName bytesInPerSecObj = new ObjectName(KafkaMetricsEnum.BYTES_IN_PER_SEC.getMetric());
            ObjectName bytesOutPerSecObj = new ObjectName(KafkaMetricsEnum.BYTES_OUT_PER_SEC.getMetric());
//            ObjectName produceRequestsPerSecObj = new ObjectName(KafkaMetricsEnum.PRODUCE_REQUEST_PER_SEC.getMetric());
//            ObjectName consumerRequestsPerSecObj = new ObjectName(KafkaMetricsEnum.CONSUMER_REQUEST_PER_SEC.getMetric());
//            ObjectName flowerRequestsPerSecObj = new ObjectName(KafkaMetricsEnum.FLOWER_REQUEST_PER_SEC.getMetric());
//            ObjectName activeControllerCountObj = new ObjectName(KafkaMetricsEnum.ACTIVE_CONTROLLER_COUNT.getMetric());
            ObjectName partCountObj = new ObjectName(KafkaMetricsEnum.PART_COUNT.getMetric());
            Long messagesInPerSec = (Long) jmxConnection.getAttribute(messageCountObj, "Count");
            Long bytesInPerSec = (Long) jmxConnection.getAttribute(bytesInPerSecObj, "Count");
            Long bytesOutPerSec = (Long) jmxConnection.getAttribute(bytesOutPerSecObj, "Count");
//            Long produceRequestCountPerSec = (Long) jmxConnection.getAttribute(produceRequestsPerSecObj, "Count");
//            Long consumerRequestCountPerSec = (Long) jmxConnection.getAttribute(consumerRequestsPerSecObj, "Count");
//            Long flowerRequestCountPerSec = (Long) jmxConnection.getAttribute(flowerRequestsPerSecObj, "Count");
//            Integer activeControllerCount = (Integer) jmxConnection.getAttribute(activeControllerCountObj, "Value");
            Integer partCount = (Integer) jmxConnection.getAttribute(partCountObj, "Value");
            kafkaDataInfo = new KafkaDataInfo();
            kafkaDataInfo.setMessagesInPerSec(messagesInPerSec);
            kafkaDataInfo.setBytesInPerSec(bytesInPerSec);
            kafkaDataInfo.setBytesOutPerSec(bytesOutPerSec);
//            kafkaDataInfo.setProduceRequestCountPerSec(produceRequestCountPerSec);
//            kafkaDataInfo.setConsumerRequestCountPerSec(consumerRequestCountPerSec);
//            kafkaDataInfo.setFlowerRequestCountPerSec(flowerRequestCountPerSec);
//            kafkaDataInfo.setActiveControllerCount(activeControllerCount);
            kafkaDataInfo.setPartCount(partCount);
        } catch (IOException e) {
            logger.error("IOException", e);
        } catch (MalformedObjectNameException e) {
            logger.error("MalformedObjectNameException", e);
        } catch (AttributeNotFoundException e) {
            logger.error("AttributeNotFoundException", e);
        } catch (MBeanException e) {
            logger.error("MBeanException", e);
        } catch (ReflectionException e) {
            logger.error("ReflectionException", e);
        } catch (InstanceNotFoundException e) {
            logger.error("InstanceNotFoundException", e);
        }
        return kafkaDataInfo;
    }

    /**
     * 获得MBeanServer 的连接
     *
     * @param jmxUrl 地址
     * @return MBeanServerConnection
     * @throws IOException 异常
     */
    private MBeanServerConnection getMBeanServerConnection(String jmxUrl) throws IOException {
        JMXServiceURL url = new JMXServiceURL(jmxUrl);
        JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
        MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
        return mbsc;
    }
}
