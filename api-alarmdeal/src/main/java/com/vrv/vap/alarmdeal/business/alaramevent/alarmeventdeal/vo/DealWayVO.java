package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.frameworks.contract.mail.MailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.sms.SmsVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通告方式
 * @author wd-pc
 *
 */
@Data
@ApiModel(value="告警处置方式对象")
public class DealWayVO {
	@ApiModelProperty(value="告警主键ID")
    private String id; //告警Id
	@ApiModelProperty(value="邮件信息")
    private List<MailVO> mail;//邮件信息
	@ApiModelProperty(value="短信信息")
    private List<SmsVO> sms; //短信信息
	@ApiModelProperty(value="工单信息")
    private String ticket; //ticket信息
	@ApiModelProperty(value="当前登陆用户")
    private String userId; //当前登陆用户Id
	@ApiModelProperty(value="当前登陆用户名称")
	private String userName; //用户名称
	@ApiModelProperty(value="告警状态改变")
    private String statusChange; //状态改变
}
