package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.analysis.model.WarnManager;
import com.vrv.vap.alarmdeal.business.analysis.vo.WarnManagerVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

import com.vrv.vap.jpa.web.Result;

public interface WarnManagerServer extends BaseService<WarnManager, String> {
    /**
     * 预警管理信息列表查询
     * @param warnManagerVO
     * @return
     */
	public PageRes<WarnManager> getWarnManagerListByPager(WarnManagerVO warnManagerVO, Pageable pageable);
	
	/**
	 * 添加预警信息
	 */
	public Result<Boolean> saveWarnMessage(WarnManager warnManager);
	
	
	/**
	 * 编辑预警信息
	 * @param warnManager
	 * @return
	 */
	public Result<Boolean> editWarnMessage(WarnManager warnManager);
	
	
}
