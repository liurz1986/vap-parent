package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;

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
public class BaseLineFrequentRole {

    private static final long serialVersionUID = 1L;

    @TableId(value = "role", type = IdType.INPUT)
    private String role;

    private String frequents;

    private Date updateTime;


    public BaseLineFrequentRole() {
    }

    public BaseLineFrequentRole(String role, String frequents, Date updateTime) {
        this.role = role;
        this.frequents = frequents;
        this.updateTime = updateTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

