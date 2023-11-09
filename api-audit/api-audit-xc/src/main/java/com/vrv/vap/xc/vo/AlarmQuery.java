package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;

public class AlarmQuery extends Query {
    /**
     * 1.违规证书操作，2。一key多机，3。红名单，4。热点库，5.跨区域访问
     */
    private int type;
    private String idCard;
    private String tableName;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
