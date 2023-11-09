package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年8月20日 下午3:49:52 
* 类说明  关于工单的前端入参使用
*/

@Data
public class TicketVO {

	@ApiModelProperty(value="工单")
    private MyTicket myTicket;
    @ApiModelProperty(value="工单模型")
    private MyticketTemplate myticketTemplate; //工单模型
    @ApiModelProperty(value="当前登陆用户Id")
    private String userId; //当前登陆用户Id
    @ApiModelProperty(value="当前登录用户名称")
    private String userName; //当前登录用户名称
    @ApiModelProperty(value="流程ID")
    private String id;
    @ApiModelProperty(value="版本号")
    private String newVersion;
}
