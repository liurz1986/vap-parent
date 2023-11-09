package com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.impl.httpRequest;

import com.google.gson.Gson;
import com.vrv.vap.jpa.http.Request;
import com.vrv.vap.jpa.http.RequestTypeEnum;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.req.HttpRequestInf;

import java.util.Map;

/**
 * http请求类
 * @author wudi
 * @date 2022/11/16 14:55
 */

public class BusinessInfoRequest implements HttpRequestInf<BusinessHttpResponseVO> {

    private Gson gson  = new Gson();
    private String url;
    private String requestType;
    private String extraParams;

    public BusinessInfoRequest(){
        super();
    }
    public BusinessInfoRequest(String url,String requestType,String extraParams){
        this.url = url;
        this.requestType = requestType;
        this.extraParams = extraParams;
    }

    private RequestTypeEnum getRequestTypeEnum(){
        if(requestType==null){
            throw new RuntimeException("requestType is null ,please check it!");
        }
        if(requestType.equals("post")){
            return RequestTypeEnum.post;
        }else{
            return RequestTypeEnum.get;
        }
    }

    private Map<String,String> getParams(){
        Map<String,String> map = gson.fromJson(extraParams,Map.class);
        return map;
    }

    @Override
    public Request request() {
        Request request = new Request();
        request.setUri(getUrl());
        request.setType(getRequestTypeEnum());
        request.setParams(getParams());
        return request;
    }

    @Override
    public BusinessHttpResponseVO format(String s) {
        BusinessHttpResponseVO businessHttpResponseVO = JsonMapper.fromJsonString(s, BusinessHttpResponseVO.class);
        return businessHttpResponseVO;
    }

    @Override
    public boolean varify(BusinessHttpResponseVO businessHttpResponseVO) {
        if(businessHttpResponseVO.getCode()==0){
            return true;
        }
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(String extraParams) {
        this.extraParams = extraParams;
    }
}
