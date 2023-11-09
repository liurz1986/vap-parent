package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import lombok.Data;

import java.util.Map;

/**
 * 表单附件
 */
@Data
public class TicketAttachment {
	private String name;
	private String status;
	private Response response;
    @Data
    public
    class Response {
		private Integer code;
		private String message;
		private Map<String, Object> data;
	}

}


