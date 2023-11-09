package com.vrv.vap.admin.vo;

public class IpRangeVO {
    private  long startIpValue;
    private  long endIpValue;
    private  String areaCode;
    private  String orgCode;
    private  String code;

    public long getStartIpValue() {
        return startIpValue;
    }

    public void setStartIpValue(long startIpValue) {
        this.startIpValue = startIpValue;
    }

    public long getEndIpValue() {
        return endIpValue;
    }

    public void setEndIpValue(long endIpValue) {
        this.endIpValue = endIpValue;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
