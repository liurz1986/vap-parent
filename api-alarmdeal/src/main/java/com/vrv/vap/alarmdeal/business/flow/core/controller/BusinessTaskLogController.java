package com.vrv.vap.alarmdeal.business.flow.core.controller;

import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessTaskLog;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskLogService;
import com.vrv.vap.alarmdeal.business.flow.core.vo.BusinessTicketVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.controller.BaseController;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("businessTaskLog")
@Api("日志记录")
public class BusinessTaskLogController extends BaseController<BusinessTaskLog, String> {

	@Autowired
	private BusinessTaskLogService businessTaskLogService;
	
	@Override
	protected BaseService<BusinessTaskLog, String> getService() {
		return businessTaskLogService;
	}


	final String[] DISALLOWED_FIELDS = new String[]{"", "",
			""};

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields(DISALLOWED_FIELDS);
	}

	/**
	 * 日志记录查询
	 * @param businessTicketVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("businessTicketLog")
	@ApiOperation(value="处理工单的历史记录",notes="")
	@SysRequestLog(description="处理工单的历史记录", actionType = ActionType.SELECT,manually = false)
	public PageRes<BusinessTaskLog> page(@RequestBody BusinessTicketVO businessTicketVO, PageReq pageReq){
		Integer start_ = businessTicketVO.getStart_();
		Integer count_ = businessTicketVO.getCount_();
		pageReq.setCount(count_);
		pageReq.setStart(start_);
		pageReq.setOrder("time"); //按时间的倒序进行排序
		pageReq.setBy("desc");
		Pageable pageable = PageReq.getPageable(pageReq, true);
		BusinessTaskLog businessTaskLog = businessTicketVO.getBusinessTaskLog();
		Page<BusinessTaskLog> findAll = getService().findAll(businessTaskLog, pageable);
		return PageRes.toRes(findAll);
	}
}
