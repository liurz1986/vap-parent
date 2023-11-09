package com.vrv.vap.admin.feign;

import com.vrv.vap.admin.vo.MailVO;
import com.vrv.vap.common.vo.VData;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.service
 * @Author tongliang@VRV
 * @CreateTime 2019/04/08 14:20
 * @Description (远程调用server-syslog 对系统日志的导入和查询)
 * @Version
 */
@FeignClient(value = "server-sys", path = "/sendEmail", contextId = "email")
public interface EmailSendFeign {

	@PostMapping("/sendSimpleEmail")
	@ApiOperation(value = "发送简单的邮件", notes = "")
	public VData<Boolean> sendSimpleEmail(@RequestBody MailVO mailVO);

	@PostMapping("/htmlEmail")
	@ApiOperation(value = "发送html格式邮件", notes = "")
	public VData<Boolean> sendHtmlEmail(@RequestBody MailVO mailVO);


	@PostMapping("/sendHtmlEmailToManyDirect")
	@ApiOperation(value = "发送多个联系人html格式邮件", notes = "")
	public VData<Boolean> sendHtmlEmailToManyDirect(@RequestBody MailVO mailVO);

	@PostMapping("/sendAttachmentsMail")
	@ApiOperation(value = "发送带附件的邮件", notes = "")
	public VData<Boolean> sendAttachmentsMail(@RequestBody MailVO mailVO);


	@PostMapping("/sendTemplateMail")
	@ApiOperation(value = "发送模板邮件", notes = "")
	public VData<Boolean> sendTemplateMail(@RequestBody MailVO mailVO);


}
