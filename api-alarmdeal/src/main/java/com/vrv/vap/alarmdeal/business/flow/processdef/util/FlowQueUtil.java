package com.vrv.vap.alarmdeal.business.flow.processdef.util;

import com.vrv.vap.alarmdeal.business.flow.core.vo.FlowMessageVO;

import java.util.concurrent.SynchronousQueue;

/**
 * 队列工具类
 * 1. 流程创建、节点审批、流程结束消息
 */
public class FlowQueUtil {
    /**
     * 流程创建和结束消息
     */
    private static final SynchronousQueue<FlowMessageVO> flowMessageQue = new SynchronousQueue<FlowMessageVO>();



    public static void flowMessagePut(FlowMessageVO data) throws InterruptedException {
        flowMessageQue.put(data);
    }

    public static FlowMessageVO flowMessageTake() {
        FlowMessageVO data = null;
        try {
            data = flowMessageQue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}
