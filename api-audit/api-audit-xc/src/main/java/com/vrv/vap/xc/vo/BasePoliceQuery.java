package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table base_police
 *
 * @mbg.generated do_not_delete_during_merge 2018-04-19 14:50:07
 */
@ApiModel
public class BasePoliceQuery extends Query {
    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String xm;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    private String sfzh;

    /**
     * 性别
     */
    @ApiModelProperty("性别")
    private String xb;

    /**
     * 性别代码
     */
    @ApiModelProperty("性别代码")
    private String xbdm;

    /**
     * 出生日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("出生日期")
    private Date csrq;

    /**
     * 警号
     */
    @ApiModelProperty("警号")
    private String jh;

    /**
     * 警衔
     */
    @ApiModelProperty("警衔")
    private String jx;

    /**
     * 警衔代码
     */
    @ApiModelProperty("警衔代码")
    private String jxdm;

    /**
     * 工作单位
     */
    @ApiModelProperty("工作单位")
    private String gzdw;

    /**
     * 单位代码
     */
    @ApiModelProperty("单位代码")
    private String dwdm;

    /**
     * 职务
     */
    @ApiModelProperty("职务")
    private String zw;

    /**
     * 职务代码
     */
    @ApiModelProperty("职务代码")
    private String zwdm;

    /**
     * 职级
     */
    @ApiModelProperty("职级")
    private String zj;

    /**
     * 职级代码
     */
    @ApiModelProperty("职级代码")
    private String zjdm;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date gxsj;

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getSfzh() {
        return sfzh;
    }

    public void setSfzh(String sfzh) {
        this.sfzh = sfzh;
    }

    public String getXb() {
        return xb;
    }

    public void setXb(String xb) {
        this.xb = xb;
    }

    public String getXbdm() {
        return xbdm;
    }

    public void setXbdm(String xbdm) {
        this.xbdm = xbdm;
    }

    public Date getCsrq() {
        return csrq;
    }

    public void setCsrq(Date csrq) {
        this.csrq = csrq;
    }

    public String getJh() {
        return jh;
    }

    public void setJh(String jh) {
        this.jh = jh;
    }

    public String getJx() {
        return jx;
    }

    public void setJx(String jx) {
        this.jx = jx;
    }

    public String getJxdm() {
        return jxdm;
    }

    public void setJxdm(String jxdm) {
        this.jxdm = jxdm;
    }

    public String getGzdw() {
        return gzdw;
    }

    public void setGzdw(String gzdw) {
        this.gzdw = gzdw;
    }

    public String getDwdm() {
        return dwdm;
    }

    public void setDwdm(String dwdm) {
        this.dwdm = dwdm;
    }

    public String getZw() {
        return zw;
    }

    public void setZw(String zw) {
        this.zw = zw;
    }

    public String getZwdm() {
        return zwdm;
    }

    public void setZwdm(String zwdm) {
        this.zwdm = zwdm;
    }

    public String getZj() {
        return zj;
    }

    public void setZj(String zj) {
        this.zj = zj;
    }

    public String getZjdm() {
        return zjdm;
    }

    public void setZjdm(String zjdm) {
        this.zjdm = zjdm;
    }

    public Date getGxsj() {
        return gxsj;
    }

    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }
}