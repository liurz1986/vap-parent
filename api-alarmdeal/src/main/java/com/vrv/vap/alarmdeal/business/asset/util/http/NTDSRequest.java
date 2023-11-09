package com.vrv.vap.alarmdeal.business.asset.util.http;

import com.vrv.vap.jpa.http.Request;
import com.vrv.vap.jpa.http.RequestTypeEnum;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.req.HttpRequestInf;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

/**
 * ntds请求类
 */
public class NTDSRequest implements HttpRequestInf<Map<String,Object>> {
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

	public NTDSRequest() {
		super();
	}

	public NTDSRequest(String url) {
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
	public Map<String,Object> format(String response) {
		Document document = Jsoup.parse(response);
		String json = document.getElementsByTag("string").text();
		Map<String,Object> responseMap = (Map<String,Object>) JsonMapper.fromJsonString(json, Map.class);
		return  responseMap;
	}

	@Override
	public boolean varify(Map<String,Object> map) {
		return true;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
