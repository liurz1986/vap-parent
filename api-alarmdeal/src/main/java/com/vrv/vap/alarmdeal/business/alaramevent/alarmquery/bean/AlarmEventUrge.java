package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;
import com.vrv.vap.alarmdeal.frameworks.config.EsField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
/**
 * 督促
 * @author sj100d
 *
 */
public class AlarmEventUrge {

	@EsField("initiator")
	@ApiModelProperty(value = "督促人员(创建人员)")
	String initiator;
	
	
	@EsField("isAuto")
	@ApiModelProperty(value = "是否自动督促")
	Boolean isAuto;
	
	@EsField("toUser")
	@ApiModelProperty(value = "接受者")
	List<GuidNameVO> toUser;
	@EsField("toRole")
	@ApiModelProperty(value = "接受者")
	List<GuidNameVO> toRole;
	
	@EsField("validityDate")
	@ApiModelProperty(value = "处置时限")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	Date validityDate;
	
	@EsField("urgeTime")
	@ApiModelProperty(value = "督促时间（创建时间）")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	Date urgeTime;
	
	@EsField("urgeRemark")
	@ApiModelProperty(value = "督促批注")
	String urgeRemark;
	
}
