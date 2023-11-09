package com.vrv.vap.alarmdeal.business.asset.datasync.service;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyCompareVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifySearchVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AssetVerifyService extends BaseService<AssetVerify, String> {

    /**
     * 待申库编辑入库前校验
     * @param asetVerifyVO
     * @return
     */
    public Result<String> validateData(AssetVerifyVO asetVerifyVO);

    /**
     * 编辑入库
     * @param asetVerifyVO
     * @return
     */
    public Result<String> saveEditdData(AssetVerifyVO asetVerifyVO);

    /**
     * 查询
     * @param assetVerifySearchVO
     * @return
     */
    public PageRes<AssetVerifyVO> query(AssetVerifySearchVO assetVerifySearchVO);

    /**
     * 忽略
     * @param guid
     * @return
     */
    public Result<String> neglect(String guid);

    /**
     * 单条数据入库
     * @param guid
     * @return
     */
    public Result<String> saveAsset(String guid);

    /**
     * 批量入库
     * @return
     */
    public Result<String> batchSaveAsset();

    /**
     * 根据二级资产类型获取类型：一级资产类型名称-二级资产类型名称
     * @param assetType
     * @return
     */
    public String getAssetVerifyType(AssetType assetType);

    /**
     * 比对入统一台账库处理
     * @param assetVerifys
     */
    public void saveAssetVerifys(List<AssetVerifyCompareVO> assetVerifys);

    public Result<String> exportAssetInfo(AssetVerifySearchVO assetVerifySearchVO);

    public  void exportAssetFile(String fileName, HttpServletResponse response);
}
