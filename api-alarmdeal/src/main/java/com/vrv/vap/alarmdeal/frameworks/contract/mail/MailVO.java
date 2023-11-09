package com.vrv.vap.alarmdeal.frameworks.contract.mail;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
/**
 * 邮件信息VO
 * @author wd-pc
 *
 */

@Data
public class MailVO {
    private String id;
	private String sendTo; //发送目的地址
	private String title; //标题
	private String content; //发送内容
	private Map<String,Object> params; //模板参数
	private String tag; //模板标题
	private List<Pair<String, File>> attachments; //附件
	private String alarmName;

	public Map<String,Object> toMessage(){
		Map<String,Object> map=new HashMap<>();
		map.put("sendTo",sendTo);
		map.put("title",title);
		map.put("content",content);
		map.put("params",params);
		map.put("tag",tag);
		map.put("attachments",attachments);
		return map;
	}

	
}
