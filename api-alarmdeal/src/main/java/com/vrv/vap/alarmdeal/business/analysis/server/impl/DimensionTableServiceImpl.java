package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.DimensionTableVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.analysis.repository.DimensionTableRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableFieldService;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableService;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;

/**
 * 维表实现接口层
 * @author Administrator
 *
 */
@Service
public class DimensionTableServiceImpl extends BaseServiceImpl<DimensionTableInfo, String> implements DimensionTableService  {

	
	private static Logger logger = LoggerFactory.getLogger(DimensionTableServiceImpl.class);
	
	@Autowired
	private DimensionTableRespository dimensionTableRespository;

	@Autowired
	private DimensionTableFieldService dimensionTableFieldService;
	
	
	@Override
	public DimensionTableRespository getRepository() {
		return dimensionTableRespository;
	}

	@Override
	public PageRes<DimensionTableInfo> getDimensionTableInfoPager(DimensionTableVO dimensionTableVO,
																  Pageable pageable) {
		String nameEn = dimensionTableVO.getNameEn();
		List<QueryCondition> conditions = new ArrayList<>();
		if(StringUtils.isNotEmpty(nameEn)) {
			conditions.add(QueryCondition.like("nameEn", nameEn));			
		}
		Page<DimensionTableInfo> page = findAll(conditions, pageable);
		PageRes<DimensionTableInfo> pageRes = PageRes.toRes(page);
		return pageRes;
	}

	@Override
	public DimensionTableInfo deleteDimensionById(String id) {
		DimensionTableInfo dimensionTableInfo = getOne(id);
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("tableGuid", id));
		List<DimensionTableField> list = dimensionTableFieldService.findAll(conditions);
		try {
			delete(dimensionTableInfo);
			dimensionTableFieldService.deleteInBatch(list);
		}catch(Exception e) {
			logger.error("删除维表异常，请检查：{}", e);
		}
		return dimensionTableInfo;
	}

	@Override
	public DimensionTableInfo getDimensionTableByIndex(String indexName) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("baselineIndex", indexName));
		List<DimensionTableInfo> dimensionTableInfos = findAll(conditions);
		if(dimensionTableInfos != null && dimensionTableInfos.size() > 0) {
			return dimensionTableInfos.get(0);
		}
		return null;
	}


}
