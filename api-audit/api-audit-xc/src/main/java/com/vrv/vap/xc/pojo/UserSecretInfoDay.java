package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@ApiModel(value="UserSecretInfoDay对象", description="每日人员处理涉密文件情况统计表")
@TableName(value = "rpt_user_secret_info_day")
public class UserSecretInfoDay {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "userNo")
    private String userNo;

    @ApiModelProperty(value = "userName")
    private String userName;

    @ApiModelProperty(value = "deviceIp")
    private String deviceIp;

    @ApiModelProperty(value = "departNo")
    private String departNo;

    @ApiModelProperty(value = "departName")
    private String departName;

    @ApiModelProperty(value = "secretFileName")
    private String secretFileName;

    @ApiModelProperty(value = "business")
    private String business;

    @ApiModelProperty(value = "data_time")
    private String dataTime;

    @ApiModelProperty(value = "count")
    private Long count;

    @ApiModelProperty(value = "time")
    private Date time;

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

    public String getSecretFileName() {
        return secretFileName;
    }

    public void setSecretFileName(String secretFileName) {
        this.secretFileName = secretFileName;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
}
