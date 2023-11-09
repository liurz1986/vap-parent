package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.dao.AlarmWhitelistDao;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AlarmWhitelist;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.repository.AlarmWhitelistRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmWhitelistService;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmWhitelistQueryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmWhitelistVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 告警白名单
 */
@Service("alarmWhitelistService")
public class AlarmWhitelistServiceImpl extends BaseServiceImpl<AlarmWhitelist, String> implements AlarmWhitelistService {

	
	private static Logger logger = LoggerFactory.getLogger(AlarmWhitelistServiceImpl.class);

	@Autowired
	private AlarmWhitelistDao alarmWhitelistDao;

	@Autowired
	private MapperUtil mapper;

	@Override
	public AlarmWhitelistRespository getRepository() {
		return alarmWhitelistRespository;
	}

	@Autowired
	private AlarmWhitelistRespository alarmWhitelistRespository;

	@Override
	public PageRes<AlarmWhitelistVO> getAlarmWhitelistPager(AlarmWhitelistQueryVO alarmWhitelistQueryVO, Pageable pageable) {
		PageRes<AlarmWhitelistVO> result = new PageRes<AlarmWhitelistVO>();

		String eventCategoryId = alarmWhitelistQueryVO.getEventCategoryId();
		String title = alarmWhitelistQueryVO.getTitle();
		String srcIp = alarmWhitelistQueryVO.getSrcIp();
		String destIp = alarmWhitelistQueryVO.getDestIp();

		List<QueryCondition> conditions = new ArrayList<>();
		if(StringUtils.isNotEmpty(eventCategoryId)){
			conditions.add(QueryCondition.like("eventCategoryId", eventCategoryId));
		}
		if(StringUtils.isNotEmpty(title)){
			conditions.add(QueryCondition.like("title", title));
		}
		if(StringUtils.isNotEmpty(srcIp)){
			conditions.add(QueryCondition.like("srcIp", srcIp));
		}
		if(StringUtils.isNotEmpty(destIp)){
			conditions.add(QueryCondition.like("destIp", destIp));
		}
		Page<AlarmWhitelist> pager = findAll(conditions, pageable);
		List<AlarmWhitelist> list = pager.getContent();
		List<AlarmWhitelistVO> mapList = mapper.mapList(list, AlarmWhitelistVO.class);
		result.setList(mapList);
		result.setCode(String.valueOf(ResultCodeEnum.SUCCESS.getCode()));
		result.setTotal(pager.getTotalElements());
		result.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		return result;
	}

	@Override
	public Result<Boolean> addAlarmWhitelist(AlarmWhitelistVO alarmWhitelistVO) {
		try {
			AlarmWhitelist alarmWhitelist = mapper.map(alarmWhitelistVO, AlarmWhitelist.class);
			alarmWhitelist.setGuid(UUIDUtils.get32UUID());
			alarmWhitelist.setUpdateTime(DateUtil.format(new Date()));
			save(alarmWhitelist);
			Result<Boolean> result = SocUtil.getBooleanResult(true);
			return result;
		} catch (AlarmDealException e) {
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
	}

	@Override
	public Result<Boolean> editAlarmWhitelist(AlarmWhitelistVO alarmWhitelistVO) {
		try {
			AlarmWhitelist alarmWhitelist = mapper.map(alarmWhitelistVO, AlarmWhitelist.class);
			alarmWhitelist.setUpdateTime(DateUtil.format(new Date()));
			save(alarmWhitelist);
			Result<Boolean> result = SocUtil.getBooleanResult(true);
			return result;
		} catch (AlarmDealException e) {
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
	}

	
	@Override
	public Result<Boolean> delAlarmWhitelists(List<String> ids) {
		try {
			for (String id : ids) {
				delete(id);
			}
			Result<Boolean> result = SocUtil.getBooleanResult(true);
			return result;
		} catch (AlarmDealException e) {
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
		}
	}

	@Override
    public List<Map<String, Object>> getOneExist(){
		return alarmWhitelistDao.getOneExist();
	}

	/**
	 * 表为空，不进行过滤；有白名单才过滤 true不过滤/flase过滤
	 * @return
	 */
	@Override
    public boolean existAlarmWhiteList(String riskEventId, String srcIps, String dstIps) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("eventCategoryId", riskEventId));
		conditions.add(QueryCondition.eq("srcIp", srcIps));
		conditions.add(QueryCondition.eq("destIp", dstIps));
		List<AlarmWhitelist> whitelists = findAll(conditions);
		if (whitelists.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}
}
