package com.vrv.vap.alarmdeal.business.flow.processdef.service;

import com.vrv.vap.alarmdeal.business.flow.processdef.repository.MyticketTemplateRepository;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketTemplate;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyticketTemplateService extends BaseServiceImpl<MyticketTemplate, String> {

	@Autowired
    MyticketTemplateRepository myticketTemplateRepository;
	
	@Override
	public BaseRepository<MyticketTemplate, String> getRepository() {
		return myticketTemplateRepository;
	}

	public boolean exitsName(String name) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", name));
		boolean exists = exists(conditions);
		
		return exists;
	}

}
