package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.KafkaDataInfo;
import com.vrv.vap.admin.service.HardwareService;
import com.vrv.vap.admin.service.KafkaDataService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取kafka运行数据
 */
@RestController
@RequestMapping("/monitor")
@Api(value = "获取kafka运行数据")
public class KafkaDataController extends ApiController {
    @Autowired
    private KafkaDataService kafkaDataService;

    @Autowired
    private HardwareService hardwareService;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    /**
     * 获取kafka运行数据
     *
     * @return Result
     */
    @Ignore
    @PostMapping("/getKafkaData")
    @SysRequestLog(description="获取kafka运行数据", actionType = ActionType.SELECT)
    @ApiOperation(value = "获取kafka运行数据", notes = "")
    public Result getKafkaData() {
        Map<String, Object> dataMap = new HashMap<>();
        List<KafkaDataInfo> dataList = new ArrayList<>();
        KafkaDataInfo kafkaDataInfo = kafkaDataService.extractKafkaData();
        if (kafkaDataInfo == null || !hardwareService.checkServiceStatus("zookeeper")) {
            dataMap.put("status", 1);
        } else {
            dataMap.put("status", 0);
            dataList.add(kafkaDataInfo);
        }

        dataMap.put("extends", dataList);
        dataMap.put("name", "kafka");
        return this.vData(dataMap);
    }

    @GetMapping("/sendKafkaData")
    @ApiOperation(value = "数据发送测试")
    public Result sendKafkaData() {
//        String personData = "{\"dataType\":\"person\",\"data\":{\"syncUid\":\"110\",\"syncSource\":\"bxy\",\"syncBatchEnd\":false,\"syncBatchNo\":\"110\",\"userNo\":\"110\",\"userName\":\"李明\",\"userIdnEx\":\"420704199811120001\",\"personType\":\"2\",\"personRank\":\"内部人员\",\"secretLevel\":\"3\",\"orgCode\":\"JG000008\",\"orgName\":\"江汉区保密办\"}}";
        String orgData = "{\"dataType\":\"org\",\"data\":{\"syncUid\":\"110\",\"syncSource\":\"bxy\",\"syncBatchEnd\":false,\"syncBatchNo\":\"1110\",\"code\":\"1\",\"name\":\"北信源汉口保密办\",\"type\":\"2\",\"parentCode\":\"000000000000\",\"secretLevel\":\"1\",\"protectionLevel\":\"1\",\"secretQualifications\":\"1\",\"orgType\":\"1\",\"ipSegment\":[{\"startIp\":\"192.168.1.1\",\"endIp\":\"192.168.1.3\"}]}}";
//        String content = "{\"dev_ip\":\"139.211.222.235\",\"dev_id\":\"8a:51:3a:aa:a7:bc\",\"event_time\":\"2022-07-07 14:26:33\",\"username\":\"wz\",\"auth_ip\":\"139.211.222.235\",\"op_result\":\"0\",\"authuser_name\":\"admin\",\"app_name\":\"主审\",\"op_type\":\"0\",\"report_log_type\":\"DT022\"}";
//        kafkaSenderService.send("sync-base-data-person",null,personData);
        kafkaSenderService.send("sync-base-data-org",null,orgData);
//        kafkaSenderService.send("offline-data-collect-common",null,content);
        return this.vData(true);
    }
}
