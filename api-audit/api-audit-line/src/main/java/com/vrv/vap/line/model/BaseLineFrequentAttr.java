package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 动态基线表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
@ApiModel(value="BaseLineFrequentAttr对象", description="访问序列频繁项属性表")
public class BaseLineFrequentAttr {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String item;
    private int hour;
    private float pck;
    private String startTime;
    private String endTime;
    private String ukey;

    public String getUkey() {
        return ukey;
    }

    public void setUkey(String ukey) {
        this.ukey = ukey;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public BaseLineFrequentAttr(String item, int hour, float pck,String ukey) {
        this.item = item;
        this.hour = hour;
        this.pck = pck;
        this.ukey = ukey;
    }

    public BaseLineFrequentAttr(String item, int hour, float pck, String startTime, String endTime,String ukey) {
        this.item = item;
        this.hour = hour;
        this.pck = pck;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ukey = ukey;
    }


    private String sysId;

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BaseLineFrequentAttr() {
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public float getPck() {
        return pck;
    }

    public void setPck(float pck) {
        this.pck = pck;
    }
}
