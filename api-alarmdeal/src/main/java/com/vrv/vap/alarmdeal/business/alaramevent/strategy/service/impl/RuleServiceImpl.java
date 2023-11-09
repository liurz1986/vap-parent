package com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.RiskRuleInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.RuleOperation;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.util.FileCommonUtils;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Dependencies;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Outputs;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterOpertorVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.FilterPagerVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.OutFieldInfo;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.ParamConfigVO;
import com.vrv.vap.alarmdeal.business.asset.util.ResultEnum;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月07日 11:25
 */
@Service
public class RuleServiceImpl implements RuleService {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RiskEventRuleService riskEventRuleService;

    @Autowired
    private RuleFilterService ruleFilterService;

    @Value("${upload.file.dir:/opt/test/upload}")
    public String fileDir;

    @Value("${upload.file.password:vrv@123}")
    public String filePassword;

    @Autowired
    private MapperUtil mapper;

    @Override
    public RiskRuleInfo getRiskRuleInfo() {
        RiskRuleInfo result = new RiskRuleInfo();
        // 统计策略总数
        List<QueryCondition> ruleConditions = new ArrayList<>();
        ruleConditions.add(QueryCondition.eq("deleteFlag", true));
        long riskTotal = riskEventRuleService.count(ruleConditions);
        result.setRuleTotal(riskTotal);
        // 统计运行状态策略个数
        ruleConditions.add(QueryCondition.eq("started","1"));
        long riskRunTotal = riskEventRuleService.count(ruleConditions);
        result.setRuleRunning(riskRunTotal);
        result.setRuleFail(riskTotal-riskRunTotal);
        return result;
    }

    @Override
    public RuleOperation getRuleOperation() {
        RuleOperation result = new RuleOperation();
        // 统计规则总数
        List<QueryCondition> filterParams = new ArrayList<>();
        filterParams.add(QueryCondition.eq("deleteFlag", true));
        filterParams.add(QueryCondition.eq("filterType","0"));
        long filterTotle = filterOperatorService.count(filterParams);
        result.setFilterTotal(filterTotle);

        // 运行中规则
        // 1、查询运行中的策略
        List<QueryCondition> riskEventRuleCondition = new ArrayList<>();
        riskEventRuleCondition.add(QueryCondition.eq("started", "1"));
        riskEventRuleCondition.add(QueryCondition.eq("deleteFlag", true));
        List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(riskEventRuleCondition);
        List<String> ruleIds = riskEventRuleList.stream().map(RiskEventRule::getId).distinct().collect(Collectors.toList());

        int runningNum = 0;
        if(CollectionUtils.isNotEmpty(ruleIds)){
            // 2、通过策略ID 查询规则ID
            List<QueryCondition> ruleFilterCondition = new ArrayList<>();
            ruleFilterCondition.add(QueryCondition.in("ruleId", ruleIds));
            List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterCondition);

            // 3、获取规则ID ，并去重，得到运行中的规则ID数量
            List<String> filterCodes = ruleFilters.stream().map(RuleFilter::getFilterCode).distinct().collect(Collectors.toList());
            runningNum = filterCodes.size();
        }
        result.setFilterRunning(Long.valueOf(runningNum));
        // 停用状态规则数
        result.setFilterFail(Long.valueOf(filterTotle - runningNum));

        return result;
    }

    /**
     * 删除规则
     *
     * @param id
     * @return
     */
    @Override
    public Boolean deleteRuleForId(String id) {
        // 1、通过id(filtercode)查询策略规则关联表获取对应策略
        // 获取规则ID列表
        List<QueryCondition> ruleFilterCondition = new ArrayList<>();
        ruleFilterCondition.add(QueryCondition.eq("filterCode", id));
        List<RuleFilter> ruleFilters = ruleFilterService.findAll(ruleFilterCondition);
        List<String> ruleIds = ruleFilters.stream().map(RuleFilter::getRuleId).distinct().collect(Collectors.toList());
        // 获取策略  开启且未删除
        List<QueryCondition> riskCondition = new ArrayList<>();
        riskCondition.add(QueryCondition.in("id", ruleIds));
        riskCondition.add(QueryCondition.eq("started", "1"));
        riskCondition.add(QueryCondition.eq("deleteFlag", true));
        // 2、过滤开启的策略  开启且未删除
        List<RiskEventRule> riskEventRuleList = riskEventRuleService.findAll(riskCondition);

        // 3、如果有开启的策略，则不能删除
        if (CollectionUtils.isNotEmpty(riskEventRuleList)) {
            return false;
        }

        // 4、如果没有开启的策略，则可以删除
        FilterOperator filterOperator = filterOperatorService.getOne(id);
        filterOperator.setDeleteFlag(false);
        filterOperatorService.save(filterOperator);

        // 5、删除策略规则引用关系
        ruleFilterService.deleteInBatch(ruleFilters);
        return true;
    }

    /**
     * 导入文件
     *
     * @param file
     * @return
     */
    @Override
    public List<String> importRule(MultipartFile file) {
        List<String> guids = new ArrayList<>();
        String filePath = fileDir+File.separator+"upload";
        File file1 = new File(filePath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            // 获取文件流
            InputStream input = file.getInputStream();
            String fileName = file.getOriginalFilename();
            // 1、校验文件
            boolean zipFlag = checkZipFile(input);
            if (!fileName.endsWith("zip") || !zipFlag) {
                // 通过文件名与文件头进行判断
                throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "上传文件不为zip文件！");
            }


            // 将文件流存在本地
            FileUtil.uploadFile(file.getBytes(),fileDir+File.separator+"upload",fileName);

            // 2、解压文件
            // FileUtil.decompressZip(filePath+File.separator+fileName, filePath);
            FileCommonUtils.decompressionZip(filePath+File.separator+fileName,filePath,filePassword);
            // 解压后文件路径
            String newFilePath = filePath + File.separator + "tmp";

            // 3、获取文件目录中的所有文件
            List<File> files = FileCommonUtils.getAllFile(newFilePath);
            // 4、解析文件，保存数据
            if (CollectionUtils.isNotEmpty(files)) {
                for (File dataFile : files) {
                    String guid = saveDataForFile(dataFile);
                    if (StringUtils.isNotBlank(guid)) {
                        guids.add(guid);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                FileUtils.cleanDirectory(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return guids;
    }

    /**
     * 导出规则文件
     *
     * @param filterPagerVO
     * @return
     */
    @Override
    public String exportRule(FilterPagerVO filterPagerVO, HttpServletResponse httpServletResponse) {
        // 根据查询条件，查询数据
        // 创建download目录
        String filePath = fileDir + File.separator + "download";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String fileTmpPath = fileDir + File.separator + "tmp";
        File fileTmp = new File(fileTmpPath);
        if (!fileTmp.exists()) {
            fileTmp.mkdirs();
        }
        try {
            String name = filterPagerVO.getName();
            String operatorType = filterPagerVO.getOperatorType();
            List<QueryCondition> conditions = new ArrayList<>();
            if(StringUtils.isNotBlank(filterPagerVO.getGuids())){
                conditions.add(QueryCondition.in("guid", filterPagerVO.getGuids().split(",")));
            }
            if (StringUtils.isNotEmpty(name)) {
                conditions.add(QueryCondition.or(QueryCondition.like("name", name), QueryCondition.like("label", name)));
            }
            if (StringUtils.isNotEmpty(operatorType)) {
                conditions.add(QueryCondition.eq("operatorType", operatorType));
            }
            conditions.add(QueryCondition.eq("deleteFlag", true));
            conditions.add(QueryCondition.or(QueryCondition.isNull("ruleType"), QueryCondition.notEq("ruleType", FilterOperator.INSTANCE)));
            List<FilterOperator> filterOperatorList = filterOperatorService.findAll(conditions);

            for (FilterOperator filterOperator : filterOperatorList) {
                // 遍历数据 创建 文件
                FilterOpertorVO filterOpertorVO = getFilterOperatorVoData(filterOperator);
                String filterStr = JSONObject.toJSONString(filterOpertorVO);
                String fileName = filterOpertorVO.getGuid()+".txt";
                FileUtil.writeFile(filterStr,fileTmpPath + File.separator + fileName,false);
            }
            String zipName = System.currentTimeMillis()+".zip";
            // FileCommonUtils.zipFileByChannel(fileTmpPath,filePath+File.separator+zipName);
            FileCommonUtils.compressZip(filePath+File.separator+zipName,fileTmpPath,filePassword);
            // FileUtil.downLoadFile(zipName,filePath,httpServletResponse);
            return zipName;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                FileUtils.cleanDirectory(new File(fileTmpPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public String getFilePath() {
        String filePath = fileDir + File.separator + "download";
        return filePath;
    }

    /**
     * 通过查询的数据，获取FilterOpertorVO
     *
     * @param filterOperator
     * @return
     */
    public FilterOpertorVO getFilterOperatorVoData(FilterOperator filterOperator) {
        // Java bean 转换
        FilterOpertorVO filterOpertorVO =filterOpertorVoToBean(filterOperator);
        // if ("1".equals(filterOperator.getFilterType())) {
        //     // 离线任务
        //     List<QueryCondition> condition = new ArrayList<>();
        //     condition.add(QueryCondition.eq("filterCode", filterOperator.getGuid()));
        //     List<OfflineExtractTask> offlineExtractTasks = offlineExtractTaskService.findAll(condition);
        //     if (offlineExtractTasks.size() == 0) {
        //         return filterOpertorVO;
        //     }
        //     OfflineExtractTask offlineExtractTask = offlineExtractTasks.get(0);
        //     OfflineExtractTaskVo offlineExtractTaskVo = mapper.map(offlineExtractTask, OfflineExtractTaskVo.class);
        //     filterOpertorVO.setOfflineExtractTask(offlineExtractTaskVo);
        // }
        return filterOpertorVO;
    }

    public FilterOpertorVO filterOpertorVoToBean(FilterOperator filterOperator){
        FilterOpertorVO filterOpertorVO = new FilterOpertorVO();
        filterOpertorVO.setGuid(filterOperator.getGuid());
        filterOpertorVO.setName(filterOperator.getName());
        filterOpertorVO.setFilterConfig(gson.fromJson(filterOperator.getFilterConfig(), FilterConfigObject.class));
        filterOpertorVO.setSourceIds(JSONArray.parseArray(filterOperator.getSourceIds(),String.class));
        filterOpertorVO.setOutFieldInfos(JSONArray.parseArray(filterOperator.getOutFieldInfos(), OutFieldInfo.class));
        filterOpertorVO.setVersion(filterOperator.getVersion());
        filterOpertorVO.setDeleteFlag(filterOperator.getDeleteFlag());
        filterOpertorVO.setDependencies(JSONArray.parseArray(filterOperator.getDependencies(), Dependencies.class));
        filterOpertorVO.setStatus(filterOperator.isStatus());
        filterOpertorVO.setOutputs(JSONArray.parseArray(filterOperator.getOutputs(), Outputs.class));
        filterOpertorVO.setOperatorType(filterOperator.getOperatorType());
        filterOpertorVO.setIdeVersion("");
        filterOpertorVO.setMultiVersion(filterOperator.getMultiVersion());
        filterOpertorVO.setCode(filterOperator.getCode());
        filterOpertorVO.setLabel(filterOperator.getLabel());
        filterOpertorVO.setDesc(filterOperator.getDesc());
        filterOpertorVO.setCreateTime(filterOperator.getCreateTime());
        filterOpertorVO.setUpdateTime(filterOperator.getUpdateTime());
        filterOpertorVO.setRoomType(filterOperator.getRoomType());
        filterOpertorVO.setFilterConfigTemplate(JSONObject.parseObject(filterOperator.getFilterConfigTemplate(),FilterConfigObject.class));
        filterOpertorVO.setParamConfig(JSONArray.parseArray(filterOperator.getParamConfig(), ParamConfigVO.class));
        filterOpertorVO.setParamValue(JSONObject.parseObject(filterOperator.getParamValue(), Map.class));
        filterOpertorVO.setChildren(null);
        filterOpertorVO.setModelId(filterOperator.getModelId());
        filterOpertorVO.setTag(filterOperator.getTag());
        filterOpertorVO.setRuleType(filterOperator.getRuleType());
        filterOpertorVO.setNewlineFlag(filterOperator.getNewlineFlag());
        filterOpertorVO.setFilterType(filterOperator.getFilterType());
        filterOpertorVO.setInitStatus(filterOperator.getInitStatus());
        filterOpertorVO.setRuleFilterType(filterOperator.getRuleFilterType());
        filterOpertorVO.setInitStatus(filterOperator.getInitStatus());

        // 增加规则描述
        filterOpertorVO.setFilterDesc(filterOperator.getFilterDesc());
        filterOpertorVO.setViolationScenario(filterOperator.getViolationScenario());
        filterOpertorVO.setHarm(filterOperator.getHarm());
        filterOpertorVO.setAttackLine(filterOperator.getAttackLine());
        filterOpertorVO.setThreatCredibility(filterOperator.getThreatCredibility());
        filterOpertorVO.setDealAdvcie(filterOperator.getDealAdvcie());
        filterOpertorVO.setPrinciple(filterOperator.getPrinciple());

        return  filterOpertorVO;
    }

    /**
     * 保存规则数据
     *
     * @param file
     */
    public String saveDataForFile(File file) {
        try {
            // 读取数据
            String fileStr = FileUtils.readFileToString(file, "utf-8");

            // 转成Java bean
            FilterOpertorVO filterOpertorVO = JSONObject.parseObject(StringEscapeUtils.unescapeHtml(fileStr), FilterOpertorVO.class);
            // FilterOpertorVO filterOpertorVO =gson.fromJson(StringEscapeUtils.unescapeHtml(fileStr), FilterOpertorVO.class);
            FilterOperator filterOperator = getFilterOperatorForVo(filterOpertorVO);

            // 判断是否是离线规则
            // if ("1".equals(filterOperator.getFilterType())) {
            //     // 若是离线规则，则需要保存离线数据
            //     OfflineExtractTaskVo offlineExtractTaskVo = filterOpertorVO.getOfflineExtractTask();
            //     OfflineExtractTask offlineExtractTask = mapper.map(offlineExtractTaskVo, OfflineExtractTask.class);
            //     offlineExtractTaskService.save(offlineExtractTask);
            // }
            filterOperatorService.save(filterOperator);
            return filterOperator.getGuid();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FilterOperator getFilterOperatorForVo(FilterOpertorVO filterOpertorVO){
        FilterOperator filterOperator = mapper.map(filterOpertorVO, FilterOperator.class);
        FilterConfigObject filterConfigObject = filterOpertorVO.getFilterConfig();
        filterOperator.setFilterConfig(gson.toJson(filterConfigObject));
        filterOperator.setOutFieldInfos(gson.toJson(filterOpertorVO.getOutFieldInfos()));
        filterOperator.setOutputs(gson.toJson(filterOpertorVO.getOutputs()));
        filterOperator.setSourceIds(gson.toJson(filterOpertorVO.getSourceIds()));
        filterOperator.setFilterConfigTemplate(gson.toJson(filterOpertorVO.getFilterConfigTemplate()));
        filterOperator.setParamConfig(gson.toJson(filterOpertorVO.getParamConfig()));
        return filterOperator;
    }

    public boolean checkZipFile(InputStream input) {
        try {
            byte[] bytes = new byte[3];
            input.read(bytes, 0, bytes.length);
            String endFileName = bytesToHexString(bytes);
            if ("504B03".equalsIgnoreCase(endFileName)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
