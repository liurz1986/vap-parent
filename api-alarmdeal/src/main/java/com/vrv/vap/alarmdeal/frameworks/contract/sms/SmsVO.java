package com.vrv.vap.alarmdeal.frameworks.contract.sms;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class SmsVO {
	 private String id; //主键
	 private String recipient; //接收人
	 private String content; //文件内容
	 private String alarmName;


	public Map<String,Object> toMessage(){
		Map<String,Object> map=new HashMap<>();
		map.put("phone",this.getRecipient());
		map.put("content",this.getContent());
		return  map;
	}
}
