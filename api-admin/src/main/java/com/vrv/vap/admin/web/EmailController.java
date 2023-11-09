package com.vrv.vap.admin.web;


import com.vrv.vap.admin.service.SendMailService;
import com.vrv.vap.admin.vo.MailVO;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 发送邮件
 * 
 * @author wd-pc
 *
 */
@RestController
@RequestMapping("/sendEmail")
@Api(value = "邮件发送")
public class EmailController {

	@Autowired
	private SendMailService sendMailService;

	/**
	 * 发送简单邮件
	 * 
	 * @param mailVO
	 * @return
	 */
	@Ignore
	@PostMapping("/sendSimpleEmail")
	@ApiOperation(value = "发送简单的邮件", notes = "")
	public Result sendSimpleEmail(@RequestBody MailVO mailVO) {
		Result result = sendMailService.sendSimpleEmail(mailVO);
		return result;
	}

	/**
	 * 发送带html邮件
	 */
	@Ignore
	@PostMapping("/htmlEmail")
	@ApiOperation(value = "发送html格式邮件", notes = "")
	public Result sendHtmlEmail(@RequestBody MailVO mailVO) {
		Result result = sendMailService.sendHtmlEmail(mailVO);
		return result;
	}

	/**
	 * 发送多个联系人带html邮件
	 */
	@Ignore
	@PostMapping("/sendHtmlEmailToManyDirect")
	@ApiOperation(value = "发送多个联系人html格式邮件", notes = "")
	public Result sendHtmlEmailToManyDirect(@RequestBody MailVO mailVO) {
		Result result = sendMailService.sendHtmlEmailToManyDirect(mailVO);
		return result;
	}

	/**
	 * 发送带附件的邮件
	 * 
	 * @param mailVO
	 * @return
	 */
	@Ignore
	@PostMapping("/sendAttachmentsMail")
	@ApiOperation(value = "发送带附件的邮件", notes = "")
	public Result sendAttachmentsMail(@RequestBody MailVO mailVO) {
		Result result = sendMailService.sendAttachmentsMail(mailVO);
		return result;
	}

	/**
	 * 发送模板邮件
	 * 
	 * @param mailVO
	 * @return
	 */
	@Ignore
	@PostMapping("/sendTemplateMail")
	@ApiOperation(value = "发送模板邮件", notes = "")
	public Result sendTemplateMail(@RequestBody MailVO mailVO) {
		Result result = sendMailService.sendTemplateMail(mailVO);
		return result;
	}

}
