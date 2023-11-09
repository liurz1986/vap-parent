package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.common.enums.KafkaMetricsEnum;
import com.vrv.vap.admin.common.manager.ESClient;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.ShellExecuteScript;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.model.EsServerStatusModel;
import com.vrv.vap.admin.model.KafkaDataInfo;
import com.vrv.vap.admin.service.StatusService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2020/10/28
 * @description
 */
@Service
public class StatusServiceImpl implements StatusService {

    private static final Logger log = LoggerFactory.getLogger(StatusServiceImpl.class);

    @Value("${kafka.jmx.url:127.0.0.1:19999}")
    private String jmxUrl;

    @Value("${pushUrl:https://127.0.0.1:8780/push/user}")
    private String pushUrl;

    private static final Integer STATUS_OK = 0;

    private static final Integer STATUS_ERROR = 1;

    @Override
    public Map<String, Object> getEsClusterInfo() {
        Map<String,Object> result = new HashMap<>();
        result.put("name","elasticsearch");
        result.put("status",STATUS_ERROR);
        result.put("extends",new ArrayList<>());
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        // 获取集群健康度
        Optional<String> health = wrapper.lowLevelResponseValue("status", "_cluster/health");
        if (!health.isPresent()) {
            log.info("未查询到健康状态，请检查es连接配置！");
            return result;
        }
        Integer healthStatus = "yellow".equals(health.orElse("none")) || "green".equals(health.orElse("none")) ? STATUS_OK : STATUS_ERROR;
        // 获取master节点id
        Optional<String> masterId = wrapper.lowLevelResponseValue("master_node", "_cluster/state/master_node");
        // 获取集群名称
        Optional<String> clusterName = wrapper.lowLevelResponseValue("cluster_name", "_cluster/health");
        // 查询集群状态
        Optional<JSONObject> nodes = wrapper.lowLevelResponseValue("nodes", "_nodes/stats/os,jvm,fs,process");
        if (!nodes.isPresent()) {
            log.info("未查询到节点，请检查es连接配置！");
            return result;
        }
        final double gb = 1024 * 1024 * 1024;
        List<EsServerStatusModel> list = nodes.get().entrySet().stream().map(e -> {
            JSONObject jsonObject = (JSONObject) e.getValue();

            String ip = wrapper.getJSONObjectValue(jsonObject, "host");
            String name = wrapper.getJSONObjectValue(jsonObject, "name");
//            int maxFiles = wrapper.getJSONObjectValue(jsonObject, "process:max_file_descriptors");
//            int openFiles = wrapper.getJSONObjectValue(jsonObject, "process:open_file_descriptors");
//            int cpu = wrapper.getJSONObjectValue(jsonObject, "process:cpu:percent");

            double jvmAll = Long.valueOf(wrapper.getJSONObjectValue(jsonObject, "jvm:mem:heap_max_in_bytes").toString())  / gb;
            double jvmUsed = wrapper.getJSONObjectValue(jsonObject, "jvm:mem:heap_used_in_bytes", Long.class) / gb;
            double osAll = Long.valueOf(wrapper.getJSONObjectValue(jsonObject, "os:mem:total_in_bytes").toString()) / gb;
            double osUsed = Long.valueOf(wrapper.getJSONObjectValue(jsonObject, "os:mem:used_in_bytes").toString()) / gb;
            double fsAll = Long.valueOf(wrapper.getJSONObjectValue(jsonObject, "fs:total:total_in_bytes").toString()) / gb;
            double fsFree = Long.valueOf(wrapper.getJSONObjectValue(jsonObject, "fs:total:free_in_bytes").toString()) / gb;

            EsServerStatusModel serverStatusModel = new EsServerStatusModel();
            serverStatusModel.setIp(ip);
            serverStatusModel.setHost(name);
//            serverStatusModel.setMaxFiles(maxFiles);
//            serverStatusModel.setOpenFiles(openFiles);
//            serverStatusModel.setCpuPercent(cpu);
            serverStatusModel.setJvmMemAll(jvmAll);
            serverStatusModel.setJvmMemUsed(jvmUsed);
            serverStatusModel.setOsMemAll(osAll);
            serverStatusModel.setOsMemUsed(osUsed);
            serverStatusModel.setDiskAll(fsAll);
            serverStatusModel.setDiskUsed(fsAll - fsFree);
            serverStatusModel.setStatus(health.orElse("none"));
            // 判断是否为master节点
            serverStatusModel.setMaster(e.getKey().equals(masterId.orElse("")));
            serverStatusModel.setAlive(true);
            return serverStatusModel;
        }).collect(Collectors.toList());

        // 将未获取到节点信息的ip状态设为离线
        ESClient client = SpringContextUtil.getApplicationContext().getBean(ESClient.class);
        List<EsServerStatusModel> list2 = Arrays.asList(client.getIPS()).stream().filter(e -> {
            return !list.stream().anyMatch(e2 -> {
                return e2.getIp().equals(e) || e2.getHost().equals(e);
            });
        }).map(e -> {
            EsServerStatusModel serverStatusModel = new EsServerStatusModel();
            serverStatusModel.setIp(e);
            serverStatusModel.setAlive(false);
            return serverStatusModel;
        }).collect(Collectors.toList());

        list.addAll(list2);

        result.put("name",clusterName.get());
        result.put("status",healthStatus);
        result.put("extends",list);
        return result;
    }

    @Override
    public Map<String,Object> getLogStashInfo() {
        Map<String,Object> map = new HashMap<>();
        map.put("status",STATUS_ERROR);
        String cmd = "systemctl status datacollect";
        log.info("将要执行命令："+cmd);
        //String result = CmdExecute.executeCmd(cmd);
        List<String> queryExecuteCmd = ShellExecuteScript.querySuccessExecuteCmd(cmd);
        for(String result : queryExecuteCmd) {
            if (StringUtils.isNoneEmpty(result)&&(result.contains("PID:")||result.contains("pid:")|| result.contains("active (running)"))) {
                map.put("status",STATUS_OK);
            }
        }
        map.put("name","LogStash");
        map.put("extends",new ArrayList<>());
        return map;
    }

    /**
     * 获取kafka运行数据
     *
     * @return kafkaDataInfo
     */
    @Override
    public Map<String,Object> extractKafkaData() {
        KafkaDataInfo kafkaDataInfo = null;
        try {
            MBeanServerConnection jmxConnection = getMBeanServerConnection(jmxUrl);
            ObjectName messageCountObj = new ObjectName(KafkaMetricsEnum.MESSAGE_IN_PER_SEC.getMetric());
            ObjectName bytesInPerSecObj = new ObjectName(KafkaMetricsEnum.BYTES_IN_PER_SEC.getMetric());
            ObjectName bytesOutPerSecObj = new ObjectName(KafkaMetricsEnum.BYTES_OUT_PER_SEC.getMetric());
            ObjectName partCountObj = new ObjectName(KafkaMetricsEnum.PART_COUNT.getMetric());
            Long messagesInPerSec = (Long) jmxConnection.getAttribute(messageCountObj, "Count");
            Long bytesInPerSec = (Long) jmxConnection.getAttribute(bytesInPerSecObj, "Count");
            Long bytesOutPerSec = (Long) jmxConnection.getAttribute(bytesOutPerSecObj, "Count");
            Integer partCount = (Integer) jmxConnection.getAttribute(partCountObj, "Value");
            kafkaDataInfo = new KafkaDataInfo();
            kafkaDataInfo.setMessagesInPerSec(messagesInPerSec);
            kafkaDataInfo.setBytesInPerSec(bytesInPerSec);
            kafkaDataInfo.setBytesOutPerSec(bytesOutPerSec);
            kafkaDataInfo.setPartCount(partCount);
        } catch (IOException e) {
            log.error("IOException", e);
        } catch (MalformedObjectNameException e) {
            log.error("MalformedObjectNameException", e);
        } catch (AttributeNotFoundException e) {
            log.error("AttributeNotFoundException", e);
        } catch (MBeanException e) {
            log.error("MBeanException", e);
        } catch (ReflectionException e) {
            log.error("ReflectionException", e);
        } catch (InstanceNotFoundException e) {
            log.error("InstanceNotFoundException", e);
        }

        Map<String, Object> dataMap = new HashMap<>();
        List<KafkaDataInfo> dataList = new ArrayList<>();
        if (kafkaDataInfo == null) {
            dataMap.put("status", 1);
        } else {
            dataMap.put("status", 0);
            dataList.add(kafkaDataInfo);
        }

        dataMap.put("extends", dataList);
        dataMap.put("name", "kafka");
        return dataMap;
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

    @Override
    public String getPushUrl() {
        return pushUrl;
    }
}
