package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jcraft.jsch.JSchException;
import com.vrv.vap.xc.config.KafkaConfig;
import com.vrv.vap.xc.mapper.core.KafkaOperateLogMapper;
import com.vrv.vap.xc.mapper.core.KafkaProducerMapper;
import com.vrv.vap.xc.mapper.core.KafkaSubscriberMapper;
import com.vrv.vap.xc.mapper.core.KafkaTopicMapper;
import com.vrv.vap.xc.model.KafkaConsumerInfo;
import com.vrv.vap.xc.pojo.*;
import com.vrv.vap.xc.service.KafkaSubscriberService;
import com.vrv.vap.xc.vo.KafkaProducerQuery;
import com.vrv.vap.xc.vo.KafkaSubscriberQuery;
import com.vrv.vap.xc.vo.KafkaTopicQuery;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.tools.LogAssistTools;
import com.vrv.vap.toolkit.tools.RemoteSSHTools;
import com.vrv.vap.toolkit.tools.ValidateTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class KafkaSubscriberServiceImpl implements KafkaSubscriberService {

    private static final Log log = LogFactory.getLog(KafkaSubscriberServiceImpl.class);

    @Autowired
    private KafkaConfig kafkaConfig;

    @Autowired
    private KafkaTopicMapper kafkaTopicMapper;

    @Autowired
    private KafkaSubscriberMapper kafkaSubscriberMapper;

    @Autowired
    private KafkaOperateLogMapper kafkaOperateLogMapper;

    @Autowired
    private KafkaProducerMapper kafkaProducerMapper;

    @ApiOperation("新建主题")
    @Override
    public VData addTopic(KafkaTopic record) {
        try {
            if (StringUtils.isEmpty(record.getTopic())) {
                return VoBuilder.vd(record, RetMsgEnum.ERROR_PARAM);
            }
            KafkaTopicQuery param = new KafkaTopicQuery();
            param.setTopic(record.getTopic());
            if (selectTopicList(param).getTotal() > 0) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("主题已存在", "5006"));
            }
            RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
//        Session session = RemoteSSHTools.openRemoteSession(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword(), kafkaConfig.isWithoutLogin());
            // bin/kafka-topics.sh --create --topic %topic% --partitions 1 --zookeeper localhost:2181 --replication-factor 1
            String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getAddTopicCmd().replace("%topic%", record.getTopic());
            //ssh连接kafka节点并发送添加acl指令
            String message = sshTools.execute(cmd);
            //添加cmd日志
            addCmdLog(cmd, "添加主题", message, record.getTopic());

            record.setAvaiable(1);
            kafkaTopicMapper.insert(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @ApiOperation("修改主题")
    @Override
    public VData updateTopic(KafkaTopic record) {
        KafkaTopic old = kafkaTopicMapper.selectById(record.getId());
        if (old == null) {
            return VoBuilder.vd(record, RetMsgEnum.EMPTY_RET);
        }
        if (!old.getTopic().equals(record.getTopic())) {
            //不能修改topic
            return VoBuilder.vd(record, RetMsgEnum.ERROR_PARAM);
        }
        if (old != null) {
            //审计变化
            String changes = LogAssistTools.compareDesc(old, record);
            record.setExtendDesc(changes);
        }
        kafkaTopicMapper.updateById(record);
        return VoBuilder.vd(record);
    }

    @ApiOperation("删除主题")
    @Override
    public VData delTopic(KafkaTopic record) {
        try {
            record = kafkaTopicMapper.selectById(record.getId());
            if (record == null) {
                return VoBuilder.vd(record, RetMsgEnum.EMPTY_RET);
            }
            KafkaSubscriberQuery record2 = new KafkaSubscriberQuery();
            record2.setTopic(record.getTopic());
            if (selectSubscriberList(record2).getTotal() > 0) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("该主题有消费者, 请先删除所有消费者", "500015"));
            }
            RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
//        Session session = RemoteSSHTools.openRemoteSession(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword(), kafkaConfig.isWithoutLogin());
            // bin/kafka-topics.sh --delete --topic %topic% --zookeeper localhost:2181
            String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getDelTopicCmd().replace("%topic%", record.getTopic());
            //ssh连接kafka节点并发送指令
            String message = sshTools.execute(cmd);
            //添加cmd日志
            addCmdLog(cmd, "删除主题", message, record.getTopic());

            kafkaTopicMapper.deleteById(record.getId());
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @ApiOperation("查询主题列表")
    @Override
    public VList<KafkaTopic> selectTopicList(KafkaTopicQuery record) {
        /*KafkaTopicExample example = new KafkaTopicExample();
        PageTools.setAll(example, record);
        ExampleTools.buildByExample(record, example).andEquals("avaiable", "topic").andLikes("name");
        long count = kafkaTopicMapper.countByExample(example);
        List<KafkaTopic> list = kafkaTopicMapper.selectByExample(example);*/
        Page<KafkaTopic> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<KafkaTopic> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(kafkaTopicMapper.selectPage(page, queryWrapper));
    }

    @ApiOperation("添加订阅者")
    @Override
    public VData addSubscriber(KafkaSubscriber record) {
        try {
            KafkaSubscriberQuery param = new KafkaSubscriberQuery();
            param.setGroupId(record.getGroupId());
            if (selectSubscriberList(param).getTotal() > 0) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("消费组已存在", "5006"));
            }
            addAcl(record);
            record.setAvaiable(1);
            record.setUpdateTime(new Date());
            kafkaSubscriberMapper.insert(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @ApiOperation("删除订阅者")
    @Override
    public VData delSubscriber(KafkaSubscriber record) {
        try {
            KafkaSubscriber oldSubscriber = kafkaSubscriberMapper.selectById(record.getId());
            delAcl(oldSubscriber);
            kafkaSubscriberMapper.deleteById(record.getId());
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @ApiOperation("修改订阅者")
    @Override
    public VData updateSubscriber(KafkaSubscriber record) {
        try {
            KafkaSubscriber oldSubscriber = kafkaSubscriberMapper.selectById(record.getId());
            if (oldSubscriber == null) {
                return VoBuilder.vd(record, RetMsgEnum.EMPTY_RET);
            }
            KafkaSubscriberQuery param = new KafkaSubscriberQuery();
            param.setGroupId(record.getGroupId());
            if (!oldSubscriber.getGroupId().equals(record.getGroupId()) && selectSubscriberList(param).getTotal() > 0) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("消费组已存在", "5006"));
            }
            if (record.getAvaiable() == 0 && oldSubscriber.getAvaiable() == 1) {
                delAcl(oldSubscriber);
            } else if (record.getAvaiable() == 1) {
                if (oldSubscriber.getAvaiable() == 0) {
                    addAcl(record);
                } else if (!oldSubscriber.getTopic().equals(record.getTopic()) || !oldSubscriber.getIp().equals(record.getIp()) || !oldSubscriber.getGroupId().equals(record.getGroupId())) {
                    if (StringUtils.isEmpty(record.getTopic())) {
                        return VoBuilder.vd(record, RetMsgEnum.ERROR_PARAM);
                    }
                    delAcl(record);
                    addAcl(record);
                }
            }
            record.setUpdateTime(new Date());
            if (oldSubscriber != null) {
                //审计变化
                String changes = LogAssistTools.compareDesc(oldSubscriber, record);
                record.setExtendDesc(changes);
            }
            kafkaSubscriberMapper.updateById(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @ApiOperation("查询订阅者列表")
    @Override
    public VList<KafkaSubscriber> selectSubscriberList(KafkaSubscriberQuery record) {
        /*KafkaSubscriberExample example = new KafkaSubscriberExample();
        PageTools.setAll(example, record);
        ExampleTools.buildByExample(record, example).andEquals("avaiable", "groupId").andLikes("system", "ip", "topic");
        long count = kafkaSubscriberMapper.countByExample(example);
        List<KafkaSubscriber> list = kafkaSubscriberMapper.selectByExample(example);*/
        Page<KafkaSubscriber> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<KafkaSubscriber> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(kafkaSubscriberMapper.selectPage(page, queryWrapper));
    }

    @Override
    public VList<KafkaProducer> selectProducerList(KafkaProducerQuery record) {
/*        KafkaProducerExample example = new KafkaProducerExample();
        PageTools.setAll(example, record);
        ExampleTools.buildByExample(record, example).andEquals("avaiable", "ip", "topic").andLikes("system");
        long count = kafkaProducerMapper.countByExample(example);
        List<KafkaProducer> list = kafkaProducerMapper.selectByExample(example);*/

        Page<KafkaProducer> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<KafkaProducer> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(kafkaProducerMapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData addProducer(KafkaProducer record) {
        try {
            KafkaProducerQuery param = new KafkaProducerQuery();
            param.setTopic(record.getTopic());
            param.setIp(record.getIp());
            param.setMyCount(1);
            if (selectProducerList(param).getTotal() > 0) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("该生产者已存在", "5006"));
            }
            addProducerAcl(record);
            record.setAvaiable(1);
            record.setUpdateTime(new Date());
            kafkaProducerMapper.insert(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @Override
    public VData delProducer(KafkaProducer record) {
        try {
            KafkaProducer oldProducer = kafkaProducerMapper.selectById(record.getId());
            delProAcl(oldProducer);
            kafkaProducerMapper.deleteById(record.getId());
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @Override
    public VData updateProducer(KafkaProducer record) {
        try {
            KafkaProducer oldProducer = kafkaProducerMapper.selectById(record.getId());
            if (oldProducer == null) {
                return VoBuilder.vd(record, RetMsgEnum.EMPTY_RET);
            }
            KafkaProducerQuery param = new KafkaProducerQuery();
            param.setTopic(record.getTopic());
            param.setIp(record.getIp());
            param.setMyCount(1);
            VList<KafkaProducer> selectProducerList = selectProducerList(param);
            if (selectProducerList.getTotal() > 0) {
                if (!selectProducerList.getList().get(0).getId().equals(oldProducer.getId())) {
                    return VoBuilder.vd(record, new ValidateTools.RetMsg("该生产者已存在", "5006"));
                }
            }
            if (record.getAvaiable() == 0 && oldProducer.getAvaiable() == 1) {
                delProAcl(oldProducer);
            } else if (record.getAvaiable() == 1) {
                if (oldProducer.getAvaiable() == 0) {
                    addProducerAcl(record);
                } else if (!oldProducer.getTopic().equals(record.getTopic()) || !oldProducer.getIp().equals(record.getIp())) {
                    if (StringUtils.isEmpty(record.getTopic())) {
                        return VoBuilder.vd(record, RetMsgEnum.ERROR_PARAM);
                    }
                    delProAcl(record);
                    addProducerAcl(record);
                }
            }
            record.setUpdateTime(new Date());
            kafkaProducerMapper.updateById(record);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record);
    }

    @Override
    public VData<KafkaConsumerInfo> getKafkaConsumerInfo(KafkaConsumerInfo record) {
        String host = record.getHost();
        if (StringUtils.isEmpty(record.getGroup())) {
            return VoBuilder.vd(record, RetMsgEnum.EMPTY_RET);
        }
        try {
            RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
            // bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group %group%  --command-config config/sasl.properties
            String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getDescribeGroupCmd().replace("%group%", record.getGroup());
            //ssh连接kafka节点并发送指令
            String message = sshTools.execute(cmd);
            //添加cmd日志
            addCmdLog(cmd, "查询组消费情况", message, record.getTopic());

            String reg = "(GROUP\\s*TOPIC\\s*PARTITION\\s*CURRENT-OFFSET\\s*LOG-END-OFFSET\\s*LAG\\s*CONSUMER-ID\\s*HOST\\s*CLIENT-ID)\\s*(\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*\\s*\\S*)";
            String[] fields = {"group", "topic", "partition", "currentOffset", "logEndOffset", "lag", "consumerId", "host", "clientId"};
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(message);
            if (!matcher.find()) {
                return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
            }
            Map<String, String> dataMap = new HashMap<>();
            String data = matcher.group(2);
            pattern = Pattern.compile("(\\S*)\\s*(\\S*)\\s*(\\S*)\\s*(\\S*)\\s*(\\S*)\\s*(\\S*)\\s*(\\S*)\\s*(\\S*)\\s*(\\S*)");
            Matcher dataMatcher = pattern.matcher(data);
            if (dataMatcher.find()) {
                for (int i = 1; i <= dataMatcher.groupCount(); i++) {
                    String item = dataMatcher.group(i);
                    dataMap.put(fields[i - 1], "-".equals(item) ? null : item);
                }
                try {
                    BeanUtils.populate(record, dataMap);
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            if (record.getHost() != null) {
                host = record.getHost().replace("/", "");
            }
            if (record.getHost() == null || (host != null && record.getHost().indexOf(host) > 0)) {
                record.setHost(host);
            }
        } catch (JSchException e) {
            log.error("", e);
            return VoBuilder.vd(record, RetMsgEnum.SERVER_ERROR);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    protected void addAcl(KafkaSubscriber record) throws JSchException {
        RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
        // bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:%user% --allow-host %ip% --consumer --group %group% --topic %topic%
        String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getAddAclCmd().replace("%user%", kafkaConfig.getDefaultUser()).replace("%ip%", record.getIp())
                .replace("%group%", record.getGroupId()).replace("%topic%", record.getTopic());
        //ssh连接kafka节点并发送添加acl指令
        String message = sshTools.execute(cmd);
        //添加cmd日志
        addCmdLog(cmd, "添加订阅者权限", message, record.getTopic());

    }

    protected void delAcl(KafkaSubscriber record) throws JSchException {
        RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
        //需要从kafka删除订阅(消费)权限
        // bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --remove --allow-principal User:%user% --allow-host %ip% --consumer --group %group% --topic %topic%
        String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getDelAclCmd().replace("%user%", kafkaConfig.getDefaultUser()).replace("%ip%", record.getIp())
                .replace("%group%", record.getGroupId()).replace("%topic%", record.getTopic());
        //ssh连接kafka节点并发送删除acl指令
        String message = sshTools.execute(cmd);
        //添加cmd日志
        addCmdLog(cmd, "删除订阅者权限", message, record.getTopic());
    }

    protected void addProducerAcl(KafkaProducer record) throws JSchException {
        RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
        // bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --add --allow-principal User:%user% --allow-host %ip% --operation Read --operation Write --topic %topic% --force
        String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getAddProAclCmd().replace("%user%", kafkaConfig.getDefaultProUser()).replace("%ip%", record.getIp())
                .replace("%topic%", record.getTopic());
        //ssh连接kafka节点并发送添加acl指令
        String message = sshTools.execute(cmd);
        //添加cmd日志
        addCmdLog(cmd, "添加生产者者权限", message, record.getTopic());

    }

    protected void delProAcl(KafkaProducer record) throws JSchException {
        RemoteSSHTools sshTools = RemoteSSHTools.build(kafkaConfig.getHost(), kafkaConfig.getPort(), kafkaConfig.getUser(), kafkaConfig.getPassword());
        //需要从kafka删除生产者权限
        // bin/kafka-acls.sh --authorizer-properties zookeeper.connect=localhost:2181 --remove --allow-principal User:%user% --allow-host %ip% --operation Read --operation Write --topic %topic% --force
        String cmd = String.format("cd %s;", kafkaConfig.getBaseHome()) + kafkaConfig.getDelProAclCmd().replace("%user%", kafkaConfig.getDefaultProUser()).replace("%ip%", record.getIp())
                .replace("%topic%", record.getTopic());
        //ssh连接kafka节点并发送删除acl指令
        String message = sshTools.execute(cmd);
        //添加cmd日志
        addCmdLog(cmd, "删除生产者者权限", message, record.getTopic());
    }


    protected void addCmdLog(String cmd, String cmdDesc, String message, String topic) {
        KafkaOperateLog operateLog = new KafkaOperateLog();
        operateLog.setCmd(cmd);
        operateLog.setCmdTypeName(cmdDesc);
        operateLog.setKafkaTopic(topic);
        operateLog.setMessage(message);
        operateLog.setOperateTime(new Date());
        try {
            kafkaOperateLogMapper.insert(operateLog);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
