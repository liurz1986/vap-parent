package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AssetTypeCacheVo;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.repository.AssetTypeRepository;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSettingsService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeSnoService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTreeVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeVO;
import com.vrv.vap.alarmdeal.business.asset.util.TreeFactory;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssetTypeServiceImpl extends BaseServiceImpl<AssetType, String> implements AssetTypeService {
	private static Logger logger = LoggerFactory.getLogger(AssetTypeServiceImpl.class);

	@Autowired
	private AssetTypeRepository assetTypeRepository;
	@Autowired
	private AssetTypeGroupService assetTypeGroupService;
	@Autowired
	private MapperUtil mapper;
	@Autowired
	private AssetTypeSnoService assetTypeSnoService;
	@Autowired
	AssetSettingsService assetSettingsService;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public AssetTypeRepository getRepository() {
		return assetTypeRepository;
	}
	
	@Override 
	public AssetType save(AssetType entity){
		if(entity.getPredefine()==null) {
			entity.setPredefine(false);
		}
		return super.save(entity);
	}

	@Override
	public List<AssetTypeCacheVo> getAssetTypeList() {
		String sql = "select guid as id, name from asset_type";
		List<AssetTypeCacheVo> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetTypeCacheVo>(AssetTypeCacheVo.class));
		return list;
	}

	@Override
	public List<AssetTypeTreeVO> getAssetTypeTree(String type) {
		List<AssetTypeTreeVO> listTree = new ArrayList<AssetTypeTreeVO>();
		//asset_type_group_tree
		List<QueryCondition> con1 = new ArrayList<>();
		con1.add(QueryCondition.eq("status", 0));
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<AssetTypeGroup> asset_type_group_list = assetTypeGroupService.findAll(con1, sort);
		List<AssetTypeTreeVO> asset_type_group_treeVO = assetTypeGroupService.mapperTreeVO(asset_type_group_list);
		listTree.addAll(asset_type_group_treeVO);
		
		//asset_type_tree
		List<QueryCondition> con2 = new ArrayList<>();
		con2.add(QueryCondition.eq("status", 0));
		List<AssetType> asset_type_list = findAll(con2, sort);
		List<AssetTypeTreeVO> asset_type_tree_list = mapperTreeVO(asset_type_list);
		listTree.addAll(asset_type_tree_list);

		AssetTypeTreeVO rootAssetTypeTreeVO = new AssetTypeTreeVO();
		rootAssetTypeTreeVO.setIconCls("icon-JK");
		rootAssetTypeTreeVO.setKey("asset");
		rootAssetTypeTreeVO.setParentId("0");
		rootAssetTypeTreeVO.setType(1);
		rootAssetTypeTreeVO.setTitle("资产类型");
		listTree.add(rootAssetTypeTreeVO);
		listTree= TreeFactory.buildTree(listTree, "0");

		// 资产管理菜单中树结构屏蔽掉	USB外设（asset-USBPeripheral）、USB存储介质（asset-USBMemory） 2021-08-16
		if("assetType".equalsIgnoreCase(type)){
			setTreeNode(listTree);
		}
		return listTree;
	}

	// 资产管理菜单中树结构屏蔽掉	USB外设（asset-USBPeripheral）、USB存储介质（asset-USBMemory） 2021-08-16
	private void setTreeNode(List<AssetTypeTreeVO> listTree) {
		AssetTypeTreeVO treeNode = listTree.get(0);
		List<AssetTypeTreeVO> childrens = treeNode.getChildren();
		if(null ==childrens || childrens.size() <= 0 ){
			return;
		}
		List<AssetTypeTreeVO> deleteNodes = new ArrayList<AssetTypeTreeVO>();
		for (AssetTypeTreeVO children : childrens) {
			if ("asset-USBPeripheral".equalsIgnoreCase(children.getKey())) {
				deleteNodes.add(children);
			}
			if ("asset-USBMemory".equalsIgnoreCase(children.getKey())) {
				deleteNodes.add(children);
			}
		}
		if (deleteNodes.size() > 0) {
			childrens.removeAll(deleteNodes);
		}
	}

	@Override
	public List<AssetTypeTreeVO> mapperTreeVO(List<AssetType> assetTypes) {
		List<AssetTypeTreeVO> assetTypeTreeVOs = new ArrayList<AssetTypeTreeVO>();
		for (AssetType assetType : assetTypes) {
			AssetTypeTreeVO assetTypeTreeVO = new AssetTypeTreeVO();
			assetTypeTreeVO.setIconCls(assetType.getIcon());
			assetTypeTreeVO.setTitle(assetType.getName());
			String id = assetType.getTreeCode();
			assetTypeTreeVO.setKey(id);
			String parentId = id.substring(0, id.lastIndexOf('-'));
			assetTypeTreeVO.setParentId(parentId);
			assetTypeTreeVO.setGuid(assetType.getGuid());
			assetTypeTreeVO.setType(3);
			assetTypeTreeVO.setUniqueCode(assetType.getUniqueCode());
			assetTypeTreeVO.setTreeCode(assetType.getTreeCode());
			assetTypeTreeVOs.add(assetTypeTreeVO);
		}
		return assetTypeTreeVOs;
	}

	@Override
	public List<AssetTypeVO> getAssetTypeList(AssetSearchVO assetSearchVO) {
		String asset_type_name = assetSearchVO.getAsset_type_name();
		String treeCode = assetSearchVO.getTreeCode();
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.like("treeCode", treeCode));
		if(StringUtils.isNotEmpty(asset_type_name)){
			conditions.add(QueryCondition.like("name", asset_type_name));
		}
		Sort sort =Sort.by(Direction.ASC, "orderNum");
		List<AssetType> list = findAll(conditions, sort);
		List<AssetTypeVO> mapList = mapper.mapList(list, AssetTypeVO.class);
		return mapList;
	}

	/**
	 * 获取资产类型：过滤掉了USB外设（asset-USBPeripheral）、USB存储介质（asset-USBMemory）
	 * @return
	 */
	@Override
	public List<AssetTypeTreeVO> getAssetTypeComboboxTree() {
		List<AssetTypeTreeVO> listTree = getAllAssetTypeComboboxTree();
		// 资产管理菜单中树结构屏蔽掉	USB外设（asset-USBPeripheral）、USB存储介质（asset-USBMemory） 2021-08-16
		List<AssetTypeTreeVO> deleteNodes = new ArrayList<AssetTypeTreeVO>();
		for (AssetTypeTreeVO children : listTree) {
			if ("asset-USBPeripheral".equalsIgnoreCase(children.getKey())) {
				deleteNodes.add(children);
			}
			if ("asset-USBMemory".equalsIgnoreCase(children.getKey())) {
				deleteNodes.add(children);
			}
		}
		if (deleteNodes.size() > 0) {
			listTree.removeAll(deleteNodes);
		}
		return listTree;
	}

	/**
	 * 获取所有资产类型树
	 *
	 * @return
	 */
	@Override
	public List<AssetTypeTreeVO> getAllAssetTypeComboboxTree(){
		List<AssetTypeTreeVO> listTree = new ArrayList<AssetTypeTreeVO>();
		//asset_type_group_tree
		List<QueryCondition> con1 = new ArrayList<>();
		con1.add(QueryCondition.eq("status", 0));
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<AssetTypeGroup> asset_type_group_list = assetTypeGroupService.findAll(con1, sort);
		List<AssetTypeTreeVO> asset_type_group_treeVO = assetTypeGroupService.mapperTreeVO(asset_type_group_list);
		listTree.addAll(asset_type_group_treeVO);
		//asset_type_tree
		List<AssetType> asset_type_list = findAll(con1, sort);
		List<AssetType>  type_list =new ArrayList<>();
		asset_type_list.forEach(type->{

			for(AssetTypeGroup group : asset_type_group_list) {
				if(type.getTreeCode().contains(group.getTreeCode()+"-")) {
					type_list.add(type);
				}
			}
		});
		List<AssetTypeTreeVO> asset_type_tree_list = mapperTreeVO(type_list);
		listTree.addAll(asset_type_tree_list);
		List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(con1, sort); // 品牌
		List<AssetTypeSno> sno_list=new ArrayList<>();
		assetTypeSnos.forEach(sno->{
			for(AssetType type : type_list) {
				if(sno.getTreeCode().contains(type.getTreeCode()+"-")) {
					sno_list.add(sno);
					break;
				}
			}
		});
		List<AssetTypeTreeVO> sno_mapperTreeVO = assetTypeSnoService.mapperTreeVO(sno_list);
		listTree.addAll(sno_mapperTreeVO);
		listTree= TreeFactory.buildTree(listTree);
		return listTree;
	}

	@Override
	public AssetType getAssetTypeByAssetTypeName(String AssetTypeName) {
		List<QueryCondition> conditions = new ArrayList<>();
		conditions.add(QueryCondition.eq("name", AssetTypeName));
		List<AssetType> list = findAll(conditions);
		if(list.size()==1){
			AssetType assetType = list.get(0);
		    return assetType;
		}else{
			throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "未找到该资产类型："+AssetTypeName);
		}
		
	}


	@Override
	public List<AssetTypeTreeVO> getAssetTypeTreeByConfigure() {
		logger.info("getAssetTypeTreeByConfigure start");
		NameValue settingScope = assetSettingsService.getSettingScope();
		String name = settingScope.getName();
		// 配置的是一级资产类型，只展示一级
		// 配置的是二级资产类型，只展示一、二级
		// 配置的是品牌型号类型，只展示一、二、三级
		switch (name) {
			case "AssetTypeGroup":
				return getAssetTypeGroupTreeVo();
			case "AssetType":
				return getAssetTypeTreeVo();
			case "AssetTypeSno":
				return getAssetTypeSnoVo();
			default:
				return null;

		}
	}

	private List<AssetTypeTreeVO> getAssetTypeGroupTreeVo() {
		List<AssetTypeTreeVO> listTree = new ArrayList<AssetTypeTreeVO>();
		List<AssetTypeTreeVO> asset_type_group_treeVO = getAssetTypeGroup();
		listTree.addAll(asset_type_group_treeVO);
		AssetTypeTreeVO rootAssetTypeTreeVO = new AssetTypeTreeVO();
		rootAssetTypeTreeVO.setIconCls("icon-JK");
		rootAssetTypeTreeVO.setKey("asset");
		rootAssetTypeTreeVO.setParentId("0");
		rootAssetTypeTreeVO.setType(1);
		rootAssetTypeTreeVO.setTitle("资产类型");
		listTree.add(rootAssetTypeTreeVO);
		listTree = TreeFactory.buildTree(listTree, "0");
		return listTree;

	}

	List<AssetTypeTreeVO> getAssetTypeTreeVo() {
		List<AssetTypeTreeVO> listTree = new ArrayList<AssetTypeTreeVO>();
		List<AssetTypeTreeVO> asset_type_group_treeVO = getAssetTypeGroup();
		listTree.addAll(asset_type_group_treeVO);
		List<AssetTypeTreeVO> asset_type_tree_list = getAssetType();
		listTree.addAll(asset_type_tree_list);
		AssetTypeTreeVO rootAssetTypeTreeVO = new AssetTypeTreeVO();
		rootAssetTypeTreeVO.setIconCls("icon-JK");
		rootAssetTypeTreeVO.setKey("asset");
		rootAssetTypeTreeVO.setParentId("0");
		rootAssetTypeTreeVO.setType(1);
		rootAssetTypeTreeVO.setTitle("资产类型");
		listTree.add(rootAssetTypeTreeVO);
		listTree = TreeFactory.buildTree(listTree, "0");
		return listTree;
	}

	List<AssetTypeTreeVO> getAssetTypeSnoVo() {
		List<AssetTypeTreeVO> listTree = new ArrayList<AssetTypeTreeVO>();
		List<AssetTypeTreeVO> asset_type_group_treeVO = getAssetTypeGroup();
		listTree.addAll(asset_type_group_treeVO);
		List<AssetTypeTreeVO> asset_type_tree_list = getAssetType();
		listTree.addAll(asset_type_tree_list);
		List<AssetTypeTreeVO> asset_type_sno_list = getAssetTypeSno();
		listTree.addAll(asset_type_sno_list);
		AssetTypeTreeVO rootAssetTypeTreeVO = new AssetTypeTreeVO();
		rootAssetTypeTreeVO.setIconCls("icon-JK");
		rootAssetTypeTreeVO.setKey("asset");
		rootAssetTypeTreeVO.setParentId("0");
		rootAssetTypeTreeVO.setType(1);
		rootAssetTypeTreeVO.setTitle("资产类型");
		listTree.add(rootAssetTypeTreeVO);
		listTree = TreeFactory.buildTree(listTree, "0");
		return listTree;
	}

	List<AssetTypeTreeVO> getAssetTypeGroup() {
		List<QueryCondition> con1 = new ArrayList<>();
		con1.add(QueryCondition.eq("status", 0));
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<AssetTypeGroup> asset_type_group_list = assetTypeGroupService.findAll(con1, sort);
		List<AssetTypeTreeVO> asset_type_group_treeVO = assetTypeGroupService.mapperTreeVO(asset_type_group_list);
		return asset_type_group_treeVO;
	}

	List<AssetTypeTreeVO> getAssetType() {
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<QueryCondition> con2 = new ArrayList<>();
		con2.add(QueryCondition.eq("status", 0));
		List<AssetType> asset_type_list = findAll(con2, sort);
		List<AssetTypeTreeVO> asset_type_tree_list = mapperTreeVO(asset_type_list);
		return asset_type_tree_list;
	}

	List<AssetTypeTreeVO> getAssetTypeSno() {
		Sort sort = Sort.by(Direction.ASC, "orderNum");
		List<QueryCondition> con2 = new ArrayList<>();
		con2.add(QueryCondition.eq("status", 0));
		List<AssetTypeSno> asset_type_sno_list = assetTypeSnoService.findAll(con2, sort);
		List<AssetTypeTreeVO> asset_type_tree_list = assetTypeSnoService.mapperTreeVO(asset_type_sno_list);
		return asset_type_tree_list;
	}
	/**
	 * 获取所有一级资产类型下的所有二级资产类型
	 * 2022-06-24
	 * @return
	 */
	public List<AssetType> getAllAssetTypeByGroup(){
		String sql = "select type.Guid as guid, type.TreeCode as treeCode,type.uniqueCode as uniqueCode,type.Name as name,type.Name_en as nameEn,type.Icon as icon ,type.monitorProtocols as monitorProtocols,type.status as status ,type.orderNum as orderNum,type.predefine as predefine from asset_type as type " +
				" inner join asset_type_group as tgroup on SUBSTRING_INDEX( type.TreeCode, '-',2 ) =tgroup.TreeCode";
		List<AssetType> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper<AssetType>(AssetType.class));
		return list;
	}
}
