package com.vrv.vap.alarmdeal.business.analysis.server.core.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年9月29日 下午7:08:38 
* 类说明 
*/

public class FlowSubRabbitConfiguration {
	

	 private Logger logger = LoggerFactory.getLogger(getClass());
	
	 public static final String ASSET_EXCHANGE = "ex.asset";
	 public static final String ASSET_KEY = "asset-change";
	 public static final String CustomerAssetEvent="asset-publish-event";
     public static final String FLOW_EXCHANGE="ex.flow";
     public static final String FLOW_KEY="0666fb88-4cc2-11e7-9226-1242ac14";
     public static final String JUDGE_KEY = "judgeRouteKey";
     public static final String TICKET_EXCHANGE = "ticket.direct"; //ticket
     public static final String TICKET_KEY="alarm-reset"; // ticketd的key
     public static final String ALARMDEAL_POINT_QUEUE = "q.alarmdeal";   //queue的名称，自己定义 
     //public static final String ALARM_RESET_QUEUE = "reset_queue"; //内置的queue
     public static final String DEAD_POINT_QUEUE = "d.flow"; //中止队列的队列名称，自己定义
     public static final String POINT_KEY = "0666fb88-4cc2-11e7-9226-1242ac14"; //队列标识，生产者和消费者保持统一，自己定义
     public static final String RESET_POINT_KEY = "alarm-reset";
     public static final String DEAD_POINT_KEY = "a0b1d08b-4ccd-11e7-9226-1242ac14";//中止队列标识，生产者和消费者保持统一，自定义
     public static final String GlobalFlowEvent="flow-global-event";
     public static final String AlarmResetEvent = "alarm-reset-event";
    
  
}
