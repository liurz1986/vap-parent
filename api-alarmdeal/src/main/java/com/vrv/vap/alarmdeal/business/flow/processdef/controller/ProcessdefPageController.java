package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.google.common.base.Strings;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicket;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyTicketPrivildge;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.PrivildgeTypeEnum;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.ProcessStateEnum;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketPrivildgeService;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyTicketService;
import com.vrv.vap.jpa.json.JsonMapper;
import org.activiti.engine.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("processdefPage")
public class ProcessdefPageController {
	
	@Autowired
	MyTicketService myticketService;
	@Autowired
    RepositoryService repositoryService;
	@Autowired
	private MyTicketPrivildgeService myTicketPrivildgeService;
	
	@RequestMapping("myticket")
	public String myticket() {
		return "myticket/myticketInfo";
	}
	
	@RequestMapping("myticket/edit")
	public String areaEdit(String myticketGuid, Model model) {
		MyTicket editTicket = null;
		if(!Strings.isNullOrEmpty(myticketGuid)) {
			editTicket = myticketService.getOne(myticketGuid);
		}
		if(editTicket == null) {
			editTicket = new MyTicket();
			editTicket.setPrivildgeType(PrivildgeTypeEnum.all);
			editTicket.setOrderNum(myticketService.getNewOrder());
			editTicket.setTicketStatus(ProcessStateEnum.draft);
		}
		List<MyTicketPrivildge> privildge = myTicketPrivildgeService.getByTicketid(editTicket.getGuid());
		String jsonString = JsonMapper.toJsonString(privildge);
		editTicket.setPersonSelect(jsonString);
		
		model.addAttribute("myticket", editTicket);
		return "myticket/myticketEdit";
	}
	
	@RequestMapping("myticketmodel")
	public String myticketmodel(String modelId, String deployId, Model model) {
		model.addAttribute("modelId", modelId);
		if(modelId != null) {
			org.activiti.engine.repository.Model jmodel = repositoryService.createModelQuery().modelId(modelId).singleResult();
			model.addAttribute("model", jmodel);
		}
		model.addAttribute("deployId", deployId);
		return "myticket/myticketmodel";
	}
}
