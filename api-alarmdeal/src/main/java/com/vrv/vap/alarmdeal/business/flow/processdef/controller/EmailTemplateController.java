package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.EmailTemplate;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.EmailTemplateService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 邮件监听器模板接口
 * 2023-1-3
 */
@RestController
@RequestMapping("emailTemplate")
public class EmailTemplateController {
    @Autowired
    private EmailTemplateService  emailTemplateService;
    @PostMapping("query")
    public Result<List<EmailTemplate>> getEmailTemplates(){
        List<EmailTemplate> emailTemplates = emailTemplateService.findAll();
        return ResultUtil.successList(emailTemplates);
    }
}
