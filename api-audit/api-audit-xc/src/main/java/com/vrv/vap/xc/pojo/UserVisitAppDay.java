package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 * 每日人员访问应用情况统计表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-20
 */
@ApiModel(value="UserVisitAppDay对象", description="每日人员访问应用情况统计表")
@TableName(value = "rpt_user_visit_app_day")
public class UserVisitAppDay {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "员工编号")
    private String userNo;

    @ApiModelProperty(value = "员工姓名")
    private String userName;

    @ApiModelProperty(value = "设备ip")
    private String deviceIp;

    @ApiModelProperty(value = "部门编号")
    private String departNo;

    @ApiModelProperty(value = "部门名称")
    private String departName;

    @ApiModelProperty(value = "应用编号")
    private String appNo;

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "访问次数")
    private Long count;

    @ApiModelProperty(value = "数据日期")
    private String dataTime;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
    public String getDepartNo() {
        return departNo;
    }

    public void setDepartNo(String departNo) {
        this.departNo = departNo;
    }
    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }
    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UserVisitAppDay{" +
            "id=" + id +
            ", userNo=" + userNo +
            ", userName=" + userName +
            ", deviceIp=" + deviceIp +
            ", departNo=" + departNo +
            ", departName=" + departName +
            ", appNo=" + appNo +
            ", appName=" + appName +
            ", count=" + count +
            ", dataTime=" + dataTime +
            ", time=" + time +
        "}";
    }
}
