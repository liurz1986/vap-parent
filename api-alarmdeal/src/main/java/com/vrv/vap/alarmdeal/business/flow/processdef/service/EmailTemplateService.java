package com.vrv.vap.alarmdeal.business.flow.processdef.service;


import com.vrv.vap.alarmdeal.business.flow.processdef.model.EmailTemplate;
import com.vrv.vap.alarmdeal.business.flow.processdef.repository.EmailTemplateRepository;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService extends BaseServiceImpl<EmailTemplate, String> {
    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    @Override
    public BaseRepository<EmailTemplate, String> getRepository() {
        return emailTemplateRepository;
    }
}
