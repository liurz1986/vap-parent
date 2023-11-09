package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Dependencies;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Outputs;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 过滤器VO
 * @author wd-pc
 *
 */
@Data
public class FilterOpertorVO  {

	private String guid; 
    private String name;
	private FilterConfigObject filterConfig;
	private List<String> sourceIds;  //eventTable Ids集合
	private List<OutFieldInfo> outFieldInfos; //最终输出的结果
	private Integer version;  //过滤器版本号
	private Boolean deleteFlag; //删除标识
	private List<Dependencies> dependencies; //依赖
	private Boolean status;
	private List<Outputs> outputs; //输出配置
	private String operatorType; //analysis or filteror
	private String ideVersion;   //编辑器版本号
    private String multiVersion;   //综合版本
    private String code;   //唯一编码
    private  String label;   //中文标签
    private  String desc;   // 过滤器/分析器描述
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date createTime ;   // 创建时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date updateTime ;   // 更新时间
	private String roomType;  //盒子类型
	private FilterConfigObject filterConfigTemplate;
	private List<ParamConfigVO> paramConfig;
	private StartConfigVO startConfig;//启动参数配置
	private Map<String, Object> paramValue;
	private List<FilterOpertorVO> children;
	private String modelId;
	private  String tag;   // 实例是否允许启动
	private String ruleType;  //规则类型
	private String newlineFlag; //换行标识
	private String filterType;// 0为实时规则   1为离线规则
	private Integer startNum; // 启动实例数
	private String ruleFilterType;// inside(内部)    warning（预警）    alarmdeal（告警）
	private String initStatus;
	// 描述信息
	@ApiModelProperty(value="攻击链阶段")//攻击链阶段
	private String attackLine;
	@ApiModelProperty(value="威胁可信度") //威胁可信度
	private String threatCredibility;
	@ApiModelProperty(value="处置建议") //处置建议
	private String dealAdvcie;
	@ApiModelProperty(value="失陷状态") //失陷状态
	private Integer failedStatus;
	@ApiModelProperty(value="危害")
	private String harm;	//危害
	@ApiModelProperty(value="原理")
	private  String principle;	//原理
	@ApiModelProperty(value="违规场景")
	private String  violationScenario;// 违规场景
	@ApiModelProperty(value="规则描述")
	private String filterDesc;

	@ApiModelProperty("是否可以配置")
	private String isConfigure;

}
