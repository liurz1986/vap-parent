package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.vrv.vap.alarmdeal.business.analysis.vo.ThreatExtraVO;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.ThreatExtra;
import com.vrv.vap.alarmdeal.business.analysis.repository.ThreatExtraRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月15日 下午3:57:21 
* 类说明   威胁额外说明
*/
@Service
public class ThreatExtraService extends BaseServiceImpl<ThreatExtra, String> {

	@Autowired
	private ThreatExtraRepository threatExtraRepository;

	@Autowired
	private MapperUtil mapper;
	
	
	@Override
	public ThreatExtraRepository getRepository() {
		return threatExtraRepository;
	}

	/**
	 * 根据威胁库获得威胁静态威胁信息
	 * @param threat_library_guid
	 * @return
	 */
	public Result<ThreatExtra> getThreatExtraInfo(String threat_library_guid){
		ThreatExtra threatExtra = getOne(threat_library_guid);
		Result<ThreatExtra> result = ResultUtil.success(threatExtra);
		return result;
	}
	
	/**
	 * 添加静态威胁详情
	 * @param threatExtra
	 * @return
	 */
	public Result<ThreatExtraVO> addThreatExtra(ThreatExtraVO threatExtra){
		try{
			ThreatExtra threatExtra1=null;
			mapper.copy(threatExtra,threatExtra1);
			if(threatExtra1 !=null){
				save(threatExtra1);
			}
			return ResultUtil.success(threatExtra);
		}catch(Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "保存静态威胁失败："+e.getMessage());
		}
	}
	
	/**
	 * 编辑威胁静态分析
	 * @param threatExtra
	 * @return
	 */
	public Result<ThreatExtraVO> editThreatExtra(ThreatExtraVO threatExtra){
		try{

			ThreatExtra threatExtra1=null;
			mapper.copy(threatExtra,threatExtra1);
			if(threatExtra1 !=null){
				save(threatExtra1);
			}
			return ResultUtil.success(threatExtra);
		}catch(Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "编辑静态威胁失败："+e.getMessage());
		}
	}
	
	
	
	
}
