package com.vrv.vap.toolkit.model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 保存请求信息
 *
 * @author xw
 * @date 2018年5月3日
 */
public class EnvironmentModel {

    private HttpServletRequest request;

    private HttpServletResponse response;

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
