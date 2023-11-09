package com.vrv.vap.alarmdeal.frameworks.contract;

/**
 * 创建时间 2018/3/9 14:02
 * @author lizj
 * @version 1.0
 */
public class ResultModel {
    /**
     * 返回码
     */
    private String code;
    /**
     * 描述
     */
    private String msg;
    /**
     * 返回的数据
     */
    private FileInfo data;
    /**
     * 是否成功
     */
    private boolean success;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public FileInfo getData() {
        return data;
    }

    public void setData(FileInfo data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
