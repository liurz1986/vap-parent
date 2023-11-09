package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.vrv.vap.alarmdeal.business.flow.processdef.service.FlowInnerFormService;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyticketTemplateService;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketInnerForm;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketTemplate;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.FlowInnerFormTreeVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("flowInnerForm")
public class FlowInnerFormController extends BaseController<MyticketInnerForm, String> {

	@Autowired
    FlowInnerFormService flowInnerFormService;
	@Autowired
    MyticketTemplateService myticketTemplateService;
	
	@Override
	protected BaseService<MyticketInnerForm, String> getService() {
		return flowInnerFormService;
	}


	/**
	 * 获得表单模板树
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getFormTree")
	@ApiOperation(value = "获得表单模板树", notes = "")
	@SysRequestLog(description="获得表单模板树", actionType = ActionType.SELECT,manually=false)
	public List<FlowInnerFormTreeVO> getVRVFormTree() {
		List<FlowInnerFormTreeVO> result = new ArrayList<FlowInnerFormTreeVO>();
		List<MyticketInnerForm> list = flowInnerFormService.findAll();
		for (MyticketInnerForm innerform : list) {
			FlowInnerFormTreeVO vo = new FlowInnerFormTreeVO();
			vo.setKey(innerform.getGuid());
			vo.setTitle(innerform.getText());
			vo.setValue(innerform.getGuid());
			vo.setParentId(innerform.getParentId());
			vo.setComplete(true);
			vo.setFormInfosGuid(innerform.getFormInfosGuid());
			vo.setProcessDesc(innerform.getProcessDesc());
			vo.setFormType("inner");
			vo.setFormVersion("old");
			result.add(vo);
		}
		List<MyticketTemplate> findAll = myticketTemplateService.findAll();
		for (MyticketTemplate template : findAll) {
			FlowInnerFormTreeVO vo = new FlowInnerFormTreeVO();
			vo.setKey(template.getGuid());
			vo.setTitle(template.getName());
			vo.setValue(template.getGuid());
			vo.setParentId("1");
			vo.setComplete(true);
			vo.setFormInfosGuid("");
			vo.setProcessDesc(template.getFormData());
			vo.setFormType("template");
			vo.setFormVersion(template.getFormVersion());
			result.add(vo);
		}
		
		return result;
	}
}
