package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableField;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.DimensionFieldsVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;


/**
 * 维表字段service层
 * @author Administrator
 *
 */
public interface DimensionTableFieldService extends BaseService<DimensionTableField, String> {

	
	/**
	 * 获得维表字段列表
	 * @param dimensionFieldsVO
	 * @return
	 */
	public PageRes<DimensionTableField> getDimensionTableFieldPager(DimensionFieldsVO dimensionFieldsVO, Pageable pageable);
	
	
	/**
	 * 判断是否存在重复字段
	 * @param tableGuid
	 * @param fieldName
	 * @return
	 */
	public boolean judgeIsExistRepeatField(String tableGuid,String fieldName);

	
}
