package com.vrv.vap.alarmdeal.business.asset.service.impl;


import com.vrv.vap.alarmdeal.business.asset.model.Cabinet;
import com.vrv.vap.alarmdeal.business.asset.model.MachineRoom;
import com.vrv.vap.alarmdeal.business.asset.repository.CabinetRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.CabinetService;
import com.vrv.vap.alarmdeal.business.asset.service.MachineRoomService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CabinetVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * * 
 * 
 * @author wudi   
 * E‐mail:wudi@vrvmail.com.cn  
 * @version 创建时间：2019年1月17日 下午3:33:40
 * 类说明 ： 机房集合
 */
@Service
public class CabinetServiceImpl extends BaseServiceImpl<Cabinet, String> implements CabinetService {

	@Autowired
	private CabinetRepository cabinetRepository;
	@Autowired
	private AssetService assetService;
	@Autowired
	private MapperUtil mapper;
	@Autowired
	private MachineRoomService machineRoomService;
	@Autowired
	private AssetBaseDataService assetBaseDataService;
	@Override
	public CabinetRepository getRepository() {
		return cabinetRepository;
	}

	@Override
	public List<Cabinet> getCabinetGrid(String guid) {
		if (StringUtils.isNotEmpty(guid)) {
			List<QueryCondition> conditions = new ArrayList<QueryCondition>();
			conditions.add(QueryCondition.eq("roomGuid", guid));
			List<Cabinet> list = findAll(conditions);
			return list;
		} else {
			List<Cabinet> list = findAll();
			return list;
		}
	}

	@Override
	public List<AssetVO> getCabinetsAssets(String[] guid) {
		List<AssetVO> list = new ArrayList<>();
		List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		conditions.add(QueryCondition.in("cabinetGuid", guid));
		List<Asset> listAssets = assetService.findAll(conditions);
		List<BaseSecurityDomain> allDomain = assetBaseDataService.queryAllDomain();
		for (Asset asset : listAssets) {
			AssetVO vo = assetService.mapperVO(asset,allDomain);
			list.add(vo);
		}
		return list;
	}

	@Override
	public List<AssetVO> getSingleCabinetAssetDetail(String cabinetGuid) {
		/*List<QueryCondition> conditions = new ArrayList<QueryCondition>();
		conditions.add(QueryCondition.eq("cabinetGuid", cabinetGuid));
		List<Asset> list = assetService.findAll(conditions);
		List<AssetVO> mapList = mapper.mapList(list, AssetVO.class);
		if (mapList != null && mapList.size() > 0) {
			for (AssetVO vo : mapList) {
				AssetRiskValue av = assetRiskValueService.getOne(vo.getGuid());
				if (av != null) {
					vo.setWeakNessWorth(String.valueOf(av.getWeakNessWorth()));
					vo.setThreatenfreqWorth(String.valueOf(av
							.getThreatenfreqWorth()));
					vo.setRiskWorth(String.valueOf(av.getRiskWorth()));

				} else {
					vo.setWeakNessWorth("0");
					vo.setThreatenfreqWorth("0");
					vo.setRiskWorth("0");
				}
			}
		}
*/
		return null;
	
	}

	@Override
	public PageRes<CabinetVO> getCabinetPager(CabinetVO cabinetVO, Pageable pageable) {
		String code = cabinetVO.getCode();
		List<QueryCondition> cons = new ArrayList<>();
		if(StringUtils.isNotEmpty(code)){
			QueryCondition condition = QueryCondition.like("code", code);
			cons.add(condition);
		}
		Page<Cabinet> page = findAll(cons, pageable);
		List<Cabinet> content = page.getContent();
		List<CabinetVO> mapList = mapper.mapList(content, CabinetVO.class);
		for (CabinetVO cabinetVO2 : mapList) {
			MachineRoom machineroom = machineRoomService.getOne(cabinetVO2.getRoomGuid());
			if(machineroom!=null){
				cabinetVO2.setRoomName(machineroom.getCode());
			}
		}
		PageRes<CabinetVO> res = new PageRes<>();
		res.setList(mapList);
		res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
		res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
		res.setTotal(page.getTotalElements());
		return res;
	
	}

}
