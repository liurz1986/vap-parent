package com.vrv.vap.admin.vo;


import com.vrv.vap.admin.model.Pair;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 邮件信息VO
 * @author wd-pc
 *
 */

@Data
@ApiModel(value="邮件对象")
public class MailVO {
	@ApiModelProperty(value="发送目的地址")
	private String sendTo; //发送目的地址
	/**
	 * 如果有多个人，一起发送，不需要多次调用
	 */
	@ApiModelProperty(value="发送多人地址")   
	private String[] directEmailAddress; //发送目的地址
	@ApiModelProperty(value="id")
	private String id; //主键
	
	@ApiModelProperty(value="标题")
	private String title; //标题
	@ApiModelProperty(value="发送内容")
	private String content; //发送内容
	@ApiModelProperty(value="模板参数")
	private Map<String,Object> params; //模板参数
	@ApiModelProperty(value="模板标题")
	private String tag; //模板标题
	@ApiModelProperty(value="附件")
	private List<Pair<String, File>> attachments; //附件
}

