package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.CollectorDataAccess;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
public class CollectorDataAccessVO extends CollectorDataAccess {

    private String collectionName;

    /**
     * 规则集是否最新 1:最新，2:可更新
     */
    private Integer collectionStatus;

    /**
     * 运行状态 1:运行中，2:停止中,3:异常中
     */
    private Integer status;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public Integer getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(Integer collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
