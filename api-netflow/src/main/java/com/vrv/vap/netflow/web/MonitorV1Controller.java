package com.vrv.vap.netflow.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.netflow.common.batch.BatchQueue;
import com.vrv.vap.netflow.common.enums.MonitorTypeEnum;
import com.vrv.vap.netflow.common.enums.NetFlowDataTypeEnum;
import com.vrv.vap.netflow.common.enums.NetFlowLogTypeEnum;
import com.vrv.vap.netflow.common.util.LogSendUtil;
import com.vrv.vap.netflow.common.util.Uuid;
import com.vrv.vap.netflow.component.ESManager;
import com.vrv.vap.netflow.mapper.CollectorDataAccessMapper;
import com.vrv.vap.netflow.model.CollectorDataAccess;
import com.vrv.vap.netflow.model.NetworkMonitor;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.model.NetworkMonitorRegAuditLog;
import com.vrv.vap.netflow.service.MonitorLogService;
import com.vrv.vap.netflow.service.NetworkMonitorAuditedService;
import com.vrv.vap.netflow.service.NetworkMonitorRegAuditLogService;
import com.vrv.vap.netflow.service.NetworkMonitorService;
import com.vrv.vap.netflow.service.kafka.KafkaSenderService;
import com.vrv.vap.netflow.vo.MonitorReturnVO;
import com.vrv.vap.netflow.vo.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2021/8/11
 * @description
 */
@RestController
@RequestMapping(path = "/V1")
public class MonitorV1Controller {
    private final Logger logger = LoggerFactory.getLogger(MonitorV1Controller.class);
    private static final String KAFKA_MONITOR_STATUS_TOPIC = "monitor_status_topic";
    @Autowired
    BatchQueue<Map<String, Object>> batchQueue;
    @Autowired
    MonitorLogService monitorLogService;
    @Resource
    CollectorDataAccessMapper collectorDataAccessMapper;
    @Resource
    private KafkaSenderService kafkaSenderService;
    @Resource
    private NetworkMonitorService networkMonitorService;
    @Resource
    private NetworkMonitorAuditedService networkMonitorAuditedService;
    @Resource
    private NetworkMonitorRegAuditLogService networkMonitorRegAuditLogService;

    @Value("${vap.udp-address:127.0.0.1:516}")
    private String udpAddress;

    @PostMapping(path = "/log")
    @ApiOperation("数据上报")
    public Result monitorReportData(@RequestBody List<Map> logs) {
        logger.debug(String.format("监视器数据上报：%s", JSON.toJSONString(logs)));
        try {
            // TODO 多线程解析，采用生产者和消费者模式进行数据的解析和发送
            if (CollectionUtils.isNotEmpty(logs)) {
                for (Map log : logs) {
                    logger.debug(String.format("item打印日志-----> %s", JSONObject.toJSONString(log)));
                    if (log.containsKey("data_type") && NetFlowDataTypeEnum.APPLICATION_AUDIT_LOG.getType().equals((log.get("data_type")))) {
                        // file有一部分从http中解析。 探测器中的log写了两份发到flume中，一份当做file的log_type写入【log_type=99】，一份当做普通的log_type写入【log_type=传入的值】
                        handlerFileLog(log);
                        logger.debug("应用行为审计日志加入到队列中!!!");
                        // 原始数据
                        batchQueue.add(log);
                    }
                    // 通联日志
                    if (log.containsKey("data_type") && NetFlowDataTypeEnum.NET_CONNECT_LOG.getType().equals(log.get("data_type"))) {
                        logger.debug("网络通连日志加入到队列中!!!");
                        batchQueue.add(log);
                    }
                    // 代表的是文件
                    if (log.containsKey("data_type") && (Integer) (log.get("data_type")) == 99) {
                        logger.debug("转发日志：" + JSON.toJSONString(log));
                        String ip = log.get("ip").toString();
                        Example example = new Example(CollectorDataAccess.class);
                        example.createCriteria().andEqualTo("sourceType", 3).andEqualTo("srcIp", ip);
                        List<CollectorDataAccess> list = collectorDataAccessMapper.selectByExample(example);
                        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
                            LogSendUtil.sendLogByUdp(log.get("content").toString(), ip + ":" + list.get(0).getPort());
                        } else {
                            logger.debug("监测器转发接收配置错误，转发ip:" + ip);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            logger.error("数据上报，接收处理报错!", exception);
        }
        return new Result("0", "Success");
    }

    @GetMapping
    @ApiOperation("健康检查")
    public Result healthCheck() {
        return new Result("0", "Success");
    }


    /**
     * @param log map参数
     *            log_type 参见附录《涉密网网络异常行为监测器技术要求》
     *            日志类型	代码 ，枚举：  NetFlowLogTypeEnum
     *            通过http的协议日志，判断file_list 是否为空， 如果不为空，写入到   《 流量-应用文件数据（netflow-app-file-YYYY）》
     *            单独提出来的写。
     *            if (log_type == 99) {
     *            event.getHeaders().put("_TYPE","netflow-app-file");
     *            return event;
     *            }
     */
    private void handlerFileLog(Map log) {
        if (!log.containsKey("log_type") || !(((Integer) log.get("log_type")).equals(NetFlowLogTypeEnum.HTTP_PROTOCOL_TYPE.getType()))) {
            return;
        }
        if (!log.containsKey("file_list") || log.get("file_list") == null) {
            return;
        }
        // http，https文件需要拆分
        Object o = log.get("file_list");
        if (o instanceof List) {
            List<Map> fileList = (List) o;
            fileList.forEach(file -> {
                Map newFileMap = new HashMap();
                copyValue(log, newFileMap, "file_list");
                copyValue(file, newFileMap, "file_list");
                /**
                 * 内部协商  99 ， http关联file文件topic。 通过flume判断log_type的日志判断
                 */
                newFileMap.put("log_type", 99);
                batchQueue.add(newFileMap);
            });
        } else if (o instanceof String) {
            if (StringUtils.isEmpty(o.toString())) {
                logger.debug("解析file_list为空，不进行处理");
            } else {
                logger.debug("解析file_list不为空，需要解析入库! value:{}", o);
            }
        } else {
            if (o != null) {
                logger.debug("转化的类型异常！类型：{}, log: {}", o.getClass(), JSONObject.toJSONString(log));
            }
        }

    }

    private void copyValue(Map source, Map target, String excludes) {
        source.keySet().stream().filter(p -> !excludes.equals(p)).forEach(p -> {
            target.put(p, source.get(p));
        });
    }

    /**
     * 监视器向综合审计监管平台注册
     *
     * @param regInfo 注册信息，包含设备信息、联系人信息、接口信息、cpu信息、磁盘信息
     * @return MonitorReturnVO
     */
    @ApiOperation("注册接口")
    @PostMapping(path = "/register/reg_request")
    public MonitorReturnVO register(@RequestBody Map regInfo) {
        logger.info(String.format("注册接口调用参数：%s", JSON.toJSONString(regInfo)));
        Integer regResult = monitorLogService.register(regInfo);
        MonitorReturnVO result = null;
        if (regResult > 0) {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_SUCCESS.getType()).message(MonitorTypeEnum.TYPE_SUCCESS.getDesc()).build();
        } else if (regResult == -1) {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_UNREGISTERED.getDesc()).build();
        } else {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_FAILED.getDesc()).build();
        }
        return result;
    }


    @ApiOperation("状态信息上传")
    @PostMapping(path = "/status")
    @ResponseBody
    public MonitorReturnVO updateStatus(@RequestBody List<Map> statusInfos) {

        logger.error(String.format("监视器状态信息: %s", JSON.toJSONString(statusInfos)));
        if (CollectionUtils.isEmpty(statusInfos)) {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message("无状态信息").build();
        }
        //监测该监测器是否注册
        String device_id=  statusInfos.get(0).get("device_id").toString();
        NetworkMonitorAudited regAudited = networkMonitorAuditedService.getItem(device_id);
        if (regAudited == null) {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_UNREGISTERED.getDesc()).build();
        }

        AtomicInteger resultNum = new AtomicInteger();
        resultNum.set(0);
        statusInfos.forEach(statusInfo -> {

            try {
                // _index/_type
                ESManager.sendPost("network-monitor-status/_doc", JSON.toJSONString(statusInfo));

                // 获取的状态信息，发送到kafka中
                sendToKafka(statusInfo);

                Integer regResult = monitorLogService.updateStatusNew(statusInfo);
                if (regResult > resultNum.get()) {
                    resultNum.set(regResult);
                }
            } catch (IOException e) {
                String data = String.format("es发送post请求失败，network-monitor-status异常！ %s", JSON.toJSONString(statusInfo));
                logger.error(data, e);
            }
        });


        if (resultNum.get() == 2) {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_UNREGISTERED.getDesc()).build();
        } else if (resultNum.get() == 1) {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_SUCCESS.getType()).message(MonitorTypeEnum.TYPE_SUCCESS.getDesc()).build();
        } else {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_FAILED.getDesc()).build();
        }
    }

    /**
     * 发送设备状态信息到kafka
     *
     * @param statusInfo
     */
    private void sendToKafka(Map statusInfo) {
        try {

            String device_id=  statusInfo.get("device_id").toString();
            NetworkMonitorAudited regAudited = networkMonitorAuditedService.getItem(device_id);

            logger.error(JSON.toJSONString(statusInfo));
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("monitor_run_state", "1");
//            statusMap.put("update_time", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            statusMap.put("update_time", System.currentTimeMillis());

            // 此处应该改为已审批注册信息查询接口
          //  List<NetworkMonitorAudited> monitorList = networkMonitorAuditedService.findByProperty(NetworkMonitorAudited.class, "deviceId", statusInfo.get("device_id").toString());
            if (regAudited != null ) {

                String deviceId = regAudited.getDeviceId();
                statusMap.put("device_id", deviceId);
                statusMap.put("guid", Uuid.uuid());
                statusMap.put("device_belong", regAudited.getDeviceBelong());
                statusMap.put("device_location", regAudited.getDeviceLocation());
                statusMap.put("contact", regAudited.getContact());
                statusMap.put("server_ip", networkMonitorService.getValueFromMapStringByKey("ip", regAudited.getInterfaceInfo()));

                kafkaSenderService.send(KAFKA_MONITOR_STATUS_TOPIC, null, JSONObject.toJSONString(statusMap));
            }
        } catch (Exception e) {
            logger.error("发送kafka的监视器monitor的状态异常", e);
        }
    }


    /**
     * 流量探针调用http请求，，综合审计监管平台进行数据获取
     * 数据进行存储到对应的es中
     *
     * @param statusInfos 状态信息list集合。
     * @return map
     */
    @ApiOperation("状态信息上传")
    @PostMapping(path = "/status_old")
    @ResponseBody
    public MonitorReturnVO updateStatus_old(@RequestBody List<Map> statusInfos) {
        MonitorReturnVO result = null;
        logger.info(String.format("监视器状态信息: %s", JSON.toJSONString(statusInfos)));
        if (CollectionUtils.isEmpty(statusInfos)) {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message("无状态信息").build();
            return result;
        }
        Map statusInfo = statusInfos.get(0);
        try {
            // _index/_type
            ESManager.sendPost("network-monitor-status/_doc", JSON.toJSONString(statusInfo));
        } catch (IOException e) {
            String data = String.format("es发送post请求失败，network-monitor-status异常！ %s", JSON.toJSONString(statusInfo));
            logger.error(data, e);
        }

        // 获取的状态信息，发送到kafka中
        sendToKafka(statusInfo);

        Integer regResult = monitorLogService.


                updateStatus(statusInfo);
        if (regResult == 2) {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_UNREGISTERED.getDesc()).build();
        } else if (regResult == 1) {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_SUCCESS.getType()).message(MonitorTypeEnum.TYPE_SUCCESS.getDesc()).build();
        } else {
            result = MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_FAILED.getType()).message(MonitorTypeEnum.TYPE_FAILED.getDesc()).build();
        }
        return result;
    }


    @PostMapping(path = "/log_extend")
    @ApiOperation("扩展日志上传接口")
    public MonitorReturnVO monitorTargetDeviceData(@RequestBody List<Map> logs) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("扩展日志上传接口：%s", JSON.toJSONString(logs)));
        }
        // TODO 活动对象日志接口用处？
        return new MonitorReturnVO(MonitorTypeEnum.TYPE_SUCCESS.getType(), MonitorTypeEnum.TYPE_SUCCESS.getDesc());
    }

    @ApiOperation("监测器注册（审批）状态查询")
    @PostMapping(path = "/reg_status")
    @ResponseBody
    public MonitorReturnVO getRegStatus(@RequestBody Map map) {

        String device_id = map.get("device_id").toString();

        // 查询历史审批记录

        NetworkMonitorRegAuditLog regLog = networkMonitorRegAuditLogService.getLastItem(device_id);
        if (regLog == null) {
            // 未审核
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_UNREGISTERED.getType()).message(MonitorTypeEnum.TYPE_UNREGISTERED.getDesc()).build();
        }


        // 1、判断是否存在未审核的注册信息

        // 查询历史注册审计记录

        List<NetworkMonitor> monitorList = networkMonitorService.findByProperty(NetworkMonitor.class, "deviceId", device_id);

        monitorList = monitorList.stream()
                .filter(monitor -> monitor.getReportTime().after(regLog.getAuditTime()))
                .collect(Collectors.toList());

        if (monitorList != null && !monitorList.isEmpty()) {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_UNREGISTERED.getType()).message(MonitorTypeEnum.TYPE_UNREGISTERED.getDesc()).build();
        }

        // 判断是否存在审批不通过的注册信息
        if (regLog.getAuditResult().intValue() == 0) {
            return MonitorReturnVO.builder().type(MonitorTypeEnum.TYPE_REG_FAILED.getType()).message(MonitorTypeEnum.TYPE_REG_FAILED.getDesc()).build();
        } else {
            return new MonitorReturnVO(MonitorTypeEnum.TYPE_SUCCESS.getType(), MonitorTypeEnum.TYPE_SUCCESS.getDesc());
        }


    }
}
