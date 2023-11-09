package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年10月9日 下午3:20:23 
* 类说明    数据运维查询
*/
@ApiModel(value="趋势图查询VO")
public class LogStasticsVO {
	 @ApiModelProperty(value="类型：点击一级结构，field：category；点击二级，field:area_name；点击三级,field:sub_category_name")
	 private String field;
	 @ApiModelProperty(value="对应key的值")
	 private String key;
	 @ApiModelProperty(value="事件类型")
     private String category;
	 @ApiModelProperty(value="品牌型号")
     private String sno_name;
	 @ApiModelProperty(value="区域")
     private String area_name;
     
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSno_name() {
		return sno_name;
	}
	public void setSno_name(String sno_name) {
		this.sno_name = sno_name;
	}
	public String getArea_name() {
		return area_name;
	}
	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}
     
     
}
