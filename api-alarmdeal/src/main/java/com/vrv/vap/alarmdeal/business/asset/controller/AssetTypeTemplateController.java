package com.vrv.vap.alarmdeal.business.asset.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.appsys.vo.AssetTypeTemplateVO;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeSno;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeTemplate;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetTypeTemplateOverride;
import com.vrv.vap.common.controller.BaseController;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/assetTypeTemplate")
public class AssetTypeTemplateController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(AssetTypeTemplateController.class);

	@Autowired
	private MapperUtil mapper;
	
	@Autowired
	private AssetTypeTemplateService assetTypeTemplateService;
	
	@Autowired
	private AssetSettingsService assetSettingsService;
	@Autowired
	AssetTypeSnoService assetTypeSnoService;
	@Autowired
	AssetTypeService assetTypeService;
	@Autowired
	AssetTypeGroupService assetTypeGroupService;
	
	@PostMapping(value = "/updateTemplate")
	@ApiOperation(value="修改资产品牌型号模板",notes="修改模板")
	@SysRequestLog(description="修改资产品牌型号模板", actionType = ActionType.UPDATE,manually = false)
    public Result<Boolean> updateTemplate(@RequestBody AssetTypeTemplateVO assetTypeTemplate) {
        String guid = assetTypeTemplate.getGuid();
        if (StringUtils.isEmpty(guid)) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：guid不可以为空");
        }
        NameValue settingScope = assetSettingsService.getSettingScope();
        if ("AssetTypeGroup".equals(settingScope.getName())) {
            return updateAssetTypeGroupTemplate(assetTypeTemplate);

        } else if ("AssetType".equals(settingScope.getName())) {
            return updateAssetTypeTemplate(assetTypeTemplate);
        } else {
            AssetTypeTemplate one = assetTypeTemplateService.getOne(guid);
            if (one == null) {
                one = new AssetTypeTemplate();
                one.setGuid(guid);
            }
            try {
                mapper.copy(assetTypeTemplate, one);
                assetTypeTemplateService.save(one);
                return ResultUtil.success(true);

            } catch (Exception e) {
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作异常："+e.getMessage());
            }
        }
    }


	// 当前为一级类型，修改模板；将一级类型下的所有三级类型更改为当前的模板
    private Result<Boolean> updateAssetTypeGroupTemplate(AssetTypeTemplateVO assetTypeTemplate) {
        AssetTypeGroup group =  assetTypeGroupService.getOne(assetTypeTemplate.getGuid());
        if (group == null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：找不到所选择的数据，请刷新页面后重新操作");
        }
		updateTemplate(group.getTreeCode(),assetTypeTemplate);
        return ResultUtil.success(true);
    }

	// 当前为二级类型，修改模板；将二级类型下的所有三级类型更改为当前的模板
	private Result<Boolean> updateAssetTypeTemplate(AssetTypeTemplateVO assetTypeTemplate) {
		AssetType assetType = assetTypeService.getOne(assetTypeTemplate.getGuid());
		if (assetType == null) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：找不到所选择的数据，请刷新页面后重新操作");
		}
		updateTemplate(assetType.getTreeCode(),assetTypeTemplate);
		return ResultUtil.success(true);
	}

	// 2021-08-18
    private void updateTemplate(String treeCode ,AssetTypeTemplateVO assetTypeTemplate){
		logger.info("updateTemplate,treeCode:{}",treeCode);
		logger.info("assetTypeTemplate:{}", JSON.toJSONString(assetTypeTemplate));
		List<QueryCondition> querys = new ArrayList<>();
		querys.add(QueryCondition.eq("status", 0));
		querys.add(QueryCondition.likeBegin("treeCode", treeCode + "-"));
		List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(querys);
		if(null == assetTypeSnos || assetTypeSnos.size() == 0){
			logger.info("没有三级类型，新增模板无效");
			return ;
		}
		logger.info("assetTypeSnos,size:{}",assetTypeSnos.size());
		List<AssetTypeTemplate> newTemps = new ArrayList<AssetTypeTemplate>();
		AssetTypeTemplate newTemp = null;
		for (AssetTypeSno assetTypeSno : assetTypeSnos) {
			newTemp=new AssetTypeTemplate();
			mapper.copy(assetTypeTemplate, newTemp);
			newTemp.setGuid(assetTypeSno.getGuid());
			newTemps.add(newTemp);
		}
		if(newTemps.size() > 0){
			assetTypeTemplateService.save(newTemps);
		}
	}




    @GetMapping(value = "/getTemplate/{guid}")
	@ApiOperation(value="查询资产品牌型号模板",notes="")
	@SysRequestLog(description="查询资产品牌型号模板", actionType = ActionType.SELECT,manually = false)
	public Result<AssetTypeTemplate> getTemplate(@PathVariable("guid") String guid) {
		try {
			AssetTypeTemplate one = assetTypeTemplateService.getAssetTypeTemplate(guid);
			return ResultUtil.success(one);
		}catch (Exception e) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "操作异常："+e.getMessage());
		}
	}


	@PostMapping(value = "/overrideTemplate/{guid}")
	@ApiOperation(value="模板覆盖",notes="")
	@SysRequestLog(description="模板覆盖", actionType = ActionType.UPDATE,manually = false)
	public Result<Boolean> overrideTemplate(@RequestBody AssetTypeTemplateOverride data) {
		if (data.getIds() == null || data.getIds().isEmpty()) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：ids不可以为空");
		}
		String keyData = data.getKeyData();
		if (StringUtils.isEmpty(keyData)) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：keyData不可以为空");
		}
		String formdata = data.getFormdata();
		if (StringUtils.isEmpty(formdata)) {
			return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：keyData不可以为空");
		}
		List<AssetTypeTemplate> items = new ArrayList<>();

		NameValue settingScope = assetSettingsService.getSettingScope();

		List<QueryCondition> querys = new ArrayList<>();
		for (String guid : data.getIds()) {

			if ("AssetTypeSno".equals(settingScope.getName())) {

				AssetTypeSno assetTypeSno = assetTypeSnoService.getOne(guid);
				if (assetTypeSno == null) {
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：找不到所选择的数据，请刷新页面后重新操作");
				}

				AssetTypeTemplate one = assetTypeTemplateService.getOne(guid);
				if (one == null) {
					// return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),
					// "操作异常：找不到原始数据");
					one = new AssetTypeTemplate();
					one.setGuid(guid);
					one.setDeleteFlag(false);
				}

				one.setFormdata(formdata);
				one.setKeyData(keyData);

				items.add(one);
			} else {

				AssetType assetType = assetTypeService.getOne(guid);
				if (assetType == null) {
					return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "传参异常：找不到所选择的数据，请刷新页面后重新操作");
				}

				querys.clear();
				querys.add(QueryCondition.eq("status", 0));
				querys.add(QueryCondition.likeBegin("treeCode", assetType.getTreeCode() + "-"));
				List<AssetTypeSno> assetTypeSnos = assetTypeSnoService.findAll(querys);
				for (AssetTypeSno assetTypeSno : assetTypeSnos) {
					AssetTypeTemplate one = assetTypeTemplateService.getOne(assetTypeSno.getGuid());
					if (one == null) {
 
						one = new AssetTypeTemplate();
				
						one.setDeleteFlag(false);
					}
					
					one.setGuid(assetTypeSno.getGuid());
					one.setFormdata(formdata);
					one.setKeyData(keyData);

					items.add(one);
				}
			}

		}

		assetTypeTemplateService.save(items);

		return ResultUtil.success(true);
	}
	
}
