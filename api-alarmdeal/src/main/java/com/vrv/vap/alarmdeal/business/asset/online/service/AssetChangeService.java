package com.vrv.vap.alarmdeal.business.asset.online.service;

import com.vrv.vap.alarmdeal.business.asset.online.model.AssetChange;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetChangeVO;
import com.vrv.vap.alarmdeal.business.asset.online.vo.SerachAssetChangeVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

import java.util.List;
import java.util.concurrent.Future;

public interface AssetChangeService extends BaseService<AssetChange,String> {

    public PageRes<AssetChange> query(SerachAssetChangeVO serachAssetChangeVO);

    public Result<AssetChange> handle(AssetChangeVO assetChangeVO);

    public void batchSave(List<AssetChange> updateChanges);

    public Future<List<AssetChange>> getAllAssetChangesFuture();

    /**
     * 获取未处理告警数
     *
     * @return
     */
    public Long getWarmCount();
}
