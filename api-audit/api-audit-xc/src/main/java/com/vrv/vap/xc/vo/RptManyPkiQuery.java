package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2021-05-28
 */
@ApiModel(value="RptManyPki对象", description="")
public class RptManyPkiQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "身份证")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "地区编码")
    private String areaCode;

    @ApiModelProperty(value = "巨龙系统编号")
    private String sysId;

    @ApiModelProperty(value = "操作时间")
    private Date operTime;

    @ApiModelProperty(value = "组织机构")
    private String organ;

    @ApiModelProperty(value = "操作条件")
    private String operation;

    @ApiModelProperty(value = "插入时间")
    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "RptManyPki{" +
            "id=" + id +
            ", userId=" + userId +
            ", userName=" + userName +
            ", ip=" + ip +
            ", areaCode=" + areaCode +
            ", sysId=" + sysId +
            ", operTime=" + operTime +
            ", organ=" + organ +
            ", operation=" + operation +
            ", insertTime=" + insertTime +
        "}";
    }
}
