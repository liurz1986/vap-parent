package com.vrv.vap.alarmdeal.business.asset.util.http;

import com.vrv.vap.jpa.http.Request;
import com.vrv.vap.jpa.http.RequestTypeEnum;
import com.vrv.vap.jpa.req.HttpRequestInf;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * 获取注册设备数量的
 */
public class GetRegisterDeviceRequest implements HttpRequestInf<Long> {
	private String url;

	private Map<String, String> param;

	private RequestTypeEnum type;

	public RequestTypeEnum getType() {
		return type;
	}

	public void setType(RequestTypeEnum type) {
		this.type = type;
	}

	private Map<String, String> header;

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public Map<String, String> getParam() {
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	public GetRegisterDeviceRequest() {
		super();
	}

	public GetRegisterDeviceRequest(String url) {
		this.url = url;
	}

	@Override
	public Request request() {
		Request request = new Request();
		request.setUri(getUrl());
		request.setType(getType());
		request.setParams(getParam());
		return request;
	}

	@Override
	public Long format(String response) {
		Document document = Jsoup.parse(response);
		String numStr = document.getElementsByTag("string").text().trim().replace("\"", "");
		return Long.parseLong(numStr);
	}



	@Override
	public boolean varify(Long deviceNum) {
		return true;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
