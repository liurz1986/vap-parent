package com.vrv.vap.alarmdeal.business.asset.datasync.controller;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.BaseSynchKafkaVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Random;

/**
 * 模拟手动发kafka消息
 */
@RestController
@RequestMapping("/assetSync")
public class AssetSyncController {
    private static Logger logger = LoggerFactory.getLogger(AssetSyncController.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 模拟发kafka消息--asset
     * @param assetKafkaVO
     * @return
     */
    @PostMapping(value="/testKafka")
    public Result<String> testKafka(@RequestBody BaseSynchKafkaVO assetKafkaVO){
        kafkaTemplate.send("sync-base-data-asset", JSON.toJSONString(assetKafkaVO));
        return ResultUtil.success("success");
    }

    /**
     * 模拟发kafka消息--file
     * @param assetKafkaVO
     * @return
     */
    @PostMapping(value="/testKafkaFile")
    public Result<String> testKafkaFile(@RequestBody BaseSynchKafkaVO assetKafkaVO){
        kafkaTemplate.send("sync-base-data-file", JSON.toJSONString(assetKafkaVO));
        return ResultUtil.success("success");
    }

}
