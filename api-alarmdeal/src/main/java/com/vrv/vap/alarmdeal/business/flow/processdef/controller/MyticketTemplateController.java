package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.vrv.vap.alarmdeal.business.flow.processdef.model.MyticketTemplate;
import com.vrv.vap.alarmdeal.business.flow.processdef.service.MyticketTemplateService;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.TicketVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("myticketTemplate")
public class MyticketTemplateController extends BaseController<MyticketTemplate, String> {
	
	@Autowired
	MyticketTemplateService myticketTemplateService;

	@Override
	protected BaseService<MyticketTemplate, String> getService() {
		return myticketTemplateService;
	}


	final String[] DISALLOWED_FIELDS = new String[]{"", "",
			""};

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	@RequestMapping("checkName")
	public Result<Boolean> checkName(String name){
		boolean result = myticketTemplateService.exitsName(name);
		
		return ResultUtil.success(!result);
	}
	
	
	@PostMapping("addMyTicketTemplate")
	@ApiOperation("新增表单模板")
	@SysRequestLog(description="新增表单模板", actionType = ActionType.ADD,manually = false)
	public Result<MyticketTemplate> addMyTicketTemplate(@RequestBody TicketVO ticketVO, BindingResult bindingResult){
		MyticketTemplate model = ticketVO.getMyticketTemplate();
		if(model.getGuid() == null) {
			model.setGuid(UUIDUtils.get32UUID());
		}
		 MyticketTemplate myticketTemplate = myticketTemplateService.save(model);
		return ResultUtil.success(myticketTemplate);
	}
	
	
	@PostMapping("editMyTicketTemplate")
	@ApiOperation("编辑表单模板")
	@SysRequestLog(description="编辑表单模板", actionType = ActionType.UPDATE,manually = false)
	public Result<MyticketTemplate> editMyTicket(@RequestBody TicketVO ticketVO, BindingResult bindingResult) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		MyticketTemplate model = ticketVO.getMyticketTemplate();
		return super.edit(model, bindingResult);
	}
	
	@GetMapping("delMyTicketTemplate/{guid}")
	@ApiOperation("删除表单模板")
	@SysRequestLog(description="删除表单模板", actionType = ActionType.DELETE,manually = false)
	public Result<Boolean> delMyTicketTemplate(@PathVariable String guid){
		Result<Boolean> delete = super.delete(guid);
		return delete;
	}
	
	@GetMapping("getMyTicketTemplateList")
	@ApiOperation("查询表单模板")
	@SysRequestLog(description="查询表单模板", actionType = ActionType.SELECT,manually = false)
	public Result<List<MyticketTemplate>> getMyTicketTemplateList(){
		List<MyticketTemplate> list = myticketTemplateService.findAll();
		return ResultUtil.success(list);
	}

}
