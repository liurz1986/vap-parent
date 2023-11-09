package com.flink.demo.model;

import java.util.List;

import com.vrv.rule.model.Outputs;
import com.vrv.rule.model.filter.Dependencies;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.model.filter.OutFieldInfo;


import lombok.Data;

/**
 * 过滤器VO
 * @author wd-pc
 *
 */
@Data
public class FilterOpertorVO {

    private String name;
	private FilterConfigObject filterConfig;
	private List<String> sourceIds;  //eventTable Ids集合
	private List<OutFieldInfo> outFieldInfos; //最终输出的结果
	private String version;  //过滤器版本号
	private Boolean deleteFlag; //删除标识
	private List<Dependencies> dependencies; //依赖
	private List<Outputs> outputs; //输出配置
	private String operatorType; //analysisor or filteror 
}
