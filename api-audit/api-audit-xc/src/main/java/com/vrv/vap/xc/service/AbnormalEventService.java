package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.vo.AbnormalEventQuery;

import java.util.List;
import java.util.Map;

public interface AbnormalEventService {

    VData<List<Map<String, Object>>> abnormalTag(AbnormalEventQuery query);

    EsResult abnormalEvent(AbnormalEventQuery query);
}
