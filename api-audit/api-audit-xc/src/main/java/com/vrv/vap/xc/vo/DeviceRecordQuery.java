package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class DeviceRecordQuery extends Query {

    /**
     * 设备ip
     */
    @ApiModelProperty("设备ip")
    private String ip;

    /**
     * 对象类型编号
     */
    @ApiModelProperty("对象类型编号")
    private Integer objectType;

    /**
     * 日期（年月，yyyy-MM）
     */
    @ApiModelProperty("日期（年月，yyyy-MM）")
    private String timeFlag;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(String timeFlag) {
        this.timeFlag = timeFlag;
    }

    public Integer getObjectType() {
        return objectType;
    }

    public void setObjectType(Integer objectType) {
        this.objectType = objectType;
    }
}