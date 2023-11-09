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
* 类说明    工单回调请求
*
*/
public class FlowInfoCallBackRequest implements HttpRequestInf<FlowInfoResponse> {

	private String url;
	private String ticketId; //工单Id
    private String userId; //用户
	
	
	public FlowInfoCallBackRequest(){
		super();
	}
	
	public FlowInfoCallBackRequest(String url,String ticketId,String userId){
		this.url = url;
		this.ticketId = ticketId;
		this.userId = userId;
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
        map.put("userId", userId);
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}



	
	
}
