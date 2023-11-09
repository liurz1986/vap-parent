package com.vrv.vap.admin.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vrv.vap.admin.common.enums.UserEnum;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BaseOrgIpSegment;
import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BaseOrgIpSegmentService;
import com.vrv.vap.admin.service.BasePersonZjgService;
import com.vrv.vap.admin.vo.SyncBaseDataVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author lilang
 * @date 2022/4/13
 * @description 同步外部系统人员、组织机构等基础数据
 */
@Component
public class BaseDataConsumerService {

    private static final Logger log = LoggerFactory.getLogger(BaseDataConsumerService.class);

    @Value("${kafka.bootstrap.servers:127.0.0.1:9092}")
    private String kafkaServer;

    @Value("${kafka.userName:admin}")
    private String kafkaUser;

    @Value("${kafka.password:vrv@12345}")
    private String kafkaPwd;

    @Value("${vap.zjg.syncBaseData.org.root:1}")
    private String rootCode;

    private static final String DATATYPE_PERSON = "person";

    private static final String DATATYPE_ORG = "org";

    private static final String SECADM = "secadm";

    private static final String SYSADM = "sysadm";

    private static final String AUDITADM = "auditadm";

    private static final String SECRETMGR = "secretmgr";

    private static final String BUSINESSMGR = "businessmgr";

    private static final String OPERATIONMGR = "operationmgr";

    @Autowired
    BasePersonZjgService basePersonZjgService;

    @Autowired
    BaseKoalOrgService baseKoalOrgService;

    @Autowired
    BaseOrgIpSegmentService baseOrgIpSegmentService;

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
            set.add("sync-base-data-person");
            set.add("sync-base-data-org");
            consumer.subscribe(set);
            AtomicBoolean runing = new AtomicBoolean(Boolean.TRUE);
            try {
                while (runing.get()) {
                    ConsumerRecords records = consumer.poll(Duration.ofMillis(1000L));
                    for (Object record : records) {
                        ConsumerRecord consumerRecord = (ConsumerRecord)record;
                        ObjectMapper objectMapper = new ObjectMapper();
                        log.info("消费到的数据：" + consumerRecord.value().toString());
                        SyncBaseDataVO syncBaseDataVO = objectMapper.readValue(consumerRecord.value().toString(), SyncBaseDataVO.class);
                        syncBaseData(syncBaseDataVO);
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

    public boolean syncBaseData(SyncBaseDataVO syncBaseDataVO) {
        String dataType = syncBaseDataVO.getDataType();
        if (DATATYPE_PERSON.equals(dataType)) {
            syncPersonData(syncBaseDataVO);
        }
        if (DATATYPE_ORG.equals(dataType)) {
            syncOrgData(syncBaseDataVO);
        }
        return true;
    }

    public void syncPersonData(SyncBaseDataVO syncBaseDataVO) {
        int result = 0;
        Map data = syncBaseDataVO.getData();
        if (data == null) {
            log.info("人员数据为空");
            return;
        }
        try {
            // 系统没有该uid则新增，有则修改
            BasePersonZjg personZjg = mapToPerson(data);
            Example example = new Example(BasePersonZjg.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("uid",personZjg.getUid());
            criteria.andEqualTo("source",personZjg.getSource());
            example.and(criteria);
            List<BasePersonZjg> personZjgList = basePersonZjgService.findByExample(example);
            if (CollectionUtils.isEmpty(personZjgList)) {
                result = basePersonZjgService.save(personZjg);
            } else {
                BasePersonZjg basePersonZjg = personZjgList.get(0);
                personZjg.setId(basePersonZjg.getId());
                result = basePersonZjgService.updateSelective(personZjg);
            }
        } catch (Exception e) {
            log.error("",e);
        }
        if (result == 0) {
            log.info("人员数据保存失败：" + new Gson().toJson(data).toString());
        }
    }

    public void syncOrgData(SyncBaseDataVO syncBaseDataVO) {
        List<BaseKoalOrg> orList = baseKoalOrgService.findAll();
        if (CollectionUtils.isNotEmpty(orList)) {
            long count = orList.stream().filter(item -> (item.getDataSourceType() != null && item.getDataSourceType() == 2)).count();
            if (count == 0) {
                baseKoalOrgService.deleteAllOrg();
            }
        }
        int orgResult = 0;
        Map data = syncBaseDataVO.getData();
        if (data == null) {
            log.info("机构数据为空");
            return;
        }
        try {
            BaseKoalOrg baseKoalOrg = mapToOrg(data);
            List<BaseOrgIpSegment> ipSegmentList = mapToOrgIpSegment(data);
            Example example = new Example(BaseKoalOrg.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("uid",baseKoalOrg.getUid());
            criteria.andEqualTo("source",baseKoalOrg.getSource());
            example.and(criteria);
            List<BaseKoalOrg> orgList = baseKoalOrgService.findByExample(example);
            if (CollectionUtils.isEmpty(orgList)) {
                orgResult = baseKoalOrgService.save(baseKoalOrg);
                if (CollectionUtils.isNotEmpty(ipSegmentList)) {
                    Integer orgIpResult = baseOrgIpSegmentService.save(ipSegmentList);
                    if (orgIpResult == 0) {
                        log.info("组织机构IP保存失败,组织机构编码为：" + baseKoalOrg.getCode());
                    }
                }
            } else {
                BaseKoalOrg koalOrg = orgList.get(0);
                baseKoalOrg.setUuId(koalOrg.getUuId());
                orgResult = baseKoalOrgService.updateSelective(baseKoalOrg);
                List<BaseOrgIpSegment> segments = baseOrgIpSegmentService.findByProperty(BaseOrgIpSegment.class,"areaCode",koalOrg.getCode());
                if (CollectionUtils.isNotEmpty(segments)) {
                    segments.stream().forEach(item -> baseOrgIpSegmentService.deleteById(item.getId()));
                }
                if (CollectionUtils.isNotEmpty(ipSegmentList)) {
                    Integer orgIpResult = baseOrgIpSegmentService.save(ipSegmentList);
                    if (orgIpResult == 0) {
                        log.info("组织机构IP保存失败,组织机构编码为：" + baseKoalOrg.getCode());
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }
        if (orgResult == 0) {
            log.info("组织机构数据保存失败：" + new Gson().toJson(data).toString());
        }
    }

    public BasePersonZjg mapToPerson(Map map) {
        BasePersonZjg personZjg = new BasePersonZjg();
        personZjg.setUid((String) map.get("syncUid"));
        personZjg.setSource((String) map.get("syncSource"));
        personZjg.setUserNo((String) map.get("userNo"));
        personZjg.setUserName((String) map.get("userName"));
        personZjg.setUserIdnEx(map.get("userIdnEx") != null ? (String) map.get("userIdnEx") : "000000000000000000");
        personZjg.setPersonType((String) map.get("personType"));
        personZjg.setPersonRank((String) map.get("personRank"));
        personZjg.setSecretLevel(map.get("secretLevel") != null ? Integer.valueOf((String) map.get("secretLevel")) : 3);
        personZjg.setOrgCode((String) map.get("orgCode"));
        personZjg.setOrgName((String) map.get("orgName"));
        personZjg.setCreateTime(new Date());
        personZjg.setDataSourceType((Integer) map.get("dataSourceType"));
        String originAccount = (String) map.get("originAccount");
        personZjg.setOriginAccount(originAccount);
        if (UserEnum.SECADM.getUserCode().equals(originAccount) || UserEnum.SYSADM.getUserCode().equals(originAccount) || UserEnum.AUDITADM.getUserCode().equals(originAccount)
                || UserEnum.SECRETMGR.getUserCode().equals(originAccount) || UserEnum.BUSINESSMGR.getUserCode().equals(originAccount) || UserEnum.OPERATIONMGR.getUserCode().equals(originAccount)) {
            personZjg.setPersonType("2");
            personZjg.setPersonRank(getPersonRank(originAccount));
        } else {
            personZjg.setPersonType("1");
            personZjg.setPersonRank("内部业务人员");
        }
        return personZjg;
    }

    private String getPersonRank(String account) {
        String personRank = "";
        switch (account) {
            case SECADM:
                personRank = UserEnum.SECADM.getUserName();
                break;
            case SYSADM:
                personRank = UserEnum.SYSADM.getUserName();
                break;
            case AUDITADM:
                personRank = UserEnum.AUDITADM.getUserName();
                break;
            case SECRETMGR:
                personRank = UserEnum.SECRETMGR.getUserName();
                break;
            case BUSINESSMGR:
                personRank = UserEnum.BUSINESSMGR.getUserName();
                break;
            case OPERATIONMGR:
                personRank = UserEnum.OPERATIONMGR.getUserName();
                break;
            default:
        }
        return personRank;
    }

    public BaseKoalOrg mapToOrg(Map map) {
        BaseKoalOrg baseKoalOrg = new BaseKoalOrg();
        baseKoalOrg.setUid((String) map.get("syncUid"));
        baseKoalOrg.setSource((String) map.get("syncSource"));
        String code = (String) map.get("code");
        String parentCode = (String) map.get("parentCode");
        baseKoalOrg.setCode(code);
        if (StringUtils.isNotEmpty(code) && code.equals(rootCode)) {
            baseKoalOrg.setParentCode("JG");
            baseKoalOrg.setType("1");
        } else {
            baseKoalOrg.setParentCode(parentCode);
            baseKoalOrg.setType("2");
        }
        baseKoalOrg.setName((String) map.get("name"));
        baseKoalOrg.setStatus("0");
        baseKoalOrg.setShortName(baseKoalOrg.getName());
        baseKoalOrg.setOtherName(baseKoalOrg.getName());
        baseKoalOrg.setUpdatetime(new Date());
        baseKoalOrg.setOrghierarchy(Byte.valueOf("0"));
        baseKoalOrgService.generateSubCode(baseKoalOrg);
        baseKoalOrg.setSort((Integer) map.get("sort"));
        baseKoalOrg.setSecretLevel(map.get("secretLevel") != null ? Integer.valueOf((String) map.get("secretLevel")) : 1);
        baseKoalOrg.setProtectionLevel(map.get("protectionLevel") != null ? Integer.valueOf((String) map.get("protectionLevel")) : 1);
        baseKoalOrg.setSecretQualifications(map.get("secretQualifications") != null ? Integer.valueOf((String) map.get("secretQualifications")) : 1);
        baseKoalOrg.setOrgType(map.get("orgType") != null ? Integer.valueOf((String) map.get("orgType")) : 1);
        baseKoalOrg.setDataSourceType((Integer) map.get("dataSourceType"));
        return baseKoalOrg;
    }

    public List<BaseOrgIpSegment> mapToOrgIpSegment(Map map) {
        List<BaseOrgIpSegment> ipSegmentList = new ArrayList<>();
        List<Map> segmentList = (List<Map>) map.get("ipSegment");
        if (CollectionUtils.isNotEmpty(segmentList)) {
            for (Map segment : segmentList) {
                BaseOrgIpSegment ipSegment = new BaseOrgIpSegment();
                ipSegment.setAreaCode((String) map.get("code"));
                ipSegment.setAreaName((String) map.get("name"));
                String startIp = (String) segment.get("startIp");
                String endIp = (String) segment.get("endIp");
                ipSegment.setStartIpSegment(startIp);
                ipSegment.setEndIpSegment(endIp);
                if (StringUtils.isNotEmpty(startIp)) {
                    ipSegment.setStartIpNum(IPUtils.ip2int(startIp));
                }
                if (StringUtils.isNotEmpty(endIp)) {
                    ipSegment.setEndIpNum(IPUtils.ip2int(endIp));
                }
                if (StringUtils.isNotEmpty(startIp) || StringUtils.isNotEmpty(endIp)) {
                    ipSegmentList.add(ipSegment);
                }
            }
        }
        return ipSegmentList;
    }
}
