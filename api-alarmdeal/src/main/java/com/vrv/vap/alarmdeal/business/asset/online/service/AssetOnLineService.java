package com.vrv.vap.alarmdeal.business.asset.online.service;

import com.vrv.vap.alarmdeal.business.asset.online.model.AssetOnLine;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetQueryVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.SerachAssetOnLineV0;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.Future;

public interface AssetOnLineService extends BaseService<AssetOnLine, String>  {

    public PageRes<AssetOnLineVO> query(SerachAssetOnLineV0 serachAssetOnLineV0);


    public void deleteByGuid(String guid);

    public Result<String> batchSetting(AssetOnLineVO assetOnLineVO);

    public Result<String> writeAsset(AssetOnLineVO assetOnLineVO);

    public  Result<String> exportAssetOnLineInfo(SerachAssetOnLineV0 serachAssetOnLineV0);

    public void exportAssetOnLineFile(String fileName, HttpServletResponse response);
    /**
     * 获取所有资产的ip及资产类型
     * @return
     */
    public Future<List<AssetQueryVO>> getAllAssetsFuture();

    /**
     * 批量保存数据
     * @param onlins
     */
    public void batchSaveDatas(List<AssetOnLine> onlins);



}
