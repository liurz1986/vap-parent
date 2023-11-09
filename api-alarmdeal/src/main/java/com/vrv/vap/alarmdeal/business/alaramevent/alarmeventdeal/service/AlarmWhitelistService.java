package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmWhitelist;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmWhitelistQueryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmWhitelistVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AlarmWhitelistService extends BaseService<AlarmWhitelist, String> {

		/**
		 * 获得告警白名单分页列表
		 * @param vo
		 * @param pageable
		 * @return
		 */

	 public PageRes<AlarmWhitelistVO> getAlarmWhitelistPager(AlarmWhitelistQueryVO vo, Pageable pageable);


	 /**
	  * 新增告警白名单
	  * @param vo
	  * @return
	  */
	 public Result<Boolean> addAlarmWhitelist(AlarmWhitelistVO vo);

	 /**
	  * 编辑告警白名单
	  * @param vo
	  * @return
	  */
	 public Result<Boolean> editAlarmWhitelist(AlarmWhitelistVO vo);

	 /**
	  * 删除告警白名单，支持批量删除
	  * @param ids
	  * @return
	  */
	 public Result<Boolean> delAlarmWhitelists(List<String> ids);

	public List<Map<String, Object>> getOneExist();

	/**
	 * 表为空，不进行过滤；有白名单才过滤  true不过滤/flase过滤
	 * @param riskEventId
	 * @param srcIps
	 * @param dstIps
	 * @return
	 */
	public boolean existAlarmWhiteList(String riskEventId, String srcIps, String dstIps);

}
