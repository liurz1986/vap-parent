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
@ApiModel(value="BaseLineResult对象", description="动态基线结果表")
public class BaseLineFrequentOrg {

    private static final long serialVersionUID = 1L;

    @TableId(value = "org", type = IdType.INPUT)
    private String org;

    private String frequents;

    private Date updateTime;

    public BaseLineFrequentOrg() {
    }

    public BaseLineFrequentOrg(String org, String frequents, Date updateTime) {
        this.org = org;
        this.frequents = frequents;
        this.updateTime = updateTime;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getFrequents() {
        return frequents;
    }

    public void setFrequents(String frequents) {
        this.frequents = frequents;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

