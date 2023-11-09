package com.vrv.vap.alarmdeal.business.analysis.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Auther: kangyi
 * @Date: 2020/9/24 09:58
 * @Description:
 */
@Data
public class CallLinkageVO {

    @ApiModelProperty("规则ID")
    private String guid;

    @ApiModelProperty("参数")
    private String params;

    /*class Params{
        @ApiModelProperty("操作类型")
        private String Action;
        @ApiModelProperty("白名单名称")
        private String name;
        @ApiModelProperty("mac地址")
        private String addr;
        @ApiModelProperty("有效模式")
        private String mode;
        @ApiModelProperty("截至日期时间戳")
        private String time;
        @ApiModelProperty("白名单备注")
        private String note;
    }*/
}
