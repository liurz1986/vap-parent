package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PlatformRegisterVo extends Query {
    @QueryLike
    @ApiModelProperty("单位名称")
    private String platformName;
    @ApiModelProperty("平台ID")
    private String platformId;
    @ApiModelProperty("上级平台IP")
    private String upHost;
    @ApiModelProperty("上级平台端口")
    private Integer upPort;
    @ApiModelProperty("本级平台IP")
    private String localHost;
    @ApiModelProperty("本级平台端口")
    private Integer localPort;
    @ApiModelProperty("本级token")
    private String token;
    @ApiModelProperty("级别")
    private String securityClassification;
    @ApiModelProperty("状态-注册状态 0:未注册 1:已注册")
    private Integer status;
    @ApiModelProperty("已注册状态（0 成功，1失败）")
    private Integer regStatus;
    @ApiModelProperty("注册返回消息")
    private String regMsg;
    @QueryLike
    @ApiModelProperty("平台ip")
    private String ip;
}
