package com.vrv.vap.xc.service;


import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.pojo.ConfLookup;
import com.vrv.vap.xc.pojo.ObjectAnalyseConfig;
import com.vrv.vap.xc.vo.ConfLookupQuery;
import com.vrv.vap.xc.vo.ObjectAnalyseConfigQuery;
import java.util.List;

public interface BaseCommonService {

    VList<ConfLookup> getConfLookup(ConfLookupQuery confLookup);

    int updateConfLookup(ConfLookup record);

    VData<List<ObjectAnalyseConfig>> queryObjectAnalyseConfig(ObjectAnalyseConfigQuery param);

    Result updateObjectAnalyseConfig(ObjectAnalyseConfig param);
}
