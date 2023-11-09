package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import java.util.ArrayList;
import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.DimensionFieldsVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.analysis.repository.DimensionTableFieldRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableFieldService;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;

/**
 * 维表实现接口层
 * @author Administrator
 *
 */
@Service
public class DimensionTableFieldServiceImpl extends BaseServiceImpl<DimensionTableField, String> implements DimensionTableFieldService  {

	@Autowired
	private DimensionTableFieldRespository dimensionTableFieldRespository;

	@Override
	public DimensionTableFieldRespository getRepository() {
		return dimensionTableFieldRespository;
	}

	@Override
	public PageRes<DimensionTableField> getDimensionTableFieldPager(DimensionFieldsVO dimensionFieldsVO,
																	Pageable pageable) {
		String tableGuid = dimensionFieldsVO.getTableGuid();
		if(StringUtils.isEmpty(tableGuid)) {
			throw new RuntimeException("维表Guid为空，请检查！");
		}
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("tableGuid", tableGuid));
		String fieldName = dimensionFieldsVO.getFieldName();
		if(StringUtils.isNotEmpty(fieldName)) {
			conditions.add(QueryCondition.like("fieldName", fieldName));
		}
		Page<DimensionTableField> page = findAll(conditions, pageable);
		PageRes<DimensionTableField> pageRes = PageRes.toRes(page);
		return pageRes;
	}

	@Override
	public boolean judgeIsExistRepeatField(String tableGuid, String fieldName) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("tableGuid", tableGuid));
		conditions.add(QueryCondition.eq("fieldName", fieldName));
		long count = count(conditions);
		if(count>0) {
			return false;
		}
		return true;
	}
	
	
	

}
