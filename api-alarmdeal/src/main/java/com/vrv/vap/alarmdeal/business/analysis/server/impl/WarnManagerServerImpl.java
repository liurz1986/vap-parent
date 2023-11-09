package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vrv.vap.alarmdeal.business.analysis.enums.WarnStatus;
import com.vrv.vap.alarmdeal.business.analysis.model.WarnManager;
import com.vrv.vap.alarmdeal.business.analysis.vo.WarnManagerVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.analysis.repository.WarnManagerRepository;
import com.vrv.vap.alarmdeal.business.analysis.server.WarnManagerServer;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
@Service
public class WarnManagerServerImpl extends BaseServiceImpl<WarnManager, String> implements WarnManagerServer {

	@Autowired
	private WarnManagerRepository warnManagerRepository;
	
	@Override
	public WarnManagerRepository getRepository() {
		return warnManagerRepository;
	}


	@Override
	public PageRes<WarnManager> getWarnManagerListByPager(WarnManagerVO warnManagerVO, Pageable pageable) {
		PageRes<WarnManager> pageRes = new PageRes<WarnManager>();
		//查询条件
		String warnName = warnManagerVO.getWarnName();
		String warnTypeName = warnManagerVO.getWarnType();
		String warnSuperType = warnManagerVO.getWarnSuperType();
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		
		if (StringUtils.isNotEmpty(warnName)) {
			conditions.add(QueryCondition.like("warnName", warnName));
		}
		if (StringUtils.isNotEmpty(warnTypeName)) {
			conditions.add(QueryCondition.like("warnType", warnTypeName));
		}
		if (StringUtils.isNotEmpty(warnSuperType)) {
			conditions.add(QueryCondition.eq("warnSuperType", warnSuperType));
		}
		
		Page<WarnManager> page = findAll(conditions, pageable);
		List<WarnManager> list = page.getContent();
		long total = page.getTotalElements();
		pageRes.setList(list);
		pageRes.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		pageRes.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		pageRes.setTotal(total);
		return pageRes;
	}


	@Override
	public Result<Boolean> saveWarnMessage(WarnManager warnManager) {
		Result<Boolean> result = new Result<>();
		warnManager.setGuid(UUIDUtils.get32UUID());
		warnManager.setCreateTime(DateUtil.format(new Date()));
		warnManager.setWarnSuperType(warnManager.getWarnSuperType());
		warnManager.setWarnStatus("1");
		warnManager.setWarnStatusName(WarnStatus.LOW.getName());
		try {
			save(warnManager);
		}catch(Exception e){
			result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
			result.setData(false);
			result.setMsg(e.getMessage());
			return result;
		}
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}


	@Override
	public Result<Boolean> editWarnMessage(WarnManager warnManager) {
		Result<Boolean> result = new Result<>();
		try {
			save(warnManager);
		}catch(Exception e){
			result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
			result.setData(false);
			result.setMsg(e.getMessage());
			return result;
		}
		result.setCode(ResultCodeEnum.SUCCESS.getCode());
		result.setData(true);
		result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}


}
