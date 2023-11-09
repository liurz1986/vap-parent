package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

/**
 * <p>
 * 全量人员访问应用情况统计表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-20
 */
@ApiModel(value="UserVisitAppTotal对象", description="全量人员访问应用情况统计表")
@TableName(value = "rpt_user_visit_app_total")
public class UserVisitAppTotal {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "员工编号")
    private String userNo;

    @ApiModelProperty(value = "员工姓名")
    private String userName;

    //@TableId(value = "device_ip")
    @ApiModelProperty(value = "设备ip")
    private String deviceIp;

    @ApiModelProperty(value = "部门编号")
    private String departNo;

    @ApiModelProperty(value = "部门名称")
    private String departName;

    //@TableId(value = "app_no")
    @ApiModelProperty(value = "应用编号")
    private String appNo;

    @ApiModelProperty(value = "应用名")
    private String appName;

    @ApiModelProperty(value = "访问次数")
    private Long count;

    @ApiModelProperty(value = "统计日期")
    private LocalDateTime time;

    @ApiModelProperty(value = "数据版本")
    private String version;

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
    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "UserVisitAppTotal{" +
            "userNo=" + userNo +
            ", userName=" + userName +
            ", deviceIp=" + deviceIp +
            ", departNo=" + departNo +
            ", departName=" + departName +
            ", appNo=" + appNo +
            ", appName=" + appName +
            ", count=" + count +
            ", time=" + time +
            ", version=" + version +
        "}";
    }
}
