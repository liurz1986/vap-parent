package com.vrv.vap.alarmdeal.business.analysis.controller;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kafkaTest")
public class KafkaTestController extends BaseController {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	private EventTabelService eventTabelService;
	
	@Autowired
	private ElasticSearchRestClient elasticSearchRestClient;
	private static Logger logger= LoggerFactory.getLogger(KafkaTestController.class);

	
	@PostMapping("/kafkaSslTest")
	@ApiOperation(value = "kafka认证发送测试", notes = "")
	@SysRequestLog(description = "kafka认证发送测试", actionType = ActionType.SELECT, manually = false)
	public Result<Boolean> kafkaSslTest() {
		String topic = "test1234";
		String data = "abcde";
        for (int i = 0;i<1000;i++){
			kafkaTemplate.send(topic, data);
		}
		return ResultUtil.success(true);
	}
	

    @PostMapping("postDataToKafka")
    @ApiOperation(value="待办督办、待办预警、上报协办统计",notes="")
	@SysRequestLog(description = "待办督办、待办预警、上报协办统计", actionType = ActionType.SELECT, manually = false)
    public  Result<Boolean> postDataToKafka(@RequestBody  NameValue data){
		logger.info("----------------测试kafka发送的数据未data={}",new Gson().toJson(data));
		kafkaTemplate.send(data.getName(), data.getValue());
		return ResultUtil.success(true);
    }


    
    @PostMapping("IdRoomTest")
    @ApiOperation(value="待办督办、待办预警、上报协办统计",notes="")
	@SysRequestLog(description = "待办督办、待办预警、上报协办统计", actionType = ActionType.SELECT, manually = false)
    public  Result<String> IdRoomTest(@RequestBody  Map<String, String[]> idRoom){

		if(idRoom!=null) {
			for(Map.Entry<String,String[]> item:  idRoom.entrySet())
			{
				String key=item.getKey();
				String[] logIds=item.getValue();
				
				
				
				EventTable  eventTable = eventTabelService.getEventTableByName(key);
				if(eventTable!=null)
				{
					
					String baseIndexName=eventTable.getIndexName();
					
					
					List<QueryCondition_ES> conditions = new ArrayList<>();
					conditions.add(QueryCondition_ES.in("guid", logIds));//logIds 集合太大时，可能存在问题，需要优化
					QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
					SearchResponse response = elasticSearchRestClient.getDocs(new String[] { baseIndexName+"-*" }, queryBuilder, null, null, 0, logIds.length);
					 
					return ResultUtil.success(response.toString());
				}
			}
		}
		return ResultUtil.success("查询异常");
    }


}
