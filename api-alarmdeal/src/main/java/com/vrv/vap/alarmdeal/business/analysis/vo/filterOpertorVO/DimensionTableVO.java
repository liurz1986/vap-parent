package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import java.util.Date;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 维表VO
 * @author Administrator
 *
 */
@Data
public class DimensionTableVO {

	@ApiModelProperty(value="主键") 
	private String guid;
	@ApiModelProperty(value="维表中文描述")
	private String name;
	@ApiModelProperty(value="维表英文描述")
	private String nameEn; 
	@ApiModelProperty(value="维表描述")
	private String description;
	
	@Column(name="table_type")
	private String tableType;   //维表类型   baseline 、 base（不可填参数） 、 other


	@Column(name="baseline_index")
	private String baselineIndex;
	
	
	@ApiModelProperty(value="维表创建时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime; 
	@ApiModelProperty(value="排序字段") 
	private String order_; 
	@ApiModelProperty(value="排序顺序") 
	private String by_; 
	@ApiModelProperty(value="起始行") 
	private Integer start_;
	@ApiModelProperty(value="页个数")
	private Integer count_;

	@ApiModelProperty(value = "数据转存天数")
	private Integer days;
	@ApiModelProperty(value = "条件过滤字段")
	private String filterCon;  // 条件过滤字段
	
}
