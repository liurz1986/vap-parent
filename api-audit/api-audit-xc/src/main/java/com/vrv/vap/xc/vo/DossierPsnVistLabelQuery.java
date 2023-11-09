package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModelProperty;

public class DossierPsnVistLabelQuery extends Query {
    @ApiModelProperty("机构id")
    private String orgCode;
    @ApiModelProperty("警种")
    private String pliceType;
    @ApiModelProperty("1.上班，2.下班，3.全部")
    private int onOffStatus;
    @ApiModelProperty("查询字段")
    private String onOffString;
    @ApiModelProperty("身份证号")
    private String idCard;
    @ApiModelProperty("1.人，2.车牌，3.全部")
    private String objectType;
    @ApiModelProperty("对象区域")
    private String countArea;
    @ApiModelProperty("对象id")
    private String content;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getPliceType() {
        return pliceType;
    }

    public void setPliceType(String pliceType) {
        this.pliceType = pliceType;
    }

    public int getOnOffStatus() {
        return onOffStatus;
    }

    public void setOnOffStatus(int onOffStatus) {
        this.onOffStatus = onOffStatus;
    }

    public String getOnOffString() {
        return onOffString;
    }

    public void setOnOffString(String onOffString) {
        this.onOffString = onOffString;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getCountArea() {
        return countArea;
    }

    public void setCountArea(String countArea) {
        this.countArea = countArea;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
