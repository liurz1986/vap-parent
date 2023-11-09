package com.vrv.vap.admin.vo;

/**
 * @author huipei.x
 * @data 创建时间 2018/11/15
 * @description 类说明 :
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@ApiModel
public class SysRequestLogVO {
    @ApiModelProperty("索引ID")
    private String id;

    /**
     *  终端标识 操作IP
     */
    @ApiModelProperty("终端标识 操作IP")
    private String requestIp;

    /**
     *  操作类型 0 登录 1查询 2 新增 3 修改 4 删除
     */
    @ApiModelProperty("操作类型 0 登录 1查询 2 新增 3 修改 4 删除")
    private Integer type;

    /**
     * 操作人ID
     */
    @ApiModelProperty("操作人ID")
    private String userId;

    /**
     * 操作人
     */
    @ApiModelProperty("操作人")
    private String userName;

    /**
     * 组织机构
     */
    @ApiModelProperty("组织机构")
    private String organizationName;

    /**
     * 操作描述
     */
    @ApiModelProperty("操作描述")
    private String description;
    /**
     * 请求路径
     */
    @ApiModelProperty("请求路径")
    private String requestUrl;
    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date requestTime;

    /**
     * 请求方式
     */
    @ApiModelProperty("请求方式")
    private String requestMethod;

    /**
     * 请求方法名
     */
    @ApiModelProperty("请求方法名")
    private String methodName;

    /**
     * 请求beanm名称
     */
    @ApiModelProperty("请求beanm名称")
    private String beanName;
    /**
     * 请求参数
     */
    @ApiModelProperty("请求参数")
    private String paramsValue;
    /**
     * 操作结果
     */
    @ApiModelProperty("操作结果")
    private Integer responseResult;

    @ApiModelProperty("用户登录类型 0：普通登录 1：证书登录 2：虹膜登录")
    private Integer loginType;

    @ApiModelProperty("用户登录类型 0：普通登录 1：证书登录 2：虹膜登录")
    private Map loginTypes= new HashMap();

    /**
     * 操作类型 0 登录 1查询 2 新增 3 修改 4 删除 5 退出 6 浏览
     */
    private Map types= new HashMap();
    /**
     * 查詢詳情
     */
    private String detail;

    private  String  eventType;

}
