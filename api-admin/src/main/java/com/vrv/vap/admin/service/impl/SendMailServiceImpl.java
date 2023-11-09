package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.common.config.MailConfiguration;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.TagEnum;
import com.vrv.vap.admin.model.Pair;
import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.service.SendMailService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.vo.MailVO;
import com.vrv.vap.common.vo.Result;
import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class SendMailServiceImpl implements SendMailService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SystemConfigService systemConfigService;

	@Autowired
	private MailConfiguration mailConfiguration;


	//private JavaMailSenderImpl mailSender ;


	@Override
	public Result sendHtmlEmail(MailVO mail) {
		JavaMailSenderImpl mailSender = buildMailSender();
		Result result = null;
		String sendTo = mail.getSendTo();
		String title = mail.getTitle();
		String content = mail.getContent();
		if (StringUtils.isEmpty(sendTo)) {
			throw new RuntimeException("目标邮件地址为空！请修改sendTo的属性值！");
		}
		try {
			logger.info(String.format("发送邮件,sendto:%s, title:%s, content:%s", sendTo, title, content));
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(getMailSenderConfig("mail_username"));
			helper.setTo(sendTo);
			helper.setSubject(title);
			helper.setText("text/html;charset=UTF-8", content);
			mailSender.send(mimeMessage);
			logger.info(String.format("success!!! 发送邮件成功,sendto:%s, title:%s, content:%s", sendTo, title, content));
			result = new Result(Const.CODE_SUCCESS, Const.RETURN_SUCCESS);
		} catch (Exception e) {
			logger.error(String.format("邮件发送报错信息，发送邮件,sendto:%s, title:%s, content:%s", sendTo, title, content));
			logger.error(e.getMessage());
			result = new Result(Const.CODE_ERROR_SERVICE, e.getMessage());
		}
		return result;
	}

	@Override
	public Result sendSimpleEmail(MailVO mail) {
		JavaMailSenderImpl mailSender = buildMailSender();
		Result result = null;
		String[] directEmailAddress = mail.getDirectEmailAddress();
		String sendTo = mail.getSendTo();
		String title = mail.getTitle();
		String content = mail.getContent();
		try {
			if (ArrayUtils.isNotEmpty(directEmailAddress)) {
				for (String _sendTo : directEmailAddress) {
					logger.info(String.format("发送邮件,sendto:%s, title:%s, content:%s", _sendTo, title, content));
					SimpleMailMessage message = new SimpleMailMessage();
					message.setFrom(getMailSenderConfig("mail_username"));
					message.setTo(_sendTo);
					message.setSubject(title);
					message.setText(content);
					mailSender.send(message);
					logger.info(String.format("success!!! 发送邮件成功,sendto:%s, title:%s, content:%s", _sendTo, title, content));
				}
			} else {
				logger.info(String.format("发送邮件,sendto:%s, title:%s, content:%s", sendTo, title, content));
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(getMailSenderConfig("mail_username"));
				message.setTo(sendTo);
				message.setSubject(title);
				message.setText(content);
				mailSender.send(message);
				logger.info(String.format("success!!! 发送邮件成功,sendto:%s, title:%s, content:%s", sendTo, title, content));
			}
			result = new Result(Const.CODE_SUCCESS, Const.RETURN_SUCCESS);
		} catch (Exception e) {
			logger.error("邮件发送报错信息：" + e.getMessage());
			result = new Result(Const.CODE_ERROR_SERVICE, e.getMessage());
		}
		return result;
	}

	@Override
	public Result sendAttachmentsMail(MailVO mail) {
		JavaMailSenderImpl mailSender = buildMailSender();
		Result result = null;
		String sendTo = mail.getSendTo();
		String content = mail.getContent();
		String title = mail.getTitle();
		List<Pair<String, File>> list = mail.getAttachments();
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				helper.setFrom(getMailSenderConfig("mail_username"));
				helper.setTo(sendTo);
				helper.setSubject(title);
				helper.setText(content);

				for (Pair<String, File> pair : list) {
					helper.addAttachment(pair.getLeft(), new FileSystemResource(pair.getRight()));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			mailSender.send(mimeMessage);

			result = new Result(Const.CODE_SUCCESS, Const.RETURN_SUCCESS);
		} catch (RuntimeException e) {
			result = new Result(Const.CODE_SUCCESS, e.getMessage());
		}
		return result;
	}

	@Override
	public Result sendTemplateMail(MailVO mail) {
		JavaMailSenderImpl mailSender = buildMailSender();
		Result result = null;
		String sendTo = mail.getSendTo();
		Map<String, Object> params = mail.getParams();
		String title = mail.getTitle();
		String tag = mail.getTag();
		String content = mail.getContent();
		String template = getTemplateByTag(tag);
		List<Pair<String, File>> list = mail.getAttachments();

		MimeMessage mimeMessage = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(getMailSenderConfig("mail_username"));
			helper.setTo(sendTo);
			helper.setSubject(title);
//			Template temp = configurer.getConfiguration().getTemplate(template);
//			String text = FreeMarkerTemplateUtils.processTemplateIntoString(temp, content);
//			helper.setText(text, true);
			if (list != null) {
				for (Pair<String, File> pair : list) {
					helper.addAttachment(pair.getLeft(), new FileSystemResource(pair.getRight()));
				}
			}
			mailSender.send(mimeMessage);
			result = new Result(Const.CODE_SUCCESS, Const.RETURN_SUCCESS);
		} catch (Exception e) {
			result = new Result(Const.CODE_SUCCESS, e.getMessage());
		}
		return result;
	}

	/**
	 * 通过tag获得对应的模板
	 * 
	 * @param tag
	 * @return
	 */
	private String getTemplateByTag(String tag) {
		String template = TagEnum.getValue(tag);
		return template;
	}

	/**
	 * 只是针对directEmailAddress为多个人的时候
	 */
	@Override
	public Result sendHtmlEmailToManyDirect(MailVO mail) {
		JavaMailSenderImpl mailSender = buildMailSender();
		Result result = null;
		String[] directEmailAddress = mail.getDirectEmailAddress();
		String title = mail.getTitle();
		String content = mail.getContent();
		if (ArrayUtils.isNotEmpty(directEmailAddress)) {
			for (String sendTo : directEmailAddress) {
				try {
					logger.info(String.format("发送邮件,sendto:%s, title:%s, content:%s", sendTo, title, content));
					MimeMessage mimeMessage = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
					helper.setFrom(getMailSenderConfig("mail_username"));
					helper.setTo(sendTo);
					helper.setSubject(title);
					helper.setText("text/html;charset=UTF-8", content);
					mailSender.send(mimeMessage);
					logger.info(
							String.format("success!!! 发送邮件成功,sendto:%s, title:%s, content:%s", sendTo, title, content));
					result = new Result(Const.CODE_SUCCESS, Const.RETURN_SUCCESS);
				} catch (Exception e) {
					logger.error(
							String.format("邮件发送报错信息，发送邮件,sendto:%s, title:%s, content:%s", sendTo, title, content));
					logger.error(e.getMessage());
					result = new Result(Const.CODE_ERROR_SERVICE, e.getMessage());
				}
			}
		} else {
			logger.error("邮件发送报错信息，发送邮件directEmailAddress数组属性没有值, mailVO数据填充错误！");
			result = new Result(Const.CODE_ERROR_SERVICE, "directEmailAddress数组属性没有值！！");
		}

		return result;
	}

	private String getMailSenderConfig(String configId){
		List<SystemConfig> all = systemConfigService.findAll();
		SystemConfig systemConfig = null;
		for(SystemConfig config : all) {
			if(config.getConfId().equals(configId))
			{
				systemConfig=config;
				break;
			}
		}
		return systemConfig==null?null:systemConfig.getConfValue();
	}

	public JavaMailSenderImpl buildMailSender(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        String mailHost = getMailSenderConfig("mail_host");
        if(StringUtils.isEmpty(mailHost)){
            mailHost = mailConfiguration.getHost();
        }
       Integer mailPort;
        if(StringUtils.isEmpty(getMailSenderConfig("mail_port"))){
            mailPort = mailConfiguration.getPort();
        }
        else {
            mailPort = Integer.valueOf(getMailSenderConfig("mail_port"));
        }
		mailSender.setHost(mailHost);
		mailSender.setPort(mailPort);
		mailSender.setUsername(getMailSenderConfig("mail_username"));
		mailSender.setPassword(getMailSenderConfig("mail_password"));
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.auth",mailConfiguration.getAuth());
		javaMailProperties.put("mail.smtp.timeout", mailConfiguration.getTimeout());
		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}

}