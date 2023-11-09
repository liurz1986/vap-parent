package com.vrv.vap.alarmdeal.frameworks.contract.syslog;


/**
 * @author huipei.x
 * @date 创建时间 2018-8-10
 * @description 类说明 :
 */
public class ResultBody {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private Object data;
    public ResultBody() {
    }


    public  ResultBody ok() {
        return ok(ResultEnum.OK.getCode(), ResultEnum.OK.getMsg());
    }


    public  ResultBody ok(String message) {
        return ok(ResultEnum.OK.getCode(), message);
    }


    public ResultBody ok(int code, String message) {
         this.code=code;
          this.msg=message;
       return this;
    }

    public  ResultBody ok(Object data) {
        this.data=data;
         ok();
        return this;
    }

    public ResultBody ok(Object data,int code, String message) {
        this.code=code;
        this.msg=message;
        ok(data);
        return this;
    }
    public  ResultBody error() {

        return error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), ResultEnum.INTERNAL_SERVER_ERROR.getMsg());
    }

    public  ResultBody error(String messag) {
        return error(ResultEnum.INTERNAL_SERVER_ERROR.getCode(), messag);
    }

    public  ResultBody error(int code, String message) {
        this.code=code;
        this.msg=message;
        return this;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}