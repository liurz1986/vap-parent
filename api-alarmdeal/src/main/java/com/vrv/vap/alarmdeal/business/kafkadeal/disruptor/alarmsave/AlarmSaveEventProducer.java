package com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.alarmsave;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common.ReformAbsFactory;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common.ReformModel;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common.ReformProducerAbs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: 梁国露
 * @since: 2022/12/15 11:02
 * @description:
 */
@Slf4j
@Service("alarmSaveEventProducer")
public class AlarmSaveEventProducer extends ReformProducerAbs {
    @Override
    public RingBuffer<ReformModel> reflormRingBuffer() {
        int core = 4;
        //单生产者模式，当多个生产者时可以用ProducerType.MULTI
        Disruptor<ReformModel> disruptor = getDisruptor(core);

        //定义消费者
        List<AlarmSaveEventConsumer> arr = new ArrayList<>();
        for(int i=0;i<core;i++){
            AlarmSaveEventConsumer msg = new AlarmSaveEventConsumer();
            arr.add(msg);
        }
        disruptor.handleEventsWithWorkerPool(arr.stream().toArray(AlarmSaveEventConsumer[]::new));

        // 启动disruptor线程
        disruptor.start();

        //获取ringbuffer环，用于接取生产者生产的事件
        RingBuffer<ReformModel> ringBuffer = disruptor.getRingBuffer();

        return ringBuffer;
    }
}
