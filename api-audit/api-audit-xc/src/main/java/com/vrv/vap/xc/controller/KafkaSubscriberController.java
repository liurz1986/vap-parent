package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.xc.model.DeleteModel;
import com.vrv.vap.xc.model.KafkaConsumerInfo;
import com.vrv.vap.xc.pojo.KafkaProducer;
import com.vrv.vap.xc.pojo.KafkaSubscriber;
import com.vrv.vap.xc.pojo.KafkaTopic;
import com.vrv.vap.xc.service.KafkaSubscriberService;
import com.vrv.vap.xc.vo.KafkaProducerQuery;
import com.vrv.vap.xc.vo.KafkaSubscriberQuery;
import com.vrv.vap.xc.vo.KafkaTopicQuery;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
public class KafkaSubscriberController {

    @Autowired
    private KafkaSubscriberService kafkaSubscriberService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @ResponseBody
    @PutMapping("kafka/topic")
    @ApiOperation("新建主题")
    public VData addTopic(@RequestBody KafkaTopic record) {
        return kafkaSubscriberService.addTopic(record);
    }

    @ResponseBody
    @PatchMapping("kafka/topic")
    @ApiOperation(value = "修改主题", notes = "仅支持修改topic以外的值")
    public VData updateTopic(@RequestBody KafkaTopic record) {
        return kafkaSubscriberService.updateTopic(record);
    }

    @ResponseBody
    @DeleteMapping("kafka/topic")
    @ApiOperation("删除主题")
    public VData delTopic(@RequestBody DeleteModel record) {
        KafkaTopic record2 = new KafkaTopic();
        record2.setId(record.getIntegerId());
        return kafkaSubscriberService.delTopic(record2);
    }

    @ResponseBody
    @PostMapping("kafka/topic")
    @ApiOperation("查询主题列表")
    public VList<KafkaTopic> selectTopicList(@RequestBody KafkaTopicQuery record) {
        return kafkaSubscriberService.selectTopicList(record);
    }

    @ResponseBody
    @PutMapping("kafka/subscriber")
    @ApiOperation("添加订阅者")
    public VData addSubscriber(@RequestBody KafkaSubscriber record) {
        return kafkaSubscriberService.addSubscriber(record);
    }

    @ResponseBody
    @PatchMapping("kafka/subscriber")
    @ApiOperation("修改订阅者")
    public VData updateSubscriber(@RequestBody KafkaSubscriber record) {
        return kafkaSubscriberService.updateSubscriber(record);
    }

    @ResponseBody
    @DeleteMapping("kafka/subscriber")
    @ApiOperation("删除订阅者")
    public VData delSubscriber(@RequestBody DeleteModel record) {
        KafkaSubscriber record2 = new KafkaSubscriber();
        record2.setId(record.getIntegerId());
        return kafkaSubscriberService.delSubscriber(record2);
    }

    @ResponseBody
    @PostMapping("kafka/subscriber")
    @ApiOperation("查询订阅者列表")
    public VList<KafkaSubscriber> selectSubscriberList(@RequestBody KafkaSubscriberQuery record) {
        return kafkaSubscriberService.selectSubscriberList(record);
    }

    @ResponseBody
    @PutMapping("kafka/producer")
    @ApiOperation("添加生产者")
    public VData addProducer(@RequestBody KafkaProducer record) {
        return kafkaSubscriberService.addProducer(record);
    }

    @ResponseBody
    @PatchMapping("kafka/producer")
    @ApiOperation("修改生产者")
    public VData updateProducer(@RequestBody KafkaProducer record) {
        return kafkaSubscriberService.updateProducer(record);
    }

    @ResponseBody
    @DeleteMapping("kafka/producer")
    @ApiOperation("删除生产者")
    public VData delProducer(@RequestBody DeleteModel record) {
        KafkaProducer record2 = new KafkaProducer();
        record2.setId(record.getIntegerId());
        return kafkaSubscriberService.delProducer(record2);
    }

    @ResponseBody
    @PostMapping("kafka/producer")
    @ApiOperation("查询生产者列表")
    public VList<KafkaProducer> selectSubscriberList(@RequestBody KafkaProducerQuery record) {
        return kafkaSubscriberService.selectProducerList(record);
    }

    @ResponseBody
    @PostMapping("kafka/consumer/info")
    @ApiOperation("查询消费信息")
    public VData<KafkaConsumerInfo> getKafkaConsumerInfo(@RequestBody KafkaConsumerInfo record) {
        return kafkaSubscriberService.getKafkaConsumerInfo(record);
    }

}
