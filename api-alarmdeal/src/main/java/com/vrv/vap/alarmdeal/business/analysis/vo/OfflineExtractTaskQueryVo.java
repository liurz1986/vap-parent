package com.vrv.vap.alarmdeal.business.analysis.vo;

import com.vrv.vap.jpa.web.page.PageReqVap;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author lps 2021/8/26
 */

@Data
@ApiModel(value = "离线抽取任务查询VO")
public class OfflineExtractTaskQueryVo extends PageReqVap {


    @ApiModelProperty(value = "数据源配置名称")
    private  String dataConfigName;

    @ApiModelProperty(value = "是否启动")
    private Boolean status;

}
