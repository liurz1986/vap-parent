package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.gson.Gson;
import com.mchange.util.AssertException;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmDataHandleService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.LogIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.RiskRuleIdVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.EvenTypeEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.PageReqESUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.EventTaVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.IdTitleValue;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AppAlarmEventAttributeVO;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.PushService;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.enums.AssetTrypeGroupEnum;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.FileInfo;
import com.vrv.vap.alarmdeal.frameworks.contract.ResultModel;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.alarmdeal.frameworks.feign.RiskFegin;
import com.vrv.vap.alarmdeal.frameworks.feign.ServerSystemFegin;
import com.vrv.vap.alarmdeal.frameworks.util.SocUtil;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchMapManage;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.exportAndImport.excel.model.JobResult;
import com.vrv.vap.exportAndImport.excel.util.ExcelExport;
import com.vrv.vap.exportAndImport.util.LambdaExceptionUtil;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月22日 18:23
 */
@Service
public class AlarmEventManagementServiceImpl implements AlarmEventManagementService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(AlarmEventManagementServiceImpl.class);

    @Autowired
    MapperUtil mapper;

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;

    @Autowired
    EventCategoryService eventCategoryService;

    @Autowired
    RiskEventRuleService riskEventRuleService;

    @Autowired
    SelfConcernAssetService selfConcernAssetService;

    @Autowired
    PushService pushService;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    ServerSystemFegin fileService;

    @Autowired
    DataSourceFegin dataSourceFegin;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ElasticSearchMapManage elasticSearchMapManage;

    @Autowired
    private AlarmDataHandleService alarmDataHandleService;

    @Autowired
    private FilterOperatorService filterOperatorService;

    private Map<String, List<RiskEventRule>> riskEventRuleMap = null;

    private Map<String, List<FilterOperator>> filterMap = null;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private RiskFegin riskFegin;

    /**
     * 获得告警事件列表
     *
     * @param query
     * @return
     */
    @Override
    public PageRes_ES<AlarmEventAttributeVO> getAlarmDealPager(EventDetailQueryVO query) {
        logger.info("获得告警事件列表 getAlarmDealPager start");
        // 定义返回
        PageRes_ES<AlarmEventAttributeVO> result = new PageRes_ES<>();
        // 判断登录
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            result.setCode("-1");
            result.setMessage("用户未登录");
            result.setTotal(0L);
            return result;
        }
//         User currentUser = new User();
//         currentUser.setId(33);
//         currentUser.setRoleCode(Arrays.asList("admin"));

        // 查询ES
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryResult(query, query, true);

        // 数据转换
        List<AlarmEventAttribute> sourceList = pageQueryResult.getList();
        List<AlarmEventAttributeVO> list = mapper.mapList(sourceList, AlarmEventAttributeVO.class);

        // 查询事件分类
        List<EventCategory> findAll = getEventCategories();
        List<String> roleCodes = currentUser.getRoleCode();

        String idStr = Integer.toString(currentUser.getId());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                for (EventCategory eventCategory : findAll) {
                    if (item.getEventCode().startsWith(eventCategory.getCodeLevel())) {
                        item.setEventTypeName(eventCategory.getTitle());
                        break;
                    }
                }
                // 处理告警数据分类信息
                getAlarmAuthData(roleCodes, idStr, item);
                // 处理告警数据告警规则
                getRiskEventRuleData(item);
            });
        }
        result.setCode(pageQueryResult.getCode());
        result.setMessage(pageQueryResult.getMessage());
        result.setTotal(pageQueryResult.getTotal());
        result.setList(list);
        return result;
    }

    @Override
    public Result updateAlarmDealTest(Map<String, Integer> param) {
        Result result = new Result();
        try {
            Integer integer = param.get("day");
            Integer count = param.get("count");
            Integer type = param.get("type");
            Integer sum = integer * count;
            EventDetailQueryVO eventDetailQueryVO = new EventDetailQueryVO();
            eventDetailQueryVO.setIsJustAssetOfConcern(false);
//            eventDetailQueryVO.setEndTime(new Date());
            PageReq pageReq = new PageReq();
            pageReq.setStart_(0);
            pageReq.setCount_(sum);
            PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryResult(eventDetailQueryVO, pageReq, false);
            List<AlarmEventAttribute> list = pageQueryResult.getList();
            Date nowDate = new Date();
            List<String> datesBetweenDays=new ArrayList<>();
            Date date=new Date();
            if (type!=null){
                if (type==1){
                    date = DateUtil.addDay(nowDate, -integer);
                    datesBetweenDays = DateUtil.getDatesBetweenDays(DateUtil.format(date, DateUtil.DEFAULT_DATE_PATTERN),DateUtil.format(nowDate, DateUtil.DEFAULT_DATE_PATTERN),  DateUtil.Year_Mouth_Day);
                }
            }else {
                DateUtil.addDay(nowDate, integer);
                datesBetweenDays = DateUtil.getDatesBetweenDays(DateUtil.format(nowDate, DateUtil.DEFAULT_DATE_PATTERN),DateUtil.format(date, DateUtil.DEFAULT_DATE_PATTERN),  DateUtil.Year_Mouth_Day);
            }
            Integer indexUp=0;
            for (int i = datesBetweenDays.size()-1; i >0 ; i--) {
                System.out.println(datesBetweenDays.get(i));
                for (int j = 0; j <count; j++) {
                    AlarmEventAttribute alarmEventAttribute = list.get(indexUp);
                    System.out.println(alarmEventAttribute.getEventCreattime());
                    alarmEventAttribute.setEventCreattime(DateUtil.parseDate(datesBetweenDays.get(i)+" "+DateUtil.format(alarmEventAttribute.getEventCreattime(),"HH:mm:ss"),DateUtil.DEFAULT_DATE_PATTERN));
//                    alarmEventAttribute.setEventCreattime(DateUtil.parseDate(datesBetweenDays.get(i)+" "+"00:00:01",DateUtil.DEFAULT_DATE_PATTERN));
                    indexUp++;
                }
            }
            alarmEventManagementForEsService.saveAlarmEventDatas(list);
        } catch (Exception e) {
            result.setCode(999);
            result.setMsg("失败");
            return result;
        }
        result.setCode(0);
        result.setMsg("成功");
        return result;
    }
    @Override
    public Result updateAlarmTypeTest() {
        Result result = new Result();
        try {
            EventDetailQueryVO eventDetailQueryVO = new EventDetailQueryVO();
            eventDetailQueryVO.setIsJustAssetOfConcern(false);
//            eventDetailQueryVO.setEndTime(new Date());
            PageReq pageReq = new PageReq();
            pageReq.setStart_(0);
            Date nowDate = new Date();
            Date date = DateUtil.addDay(nowDate, -8);
            List<Map<String,String>> mapList=new ArrayList<>();
            Map<String,String> map1=new HashMap<>();
            map1.put("ruleId","5c5a9afd07964f9aa7e2a014d0e25087");
            map1.put("evenType","4");
            Map<String,String> map2=new HashMap<>();
            map2.put("ruleId","cb31387d9b7c4c2a9361005c4ed5dc83");
            map2.put("evenType","3");
            Map<String,String> map3=new HashMap<>();
            map3.put("ruleId","29c99de46b4a49968445d29ff2c0efe1");
            map3.put("evenType","3");
            Map<String,String> map4=new HashMap<>();
            map4.put("ruleId","637049076f49432494188c71338a1e43");
            map4.put("evenType","3");
            Map<String,String> map5=new HashMap<>();
            map5.put("ruleId","31c125040d0c400495fdc793c0c46002");
            map5.put("evenType","5");
            Map<String,String> map6=new HashMap<>();
            map6.put("ruleId","4e2cf7282bb844c3938e0e6d894f63e4");
            map6.put("evenType","6");
            Map<String,String> map7=new HashMap<>();
            map7.put("ruleId","7c7cca97154a4dc4ab0cfcd84d09eec3");
            map7.put("evenType","2");
            mapList.add(map1);
            mapList.add(map2);
            mapList.add(map3);
            mapList.add(map4);
            mapList.add(map5);
            List<String> datesBetweenDays = DateUtil.getDatesBetweenDays(DateUtil.format(date, DateUtil.DEFAULT_DATE_PATTERN),DateUtil.format(nowDate, DateUtil.DEFAULT_DATE_PATTERN),  DateUtil.Year_Mouth_Day);
            for (int i = datesBetweenDays.size()-1; i >0 ; i--) {
                System.out.println(datesBetweenDays.get(i));
                eventDetailQueryVO.setEndTime(DateUtil.parseDate(datesBetweenDays.get(i)+" 23:59:59",DateUtil.DEFAULT_DATE_PATTERN));
                eventDetailQueryVO.setBeginTime(DateUtil.parseDate(datesBetweenDays.get(i)+" 00:00:00",DateUtil.DEFAULT_DATE_PATTERN));
                PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryResult(eventDetailQueryVO, pageReq, false);
                List<AlarmEventAttribute> list = pageQueryResult.getList();
                if (list.size()>0){
                    Random random=new Random();
                    for (int j = 0; j <5 ; j++) {
                        AlarmEventAttribute alarmEventAttribute = list.get(j);
                        int i1 = random.nextInt(mapList.size());
                        Map<String, String> map = mapList.get(i1);
                        alarmEventAttribute.setRuleId(map.get("ruleId"));
                        alarmEventAttribute.setEventType(Integer.valueOf(map.get("evenType")));
                    }
                    alarmEventManagementForEsService.saveAlarmEventDatas(list);
                }
            }
        } catch (Exception e) {
            result.setCode(999);
            result.setMsg("失败");
            return result;
        }
        result.setCode(0);
        result.setMsg("成功");
        return result;
    }

    @Override
    public List<NameValue> getAssetAlarmEventTop10(String type) {
        List<NameValue> nameValues=new ArrayList<>();
        String treeCode = "";
        switch(type){
            case "assetHost":
                treeCode = AssetTrypeGroupEnum.ASSETHOSt.getTreeCode();
                break;
            case "assetService" :
                treeCode = AssetTrypeGroupEnum.ASSETSERVICE.getTreeCode();
                break;
            case "assetNetworkDevice" :
                treeCode = AssetTrypeGroupEnum.ASSETNET.getTreeCode();
                break;
            case "assetSafeDevice" :
                treeCode = AssetTrypeGroupEnum.ASSETSAFE.getTreeCode();
                break;
            case "assetMaintenHost" :
                treeCode = AssetTrypeGroupEnum.ASSETMAINTEN.getTreeCode();
                break;
            default:
                break;
        }
        //获取该类型ips
        List<String> ips=assetService.getAssetIpsByTypeGroup(treeCode);
        List<QueryCondition_ES> querys =  new ArrayList<>();
        querys.add(QueryCondition_ES.in("principalIp", ips));
        querys.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        Map<String, Long> principalIp = alarmEventManagementForEsService.getCountGroupNumByFieldSize(alarmEventManagementForEsService.getIndexName(), "principalIp", querys, 10);
        List<Map.Entry<String,Long>> list = new ArrayList<Map.Entry<String,Long>>(principalIp.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Long>>() {
            //降序排序
            @Override
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        for (Map.Entry<String, Long> entry:list) {
            nameValues.add(new NameValue(entry.getValue().toString(),entry.getKey()));
        }
        return nameValues;
    }

    @Override
    public List<Map<String, Map<String, Long>>> culStealLeakValue() {
        Date date = new Date();
        List<QueryCondition_ES> querys =  new ArrayList<>();
        querys.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        List<Map<String, Map<String, Long>>> list=alarmEventManagementForEsService.getAggIpLevelData(alarmEventManagementForEsService.getIndexName(),"principalIp","alarmRiskLevel",querys);
        //计算资产窃密值
        assetService.culStealLeakValue(list,date);
        //计算应用系统且泄密密
        assetService.culAppStealLeakValue(date);
        //计算网络边界窃泄密
        assetService.culAppMaintenStealLeakValue(date);
        return list;
    }

    @Override
    public void setEventNumber(List<AssetVO> list) {
        if (list.size()>0){
            for (AssetVO assetVO:list) {
                String ip = assetVO.getIp();
                List<QueryCondition_ES> querys =  new ArrayList<>();
                querys.add(QueryCondition_ES.eq("principalIp",ip));
                querys.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
                long count = alarmEventManagementForEsService.count(querys);
                assetVO.setEventNumber(Math.toIntExact(count));
            }
        }
    }


    /**
     * 处理告警数据告警规则
     *
     * @param item
     */
    private void getRiskEventRuleData(AlarmEventAttributeVO item) {
        if (!StringUtils.isEmpty(item.getFilterCode())) {
//            if(riskEventRuleMap == null){
//                riskEventRuleMap = alarmDataHandleService.getRiskEventRuleMapForId();
//            }
////            RiskEventRule riskEventRule = riskEventRuleService.getOne(item.getRuleId());
//            List<RiskEventRule> riskEventRules = riskEventRuleMap.get(item.getRuleId());
//            if (CollectionUtils.isNotEmpty(riskEventRules)) {
//                RiskEventRule riskEventRule = riskEventRules.get(0);
//                item.setHarm(riskEventRule.getHarm());
//                item.setPrinciple(riskEventRule.getPrinciple());
//                item.setDealAdvice(riskEventRule.getDealAdvcie());
//            }
            if (filterMap == null) {
                List<QueryCondition> conditions = new ArrayList<>();
                conditions.add(QueryCondition.eq("deleteFlag", true));
                List<FilterOperator> list = filterOperatorService.findAll(conditions);
                filterMap = list.stream().collect(Collectors.groupingBy(FilterOperator::getCode));
            }
            List<FilterOperator> filterOperators = filterMap.get(item.getFilterCode());
            if (CollectionUtils.isNotEmpty(filterOperators)) {
                FilterOperator filterOperator = filterOperators.get(0);
                item.setHarm(filterOperator.getHarm());
               // item.setPrinciple(filterOperator.getPrinciple());
                item.setDealAdvice(filterOperator.getDealAdvcie());
            }
        }
    }

    /**
     * 处理告警数据分类信息
     *
     * @param roleCodes
     * @param idStr
     * @param item
     */
    private void getAlarmAuthData(List<String> roleCodes, String idStr, AlarmEventAttributeVO item) {
        AuthorizationControl authorization = item.getAuthorization();
        if (authorization != null) {
            item.setCanRead(true);
            item.setCanDeal(false);
            List<GuidNameVO> canOperateRole = authorization.getCanOperateRole();
            if (canOperateRole != null && !canOperateRole.isEmpty()) {
                for (GuidNameVO vo : canOperateRole) {
                    if (roleCodes.contains(vo.getGuid())) {
                        item.setCanDeal(true);
                        break;
                    }
                }
            }
            if (!Boolean.TRUE.equals(item.getCanDeal())) {
                List<GuidNameVO> canOperateUser = authorization.getCanOperateUser();
                if (canOperateUser != null && !canOperateUser.isEmpty()) {
                    for (GuidNameVO vo : canOperateUser) {
                        if (idStr.equals(vo.getGuid())) {
                            item.setCanDeal(true);
                            break;
                        }
                    }
                }
            }
        } else {
            authorization = new AuthorizationControl();
            item.setCanRead(true);
            item.setCanDeal(false);
            item.setAuthorization(authorization);
        }
    }

    /**
     * 查询事件分类
     *
     * @return
     */
    private List<EventCategory> getEventCategories() {
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.likeBegin("codeLevel", "/safer/"));
        conditions.add(QueryCondition.not(QueryCondition.likeBegin("codeLevel", "/safer/%/")));
        return eventCategoryService.findAll(conditions);
    }

    /**
     * 获取关注资产统计
     *
     * @param query 参数
     * @param top   参数
     * @return
     */
    @Override
    public List<NameValue> getAssetOfConcern(EventDetailQueryVO query, Integer top) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        //获取关注的资产的ip
        List<String> concerns = alarmEventManagementForEsService.getIpsOfConcern();
        if (top == 0) {
            top = 1000;
        }
        List<String> ips = concerns.subList(0, concerns.size() > top ? top : concerns.size());
        List<QueryCondition_ES> cons = new ArrayList<>();
        cons.addAll(querys);
        cons.add(QueryCondition_ES.in(getBaseField() + "principalIp", ips));
        List<NameValue> result = alarmEventManagementForEsService.getStatisticsByStringField(cons, top, "principalIp");
        return result;
    }

    /**
     * 设置关注的资产的ip
     *
     * @param param
     * @return java.lang.Boolean
     */
    @Override
    public Boolean setConcernIps(Map<String, List<String>> param) {
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("userId", currentUser.getId()));
        conditions.add(QueryCondition.eq("type",0));
        List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(conditions);
        selfConcernAssetService.deleteInBatch(findAll);

        logger.info("setConcernIps deleteInBatch start");
        List<SelfConcernAsset> datas = new ArrayList<>();
        List<String> ips = param.get("ips");
        if (CollectionUtils.isNotEmpty(ips)) {
            for (String ip : ips) {
                SelfConcernAsset item = new SelfConcernAsset();
                item.setGuid(UUIDUtils.get32UUID());
                item.setIp(ip);
                item.setUserId(Integer.toString(currentUser.getId()));
                item.setType(0);
                datas.add(item);
            }
            selfConcernAssetService.save(datas);
        }
        return true;
    }

    /**
     * 关注的资产
     *
     * @return
     */
    @Override
    public List<String> getConcernIps() {
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("userId", currentUser.getId()));
        conditions.add(QueryCondition.eq("type",0));
        List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(conditions);
        List<String> ids = findAll.stream().map(SelfConcernAsset::getIp).collect(Collectors.toList());
        return ids;
    }

    /**
     * 按级别统计
     *
     * @param query
     * @return 返回每个级别的数量、总数、已读数量
     */
    @Override
    public List<NameValue> getStatisticsByAlarmRiskLevel(EventDetailQueryVO query) {
        List<NameValue> result = new ArrayList<>();
        List<NameValue> statistics = getStatisticsField(query, 5, "alarmRiskLevel");
        Map<String, String> map = statistics.stream().collect(Collectors.toMap(NameValue::getName, NameValue::getValue));
        for (int i = 1; i <= 5; i++) {
            String value = map.get(String.valueOf(i)) == null ? "0" : map.get(String.valueOf(i));
            result.add(new NameValue(value, String.valueOf(i)));
        }
        // result.addAll(statistics);
        // 统计总数
        long total = getCount(query);
        result.add(new NameValue(Long.toString(total), "total"));
        // 统计只读类型数据
        query.setIsRead(true);
        long isRead = getCount(query);
        result.add(new NameValue(Long.toString(isRead), "isRead"));

        return result;
    }

    /**
     * 统计总数
     *
     * @param query
     * @return long
     */
    private long getCount(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        long counts = alarmEventManagementForEsService.count(querys);
        return counts;
    }

    /**
     * 根据字段统计数据
     *
     * @param query
     * @param top
     * @param groupByName
     * @return
     */
    private List<NameValue> getStatisticsField(EventDetailQueryVO query, Integer top, String groupByName) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        List<NameValue> result = alarmEventManagementForEsService.getStatisticsByStringField(querys, top,
                groupByName);
        return result;
    }

    /**
     * 根据事件类型统计数据
     *
     * @param query
     * @param top
     * @return java.util.List<com.vrv.vap.alarmdeal.vo.IdTitleValue>
     */
    @Override
    public List<IdTitleValue> getStatisticsByEventType(EventDetailQueryVO query, Integer top) {
        List<NameValue> statisticsByStringField = getStatisticsField(query, top, "eventType");
        List<EventCategory> findAll = getEventCategories();
        List<IdTitleValue> result = new ArrayList<>();
        for (EventCategory eventCategory : findAll) {
            IdTitleValue vo = null;
            String riskEventCode = eventCategory.getCodeLevel();
            int eventTypeNum = AlarmDealUtil.getEventTypeNum(riskEventCode);
            for (NameValue item : statisticsByStringField) {
                if (Integer.toString(eventTypeNum).equals(item.getName())) {
                    vo = new IdTitleValue(Integer.toString(eventTypeNum), eventCategory.getTitle(), item.getValue());
                    break;
                }
            }
            if (vo != null) {
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 按照事件名称分组统计
     *
     * @param query
     * @param top
     * @return
     */
    @Override
    public List<NameValue> getStatisticsByEventName(EventDetailQueryVO query, Integer top) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        return alarmEventManagementForEsService.getStatisticsByStringField(querys, top, getBaseField() + "eventName");
    }

    /**
     * 根据告警处理状态统计
     *
     * @param query
     * @param top
     * @return
     */
    @Override
    public List<IdTitleValue> getStatisticsByAlarmDealState(EventDetailQueryVO query, Integer top) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        List<NameValue> statisticsByStringField = alarmEventManagementForEsService.getStatisticsByStringField(querys, top, getBaseField() + "alarmDealState");
        List<IdTitleValue> result = new ArrayList<>();
        AlarmDealStateEnum[] values = AlarmDealStateEnum.values();
        for (AlarmDealStateEnum item : values) {
            IdTitleValue vo = null;
            for (NameValue nameValue : statisticsByStringField) {
                if (nameValue.getName().equals(item.getCode().toString())) {
                    vo = new IdTitleValue(item.getCode().toString(), item.getTitle(), "0");
                    vo.setValue(nameValue.getValue());
                    break;
                }
            }
            if (values.length <= top) {
                if (vo == null) {
                    vo = new IdTitleValue(item.getCode().toString(), item.getTitle(), "0");
                }
            }
            if (vo != null) {
                result.add(vo);
            }
        }
        return result;
    }

    /**
     * 按照部门分组统计
     *
     * @param query 参数
     * @param top   参数
     * @return
     */
    @Override
    public List<NameValue> getStatisticsByDepartment(EventDetailQueryVO query, Integer top) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        return alarmEventManagementForEsService.getStatisticsByStringField(querys, top, getBaseField() + "unitList.unitDepartName");
    }
    @Override
    public PageRes_ES<Map<String, Object>> getAlarmEventLogsPage(EventDetailQueryVO query) {
        PageRes_ES<Map<String, Object>> byPage =new PageRes_ES<>();
        PageReq_ES pageQuery = getQueryPageEsParam(query);
        String eventTableName = query.getEventTableName();
        String index = getDataSource(eventTableName);
        AlarmEventAttribute doc = alarmEventManagementForEsService.getDocByEventId(query.getEventId());
        List<LogIdVO> logs = doc.getLogs();
        List<LogIdVO> collect = logs.stream().filter(a -> a.getEventTableName().equals(eventTableName)).collect(Collectors.toList());
        if (collect!=null){
            LogIdVO logIdVO = collect.get(0);
            List<QueryCondition_ES> conditions = new ArrayList<>();
            conditions.add(QueryCondition_ES.in("guid", logIdVO.getIds()));
            byPage = elasticSearchMapManage.findByPage(index, pageQuery, conditions);
        }
        return byPage;
    }

    @Override
    public Integer abnormalAssetCount() {
        Set<String> ips=new HashSet<>();
        List<QueryCondition_ES> cons = new ArrayList<>();
        cons.add(QueryCondition_ES.eq("alarmRiskLevel",5));
//        cons.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        Map<String, Long> ip = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(), "principalIp", cons);
        if (ip!=null){
                Set<Map.Entry<String, Long>> entries = ip.entrySet();
                for (Map.Entry<String, Long> m:entries){
                    ips.add(m.getKey());
                    System.out.println(m.getKey());
                }
        }
        Result<Map<String, Long>> mapResult = riskFegin.queryHighRiskCount();
        if (mapResult!=null){
            Map<String, Long> list = mapResult.getData();
            if (list!=null){
                Set<Map.Entry<String, Long>> entries = list.entrySet();
                for (Map.Entry<String, Long> m:entries){
                    ips.add(m.getKey());
                    System.out.println(m.getKey());
                }
            }
        }
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("ip",ips));
        long count = assetService.count(queryConditions);
        if (count>0){
            return Math.toIntExact(count);
        }
        return 0;
    }



    private PageReq_ES getQueryPageEsParam(PageReq pageReq) {
        PageReq_ES pageQuery = PageReqESUtil.getPageReq_ES(pageReq);
        if (StringUtils.isEmpty(pageQuery.getOrder_())) {
            pageQuery.setOrder_("event_time");
            pageQuery.setBy_("desc");
        }
        return pageQuery;
    }
    /**
     * 统计数量
     *
     * @param query 参数
     * @return
     */
    @Override
    public Long getEventCount(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        return alarmEventManagementForEsService.count(querys);
    }

    /**
     * 告警趋势
     *
     * @param query    参数
     * @param timeType 类型
     * @return
     */
    @Override
    public List<NameValue> getAlarmTrend(EventDetailQueryVO query, String timeType) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        String timeFormat = "yyyy-MM-dd";
        Integer interval = 24;
        if ("hour".equals(timeType)) {
            timeFormat = "yyyy-MM-dd HH";
            timeInterval = DateHistogramInterval.HOUR;
            interval = 1;
        } else if ("month".equals(timeType)) {
            timeFormat = "yyyy-MM";
            timeInterval = DateHistogramInterval.MONTH;
            interval = 30 * 24 ;
        } else if ("year".equals(timeType)) {
            timeFormat = "yyyy";
            timeInterval = DateHistogramInterval.YEAR;
            interval = 365 * 24;
        }
        AlarmDealUtil.getDataTimeUtil(timeType, timeFormat, timeInterval);
        SearchField searchField = new SearchField(getBaseField() + "eventCreattime", FieldType.Date, timeFormat, timeInterval, null, 0, 50);
        List<NameValue> result = new ArrayList<>();
        List<Map<String, Object>> queryStatistics = alarmEventManagementForEsService.queryStatistics(querys, searchField);
        List<Map<String, Object>> queryStatisticsList = SocUtil.getTimeFullMapForField2(query.getBeginTime(), query.getEndTime(), "eventCreattime", interval, timeFormat, queryStatistics);
        queryStatisticsList.forEach(map -> {
            result.add(new NameValue(map.get("doc_count").toString(), map.get(getBaseField() + "eventCreattime").toString()));
        });
        return result;
    }
    @Override
    public List<NameValue> abnormalAssetCountTrend(EventDetailQueryVO query, String timeType) {
        //获取api-risk 风险资产数据
        Map<String,List<String>> ipRisklist=new HashMap<>();
        try {
            ThreatReq threatReq =new ThreatReq();
            threatReq.setStartTime(com.vrv.vap.es.util.DateUtil.format(query.getBeginTime(), com.vrv.vap.es.util.DateUtil.DEFAULT_DATE_PATTERN));
            threatReq.setEndTime(com.vrv.vap.es.util.DateUtil.format(query.getEndTime(), com.vrv.vap.es.util.DateUtil.DEFAULT_DATE_PATTERN));
            threatReq.setTimeType(timeType);
            ipRisklist = riskFegin.queryHighRiskTrend(threatReq).getData();
        } catch (Exception e) {
            logger.info("风险资产数据异常");
            throw new RuntimeException(e);
        }
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.add(QueryCondition_ES.eq("alarmRiskLevel",5));
//        querys.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        String timeFormat = "yyyy-MM-dd";
        Integer interval =  24;
        if ("hour".equals(timeType)) {
            timeFormat = "yyyy-MM-dd HH";
            timeInterval = DateHistogramInterval.HOUR;
            interval = 1;
        } else if ("month".equals(timeType)) {
            timeFormat = "yyyy-MM";
            timeInterval = DateHistogramInterval.MONTH;
            interval = 30 * 24;
        } else if ("year".equals(timeType)) {
            timeFormat = "yyyy";
            timeInterval = DateHistogramInterval.YEAR;
            interval = 365 * 24 ;
        }
        AlarmDealUtil.getDataTimeUtil(timeType, timeFormat, timeInterval);
        SearchField childField = new SearchField(getBaseField() + "principalIp", FieldType.String, 0, 10000, null);
        SearchField searchField = new SearchField(getBaseField() + "eventCreattime", FieldType.Date, timeFormat, timeInterval, childField, 0, 1000);
        List<Map<String, Object>> queryStatistics = alarmEventManagementForEsService.queryStatistics(querys, searchField);
        List<Map<String, Object>> queryStatisticsList = SocUtil.getTimeFullMapForField2(query.getBeginTime(), query.getEndTime(), "eventCreattime", interval, timeFormat, queryStatistics);
        List<Map<String,Integer >> iplist=new ArrayList<>();
        for (Map<String, Object> m:queryStatisticsList){
            Map<String,Integer > map=new HashMap<>();
            List<String> strings=new ArrayList<>();
            String eventCreattime = m.get("eventCreattime").toString();
            List<Map<String,Object>> principalIp = (List<Map<String, Object>>) m.get("principalIp");
            if (principalIp!=null&&principalIp.size()>0){
                for (Map<String,Object> objectMap:principalIp){
                    Object principalIp1 = objectMap.get("principalIp");
                    if (principalIp1!=null){
                        strings.add(principalIp1.toString());
                    }
                }
            }
            List<String> strings1 = ipRisklist.get(eventCreattime);
            if (strings1!=null&&strings1.size()>0){
                strings.addAll(strings1);
            }
            //计算资产数量
            Integer i=culAssetNum(strings);
            map.put(eventCreattime,i);
            iplist.add(map);
        }
        List<NameValue> result = new ArrayList<>();
        iplist.forEach(map -> {
            Set<Map.Entry<String, Integer>> entries = map.entrySet();
            for (Map.Entry<String, Integer> mm:entries){
                result.add(new NameValue(String.valueOf(mm.getValue()), mm.getKey()));
            }
        });
        return result;
    }

    @Override
    public Map<String, Integer> dayAlarmWorthAssetCount() {
        Map<String,Integer> map=new HashMap<>();
        map.put("worth",0);
        map.put("alarm",0);
        //重要资产数量
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("worth",5));
        long count = assetService.count(queryConditions);
        map.put("worth", Math.toIntExact(count));
        //今日告警事件资产数
        Date date = new Date();
        String format = DateUtil.format(date, DateUtil.Year_Mouth_Day)+" 00:00:00";
        String format1 = DateUtil.format(date, DateUtil.Year_Mouth_Day)+" 23:59:59";
        List<QueryCondition_ES> dayDisposedConditions = new ArrayList<>();
        dayDisposedConditions.add(QueryCondition_ES.ge("eventCreattime", format));
        dayDisposedConditions.add(QueryCondition_ES.le("eventCreattime", format1));
        Map<String, Long> principalIp = alarmEventManagementForEsService.getCountGroupByField(alarmEventManagementForEsService.getIndexName(), "principalIp", dayDisposedConditions);
        map.put("alarm",principalIp.size());
        return map;
    }

    @Override
    public Map<String, Long> getIpGroup() {
        List<QueryCondition_ES> dayDisposedConditions = new ArrayList<>();
        dayDisposedConditions.add(QueryCondition_ES.notNull("principalIp"));
        dayDisposedConditions.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        Map<String, Long> principalIp = alarmEventManagementForEsService.getCountGroupNumByFieldSize(alarmEventManagementForEsService.getIndexName(), "principalIp", dayDisposedConditions,1000);
        return principalIp;
    }

    @Override
    public List<EventTaVo> getEventObject(String eventId) {
        List<EventTaVo> eventTaVos=new ArrayList<>();
        String[] split = eventId.split("\\|");
        if (split.length==2){
            String guid = split[0];
            String topicName = split[1];
            String indexName=topicName+"-*";
            //获取对象数据
            List<QueryCondition_ES> queryConditionEs=new ArrayList<>();
            queryConditionEs.add(QueryCondition_ES.eq("_id",guid));
            List<Map<String, Object>> all = elasticSearchMapManage.findAll(indexName, queryConditionEs);
            if (all.size()>0){
                System.out.println(all.get(0));
                //组装数据
                assembleData(all.get(0),eventTaVos,topicName);
            }
        }
        return eventTaVos;
    }
    @Autowired
    private EventTabelService eventTabelService;
    @Autowired
    private EventColumService eventColumService;
    private void assembleData(Map<String, Object> doc, List<EventTaVo> eventTaVos,String topicName) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("indexName",topicName));
        List<EventTable> all = eventTabelService.findAll(queryConditions);
        if (all.size()>0){
            List<QueryCondition> queryConditions1=new ArrayList<>();
            queryConditions1.add(QueryCondition.eq("eventTableId",all.get(0).getId()));
            List<EventColumn> all1 = eventColumService.findAll(queryConditions1);
            if (all1.size()>0){
                for (EventColumn eventColumn:all1){
                    Object o = doc.get(eventColumn.getName());
                    if (o!=null){
                        EventTaVo eventTaVo=new EventTaVo();
                        eventTaVo.setColumnName(eventColumn.getLabel());
                        eventTaVo.setFieldName(eventColumn.getName());
                        eventTaVo.setFieldValue(o.toString());
                        eventTaVos.add(eventTaVo);
                    }
                }
            }
        }
    }


    private Integer culAssetNum(List<String> strings) {
        if (strings.size()==0){
            return 0;
        }
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("ip",strings));
        long count = assetService.count(queryConditions);
        return Math.toIntExact(count);
    }

    /**
     * 将事件标记为已读
     *
     * @param eventId 事件ID
     * @return
     */
    @Override
    public AlarmEventAttribute setAlarmEventMarkRead(String eventId) {
        AlarmEventAttribute doc = alarmEventManagementForEsService.getDocByEventId(eventId);
        if (doc == null) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "未找到操作的数据");
        }
        doc.setIsRead(true);
        alarmEventManagementForEsService.saveAlarmEventData(doc);
        return doc;
    }

    /**
     * 将事件标记为已读
     *
     * @param vo
     * @return
     */
    @Override
    public List<AlarmEventAttribute> setAlarmEventMarkRead(RiskRuleIdVO vo) {
        List<AlarmEventAttribute> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(vo.getId())) {
            AlarmEventAttribute doc = alarmEventManagementForEsService.getDocByEventId(vo.getId());
            doc.setIsRead(true);
            list.add(doc);
        }

        if (CollectionUtils.isNotEmpty(vo.getIds())) {
            List<QueryCondition_ES> reqs = new ArrayList<>();
            reqs.add(QueryCondition_ES.in("eventId", vo.getIds()));
            List<AlarmEventAttribute> docs = alarmEventManagementForEsService.findAll(reqs);
            docs.stream().forEach(item -> {
                item.setIsRead(true);
            });
            list.addAll(docs);
        }
        if (CollectionUtils.isNotEmpty(list)) {
            alarmEventManagementForEsService.saveAlarmEventDatas(list);
        }
        return list;
    }

    /**
     * 查询单条事件信息
     *
     * @param eventId 事件ID
     * @return
     */
    @Override
    public AlarmEventAttribute getAlarmEvent(String eventId) {
        AlarmEventAttribute doc = alarmEventManagementForEsService.getDocByEventId(eventId);
        if (doc == null) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "未找到操作的数据");
        }
        return doc;
    }

    /**
     * 获得追溯原始日志
     *
     * @param eventId 事件ID
     * @return
     */
    @Override
    public List<Map<String, Object>> getAlarmEventLogs(String eventId) {
        List<Map<String, Object>> results = new ArrayList<>();
        AlarmEventAttribute doc = alarmEventManagementForEsService.getDocByEventId(eventId);
        List<LogIdVO> logs = doc.getLogs();
        if (CollectionUtils.isNotEmpty(logs)) {
            logs.stream().forEach(item -> {
                String indexName = item.getEventTableName();
                String index = getDataSource(indexName);
                List<String> ids = item.getIds();
                if (index != null) {
                    for (String id : ids) {
                        List<QueryCondition_ES> conditions = new ArrayList<>();
                        conditions.add(QueryCondition_ES.eq("guid", id));
                        List<Map<String, Object>> list = elasticSearchMapManage.findAll(index, conditions);
                        results.addAll(list);
                    }
                } else {
                    if (CollectionUtils.isNotEmpty(ids)) {
                        List<String> idList = new ArrayList<>();
                        ids.forEach(id -> {
                            idList.add("'" + id + "'");
                        });
                        String idStr = String.join(",", idList);
                        String sql = "select * from " + getDataSourceTableName(indexName) + " where guid in ({0});";
                        sql = sql.replace("{0}", idStr);
                        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
                        results.addAll(result);
                    }
                }
            });
        }
        return results;
    }


    public String getDataSource(String indexName) {
        Map<String, Object> conditionParam = new HashMap<>();
        conditionParam.put("topicAlias", indexName);
        List<DataSource> dataSources = null;
        try {
            ResultObjVO<List<DataSource>> dataSourceVo = dataSourceFegin.querySource(conditionParam);
            dataSources = dataSourceVo.getList();
        } catch (Exception ex) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "data source fegin 接口调用失败！");
        }
        if (CollectionUtils.isNotEmpty(dataSources)) {
            DataSource dataSource = dataSources.get(0);
            if (dataSource.getType() == 1) {
                return dataSource.getName();
            }
            return null;
        }
        return null;
    }

    public String getDataSourceTableName(String indexName) {
        Map<String, Object> conditionParam = new HashMap<>();
        conditionParam.put("name", indexName);
        ResultObjVO<List<DataSource>> dataSourceVo = dataSourceFegin.querySource(conditionParam);
        List<DataSource> dataSources = dataSourceVo.getList();
        if (CollectionUtils.isNotEmpty(dataSources)) {
            DataSource dataSource = dataSources.get(0);
            if (dataSource.getType() == 2) {
                return dataSource.getTopicAlias();
            }
        }
        return null;
    }

    /**
     * 获得追溯原始日志
     *
     * @param eventId 事件ID
     * @param indexid 索引ID
     * @return
     */
    @Override
    public List<Map<String, Object>> getAlarmEventLogs(String eventId, String indexid) {
        List<Map<String, Object>> results = new ArrayList<>();
        AlarmEventAttribute doc = alarmEventManagementForEsService.getDocByEventId(eventId);
        List<LogIdVO> logs = doc.getLogs();
        if (logs != null && !logs.isEmpty()) {
            String indexId = indexid.replace("*", "");
            List<LogIdVO> logIdVoS = logs.stream().filter(item -> item.getIndexName().startsWith(indexId)).collect(Collectors.toList());
            logIdVoS.stream().forEach(item -> {
                for (String id : item.getIds()) {
                    Map<String, Object> res = alarmEventManagementForEsService.getDoc(item.getIndexName(), id);
                    results.add(res);
                }
            });
        }
        return results;
    }


    /**
     * 生成导出文件
     *
     * @param query
     * @param request
     * @return
     */
    @Override
    public String createReportFile(EventDetailQueryVO query, HttpServletRequest request) {
        String msg = "由于数据量过大,执行结果将以消息的形式推送给您，请等待！";
        String fileName = "事件处置详情导出信息" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
        // 构造执行时间达到阈值时的执行方法
        Function<String, String> timeOutRun = token -> {
            return msg;
        };
        // 构造消息系统超时之后，正常业务代码执行完成之后回调的方法（消息推送）
        BiConsumer<String, JobResult<String>> callBackFun = (token, jobResult) -> {
            // 准备上传文件
            com.vrv.vap.common.model.User currentUser = SessionUtil.getCurrentUser();
            try {
                if (jobResult.getCode() == 0) {
                    try {
                        String fileGuid = updateFileReturnFileGuid(fileName);
                        String url = request.getServerPort() + "server-sys/fileup/download/" + fileGuid;
                        pushService.pushMessageToUser("文件下载提示", "文件生成成功，下载连接：" + url, url,
                                Integer.toString(currentUser.getId()));
                    } catch (Exception e) {
                        pushService.pushMessageToUser("文件下载提示", "导出文件生成成功，但文件上传失败，请联系管理员。错误提示：" + e.getMessage(), null,
                                Integer.toString(currentUser.getId()));
                        logger.error("updateFileReturnFileGuid:", e);
                        return;
                    }
                } else {
                    pushService.pushMessageToUser("文件下载提示", "文件生成失败:" + jobResult.getError().getMessage(), null,
                            Integer.toString(currentUser.getId()));
                }
            } catch (Exception e) {
                logger.error("pushService:", e);
            }
        };
        // 构造导出方法
        LambdaExceptionUtil.Consumer_WithExceptions<String, Exception> exportFun = token -> {
            alarmEventManagementForEsService.createReportFile(query, fileName);
        };
        // 执行调度
        try {
            JobResult<String> exportBigExcel = ExcelExport.exportBigExcel(5, exportFun, callBackFun, timeOutRun);
            if (exportBigExcel.getCode() == 0) {
                if (msg.equals(exportBigExcel.getResult())) {
                    throw new AlarmDealException(200, exportBigExcel.getResult());
                } else {
                    return fileName;
                }
            } else {
                throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), exportBigExcel.getError().getMessage());
            }
        } catch (Exception e) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    /**
     * 导出文件
     *
     * @param fileName
     * @param request
     * @param response
     */
    @Override
    public void downloadReportFile(String fileName, HttpServletRequest request, HttpServletResponse response) {
        FileUtil.downLoadFile(fileName + ".xls", fileConfiguration.getFilePath(), response);
    }


    /**
     * 处理文件路径
     *
     * @param token
     * @return java.lang.String
     */
    public String getFilePath(String token) {
        String fileName = token + ".xls";// 文件名称
        String filePath = Paths.get(fileConfiguration.getFilePath(), fileName).toString();
        return filePath;
    }

    /**
     * 上传文件
     *
     * @param token
     * @return java.lang.String
     */
    private String updateFileReturnFileGuid(String token) {
        User currentUser = SessionUtil.getCurrentUser();
        String filePath = getFilePath(token);
        File file = new File(filePath);
        if (file.exists()) {
            Map<String, Object> map = new HashMap<>();
            map.put("override", "0");
            FileInputStream input = null;
            try {
                input = new FileInputStream(file);
                MultipartFile multipartFile = new CommonsMultipartFile(AlarmDealUtil.createFileItem(file, "file"));

                ResultModel uploadFile = fileService.uploadFile(multipartFile, "alarmdeal",
                        currentUser.getAccount().toString(), Integer.toString(currentUser.getId()), map);
                logger.debug("上传文件返回结果：" + new Gson().toJson(uploadFile));
                if ("200".equals(uploadFile.getCode())) {
                    FileInfo data = uploadFile.getData();
                    return data.getGuid();
                } else {
                    logger.error("上传文件发生异常:" + uploadFile.getMsg());
                    throw new AssertException("上传文件发生异常");
                }
            } catch (Exception e) {
                logger.error("上传文件发生异常", e);
                throw new AssertException("上传文件发生异常");
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception e) {
                        logger.error("input.close() Exception:", e);
                    }
                }
            }
        } else {
            throw new AssertException("文件不存在，操作失败");
        }
    }

    public String getBaseField() {
        return "";
    }
    /**
     * 获得告警异常行为事件列表
     *
     * @param query
     * @return
     */
    @Override
    public PageRes_ES<AlarmEventAttributeVO> getAlarmDealAbnormalPager(EventDetailQueryVO query) {
        logger.info("获得告警事件列表 getAlarmDealPager start");
        // 定义返回
        PageRes_ES<AlarmEventAttributeVO> result = new PageRes_ES<>();
        // 判断登录
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            result.setCode("-1");
            result.setMessage("用户未登录");
            result.setTotal(0L);
            return result;
        }
//         User currentUser = new User();
//         currentUser.setId(33);
//         currentUser.setRoleCode(Arrays.asList("admin"));

        // 查询ES
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryAbnormalResult(query, query, true);

        // 数据转换
        List<AlarmEventAttribute> sourceList = pageQueryResult.getList();
        List<AlarmEventAttributeVO> list = mapper.mapList(sourceList, AlarmEventAttributeVO.class);

        // 查询事件分类
        List<EventCategory> findAll = getEventCategories();
        List<String> roleCodes = currentUser.getRoleCode();

        String idStr = Integer.toString(currentUser.getId());
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                for (EventCategory eventCategory : findAll) {
                    if (item.getEventCode().startsWith(eventCategory.getCodeLevel())) {
                        item.setEventTypeName(eventCategory.getTitle());
                        break;
                    }
                }
                // 处理告警数据分类信息
                getAlarmAuthData(roleCodes, idStr, item);
                // 处理告警数据告警规则
                getRiskEventRuleData(item);
            });
        }
        result.setCode(pageQueryResult.getCode());
        result.setMessage(pageQueryResult.getMessage());
        result.setTotal(pageQueryResult.getTotal());
        result.setList(list);
        return result;
    }
    @Override
    public PageRes_ES<AppAlarmEventAttributeVO> getAlarmDealAppAbnormalPager(EventDetailQueryVO query) {
        logger.info("获得告警事件列表 getAlarmDealPager start");
        // 定义返回
        PageRes_ES<AppAlarmEventAttributeVO> result = new PageRes_ES<>();
        // 判断登录
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            result.setCode("-1");
            result.setMessage("用户未登录");
            result.setTotal(0L);
            return result;
        }
//        User currentUser = new User();
//        currentUser.setId(33);
//        currentUser.setRoleCode(Arrays.asList("admin"));
        List<Integer> integers=new ArrayList<>();
        integers.add(2);
        // 查询ES
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryAppAbnormalResult(query, query, true,integers);

        // 数据转换
        List<AlarmEventAttribute> sourceList = pageQueryResult.getList();
        List<AppAlarmEventAttributeVO> list=new ArrayList<>();
        if (sourceList.size()>0){
            for (AlarmEventAttribute alarmEventAttribute:sourceList) {
                AppAlarmEventAttributeVO appAlarmEventAttributeVO=new AppAlarmEventAttributeVO();
                appAlarmEventAttributeVO.setEventId(alarmEventAttribute.getEventId());
                appAlarmEventAttributeVO.setEventCreattime(alarmEventAttribute.getEventCreattime());
                appAlarmEventAttributeVO.setAppName("");
                appAlarmEventAttributeVO.setIp(StringUtils.isNotBlank(alarmEventAttribute.getPrincipalIp())?alarmEventAttribute.getPrincipalIp():alarmEventAttribute.getDstIps());
                appAlarmEventAttributeVO.setEventDetails(alarmEventAttribute.getEventDetails());
                appAlarmEventAttributeVO.setEventTypeName(alarmEventAttribute.getRuleName());
                appAlarmEventAttributeVO.setEventCode(alarmEventAttribute.getEventCode());
                appAlarmEventAttributeVO.setEventName(alarmEventAttribute.getEventName());
                list.add(appAlarmEventAttributeVO);
            }
            if (CollectionUtils.isNotEmpty(sourceList)) {
                list.forEach(item -> {
                    //查询应用
                    List<QueryCondition> cons = new ArrayList<>();
                    cons.add(QueryCondition.eq("ip", item.getIp()));
                    List<Asset> findAll = assetService.findAll(cons);
                    if (findAll!=null&&findAll.size()>0){
                        Asset asset = findAll.get(0);
                        List<QueryCondition> conditions = new ArrayList<>();
                        conditions.add(QueryCondition.like("serviceId","%"+asset.getGuid()+"%"));
                        List<AppSysManager> all = appSysManagerService.findAll(conditions);
                        if (all.size()>0){
                            item.setAppName(all.get(0).getAppName());
                        }

                    }
//                    for (EventCategory eventCategory : findAll) {
//                        if (item.getEventCode().startsWith(eventCategory.getCodeLevel())) {
//                            item.setEventTypeName(eventCategory.getTitle());
//                            break;
//                        }
//                    }
                });
            }

        }

        result.setCode(pageQueryResult.getCode());
        result.setMessage(pageQueryResult.getMessage());
        result.setTotal(pageQueryResult.getTotal());
        result.setList(list);
        return result;
    }
    @Override
    public PageRes_ES<AppAlarmEventAttributeVO> getAlarmDealUserAbnormalPager(EventDetailQueryVO query) {
        logger.info("获得告警事件列表 getAlarmDealPager start");
        // 定义返回
        PageRes_ES<AppAlarmEventAttributeVO> result = new PageRes_ES<>();
        // 判断登录
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            result.setCode("-1");
            result.setMessage("用户未登录");
            result.setTotal(0L);
            return result;
        }
//        User currentUser = new User();
//        currentUser.setId(33);
//        currentUser.setRoleCode(Arrays.asList("admin"));
        List<Integer> integers=new ArrayList<>();
        integers.add(3);
        // 查询ES
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryAppAbnormalResult(query, query, true,integers);

        // 数据转换
        List<AlarmEventAttribute> sourceList = pageQueryResult.getList();
        List<AppAlarmEventAttributeVO> list=new ArrayList<>();
        if (sourceList.size()>0){
            for (AlarmEventAttribute alarmEventAttribute:sourceList) {
                AppAlarmEventAttributeVO appAlarmEventAttributeVO=new AppAlarmEventAttributeVO();
                appAlarmEventAttributeVO.setEventId(alarmEventAttribute.getEventId());
                appAlarmEventAttributeVO.setEventCreattime(alarmEventAttribute.getEventCreattime());
                appAlarmEventAttributeVO.setAppName("");
                appAlarmEventAttributeVO.setIp(StringUtils.isNotBlank(alarmEventAttribute.getPrincipalIp())?alarmEventAttribute.getPrincipalIp():alarmEventAttribute.getDstIps());
                appAlarmEventAttributeVO.setEventDetails(alarmEventAttribute.getEventDetails());
                appAlarmEventAttributeVO.setEventTypeName(alarmEventAttribute.getRuleName());
                appAlarmEventAttributeVO.setEventCode(alarmEventAttribute.getEventCode());
                appAlarmEventAttributeVO.setEventName(alarmEventAttribute.getEventName());
                if (alarmEventAttribute.getStaffInfos()!=null&&alarmEventAttribute.getStaffInfos().size()>0){
                    StaffInfo staffInfo = alarmEventAttribute.getStaffInfos().get(0);
                    if (staffInfo!=null&&StringUtils.isNotBlank(staffInfo.getStaffName())){
                        appAlarmEventAttributeVO.setStaffName(staffInfo.getStaffName());
                    }
                }
                list.add(appAlarmEventAttributeVO);
            }

        }

        result.setCode(pageQueryResult.getCode());
        result.setMessage(pageQueryResult.getMessage());
        result.setTotal(pageQueryResult.getTotal());
        result.setList(list);
        return result;
    }
    @Override
    public PageRes_ES<AppAlarmEventAttributeVO> getAlarmEventPager(EventDetailQueryVO query) {
        logger.info("获得告警事件列表 getAlarmDealUserAbnormalPager  start");
        // 定义返回
        PageRes_ES<AppAlarmEventAttributeVO> result = new PageRes_ES<>();
        // 判断登录
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            result.setCode("-1");
            result.setMessage("用户未登录");
            result.setTotal(0L);
            return result;
        }
//        User currentUser = new User();
//        currentUser.setId(33);
//        currentUser.setRoleCode(Arrays.asList("admin"));
        List<Integer> integers=new ArrayList<>();
        // 查询ES
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryAppAbnormalResult(query, query, true,integers);

        // 数据转换
        List<AlarmEventAttribute> sourceList = pageQueryResult.getList();
        List<AppAlarmEventAttributeVO> list=new ArrayList<>();
        if (sourceList.size()>0){
            for (AlarmEventAttribute alarmEventAttribute:sourceList) {
                AppAlarmEventAttributeVO appAlarmEventAttributeVO=new AppAlarmEventAttributeVO();
                appAlarmEventAttributeVO.setEventId(alarmEventAttribute.getEventId());
                appAlarmEventAttributeVO.setEventCreattime(alarmEventAttribute.getEventCreattime());
                appAlarmEventAttributeVO.setAppName("");
                appAlarmEventAttributeVO.setIp(StringUtils.isNotBlank(alarmEventAttribute.getPrincipalIp())?alarmEventAttribute.getPrincipalIp():alarmEventAttribute.getDstIps());
                appAlarmEventAttributeVO.setEventDetails(alarmEventAttribute.getEventDetails());
                appAlarmEventAttributeVO.setEventTypeName(alarmEventAttribute.getRuleName());
                appAlarmEventAttributeVO.setEventCode(alarmEventAttribute.getEventCode());
                appAlarmEventAttributeVO.setEventName(alarmEventAttribute.getEventName());
                if (alarmEventAttribute.getStaffInfos()!=null&&alarmEventAttribute.getStaffInfos().size()>0){
                    StaffInfo staffInfo = alarmEventAttribute.getStaffInfos().get(0);
                    if (staffInfo!=null&&StringUtils.isNotBlank(staffInfo.getStaffName())){
                        appAlarmEventAttributeVO.setStaffName(staffInfo.getStaffName());
                    }
                }
                list.add(appAlarmEventAttributeVO);
            }

        }

        result.setCode(pageQueryResult.getCode());
        result.setMessage(pageQueryResult.getMessage());
        result.setTotal(pageQueryResult.getTotal());
        result.setList(list);
        return result;
    }

    @Override
    public Map<String, Long> getAppEventCount(EventDetailQueryVO query) {
        List<QueryCondition_ES> conditions = alarmEventManagementForEsService.getQuerys(query);
        conditions.add(QueryCondition_ES.eq("eventType", 2));
        Map<String, Long> counts = new HashMap<>();
        // 全部应用事件数量
        counts.computeIfAbsent("appAlarmCount", k -> alarmEventManagementForEsService.count(conditions));
        // 已处置事件总数
        counts.computeIfAbsent("dealAppAlarmCount", k -> countByStatus(conditions, AlarmDealStateEnum.PROCESSED.getCode()));
        // 未处置事件总数 = 未处置 + 处置中
        counts.computeIfAbsent("noDealAppAlarmCount", k ->
                countByStatus(conditions, AlarmDealStateEnum.PROCESSING.getCode()) + countByStatus(conditions, AlarmDealStateEnum.UNTREATED.getCode()
                ));
        return counts;
    }

    /**
     * 根据报警状态查询报警数量
     * @param query 基础查询条件
     * @param status 报警状态
     * @return 给定状态的报警数量
     */
    private long countByStatus(List<QueryCondition_ES> query, int status) {
        List<QueryCondition_ES> condition = new ArrayList<>(query);
        condition.add(QueryCondition_ES.eq("alarmDealState", status));
        return alarmEventManagementForEsService.count(condition);
    }



    @Override
    public Long getEventAbnormalCount(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(2,3,4,5,6)));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        return alarmEventManagementForEsService.count(querys);
    }
    @Override
    public Long getEventUserAbnormalCount(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(3)));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        return alarmEventManagementForEsService.count(querys);
    }
    @Override
    public Integer getEventAbnormalUserCount(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(2,3,4,5,6)));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        Map<String, Long> staffInfos = alarmEventManagementForEsService.getCountGroupNumByField(alarmEventManagementForEsService.getIndexName(), "relatedStaffInfos.staffNo", querys);
        if (staffInfos!=null){
            return staffInfos.size();
        }
        return 0;
    }
    @Override
    public List<NameValue> getEventAbnormalTypeUserCount(EventDetailQueryVO query) {
        List<NameValue>  list =new ArrayList<>();
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(2,3,4,5,6)));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        Map<String, Long> staffInfos = alarmEventManagementForEsService.getCountGroupNumByField(alarmEventManagementForEsService.getIndexName(), "eventType", querys);
        if (staffInfos!=null){
            for (Map.Entry<String, Long> m:staffInfos.entrySet()) {
                List<QueryCondition_ES> querysUser =  alarmEventManagementForEsService.getQuerys(query);
                querysUser.addAll(alarmEventManagementForEsService.getDataPermissions());
                querysUser.add(QueryCondition_ES.eq("eventType", m.getKey()));
                Map<String, Long> staffInfoUser = alarmEventManagementForEsService.getCountGroupNumByField(alarmEventManagementForEsService.getIndexName(), "relatedStaffInfos.staffNo", querysUser);
                if (staffInfoUser!=null){
                    m.setValue(Long.valueOf(staffInfoUser.size()));
                }
            }
            for (Map.Entry<String, Long> m:staffInfos.entrySet()) {
                Map<String, Long> staffInfo=new HashMap<>();
                String desc = EvenTypeEnum.getDesc(m.getKey());
                staffInfo.put(desc,m.getValue());
                NameValue nameValue = new NameValue(m.getValue().toString(),desc);
                list.add(nameValue);
            }
            return list;
        }
        return list;
    }
    @Override
    public List<NameValue> getEventAbnormalTypeCount(EventDetailQueryVO query) {
        List<NameValue>  list =new ArrayList<>();
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(2,3,4,5,6)));
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        Map<String, Long> staffInfos = alarmEventManagementForEsService.getCountGroupNumByField(alarmEventManagementForEsService.getIndexName(), "eventType", querys);
        if (staffInfos!=null){
            for (Map.Entry<String, Long> m:staffInfos.entrySet()) {
                String desc = EvenTypeEnum.getDesc(m.getKey());
                NameValue nameValue = new NameValue(m.getValue().toString(),desc);
                list.add(nameValue);
            }
            return list;
        }
        return list;
    }
    @Override
    public List<NameValue> getEventAbnormalAreaCount(EventDetailQueryVO query) {
        List<NameValue>  list =new ArrayList<>();
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        Map<String, Long> staffInfos = alarmEventManagementForEsService.getCountGroupNumByField(alarmEventManagementForEsService.getIndexName(), "deviceInfos.deviceSecurityDomain", querys);
        if (staffInfos!=null){
            for (Map.Entry<String, Long> m:staffInfos.entrySet()) {
                NameValue nameValue = new NameValue(m.getValue().toString(),m.getKey());
                list.add(nameValue);
            }
            return list;
        }
        return list;
    }
    @Override
    public List<NameValue> getEventAbnormalOrgCount(EventDetailQueryVO query) {
        List<NameValue>  list =new ArrayList<>();
        List<QueryCondition_ES> querys =  alarmEventManagementForEsService.getQuerys(query);
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        Map<String, Long> staffInfos = alarmEventManagementForEsService.getCountGroupNumByField(alarmEventManagementForEsService.getIndexName(), "unitList.unitDepartName", querys);
        if (staffInfos!=null){
            for (Map.Entry<String, Long> m:staffInfos.entrySet()) {
                NameValue nameValue = new NameValue(m.getValue().toString(),m.getKey());
                list.add(nameValue);
            }
            return list;
        }
        return list;
    }
    @Override
    public List<NameValue> getAlarmTypeTrend(EventDetailQueryVO query, String loginException) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.add(QueryCondition_ES.in("ruleId", Arrays.asList(loginException.split(","))));
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        String timeFormat = "yyyy-MM-dd";
        long interval = (long) 24 * 60 * 60 * 1000;
        AlarmDealUtil.getDataTimeUtil("day", timeFormat, timeInterval);
        SearchField searchField = new SearchField(getBaseField() + "eventCreattime", FieldType.Date, timeFormat, timeInterval, null, 0, 50);
        List<NameValue> result = new ArrayList<>();
        List<Map<String, Object>> queryStatistics = alarmEventManagementForEsService.queryStatistics(querys, searchField);
        List<Map<String, Object>> queryStatisticsList = SocUtil.getTimeFullMapForField(query.getBeginTime(), query.getEndTime(), "eventCreattime", interval, timeFormat, queryStatistics);
        queryStatisticsList.forEach(map -> {
            result.add(new NameValue(map.get("doc_count").toString(), map.get(getBaseField() + "eventCreattime").toString()));
        });
        return result;
    }

    @Override
    public List<NameValue> getAlarmUserAbnormalTrend(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getAlarmDealQuerys(query);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(3)));
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        String timeFormat = "yyyy-MM-dd";
        long interval = (long) 24 * 60 * 60 * 1000;
        AlarmDealUtil.getDataTimeUtil("day", timeFormat, timeInterval);
        SearchField searchField = new SearchField(getBaseField() + "eventCreattime", FieldType.Date, timeFormat, timeInterval, null, 0, 50);
        List<NameValue> result = new ArrayList<>();
        List<Map<String, Object>> queryStatistics = alarmEventManagementForEsService.queryStatistics(querys, searchField);
        List<Map<String, Object>> queryStatisticsList = SocUtil.getTimeFullMapForField(query.getBeginTime(), query.getEndTime(), "eventCreattime", interval, timeFormat, queryStatistics);
        queryStatisticsList.forEach(map -> {
            result.add(new NameValue(map.get("doc_count").toString(), map.get(getBaseField() + "eventCreattime").toString()));
        });
        return result;
    }
    @Override
    public Map<String, Long> getAllEventCount() {
        Map<String, Long> map=new HashMap<>();
        List<QueryCondition_ES> querys =  new ArrayList<>();
        //全部应用事件数量
        long count = alarmEventManagementForEsService.count(querys);
        map.put("appAlarmCount",count);
        // 已处置事件总数
        List<QueryCondition_ES> disposedConditions = new ArrayList<>();
        disposedConditions.addAll(querys);
        disposedConditions.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        long disposedCount = alarmEventManagementForEsService.count(disposedConditions);
        map.put("dealAppAlarmCount",disposedCount);

        // 未处置事件总数
        List<QueryCondition_ES> notDisposedConditions = new ArrayList<>();
        notDisposedConditions.addAll(querys);
        notDisposedConditions.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.UNTREATED.getCode()));
        long notDisposedCount = alarmEventManagementForEsService.count(notDisposedConditions);
        map.put("noDealAppAlarmCount",notDisposedCount);
        // 处置中事件总数
        List<QueryCondition_ES> doDisposedConditions = new ArrayList<>();
        doDisposedConditions.addAll(querys);
        doDisposedConditions.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.PROCESSING.getCode()));
        long doDisposedConditionsCount= alarmEventManagementForEsService.count(doDisposedConditions);
        map.put("doDisposedConditionsCount",doDisposedConditionsCount);
        // 今日新增
        Date date = new Date();
        String format = DateUtil.format(date, DateUtil.Year_Mouth_Day)+" 00:00:00";
        String format1 = DateUtil.format(date, DateUtil.Year_Mouth_Day)+" 23:59:59";
        List<QueryCondition_ES> dayDisposedConditions = new ArrayList<>();
        dayDisposedConditions.addAll(querys);
        dayDisposedConditions.add(QueryCondition_ES.ge("eventCreattime", format));
        dayDisposedConditions.add(QueryCondition_ES.le("eventCreattime", format1));
        long dayCount = alarmEventManagementForEsService.count(dayDisposedConditions);
        map.put("dayCount",dayCount);
        return map;
    }
}
