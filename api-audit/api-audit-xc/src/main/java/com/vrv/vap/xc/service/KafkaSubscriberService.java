package com.vrv.vap.xc.service;

import com.vrv.vap.xc.model.KafkaConsumerInfo;
import com.vrv.vap.xc.pojo.KafkaProducer;
import com.vrv.vap.xc.pojo.KafkaSubscriber;
import com.vrv.vap.xc.pojo.KafkaTopic;
import com.vrv.vap.xc.vo.KafkaProducerQuery;
import com.vrv.vap.xc.vo.KafkaSubscriberQuery;
import com.vrv.vap.xc.vo.KafkaTopicQuery;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestBody;

public interface KafkaSubscriberService {

    @ApiOperation("新建主题")
    VData addTopic(@RequestBody KafkaTopic record);

    @ApiOperation("修改主题")
    VData updateTopic(@RequestBody KafkaTopic record);

    @ApiOperation("删除主题")
    VData delTopic(@RequestBody KafkaTopic record);

    @ApiOperation("查询主题列表")
    VList selectTopicList(@RequestBody KafkaTopicQuery record);

    @ApiOperation("添加订阅者")
    VData addSubscriber(@RequestBody KafkaSubscriber record);

    @ApiOperation("删除订阅者")
    VData delSubscriber(@RequestBody KafkaSubscriber record);

    @ApiOperation("修改订阅者")
    VData updateSubscriber(@RequestBody KafkaSubscriber record);

    @ApiOperation("查询订阅者列表")
    VList selectSubscriberList(@RequestBody KafkaSubscriberQuery record);

    VList<KafkaProducer> selectProducerList(KafkaProducerQuery record);

    VData addProducer(KafkaProducer record);

    VData delProducer(KafkaProducer record);

    VData updateProducer(KafkaProducer record);

    @ApiOperation("查询组的消费信息")
    VData<KafkaConsumerInfo> getKafkaConsumerInfo(KafkaConsumerInfo record);
}
