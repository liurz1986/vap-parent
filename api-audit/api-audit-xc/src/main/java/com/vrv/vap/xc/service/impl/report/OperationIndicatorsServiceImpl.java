package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.pojo.BasePersonZjg;
import com.vrv.vap.xc.service.IBasePersonZjgService;
import com.vrv.vap.xc.service.report.OperationIndicatorsService;
import com.vrv.vap.xc.tools.QueryTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OperationIndicatorsServiceImpl implements OperationIndicatorsService {

    @Resource
    private IBasePersonZjgService iBasePersonZjgService;

    @Resource
    private PrintIndicatorsServiceImpl printIndicatorsService;

    @Override
    public VList<OperationModel> statisticsTypeFrequency(ReportParam model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, printIndicatorsService.parseModel(model), LogTypeConstants.OPERATION_AUDIT, "event_time");
        List<Map<String, Object>> result = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_user_no",
                "std_dev_type_group", 10, 10, "count", "std_user_no".split(","));
        List<OperationModel> transfer = transfer(result);
        return VoBuilder.vl(result.size(), transfer);
    }

    /**
     * 运维次数、人员名称转换
     *
     * @param result 查询结果集
     * @return
     */
    public List<OperationModel> transfer(List<Map<String, Object>> result) {
        List<BasePersonZjg> personList = iBasePersonZjgService.list();
        Map<String, String> personMap = personList.stream().collect(Collectors.toMap(BasePersonZjg::getUserNo, BasePersonZjg::getUserName));
        return result.stream().collect(Collectors.groupingBy(m -> (String) m.get("std_user_no"))).entrySet().stream().map(entry -> {
            OperationModel model = new OperationModel();
            List<Map<String, Object>> value = entry.getValue();
            Map<String, Integer> typeCount = value.stream().collect(Collectors.toMap(m -> (String) m.get("std_dev_type_group"), m -> (Integer) m.get("count")));
            model.setServerNumber(typeCount.getOrDefault("1", 0));
            model.setProductNumber(typeCount.getOrDefault("2", 0));
            model.setApplicationNumber(typeCount.getOrDefault("3", 0));
            model.setNetworkNumber(typeCount.getOrDefault("4", 0));
            model.setPersonName(personMap.getOrDefault(entry.getKey(), ""));
            return model;
        }).collect(Collectors.toList());
    }
}
