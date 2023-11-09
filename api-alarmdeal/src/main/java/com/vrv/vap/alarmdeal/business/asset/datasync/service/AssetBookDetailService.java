package com.vrv.vap.alarmdeal.business.asset.datasync.service;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;

import java.util.List;
import java.util.Map;

public interface AssetBookDetailService extends BaseService<AssetBookDetail, String> {
  public  void comparison();

  public Result<List<String>> queryDataSources();

  public void deleteByBatchNo(Map<String,String> curBatchNo);
}
