package com.vrv.vap.alarmdeal.business.flow.processdef.repository;


import com.vrv.vap.alarmdeal.business.flow.processdef.model.EmailTemplate;
import com.vrv.vap.jpa.basedao.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailTemplateRepository extends BaseRepository<EmailTemplate, String> {

}
