package com.vrv.vap.alarmdeal.business.asset.datasync.service;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDiff;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetBookDiffDetailVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetBookDiffSearchVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import java.util.List;


public interface AssetBookDiffService extends BaseService<AssetBookDiff, String> {
   public PageRes<AssetBookDiff> getPage(AssetBookDiffSearchVO assetBookDiffSearchVO);

   public  Result<List<AssetBookDiffDetailVO>> getDiffDetails(String guid);

   public Result<String> handle(AssetBookDiffDetailVO assetBookDiffDetailVO) throws NoSuchFieldException, IllegalAccessException;
}
