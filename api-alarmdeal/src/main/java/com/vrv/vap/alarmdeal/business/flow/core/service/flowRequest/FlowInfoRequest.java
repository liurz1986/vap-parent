package com.vrv.vap.alarmdeal.business.flow.core.service.flowRequest;

import com.vrv.vap.jpa.http.Request;
import com.vrv.vap.jpa.http.RequestTypeEnum;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.req.HttpRequestInf;

import java.util.HashMap;
import java.util.Map;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年3月14日 上午10:33:45 
* 类说明    dashBoard参数系数据集请求
*
*/
public class FlowInfoRequest implements HttpRequestInf<FlowInfoResponse> {

	private String url;
	private String ticketId; //工单Id
	private String mapRegionContent; //级联信息
	private String ticketContent; //工单信息
	private String upIp;
	
	public FlowInfoRequest(){
		super();
	}
	
	public FlowInfoRequest(String url,String ticketId,String mapRegionContent,String ticketContent,String upIp){
		this.url = url;
		this.ticketId = ticketId;
		this.mapRegionContent = mapRegionContent;
		this.ticketContent = ticketContent;
		this.upIp = upIp;
	}
	
	@Override
	public Request request() {
		Request request = new Request();
		request.setUri(getUrl());
		request.setType(RequestTypeEnum.post);
		request.setParams(getParams());
		return request;
	}
	
	private Map<String, String> getParams() {
		Map<String, String> map = new HashMap<>();
        map.put("ticketId", ticketId);
        map.put("mapRegionContent", mapRegionContent);
        map.put("ticketContent", ticketContent);
        map.put("upIp", upIp);
		return map;
	}

	@Override
	public FlowInfoResponse format(String response) {
		FlowInfoResponse dashBoardResponse = JsonMapper.fromJsonString(response, FlowInfoResponse.class);
		return dashBoardResponse;
	}

	@Override
	public boolean varify(FlowInfoResponse format) {
		if(format.getCode()==0){
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

	public String getTicketId() {
		return ticketId;
	}

	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}

	public String getMapRegionContent() {
		return mapRegionContent;
	}

	public void setMapRegionContent(String mapRegionContent) {
		this.mapRegionContent = mapRegionContent;
	}

	public String getTicketContent() {
		return ticketContent;
	}

	public void setTicketContent(String ticketContent) {
		this.ticketContent = ticketContent;
	}

	public String getUpIp() {
		return upIp;
	}

	public void setUpIp(String upIp) {
		this.upIp = upIp;
	}

	
	
}
