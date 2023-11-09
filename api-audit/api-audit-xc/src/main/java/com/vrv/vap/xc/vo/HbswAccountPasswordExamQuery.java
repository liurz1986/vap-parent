package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 河北税务-账号密码检测记录表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="HbswAccountPasswordExam对象", description="河北税务-账号密码检测记录表")
public class HbswAccountPasswordExamQuery extends Query {

    @ApiModelProperty(value = "主键Id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "账号")
    private String account;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "身份证号")
    private String userId;

    @ApiModelProperty(value = "设备责任人")
    private String username;

    @ApiModelProperty(value = "设备IP")
    private String ip;

    @ApiModelProperty(value = "设备所属区域")
    private String areaCode;

    @ApiModelProperty(value = "设备所属单位")
    private String organization;

    @ApiModelProperty(value = "含大写(1.包含 0.不包含)")
    private Integer containUpperCase;

    @ApiModelProperty(value = "含小写(1.包含 0.不包含)")
    private Integer containLowerCase;

    @ApiModelProperty(value = "含数字(1.包含 0.不包含)")
    private Integer containNumber;

    @ApiModelProperty(value = "含特殊字符(1.包含 0.不包含)")
    private Integer containSpecialChar;

    @ApiModelProperty(value = "密码长度")
    private Integer passwordLength;

    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    @ApiModelProperty(value = "倒数第二次密码修改时间")
    private Date passwordUpdateTime;

    @ApiModelProperty(value = "最后一次密码修改时间")
    private Date lastPasswordUpdateTime;

    @ApiModelProperty(value = "账号所属系统ID")
    private String appId;

    @ApiModelProperty(value = "系统名称")
    private String appName;

    @ApiModelProperty(value = "数据更新时间")
    private Date lastUpdateTime;

    /**
     * 密码天数
     */
    @ApiModelProperty("密码天数")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private int passwordDays;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
    public Integer getContainUpperCase() {
        return containUpperCase;
    }

    public void setContainUpperCase(Integer containUpperCase) {
        this.containUpperCase = containUpperCase;
    }
    public Integer getContainLowerCase() {
        return containLowerCase;
    }

    public void setContainLowerCase(Integer containLowerCase) {
        this.containLowerCase = containLowerCase;
    }
    public Integer getContainNumber() {
        return containNumber;
    }

    public void setContainNumber(Integer containNumber) {
        this.containNumber = containNumber;
    }
    public Integer getContainSpecialChar() {
        return containSpecialChar;
    }

    public void setContainSpecialChar(Integer containSpecialChar) {
        this.containSpecialChar = containSpecialChar;
    }
    public Integer getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(Integer passwordLength) {
        this.passwordLength = passwordLength;
    }
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    public Date getPasswordUpdateTime() {
        return passwordUpdateTime;
    }

    public void setPasswordUpdateTime(Date passwordUpdateTime) {
        this.passwordUpdateTime = passwordUpdateTime;
    }
    public Date getLastPasswordUpdateTime() {
        return lastPasswordUpdateTime;
    }

    public void setLastPasswordUpdateTime(Date lastPasswordUpdateTime) {
        this.lastPasswordUpdateTime = lastPasswordUpdateTime;
    }
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getPasswordDays() {
        return passwordDays;
    }

    public void setPasswordDays(int passwordDays) {
        this.passwordDays = passwordDays;
    }

    @Override
    public String toString() {
        return "HbswAccountPasswordExam{" +
            "id=" + id +
            ", account=" + account +
            ", password=" + password +
            ", userId=" + userId +
            ", username=" + username +
            ", ip=" + ip +
            ", areaCode=" + areaCode +
            ", organization=" + organization +
            ", containUpperCase=" + containUpperCase +
            ", containLowerCase=" + containLowerCase +
            ", containNumber=" + containNumber +
            ", containSpecialChar=" + containSpecialChar +
            ", passwordLength=" + passwordLength +
            ", lastLoginTime=" + lastLoginTime +
            ", passwordUpdateTime=" + passwordUpdateTime +
            ", lastPasswordUpdateTime=" + lastPasswordUpdateTime +
            ", appId=" + appId +
            ", appName=" + appName +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
