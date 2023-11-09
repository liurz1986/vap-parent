package com.vrv.vap.admin.service;


import com.vrv.vap.admin.vo.MailVO;
import com.vrv.vap.common.vo.Result;

public interface SendMailService {

	/**
	 * 使用html格式的context发送多个人，不用Feign接口调用
	 * @param mail
	 * @return
	 */
	public Result sendHtmlEmailToManyDirect(MailVO mail);
	
	/**
	 * 发送Html格式邮件
	 * @param mail
	 * @return
	 */
	public Result sendHtmlEmail(MailVO mail);

	/**
	 * 发送简单的邮件
	 * @param mail
	 * @return
	 */
	public Result sendSimpleEmail(MailVO mail);

	/**
	 * 发送待附件的邮件
	 * @param mail
	 * @return
	 */
	public Result sendAttachmentsMail(MailVO mail);

	/**
	 * 发送模板邮件
	 * @param mail
	 * @return
	 */
	public Result sendTemplateMail(MailVO mail);
}
