package com.vrv.vap.admin.model;

import com.vrv.vap.syslog.common.annotation.LogField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@ApiModel("用户信息")
@Getter
@Setter

public class User {
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /**
     * 楚天云用戶id
     * */
    @ApiModelProperty("楚天云用戶id")
    @Ignore
    private Integer ctyId;

    /**
     * 用户名称
     */
    @ApiModelProperty("用户名称")
    private String name;

    /**
     * 用户账号
     */
    @ApiModelProperty("用户账号")
    private String account;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码",hidden = true)
    @Ignore
    private String password;

    @Column(name = "role_id")
    @ApiModelProperty("角色ID")
    private String roleId;

    @ApiModelProperty("身份证号")
    @LogField(name = "idcard", description = "身份证号", desensitization = true)
    private String idcard;

    @ApiModelProperty("电话号码 ")
    @LogField(name = "phone", description = "电话号码", desensitization = true)
    private String phone;

    @ApiModelProperty("email ")
    @LogField(name = "email", description = "邮箱", desensitization = true)
    private String email;

    @ApiModelProperty("状态")
    @LogField(name = "status", description = "状态")
    private Byte status;

    @ApiModelProperty("机构编码")
    @Column(name = "org_code")
    @Ignore
    private String orgCode;

    @ApiModelProperty("机构名称")
    @Column(name = "org_name")
    @Ignore
    private String orgName;

    @ApiModelProperty("所在省")
    @Ignore
    private String province;

    @ApiModelProperty("所在市")
    @Ignore
    private String city;

    @Column(name = "is_leader")
    @ApiModelProperty("是否是领导")
    @Ignore
    private Byte isLeader;

    @ApiModelProperty("安全域编码")
    @Column(name = "domain_code")
    private String domainCode;

    @ApiModelProperty("安全域名称")
    @Column(name = "domain_name")
    private String domainName;

    @ApiModelProperty("安全域数据权限控制")
    @Column(name = "authority_type")
    private Integer authorityType;

    @Column(name = "lastpwdupdatetime")
    @ApiModelProperty("最后一次密码修改时间")
    @Ignore
    private Date lastUpdateTime;

    @Column(name = "lastlogintime")
    @ApiModelProperty("最后一次登陆时间")
    @Ignore
    private Date lastLoginTime;

    @Column(name = "logintimes")
    @ApiModelProperty("尝试登陆次数")
    @Ignore
    private Integer loginTimes;

    @Column(name = "creator")
    @ApiModelProperty("创建人")
    @Ignore
    private Integer creator;


    @Column(name = "pwd_status")
    @ApiModelProperty(value = "密码状态",hidden = true)
    @Ignore
    private Byte pwdStatus;


    @Column(name = "salt")
    @ApiModelProperty(value = "密码盐",hidden = true)
    @Ignore
    private String salt;


    @Column(name="guid")
    @ApiModelProperty(value="人员guid")
    @Ignore
    private String guid;

    @Column(name = "person_id")
    @ApiModelProperty(value = "人员ID")
    @Ignore
    private Integer personId;

    @Column(name = "org_id")
    @ApiModelProperty(value = "用户区域ID")
    private String orgId;

    @ApiModelProperty(value = "关联人员姓名")
    @Transient
    private String personName;

    @Column(name = "ip_login")
    @ApiModelProperty(value = "IP登录")
    private Integer ipLogin;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", ctyId=" + ctyId +
                ", name='" + name + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", roleId='" + roleId + '\'' +
                ", idcard='" + idcard + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", orgCode='" + orgCode + '\'' +
                ", orgName='" + orgName + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", isLeader=" + isLeader +
                ", domainCode='" + domainCode + '\'' +
                ", domainName='" + domainName + '\'' +
                ", authorityType=" + authorityType +
                ", lastUpdateTime=" + lastUpdateTime +
                ", lastLoginTime=" + lastLoginTime +
                ", loginTimes=" + loginTimes +
                ", creator=" + creator +
                ", pwdStatus=" + pwdStatus +
                ", salt='" + salt + '\'' +
                ", guid='" + guid + '\'' +
                ", personId=" + personId +
                ", orgId='" + orgId + '\'' +
                '}';
    }
}