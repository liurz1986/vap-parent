package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util;

import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;

import java.util.concurrent.SynchronousQueue;

/**
 * @author 梁国露
 * @date 2021年11月10日 10:10
 */
public class QueueUtil {
    /**
     * 初始化有界队列队列
     */
    private static final SynchronousQueue<WarnResultLogTmpVO> LINKED_BLOCKING_QUEUE = new SynchronousQueue<WarnResultLogTmpVO>();

    public static void put(WarnResultLogTmpVO warnResultLogTmpVO) {
        try {
            LINKED_BLOCKING_QUEUE.put(warnResultLogTmpVO);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static WarnResultLogTmpVO poll() {
        WarnResultLogTmpVO warnResultLogTmpVO = LINKED_BLOCKING_QUEUE.poll();
        return warnResultLogTmpVO;
    }
}
