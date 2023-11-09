package com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日志信息
 * 2023-03-23
 */
@Data
public class WebLoginAudit {
    //	日志id
    String guid;
    //	上报产品类型
    String reportDevType;
    //	用户
    String userName;
}
