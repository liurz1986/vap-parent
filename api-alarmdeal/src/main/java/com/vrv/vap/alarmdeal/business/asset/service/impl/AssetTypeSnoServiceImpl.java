package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.repository.AssetTypeSnoRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeSnoVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
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
public class AssetTypeSnoServiceImpl extends BaseServiceImpl<AssetTypeSno, String> implements AssetTypeSnoService {

	@Autowired
	private AssetTypeSnoRepository assetTypeSnoRepository;
	@Autowired
	private MapperUtil mapper;

	@Override
	public AssetTypeSnoRepository getRepository() {
		return assetTypeSnoRepository;
	}

	@Override
	public AssetTypeSno save(AssetTypeSno entity) {
		if (entity.getPredefine() == null) {
			entity.setPredefine(false);
		}
		return super.save(entity);
	}

	@Override
	public List<AssetTypeSnoVO> getAssetTypeSnoList(AssetSearchVO assetSearchVO) {
		String asset_type_name = assetSearchVO.getAsset_type_name();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.like("treeCode", assetSearchVO.getTreeCode()));
		if (!StringUtils.isEmpty(asset_type_name)) {
			conditions.add(QueryCondition.like("name", asset_type_name));
		}
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<AssetTypeSno> list = findAll(conditions, sort);
		List<AssetTypeSnoVO> mapList = mapper.mapList(list, AssetTypeSnoVO.class);
		return mapList;
	}

	@Override
	public List<AssetTypeTreeVO> mapperTreeVO(List<AssetTypeSno> assetTypeSnos) {
		List<AssetTypeTreeVO> assetTypeTreeVOs = new ArrayList<AssetTypeTreeVO>();
		for (AssetTypeSno assetTypeSno : assetTypeSnos) {
			AssetTypeTreeVO assetTypeTreeVO = new AssetTypeTreeVO();
			assetTypeTreeVO.setIconCls(assetTypeSno.getIcon());
			assetTypeTreeVO.setTitle(assetTypeSno.getName());
			String id = assetTypeSno.getTreeCode();
			assetTypeTreeVO.setKey(id);
			String parentId = id.substring(0, id.lastIndexOf('-'));
			assetTypeTreeVO.setParentId(parentId);
			assetTypeTreeVO.setGuid(assetTypeSno.getGuid());
			assetTypeTreeVO.setType(4);
			assetTypeTreeVO.setUniqueCode(assetTypeSno.getUniqueCode());
			assetTypeTreeVO.setTreeCode(assetTypeSno.getTreeCode());
			assetTypeTreeVOs.add(assetTypeTreeVO);
		}
		return assetTypeTreeVOs;
	}

	@Override
	public AssetTypeSno getAssetTypeSnoGuidByAssetTypeSnoName(String treeCode, String AssetTypeSnoName) {

		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", AssetTypeSnoName));
		conditions.add(QueryCondition.likeBegin("treeCode", treeCode));
		List<AssetTypeSno> list = findAll(conditions);
		if (list.size() > 0) {
			AssetTypeSno assetTypeSno = list.get(0);
			return assetTypeSno;
		} else {
			return null;
		}

	}

}
