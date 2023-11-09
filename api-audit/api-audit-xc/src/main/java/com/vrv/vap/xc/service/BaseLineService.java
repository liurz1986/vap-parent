package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.LineExportModel;
import com.vrv.vap.xc.pojo.BaseLine;
import com.vrv.vap.xc.pojo.BaseLineResult;
import com.vrv.vap.xc.vo.BaseLineQuery;
import com.vrv.vap.xc.vo.BaseResultQuery;

import java.util.List;

public interface BaseLineService {
   VData<BaseLine> add(BaseLine line);
   VData<BaseLine> update(BaseLine line);
   Result delete(String ids);
   VList<BaseLine> findAll(BaseLineQuery query);
   List<BaseLine> findAll();
   List<BaseLine> findAllEnable();
   List<BaseLine> selectByIds(String ids);
   LineExportModel exportConfigs(String ids);
   void importConfigs(LineExportModel model);
   VList<BaseLineResult> findResult(BaseResultQuery query);
}
