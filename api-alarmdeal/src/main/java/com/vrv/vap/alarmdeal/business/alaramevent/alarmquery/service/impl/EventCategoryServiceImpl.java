package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.ThreatLibraryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.ThreatExtra;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventAlarmSettingService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.repository.EventCategoryRespository;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ITreeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RiskEventRuleQueryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.ThreatLibraryExcelVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventCategoryVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event.EventCategoryVRVTreeVO;
import com.vrv.vap.alarmdeal.business.analysis.model.EventAlarmSetting;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.AlarmInfoMergerHandler;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.RuleMergeHandler;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.ThreatExtraService;
import com.vrv.vap.alarmdeal.business.analysis.vo.RuleInfoVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.util.CommomLocalCache;
import com.vrv.vap.common.model.User;
import com.vrv.vap.exportAndImport.excel.ExcelUtils;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.struct.tree.TreeFactory;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class EventCategoryServiceImpl extends BaseServiceImpl<EventCategory, String> implements EventCategoryService {

    private static Logger logger = LoggerFactory.getLogger(EventCategoryServiceImpl.class);

    @Autowired
    private EventCategoryRespository eventCategoryRespository;

    @Autowired
    private MapperUtil mapper;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private ThreatExtraService threatExtraService;

    @Autowired
    private AlarmInfoMergerHandler alarmInfoMergerHandler;

    @Autowired
    private EventAlarmSettingService eventAlarmSettingService;

    @Autowired
    private RiskEventRuleService riskEventRuleService;

    @Override
    public EventCategoryRespository getRepository() {
        return eventCategoryRespository;
    }

    @Override
    public List<EventCategoryVRVTreeVO> getEventCateTree() {
        List<QueryCondition> conditions = new ArrayList<>();
        // 获得规则类和模型类
//		conditions.add(QueryCondition.like("codeLevel", "safer"));
//		conditions.add(QueryCondition.like("codeLevel", "model"));

        Sort sort = Sort.by(Sort.Direction.ASC, "orderNum");
        List<EventCategory> list = findAll(conditions, sort);

        Set<EventCategory> copylist = new HashSet<>();
        copylist.addAll(list);
        User currentUser = SessionUtil.getCurrentUser();
		// User currentUser = new User();
		// currentUser.setRoleCode(Arrays.asList("businessMgr"));
		// currentUser.setId(54);
        if (currentUser != null) {
            List<String> roleCode = currentUser.getRoleCode();
            // if (roleCode != null && !roleCode.contains("admin") && !roleCode.contains("secretMgr")) {
            //     List<QueryCondition> cons = new ArrayList<>();
            //     List<QueryCondition> ors = new ArrayList<>();
            //     for (String code : roleCode) {
            //         ors.add(QueryCondition.like("toRole", "\"" + code + "\""));
            //     }
            //     ors.add(QueryCondition.like("toUser", ":" + currentUser.getId() + ","));
            //
            //     cons.add(QueryCondition.or(ors));
            //     List<EventAlarmSetting> findAll = eventAlarmSettingService.findAll(cons);
            //     List<EventAlarmSetting> initSettingDatas = findAll.stream().filter(item -> item.getGuid().equals(item.getLinkGuid())).collect(Collectors.toList());
            //     List<String> guids = new ArrayList<>();
            //     for (EventAlarmSetting setting : initSettingDatas) {
            //         guids.add(setting.getLinkGuid());
            //     }
            //     copylist.clear();
            //     queryRuleData(list, copylist, guids);
            //
            //     // List<EventAlarmSetting> settingDatas = findAll.stream().filter(item -> !item.getGuid().equals(item.getLinkGuid())).collect(Collectors.toList());
            //     // List<String> guids1 = new ArrayList<>();
            //     // for (EventAlarmSetting setting : settingDatas) {
            //     //
            //     //     guids1.add(setting.getLinkGuid());
            //     // }
            //     // queryRuleData(list, copylist, guids1);
            // }
        }

        List<EventCategoryVRVTreeVO> mapList = mapper.mapList(copylist, EventCategoryVRVTreeVO.class);
        List<EventCategoryVRVTreeVO> buildTree = buildTree(mapList, "0");
        //List<ITreeVO> itreeVO = getItreeVO(buildTree);
        return buildTree;
    }

    /**
     * 查询规则信息
     * @param list
     * @param copylist
     * @param guids
     */
    private void queryRuleData(List<EventCategory> list, Set<EventCategory> copylist, List<String> guids) {
        while (!guids.isEmpty()) {
            List<String> parentIds = new ArrayList<>();
            for (EventCategory category : list) {
                if (guids.contains(category.getId())) {
                    RiskEventRuleQueryVO riskEventRuleVO = new RiskEventRuleQueryVO();
                    riskEventRuleVO.setWarmType(category.getCodeLevel());
                    List<QueryCondition> querys = riskEventRuleService.getRiskEventQueryConditions(riskEventRuleVO);
                    long count = riskEventRuleService.count(querys);
                    if (count == 0) {
                        continue;
                    }
                    copylist.add(category);
                    parentIds.add(category.getParentId());
                }
            }
            guids = parentIds;
        }
    }

    public static List<EventCategoryVRVTreeVO> buildTree(Collection<EventCategoryVRVTreeVO> treeNodes, String topParentId) {
        List<EventCategoryVRVTreeVO> result = new ArrayList<>();
        Map<String, EventCategoryVRVTreeVO> tmp = new HashMap<>();
        for (EventCategoryVRVTreeVO node : treeNodes) {
            tmp.put(node.getKey(), node);
            node.setChildren(new ArrayList<>());
        }

        for (EventCategoryVRVTreeVO cNode : treeNodes) {
            String parentId = cNode.getParentId();
            if (parentId != null && parentId.equals(topParentId)) {
                //result.add(cNode);

                appendNodeForOrder(result, cNode);

            } else {
                EventCategoryVRVTreeVO parentNode = tmp.get(parentId);
                if (parentNode != null) {
                    //parentNode.getChildren().add(cNode);
                    appendNodeForOrder(parentNode.getChildren(), cNode);
                }
            }
        }

        return result;
    }

    private static void appendNodeForOrder(List<EventCategoryVRVTreeVO> result, EventCategoryVRVTreeVO cNode) {
        if (result.isEmpty()) {
            result.add(cNode);
        } else {
            int index = 0;
            for (EventCategoryVRVTreeVO item : result) {
                if (cNode.getOrderNum() <= item.getOrderNum()) {
                    break;
                } else {
                    index++;
                }
            }
            result.add(index, cNode);
        }
    }

    private List<ITreeVO> getItreeVO(List<EventCategoryVRVTreeVO> list) {
        List<ITreeVO> result = new ArrayList<>();
        for (EventCategoryVRVTreeVO eventCategoryVrvTreeVO : list) {
            List<ITreeVO> itreeVO = getItreeVO(eventCategoryVrvTreeVO, eventCategoryVrvTreeVO.getParentId());
            result.addAll(itreeVO);
        }

        return result;
    }

    private List<ITreeVO> getItreeVO(EventCategoryVRVTreeVO treevo, String parentKey) {
        List<ITreeVO> result = new ArrayList<>();
        ITreeVO itreeVO = new ITreeVO();
        String key = null;
        if ("0".equals(treevo.getParentId())) {
            key = treevo.getKey();
            itreeVO.setKey(treevo.getKey());
        } else {
            key = parentKey + "-" + treevo.getKey();
            itreeVO.setKey(key);
        }
        itreeVO.setTitle(treevo.getTitle());
        if (treevo.getChildren() != null) {
            for (EventCategoryVRVTreeVO child : treevo.getChildren()) {
                result.addAll(getItreeVO(child, key));
            }
        }
        result.add(itreeVO);
        return result;
    }

    @Override
    public Result<Boolean> addEventCategory(EventCategory eventCategory) {
        Result<Boolean> result = new Result<>();
        String createdTime = DateUtil.format(new Date());
        eventCategory.setId(UUIDUtils.getUUID());
        eventCategory.setStatus(0);// 1系统内置0其他
        eventCategory.setType(EventCategory.CUSTOM_TYPE);
        eventCategory.setCreatedTime(createdTime);
        try {
            save(eventCategory);
        } catch (Exception e) {
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
            result.setData(false);
            result.setMsg(e.getMessage());
            return result;
        }
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setData(true);
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        return result;
    }

    @Override
    public Result<EventCategory> editEventCategory(EventCategory eventCategory) {
        String modifyTime = DateUtil.format(new Date());
        eventCategory.setModifiedTime(modifyTime);
        try {
            save(eventCategory);
            updateRuleMergeHandlerMap(eventCategory);
        } catch (Exception e) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
        return ResultUtil.success(eventCategory);
    }

    /**
     * 更新RuleMergeHandlerMap
     *
     * @param eventCategory
     */
    private void updateRuleMergeHandlerMap(EventCategory eventCategory) {
        Map<String, RuleMergeHandler> map = alarmInfoMergerHandler.getMap();
        for (Map.Entry<String, RuleMergeHandler> entry : map.entrySet()) {
            RuleMergeHandler ruleMergeHandler = entry.getValue();
            RuleInfoVO ruleInfoVO = ruleMergeHandler.getRuleInfoVO();
            String riskEventId = ruleInfoVO.getRiskEventId();
            if (riskEventId.equals(eventCategory.getId())) {
                ruleInfoVO.setPrinciple(eventCategory.getPrinciple());
                ruleInfoVO.setHarm(eventCategory.getHarm());
                ruleMergeHandler.setRuleInfoVO(ruleInfoVO);
                entry.setValue(ruleMergeHandler);
            }
        }
        alarmInfoMergerHandler.setMap(map);
    }

    @Override
    public PageRes<EventCategoryVO> getEventCategoryPager(EventCategoryVO eventCategoryVO, Pageable pageable) {
        PageRes<EventCategoryVO> pageRes = new PageRes<>();
        String id = eventCategoryVO.getId();
        EventCategory eventCategory = getOne(id);
        String codeLevel = null;
        List<QueryCondition> conditions = new ArrayList<>();
        if (eventCategory != null) {
            codeLevel = eventCategory.getCodeLevel();
            if (StringUtils.isNotEmpty(codeLevel)) {
                conditions.add(QueryCondition.likeBegin("codeLevel", codeLevel));
            }
        }
        String title = eventCategoryVO.getTitle();
        if (StringUtils.isNotEmpty(title)) {
            conditions.add(QueryCondition.like("title", title));
        }
        Page<EventCategory> findAll = findAll(conditions, pageable);
        List<EventCategory> content = findAll.getContent();
        List<EventCategoryVO> list = mapper.mapList(content, EventCategoryVO.class);
        pageRes.setCode("0");
        pageRes.setList(list);
        pageRes.setTotal(findAll.getTotalElements());
        return pageRes;
    }

    /**
     * 递归获得事件分类的根节点
     *
     * @param parentId
     * @return
     */
    private List<EventCategory> getParentCategory(String parentId) {
        List<EventCategory> list = new ArrayList<>();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("id", parentId));
        List<EventCategory> findAll = findAll(conditions);
        list.addAll(findAll);
        for (EventCategory eventCategory : findAll) {
            String id = eventCategory.getParentId();
            list.addAll(getParentCategory(id));
        }
        return list;
    }

    @Override
    public List<EventCategoryVRVTreeVO> getEventCategoryTreeByEventCode(String eventCode) {
        List<EventCategory> list = new ArrayList<>();
        List<QueryCondition> eventConditions = new ArrayList<>();
        eventConditions.add(QueryCondition.eq("codeLevel", eventCode));
        List<EventCategory> eventList = findAll(eventConditions);
        if (eventList.size() == 1) {
            EventCategory eventCategory = eventList.get(0);
            list.add(eventCategory);
            String parentId = eventCategory.getParentId();
            List<EventCategory> parentCategoryList = getParentCategory(parentId);
            list.addAll(parentCategoryList);
        }
        List<EventCategoryVRVTreeVO> mapList = mapper.mapList(list, EventCategoryVRVTreeVO.class);
        List<EventCategoryVRVTreeVO> buildTree = TreeFactory.buildTree(mapList, "0");
        return buildTree;
    }

    @Override
    public Result<Boolean> exportThreatLibrary(ThreatLibraryVO threatLibraryVO) {
        String filePath = Paths.get(fileConfiguration.getFilePath(), fileConfiguration.getThreatLibraryName()).toString(); //获得文件路径
        String threatName = threatLibraryVO.getThreat_name(); //名称
        String threatClassification = threatLibraryVO.getThreat_classification(); //类型
        String relateVulnerability = threatLibraryVO.getRelate_vulnerability(); //关联漏洞

        List<QueryCondition> conditions = new ArrayList<>();
        if (StringUtils.isNotEmpty(threatName)) {
            conditions.add(QueryCondition.like("threatName", threatName));
        }
        if (StringUtils.isNotEmpty(threatClassification)) { //威胁分类
            conditions.add(QueryCondition.likeBegin("threatClassification", threatClassification));
        }
        if (StringUtils.isNotEmpty(relateVulnerability)) {
            conditions.add(QueryCondition.eq("relateVulnerability", relateVulnerability));
        }
        List<EventCategory> list = findAll(conditions);
        List<ThreatLibraryExcelVO> excelList = new ArrayList<>();
        for (EventCategory eventCategory : list) {
            ThreatLibraryExcelVO threatLibraryExcelVO = new ThreatLibraryExcelVO();
            String threatLibraryId = eventCategory.getId();
            mapper.copy(eventCategory, threatLibraryExcelVO);
            ThreatExtra threatExtra = threatExtraService.getOne(threatLibraryId);
            if (threatExtra != null) {
                mapper.copy(threatExtra, threatLibraryExcelVO);
            }
            excelList.add(threatLibraryExcelVO);
        }
        try {

            ExcelUtils.getInstance().exportObjects2Excel(excelList, ThreatLibraryExcelVO.class, true, filePath);
            return ResultUtil.success(true);
        } catch (ExcelException | IOException e) {
            logger.error("导出excel问题:{}", e.getMessage());
        }
        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "生成excel失败");
    }

    @Override
    public Result<Boolean> importThreatLibraryInfo(CommonsMultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        String realPath = fileConfiguration.getFilePath(); // 文件路径
        String fileName = file.getOriginalFilename();
        String filePath = Paths.get(FilenameUtils.normalize(realPath), FilenameUtils.normalize(fileName)).toString();
        try {
            FileUtil.uploadFile(fileBytes, realPath, fileName); //上传文件
        } catch (Exception e) {
            logger.error("上传文件失败", e);
        }
        //解析文件
        try {
            List<ThreatLibraryExcelVO> list = ExcelUtils.getInstance().readExcel2Objects(filePath, ThreatLibraryExcelVO.class);
            List<EventCategory> threatLibrarylist = mapper.mapList(list, EventCategory.class);
            List<ThreatExtra> threatExtraList = new ArrayList<>();
            for (ThreatLibraryExcelVO threatLibraryExcelVO : list) {
                String id = threatLibraryExcelVO.getId();
                ThreatExtra threatExtra = threatExtraService.getOne(id);
                if (threatExtra != null) {
                    ThreatExtra threatExtraImport = new ThreatExtra();
                    threatExtraImport.setThreat_library_id(id);
                    mapper.copy(threatLibraryExcelVO, threatExtraImport);
                    threatExtraList.add(threatExtraImport);
                }
            }
            save(threatLibrarylist);
            threatExtraService.save(threatExtraList);
            FileUtil.deleteFile(fileName, realPath);
            return ResultUtil.success(true);
        } catch (InvalidFormatException e) {
            logger.error("上传报错信息：" + e.getMessage());
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    @Override
    public List<EventCategory> queryAllEventCategory() {
        List<EventCategory> result = new ArrayList<>();
        if (CommomLocalCache.containsKey("eventCategory-list")) {
            result.addAll(CommomLocalCache.get("eventCategory-list"));
        } else {
            // 缓存中不存在，查询数据
            List<EventCategory> eventCategoryList = findAll();
            if (CollectionUtils.isNotEmpty(eventCategoryList)) {
                // 查询新数据后，更新缓存
                result.addAll(eventCategoryList);
                CommomLocalCache.put("eventCategory-list", eventCategoryList, 2, TimeUnit.HOURS);
            }
        }
        return result;
    }

}
