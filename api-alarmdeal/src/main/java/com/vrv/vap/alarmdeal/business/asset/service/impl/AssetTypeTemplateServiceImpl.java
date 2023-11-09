package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetTypeTemplateRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeTemplateService;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AssetTypeTemplateServiceImpl extends BaseServiceImpl<AssetTypeTemplate, String>  implements AssetTypeTemplateService {

	@Autowired
	private AssetTypeTemplateRepository assetTypeTemplateRepository;

	@Override
	public BaseRepository<AssetTypeTemplate, String> getRepository() {
		// TODO Auto-generated method stub
		return assetTypeTemplateRepository;
	}

	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	@Autowired
	private AssetTypeService assetTypeService;
	@Autowired
	private AssetTypeGroupService assetTypeGroupService;

	public AssetTypeTemplate getAssetTypeTemplate(String guid) {
		try {
			AssetTypeTemplate one = this.getOne(guid);

			if (one == null)//自身没有
			{
				AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(guid);
				if (assetTypeSno != null)//是品牌型号   逐级找父类
				{

					AssetTypeTemplate parentTemplate = getParentTemplate(assetTypeSno.getTreeCode());
					return parentTemplate;
				} else {//传参不是品牌型号

					AssetType assetType = assetTypeService.getOne(guid);
					if (assetType != null)//传参是资产类型
					{
						AssetTypeTemplate parentTemplate = getParentTemplate(assetType.getTreeCode());
						return parentTemplate;
					} else {
						AssetTypeGroup assetTypeGroup = assetTypeGroupService.getOne(guid);
						if (assetTypeGroup != null) {
							AssetTypeTemplate parentTemplate = getParentTemplate(assetTypeGroup.getTreeCode());
							return parentTemplate;
						} else {
							throw new RuntimeException("传参异常：找不到所选择的数据，请刷新页面后重新操作");
						}
					}
				}

			} else {
				return one;
			}
		} catch (Exception e) {
			throw new RuntimeException("数据查询异常");
		}
	}

	public AssetTypeTemplate getParentTemplate(String treeCode) {
		if (treeCode.contains("-") && treeCode.split("-").length > 2) {
			Sort sort = Sort.by(Direction.DESC, "treeCode");
			List<QueryCondition> querys = new ArrayList<>();
			querys.add(QueryCondition.eq("status", 0));
			querys.add(QueryCondition.likeBegin("treeCode",
					treeCode.substring(0, treeCode.lastIndexOf("-")) + "-"));
			List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(querys, sort);

			for (AssetTypeSno itemSno : assetTypeSnos) {
				AssetTypeTemplate item = getOne(itemSno.getGuid());
				if (item != null) {
					return item;
				}
			}
			return getParentTemplate(treeCode.substring(0, treeCode.lastIndexOf("-")));
		} else {
			Sort sort = Sort.by(Direction.DESC, "treeCode");
			List<QueryCondition> querys = new ArrayList<>();
			querys.add(QueryCondition.eq("status", 0));
			querys.add(QueryCondition.likeBegin("treeCode", treeCode + "-"));
			List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(querys, sort);

			for (AssetTypeSno itemSno : assetTypeSnos) {
				AssetTypeTemplate item = getOne(itemSno.getGuid());
				if (item != null) {
					return item;
				}
			}
			return new AssetTypeTemplate();
		}

	}


	// 当前资产类型下所有三级类型
	public List<String> geteAssetTypeSnoGuids(String treeCode) {
		List<String> guids = new ArrayList<String>();
		int length = treeCode.split("-").length;
		if(length > 3){ // 当前为三级类型
			List<QueryCondition> querys = new ArrayList<>();
			querys.add(QueryCondition.eq("status", 0));
			querys.add(QueryCondition.eq("treeCode", treeCode ));
			List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(querys);
			for (AssetTypeSno itemSno : assetTypeSnos) {
				if(!guids.contains(itemSno.getGuid())){
					guids.add(itemSno.getGuid());
				}
			}
			return guids;
		}
		if(0 < length && length <= 3){ // 当前为一级、二级类型、顶层(asset)
			List<QueryCondition> querys = new ArrayList<>();
			querys.add(QueryCondition.eq("status", 0));
			querys.add(QueryCondition.likeBegin("treeCode", treeCode + "-"));
			List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(querys);
			for (AssetTypeSno itemSno : assetTypeSnos) {
				if(!guids.contains(itemSno.getGuid())){
					guids.add(itemSno.getGuid());
				}
			}
			return guids;
		}
		return null;

	}
}