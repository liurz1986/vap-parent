package com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.common;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: 梁国露
 * @since: 2023/4/10 18:43
 * @description:
 */
public abstract class ReformProducerAbs <T extends ReformModel> {

    private static Logger log  = LoggerFactory.getLogger(ReformProducerAbs.class);

    public Disruptor<ReformModel> getDisruptor(int core){
        //定义用于事件处理的线程池， Disruptor通过java.util.concurrent.ExecutorSerivce提供的线程池来触发consumer的事件处理。
        ThreadPoolExecutor executor = new ThreadPoolExecutor(core,core+2,5, TimeUnit.MICROSECONDS,new LinkedBlockingDeque<>(100));

        //指定事件工厂
        ReformAbsFactory factory = new ReformAbsFactory();

        //指定ringbuffer字节大小，必须为2的N次方（能将求模运算转为位运算提高效率），否则将影响效率
        int bufferSize = 1024 * 1024;
        Disruptor<ReformModel> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.SINGLE, new BlockingWaitStrategy());
        return disruptor;
    }

    protected RingBuffer<T> reflormRingBuffer() {

        return null;
    }

    public void send(T t) {
        RingBuffer<T> reflormRingBuffer = reflormRingBuffer();
        //获取下一个Event槽的下标
        long sequence = reflormRingBuffer.next();
        try {
            //给Event填充数据
            T event = reflormRingBuffer.get(sequence);
            event.setMessage(t);
            log.info("往消息队列中添加消息：{}", event);
        } catch (Exception e) {
            log.error("failed to add event to messageModelRingBuffer for : e = {},{}",e,e.getMessage());
        } finally {
            //发布Event，激活消费者去消费，将sequence传递给消费者
            //注意最后的publish方法必须放在finally中以确保必须得到调用；如果某个请求的sequence未被提交将会堵塞后续的发布操作或者其他的producer
            reflormRingBuffer.publish(sequence);
        }
    }
}
