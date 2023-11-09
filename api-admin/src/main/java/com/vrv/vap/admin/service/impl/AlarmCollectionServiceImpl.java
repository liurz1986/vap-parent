package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.AlarmCollectionMapper;
import com.vrv.vap.admin.mapper.AlarmItemGroupMapper;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.admin.service.AlarmCollectionService;
import com.vrv.vap.admin.vo.AlarmItemGroupVO;
import com.vrv.vap.admin.vo.AlarmItemVO;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * Created by CodeGenerator on 2021/08/04.
 */
@Service
@Transactional
public class AlarmCollectionServiceImpl extends BaseServiceImpl<AlarmItem> implements AlarmCollectionService {
    @Resource
    private AlarmCollectionMapper alarmCollectionMapper;

    @Resource
    private AlarmItemGroupMapper alarmItemGroupMapper;

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;

    private final static Logger log = LoggerFactory.getLogger(AlarmCollectionServiceImpl.class);

    private static final Integer ALARM_STATUS_UNDEAL = 0;

    @Override
    public Page<AlarmItemGroup> getAlarmItemsByGroup(AlarmItemVO alarmItemVO) {
        if (alarmItemVO.getCount_() == 0) {
            alarmItemVO.setCount_(10);
        }
        Page<AlarmItemGroup> alarmItemGroups = new Page<>();
        List<AlarmItemGroup> groupList;
        int total;
        if (alarmItemVO != null && StringUtils.isNotEmpty(alarmItemVO.getAlarmType())) {
            Example example = new Example(AlarmItemGroup.class);
            example.createCriteria().andEqualTo("alarmType",alarmItemVO.getAlarmType());
            groupList = alarmItemGroupMapper.selectByExample(example);
            total = groupList.size();
        } else {
            groupList = alarmItemGroupMapper.selectAll();
            total = groupList.size();
            groupList = groupList.subList(alarmItemVO.getStart_(),groupList.size() > alarmItemVO.getStart_() + alarmItemVO.getCount_() ? alarmItemVO.getStart_() + alarmItemVO.getCount_() : groupList.size());
        }
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
                itemGroup.setAlarmStatus(ALARM_STATUS_UNDEAL);
            }
            alarmItemGroups.addAll(groupList);
            alarmItemGroups.setTotal(total);
        }
        return alarmItemGroups;
    }

    @Override
    public boolean updateAlarmItems(AlarmItemGroupVO alarmItemGroupVO) {
        Boolean result = alarmCollectionMapper.updateAlarmItems(alarmItemGroupVO) > 0;
        if (result) {
            this.deleteDealedGroup(alarmItemGroupVO);
        }
        return result;
    }

    public void deleteDealedGroup(Object object) {
        AlarmItemVO alarmItemVO = new AlarmItemVO();
        BeanUtils.copyProperties(object,alarmItemVO);
        AlarmItemGroup alarmItemGroup = alarmCollectionMapper.getAlarmItemsByGroup(alarmItemVO);
        if (alarmItemGroup.getAlarmCount() == 0) {
            Example example = new Example(AlarmItemGroup.class);
            example.createCriteria().andEqualTo("alarmType",alarmItemVO.getAlarmType())
                    .andEqualTo("alarmLevel",alarmItemVO.getAlarmLevel())
                    .andEqualTo("alarmSource",alarmItemVO.getAlarmSource())
                    .andEqualTo("alarmDesc",alarmItemVO.getAlarmDesc());
            alarmItemGroupMapper.deleteByExample(example);
        }
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

    @PostConstruct
    public void consume() {
        FutureTask futureTask = new FutureTask<>(() -> {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group");
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
            props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + kafkaUser +"\" password=\""+ kafkaPwd +"\";");
            props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            // 自动提交offset,每1s提交一次
            props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
            KafkaConsumer consumer = new KafkaConsumer(props);
            consumer.subscribe(Collections.singleton("alarm-item-collection"));
            AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
            try {
                while (runing.get()) {
                    Thread.sleep(100L);
                    ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
                    for (Object record : records) {
                        ConsumerRecord consumerRecord = (ConsumerRecord)record;
                        ObjectMapper objectMapper = new ObjectMapper();
                        AlarmItem alarmItem = objectMapper.readValue(consumerRecord.value().toString(), AlarmItem.class);
                        if (this.save(alarmItem) != 1) {
                            log.info("告警数据:" + record.toString());
                            log.error("告警数据入库失败");
                        }
                    }
                }
            } catch (Exception e) {
                log.error("consumer error : " + e.getMessage());
            } finally {
                consumer.close();
            }
            return null;
        });
        Thread thread = new Thread(futureTask);
        thread.start();
    }
}
