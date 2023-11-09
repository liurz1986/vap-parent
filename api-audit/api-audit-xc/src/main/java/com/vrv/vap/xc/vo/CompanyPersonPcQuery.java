package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 企业人员个人电脑
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyPersonPc对象", description="企业人员个人电脑")
public class CompanyPersonPcQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "人员id")
    private Integer personId;

    @ApiModelProperty(value = "ip(网段)")
    private String ip;

    @ApiModelProperty(value = "mac")
    private String mac;

    @ApiModelProperty(value = "终端类型")
    private String type;

    @ApiModelProperty(value = "型号")
    private String category;

    @ApiModelProperty(value = "序列号")
    private String sn;

    @ApiModelProperty(value = "网络环境")
    private String networkEnv;

    @ApiModelProperty(value = "是否多网卡")
    private Integer multiNic;

    @ApiModelProperty(value = "终端具体位置")
    private String position;

    @ApiModelProperty(value = "主要功能（是否核查存在私自存储警务工作信息的情况）")
    private String mainFeature;

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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        return "CompanyPersonPcQuery{" +
            "id=" + id +
            ", personId=" + personId +
            ", ip=" + ip +
            ", mac=" + mac +
            ", type=" + type +
            ", category=" + category +
            ", sn=" + sn +
            ", networkEnv=" + networkEnv +
            ", multiNic=" + multiNic +
            ", position=" + position +
            ", mainFeature=" + mainFeature +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
