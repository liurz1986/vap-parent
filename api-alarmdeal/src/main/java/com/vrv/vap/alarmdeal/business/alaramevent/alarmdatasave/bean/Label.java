package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Label
{
	@ApiModelProperty(value = "标签标题")
	String title;
	@ApiModelProperty(value = "标签颜色")
	String color;
	
	@ApiModelProperty(value = "描述信息")
	String descript;
	
	public Label(	String title,
	String color,
	String descript)
	{
		this.title=title;
		this.color=color;
		this.descript=descript;
	}
	
}