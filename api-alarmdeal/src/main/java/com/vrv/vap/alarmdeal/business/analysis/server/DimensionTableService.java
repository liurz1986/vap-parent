package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.DimensionTableVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;


/**
 * 维表service层
 * @author Administrator
 *
 */
public interface DimensionTableService extends BaseService<DimensionTableInfo, String> {

	
	/**
	 * 获得维表列表
	 * @param dimensionTableVO
	 * @return
	 */
	public PageRes<DimensionTableInfo> getDimensionTableInfoPager(DimensionTableVO dimensionTableVO, Pageable pageable);
	

	/**
	 * 删除维表 
	 * @param id
	 * @return
	 */
    public DimensionTableInfo deleteDimensionById(String id);


	/**
	 * 根据indexName查询到对应的维表名称
	 * @param indexName
	 * @return
	 */
	public DimensionTableInfo getDimensionTableByIndex(String indexName);

	
}
