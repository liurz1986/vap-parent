package com.vrv.vap.alarmdeal.business.asset.service;


import com.vrv.vap.alarmdeal.business.asset.model.Cabinet;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.business.asset.vo.CabinetVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CabinetService extends BaseService<Cabinet, String> {
	
	
	/**
	 * 获取机柜列表（无分页）
	 * @param guid
	 * @return
	 */
	public List<Cabinet> getCabinetGrid(String guid);
	
	
	/**
	 * 获得所有机柜中的资产
	 * @param guid
	 * @return
	 */
	public List<AssetVO> getCabinetsAssets(String[] guid);
	
	
	/**
	 * 获得单个机柜中的资产信息、风险权值
	 * @param cabinetGuid
	 * @return
	 */
	public List<AssetVO> getSingleCabinetAssetDetail(String cabinetGuid);
	
	
	/**
	 * 获取机房列表
	 * @param machineRoomVO
	 * @param pageable
	 * @return
	 */
	public PageRes<CabinetVO> getCabinetPager(CabinetVO cabinetVO, Pageable pageable);
	
}
