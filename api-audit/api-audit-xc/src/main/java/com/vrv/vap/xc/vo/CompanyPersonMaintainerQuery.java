package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 企业人员日常维护服务器
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyPersonMaintainer对象", description="企业人员日常维护服务器")
public class CompanyPersonMaintainerQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "人员id")
    private Integer personId;

    @ApiModelProperty(value = "服务器类型")
    private String serverType;

    @ApiModelProperty(value = "网络环境")
    private String networkEnv;

    @ApiModelProperty(value = "是否多网卡")
    private Integer multiNic;

    @ApiModelProperty(value = "服务器系统")
    private String system;

    @ApiModelProperty(value = "是否接入堡垒机运维")
    private String hasBastion;

    @ApiModelProperty(value = "服务器具体位置")
    private String position;

    @ApiModelProperty(value = "主要功能（是否核查存在私自存储警务工作信息的情况）")
    private String mainFeature;

    @ApiModelProperty(value = "ip(网段)")
    private String ip;

    @ApiModelProperty(value = "mac")
    private String mac;

    @ApiModelProperty(value = "型号")
    private String category;

    @ApiModelProperty(value = "序列号")
    private String sn;

    @ApiModelProperty(value = "维护日期")
    private String maintainDate;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }
    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
    public String getNetworkEnv() {
        return networkEnv;
    }

    public void setNetworkEnv(String networkEnv) {
        this.networkEnv = networkEnv;
    }
    public Integer getMultiNic() {
        return multiNic;
    }

    public void setMultiNic(Integer multiNic) {
        this.multiNic = multiNic;
    }
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
    public String getHasBastion() {
        return hasBastion;
    }

    public void setHasBastion(String hasBastion) {
        this.hasBastion = hasBastion;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
    public String getMainFeature() {
        return mainFeature;
    }

    public void setMainFeature(String mainFeature) {
        this.mainFeature = mainFeature;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getMaintainDate() {
        return maintainDate;
    }

    public void setMaintainDate(String maintainDate) {
        this.maintainDate = maintainDate;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CompanyPersonMaintainer{" +
            "id=" + id +
            ", personId=" + personId +
            ", serverType=" + serverType +
            ", networkEnv=" + networkEnv +
            ", multiNic=" + multiNic +
            ", system=" + system +
            ", hasBastion=" + hasBastion +
            ", position=" + position +
            ", mainFeature=" + mainFeature +
            ", ip=" + ip +
            ", mac=" + mac +
            ", category=" + category +
            ", sn=" + sn +
            ", maintainDate=" + maintainDate +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
