package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicketPrivildge;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketPrivildgeService;
import com.vrv.vap.jpa.baseservice.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("myTicketPrivildge")
public class MyTicketPrivildgeController extends BaseController<MyTicketPrivildge, String> {

	@Autowired
	MyTicketPrivildgeService myTicketPrivildgeService;
	
	@Override
	protected BaseService<MyTicketPrivildge, String> getService() {
		return myTicketPrivildgeService;
	}


}
