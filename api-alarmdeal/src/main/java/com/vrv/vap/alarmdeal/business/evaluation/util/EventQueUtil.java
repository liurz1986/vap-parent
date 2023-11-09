package com.vrv.vap.alarmdeal.business.evaluation.util;

import com.vrv.vap.alarmdeal.business.evaluation.vo.EventResultVO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;

/**
 * 事件已处置消息队列
 *
 * @Date 2023-09
 * @author liurz
 */
public class EventQueUtil {


    private static final SynchronousQueue<EventResultVO> datas = new SynchronousQueue<EventResultVO>();


    public static void put(EventResultVO data){
        try {
            datas.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static EventResultVO poll(){
        return datas.poll();
    }

}
