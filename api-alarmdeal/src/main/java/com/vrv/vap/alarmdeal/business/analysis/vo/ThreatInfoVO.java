package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.analysis.model.ThreatInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 下午5:40:55 
* 类说明 
*/
@Data
public class ThreatInfoVO {

	
	@ApiModelProperty(value="威胁信息")
	private ThreatInfo threatInfo;
	@ApiModelProperty(value="排序字段")
	private String order_;   
    @ApiModelProperty(value="排序顺序")
	private String by_; 
    @ApiModelProperty(value="开始行")
	private Integer start_;
    @ApiModelProperty(value="每页个数")
	private Integer count_;
    @ApiModelProperty(value="威胁集合")
	private List<ThreatInfo> threatInfos;
    
    
}
