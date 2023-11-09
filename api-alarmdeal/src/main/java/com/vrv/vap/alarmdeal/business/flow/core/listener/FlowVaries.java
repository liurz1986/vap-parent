package com.vrv.vap.alarmdeal.business.flow.core.listener;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 流程变量
 * @author wd-pc
 *
 */
@Data
public class FlowVaries {

	  private String userId; //登陆用户ID
	  private String advice;//意见
	  private String action;//流向状态
	  private String taskDefineName; //节点定义名称
	  private String taskDefineKey;//任务定义标识
	  private String processInstanceId; //流程实例Id
	  private String processDefinitionId; //流程定义标识
	  private List<Map<String,Object>> params;
	  private String contextKey;
	  private String contextId;
}
