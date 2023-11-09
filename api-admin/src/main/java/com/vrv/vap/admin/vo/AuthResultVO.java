package com.vrv.vap.admin.vo;

import java.util.Map;

/**
 * Created by zhujie on 2017/4/14.
 */
public class AuthResultVO {
    private boolean isSuccess;
    private String errCode;
    private String errDesc;
    private Map<String,String> certAttributeNodeMap;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public Map<String,String>  getCertAttributeNodeMap() {
        return certAttributeNodeMap;
    }

    public void setCertAttributeNodeMap(Map certAttributeNodeMap) {
        this.certAttributeNodeMap = certAttributeNodeMap;
    }

}
