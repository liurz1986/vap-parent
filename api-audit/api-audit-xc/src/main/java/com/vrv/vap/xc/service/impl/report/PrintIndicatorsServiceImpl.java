package com.vrv.vap.xc.service.impl.report;

import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.model.PrintModel;
import com.vrv.vap.xc.model.ReportParam;
import com.vrv.vap.xc.pojo.BaseKoalOrg;
import com.vrv.vap.xc.pojo.BasePersonZjg;
import com.vrv.vap.xc.service.IBaseKoalOrgService;
import com.vrv.vap.xc.service.IBasePersonZjgService;
import com.vrv.vap.xc.service.report.PrintIndicatorsService;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PrintIndicatorsServiceImpl implements PrintIndicatorsService {
    @Resource
    private IBaseKoalOrgService iBaseKoalOrgService;
    @Resource
    private IBasePersonZjgService iBasePersonZjgService;

    public PageModel parseModel(ReportParam model) {
        PageModel pageModel = new PageModel();
        pageModel.setMyStartTime(model.getStartTime());
        pageModel.setMyEndTime(model.getEndTime());
        return pageModel;
    }

    @Override
    public VList<PrintModel> statisticsPrintFile(ReportParam model, String opType, String type, boolean total) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, parseModel(model), LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder builder = new BoolQueryBuilder();
        builder.must(QueryBuilders.termQuery("op_type", opType));
        queryModel.setQueryBuilder(builder);
        String condition = "org".equals(type) ? "std_org_code" : "std_user_no";
        List<Map<String, Object>> result = QueryTools.twoLevelAggToHits(queryModel, wrapper, condition,
                "file_level", 10, 10, "count", condition.split(","));
        List<PrintModel> transfer = transfer(result, condition, total);
        return VoBuilder.vl(result.size(), transfer);
    }

    /**
     * 文件密级、人员及机构名称转换
     *
     * @param queryResult 查询结果集
     * @param condition 查询条件
     * @return
     */
    public List<PrintModel> transfer(List<Map<String, Object>> queryResult, String condition, boolean total) {
        List<BasePersonZjg> personList = new ArrayList<>();
        List<BaseKoalOrg> orgList = new ArrayList<>();
        if (Objects.equals("std_user_no", condition)) {
            personList = iBasePersonZjgService.list();
        } else {
            orgList = iBaseKoalOrgService.list();
        }
        Map<String, String> orgMap = orgList.stream().collect(Collectors.toMap(BaseKoalOrg::getCode, BaseKoalOrg::getName));
        Map<String, String> personMap = personList.stream().collect(Collectors.toMap(BasePersonZjg::getUserNo, BasePersonZjg::getUserName));
        List<PrintModel> result = queryResult.stream().collect(Collectors.groupingBy(m -> (String) m.get(condition))).entrySet().stream().map(entry -> {
            PrintModel model = new PrintModel();
            List<Map<String, Object>> value = entry.getValue();
            Map<Integer, Integer> fileCount = value.stream().collect(Collectors.toMap(m -> (Integer) m.get("file_level"), m -> (Integer) m.get("count")));
            model.setSuperSecret(fileCount.getOrDefault(0, 0));
            model.setConfidential(fileCount.getOrDefault(1, 0));
            model.setSecret(fileCount.getOrDefault(2, 0));
            model.setInternal(fileCount.getOrDefault(3, 0));
            model.setOpen(fileCount.getOrDefault(4, 0));
            if (Objects.equals("std_user_no", condition)) {
                model.setName(personMap.getOrDefault(entry.getKey(), ""));
            } else {
                model.setName(orgMap.getOrDefault(entry.getKey(), ""));
            }
            return model;
        }).collect(Collectors.toList());
        if (total) {
            int totalSuperSecret = result.stream().mapToInt(PrintModel::getSuperSecret).sum();
            int totalConfidential = result.stream().mapToInt(PrintModel::getConfidential).sum();
            int totalSecret = result.stream().mapToInt(PrintModel::getSecret).sum();
            int totalInternal = result.stream().mapToInt(PrintModel::getInternal).sum();
            int totalOpen = result.stream().mapToInt(PrintModel::getOpen).sum();
            PrintModel totalModel = new PrintModel();
            totalModel.setName("总计");
            totalModel.setSuperSecret(totalSuperSecret);
            totalModel.setConfidential(totalConfidential);
            totalModel.setSecret(totalSecret);
            totalModel.setInternal(totalInternal);
            totalModel.setOpen(totalOpen);
            totalModel.setTotal(totalSuperSecret, totalConfidential, totalSecret, totalInternal, totalOpen);
            result.add(totalModel);
        }
        return result;
    }

}
