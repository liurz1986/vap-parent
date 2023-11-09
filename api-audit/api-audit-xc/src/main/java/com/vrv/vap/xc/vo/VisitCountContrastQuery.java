package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;

public class VisitCountContrastQuery extends Query {
    private int id; // required
    private String taskId; // required
    private String areaCode; // required
    private String policeTypeCode; // required
    private String policeTypeName; // required
    private String idCard; // required
    private String userName; // required
    private String organ; // required
    private String postTypeName; // required
    private int beforeCount; // required
    private int afterCount; // required
    private int diffCount; // required
    private String areaName; // required
    private String orderType; // required

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPoliceTypeCode() {
        return policeTypeCode;
    }

    public void setPoliceTypeCode(String policeTypeCode) {
        this.policeTypeCode = policeTypeCode;
    }

    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getPostTypeName() {
        return postTypeName;
    }

    public void setPostTypeName(String postTypeName) {
        this.postTypeName = postTypeName;
    }

    public int getBeforeCount() {
        return beforeCount;
    }

    public void setBeforeCount(int beforeCount) {
        this.beforeCount = beforeCount;
    }

    public int getAfterCount() {
        return afterCount;
    }

    public void setAfterCount(int afterCount) {
        this.afterCount = afterCount;
    }

    public int getDiffCount() {
        return diffCount;
    }

    public void setDiffCount(int diffCount) {
        this.diffCount = diffCount;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
