package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetTypeGroupRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeGroupVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetTypeGroupServiceImpl extends BaseServiceImpl<AssetTypeGroup, String> implements AssetTypeGroupService {

	@Autowired
	private AssetTypeGroupRepository assetTypeGroupRepository;
	@Autowired
	private MapperUtil mapper;
	
	@Override
	public AssetTypeGroupRepository getRepository() {
		return assetTypeGroupRepository;
	}
	
	@Override 
	public AssetTypeGroup save(AssetTypeGroup entity){
		if(entity.getPredefine()==null) {
			entity.setPredefine(false);
		}
		return super.save(entity);
	}

	@Override
	public List<AssetTypeTreeVO> mapperTreeVO(List<AssetTypeGroup> assetTypeGroups) {
     
		List<AssetTypeTreeVO> assetTypeTreeVOs = new ArrayList<>();
		
		for (AssetTypeGroup assetTypeGroup : assetTypeGroups) {
			AssetTypeTreeVO assetTypeTreeVO = new AssetTypeTreeVO();
			assetTypeTreeVO.setIconCls(assetTypeGroup.getIcon());
			assetTypeTreeVO.setTitle(assetTypeGroup.getName());
			String id = assetTypeGroup.getTreeCode();
			assetTypeTreeVO.setKey(id);
			String parentId = id.substring(0, id.lastIndexOf('-'));
			assetTypeTreeVO.setParentId(parentId);
			assetTypeTreeVO.setGuid(assetTypeGroup.getGuid());
			assetTypeTreeVO.setType(2);
			assetTypeTreeVO.setUniqueCode(assetTypeGroup.getUniqueCode());
			assetTypeTreeVO.setTreeCode(assetTypeGroup.getTreeCode());
			assetTypeTreeVOs.add(assetTypeTreeVO);
		}
		return assetTypeTreeVOs;
	}

	@Override
	public List<AssetTypeGroupVO> getAssetTypeGroupList(AssetSearchVO assetSearchVO) {
		String asset_type_name = assetSearchVO.getAsset_type_name();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.like("treeCode", assetSearchVO.getTreeCode()));
		if(StringUtils.isNoneEmpty(asset_type_name)){
			conditions.add(QueryCondition.like("name", asset_type_name));
		}
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<AssetTypeGroup> list = findAll(conditions,sort);
		List<AssetTypeGroupVO> mapList = mapper.mapList(list, AssetTypeGroupVO.class);
		return mapList;
	}

	 
	
}
