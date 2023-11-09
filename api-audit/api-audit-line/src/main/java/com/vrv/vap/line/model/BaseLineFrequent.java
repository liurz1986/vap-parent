package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * <p>
 * 动态基线表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
@ApiModel(value="BaseLineFrequent对象", description="访问序列频繁项集表")
public class BaseLineFrequent {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户编号")
    private String userId;

    @ApiModelProperty(value = "频繁项集")
    private String frequents;

    @ApiModelProperty(value = "次数")
    private Integer count;

    @ApiModelProperty(value = "是否连续（0：否；1：是）")
    private String isContinue;

    private String type;

    private String org;

    private String role;

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @ApiModelProperty(value = "入库时间")
    private Date time;

    private String sysId;

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsContinue() {
        return isContinue;
    }

    public void setIsContinue(String isContinue) {
        this.isContinue = isContinue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFrequents() {
        return frequents;
    }

    public void setFrequents(String frequents) {
        this.frequents = frequents;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
