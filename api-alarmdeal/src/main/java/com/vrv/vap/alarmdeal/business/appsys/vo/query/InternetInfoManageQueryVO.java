package com.vrv.vap.alarmdeal.business.appsys.vo.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;



/**
 * @author vrv
 */
@Data
public class InternetInfoManageQueryVO {


    private Integer id;

    @ApiModelProperty(value = "互联网单位名称")
    private String internetName;

    /**
     * 远程登录  网络接入
     */
    @ApiModelProperty(value = "接入方式")
    private String internetType;


    @ApiModelProperty(value = "涉密等级")
    private String secretLevel;

    /**
     * 秘密 1； 机密；2 机密(增强)3；绝密 4
     */
    @ApiModelProperty(value = "防护等级")
    private String protectLevel;

}
