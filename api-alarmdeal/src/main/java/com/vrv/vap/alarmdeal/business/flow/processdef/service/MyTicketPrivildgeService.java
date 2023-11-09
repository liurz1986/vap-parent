package com.vrv.vap.alarmdeal.business.flow.processdef.service;

import com.google.common.base.Strings;
import com.vrv.vap.alarmdeal.business.flow.processdef.repository.MyTicketPrivildgeRepository;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicketPrivildge;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyTicketPrivildgeService extends BaseServiceImpl<MyTicketPrivildge, String> {
	
	@Autowired
    MyTicketPrivildgeRepository myTicketPrivildgeRepository;

	@Override
	public BaseRepository<MyTicketPrivildge, String> getRepository() {
		return myTicketPrivildgeRepository;
	}

	public List<MyTicketPrivildge> getByTicketid(String myTicketGuid) {
		if(Strings.isNullOrEmpty(myTicketGuid)) {
			return new ArrayList<>();
		}
		
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("myTicketGuid", myTicketGuid));
		List<MyTicketPrivildge> findAll = findAll(conditions);
		
		return findAll;
	}

	public void deleteByTicketGuid(String guid) {
		List<MyTicketPrivildge> byTicketid = getByTicketid(guid);
		deleteInBatch(byTicketid);
	}

}
