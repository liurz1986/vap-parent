package com.vrv.vap.alarmdeal.business.evaluation.service.impl;

import com.vrv.vap.alarmdeal.business.evaluation.model.SelfInspectionEvaluationProcess;
import com.vrv.vap.alarmdeal.business.evaluation.repository.SelfInspectionEvaluationProcessRepository;
import com.vrv.vap.alarmdeal.business.evaluation.service.SelfInspectionEvaluationProcessService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自查自评中间数据处理
 *
 * @Date 2023-09
 * @author liurz
 *
 */
@Service
@Transactional
public class SelfInspectionEvaluationProcessServiceImpl extends BaseServiceImpl<SelfInspectionEvaluationProcess,String>  implements SelfInspectionEvaluationProcessService {
    private static Logger logger = LoggerFactory.getLogger(SelfInspectionEvaluationProcessServiceImpl.class);
    @Autowired
    private SelfInspectionEvaluationProcessRepository selfInspectionEvaluationProcessRepository;

    @Override
    public BaseRepository<SelfInspectionEvaluationProcess, String> getRepository() {
        return selfInspectionEvaluationProcessRepository;
    }
}
