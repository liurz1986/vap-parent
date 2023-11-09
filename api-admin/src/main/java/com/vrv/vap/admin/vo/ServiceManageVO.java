package com.vrv.vap.admin.vo;


import io.swagger.annotations.ApiModelProperty;

public class ServiceManageVO  {

    @ApiModelProperty("服务名称")
    private  String serviceName;

    @ApiModelProperty("操作类型，start 启动，restart 重启 ，stop 停止")
    private  String operType;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }
}
