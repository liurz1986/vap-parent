package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthPrintBurn;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthPrintBurnVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthPrintBurnQueryVo;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

import java.util.List;
import java.util.Map;

/**
 * 2023-08
 * @author liurz
 */
public interface BaseAuthPrintBurnService extends BaseService<BaseAuthPrintBurn, Integer> {

    PageRes<BaseAuthPrintBurnVo> getPager(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo);

    Result<Map<String,List<String>>> saveData(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo);

    Result<List<BaseAuthPrintBurn>> updatePrintBurn(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo);

    Result<String> delPrintBurn(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo);

    Result<List<String>> getIpsByAssetType(AssetTypeGroup assetTypeGroup);

    Result<List<AssetTypeGroup>> getPrintBrunAssetType();

    Result<List<AssetTypeGroup>> getMaintenAssetType();
}
