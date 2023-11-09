package com.vrv.rule.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 过滤器组件model
 * @author wd-pc
 *
 */
@Data
public class FilterOperator implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private String guid; //guid
	private String name; //过滤器组件名称
	private String config; //过滤器组件配置
	private String source; //数组类型，只能由eventtable
	private String outputFields; //最终输出结果
	private String version;  //过滤器版本号
	private Boolean deleteFlag; //删除标识
	private String dependencies; //依赖
	private String outputs; //对应输出结构
	private String operatorType; //filter or analysis
    private  String multiVersion;   //综合版本
    private  String code;   //唯一编码
    private  String label;   //中文标签
    private  String desc;   // 过滤器/分析器描述
    private  Date createTime ;   // 创建时间
    private  Date updateTime ;   // 更新时间
    private String roomType; //盒子类型
	private String tag; //数据源类型
	private String startConfig; //离线启动参数

    
	
	
}
