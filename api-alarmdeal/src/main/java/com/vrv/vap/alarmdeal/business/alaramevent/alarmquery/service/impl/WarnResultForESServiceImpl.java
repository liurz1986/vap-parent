package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.json.JsonSanitizer;
import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.vap.alarmModel.model.WarnAnalysisVO;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AlarmAttackPath;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ExpertVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.EventTable;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmDealServer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AnalysisStatusVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealInfoVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.AlarmStatusConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.enums.StrategyEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmreport.service.BaseSysinfoService;
import com.vrv.vap.alarmdeal.business.analysis.enums.TypeClass;
import com.vrv.vap.alarmdeal.business.analysis.enums.WeightEnum;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.alarmdeal.business.analysis.server.ConfAttackMappingService;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.InterruptKeyService;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.ResponseStrategyService;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.SelectRelateStrategy;
import com.vrv.vap.alarmdeal.business.analysis.vo.AttackNodeVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.AttackVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetDetailVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.business.flow.core.model.WorkDataVOByName;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.frameworks.config.EventConfig;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.KnowledgeFeign;
import com.vrv.vap.alarmdeal.frameworks.util.*;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchMapManage;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.IndexsInfoVO;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.exportAndImport.excel.ExcelUtils;
import com.vrv.vap.jpa.common.*;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年01月20日 10:21
 */
@Service
public class WarnResultForESServiceImpl extends ElasticSearchRestClientService<WarnResultLogTmpVO> implements WarnResultForESService  {
    public static final String WARN_RESULT_TMP = "warnresulttmp";
    private static final String PROCESS_NAME = "涓撳鍒嗘瀽娴佺▼";
    private static final String RULE_ID = "04f93dfc0f9d413d9c7cce2bf1d5786b"; // 瑙勫垯Id
    private static final String MODEL_ID = "04f93dfc0f9d413d9c7cce2bf1d5786a"; // 妯″瀷Id
    private static final String THREAT_ID = ""; // TODO 濞佽儊Id
    private static final String HONEY_ID = "14f93dfc0f9d413d9c7cce2bf1d5786e"; // TODO 濞佽儊Id
    private static final String AUDIT_ID = "04f93dfc0f9d413d9c7cce2bf1d57890";
    private static final Integer ADMIN_ID = 31;
    private static Logger logger = LoggerFactory.getLogger(WarnResultForESServiceImpl.class);

    private static String IP_PATTERN = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";

    private static String EMAIL_PATTERN = "[A-z]+[A-z0-9_-]*\\@[A-z0-9]+\\.[A-z]+";

    private static String WEB_SIT_PATTERN = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";

    Gson gson=new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            .create();

    @Autowired
    private AlarmDealServer alarmDealServer;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private FlowService flowService;
    @Autowired
    private EventCategoryService eventCategoryService;
    @Autowired
    private KnowledgeFeign knowledgeFeign;
    @Autowired
    private RiskEventRuleService riskEventRuleService;
    @Autowired
    private InterruptKeyService interruptKeyService;
    @Autowired
    private BaseSysinfoService baseSysinfoService;
    @Autowired
    private ConfAttackMappingService confAttackMappingService;

    @Autowired
    private EventColumService eventColumService;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private ElasticSearchMapManage elasticSearchMapManage;

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private SelectRelateStrategy selectRelateStrategy;

    @Autowired
    private AssetService assetService;

    private ThreadLocal<ResponseStrategyService> responseStrategyService = new ThreadLocal<>();

    @Override
    public String getIndexName() {
        return WARN_RESULT_TMP;
    }


    @Override
    public void addList(List<WarnResultLogTmpVO> entities){
        Map<String, List<WarnResultLogTmpVO>> map=new HashMap<>();
        for(WarnResultLogTmpVO entity : entities)
        {
            String  indexName= IndexUtils.GetIndexName(getIndexName(), entity.getTriggerTime());
            if(map.containsKey(indexName))
            {
                List<WarnResultLogTmpVO> list = map.get(indexName);
                list.add(entity);
            }else
            {
                List<WarnResultLogTmpVO> list =new ArrayList<>();
                list.add(entity);
                map.put(indexName, list);
            }
        }

        map.forEach((key,value)->{
            super.addList(key, value);
        });
    }
    @Override
    public Serializable save(WarnResultLogTmpVO entity){
        String  indexName=IndexUtils.GetIndexName(getIndexName(), entity.getTriggerTime());

        return super.save(indexName, entity);
    }

    @Override
    public WarnResultLogTmpVO getDoc(Serializable id) {
        String[] indexListByBaseIndexName = super.getIndexListByBaseIndexName(getIndexName());
        for (String indexName : indexListByBaseIndexName) {
            WarnResultLogTmpVO doc = super.getDoc(indexName, null, id);
            if(doc!=null)
            {
                return doc;
            }
        }
        return null;
    }



    /**
     * 鍛婅姒傝鍒楄〃
     * @param analysisVO
     * @param request
     * @return
     */

    @Override
    public PageRes_ES<WarnResultLogTmpVO> getAlarmPager(AnalysisVO analysisVO, PageReq_ES request) {
        Integer start_ = analysisVO.getStart_();
        Integer count_ = analysisVO.getCount_();
        String order_ = analysisVO.getOrder_();
        String by_ = analysisVO.getBy_();
        request.setCount_(count_);
        request.setStart_(start_);
        request.setOrder_(order_);
        request.setBy_(by_);
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        //logger.info("鍒楄〃ES鏌ヨ鏉′欢:"+gson.toJson(conditions));
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                conditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                PageRes_ES<WarnResultLogTmpVO> findByPage = new PageRes_ES<WarnResultLogTmpVO>();
                findByPage.setList(new ArrayList<>());
                findByPage.setTotal(0L);
                findByPage.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
                findByPage.setMessage("鐢ㄦ埛瀹夊叏鍩熸煡璇㈠け璐ユ垨涓虹┖");
                return findByPage;

            }
        }

        PageRes_ES<WarnResultLogTmpVO> PageRes_ES = findByPage(request, conditions);
        return PageRes_ES;
    }

    @Override
    public String[] getRuleCodeArr() {
        List<String> list =new ArrayList<>();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("tableName","apt_event"));
        List<RiskEventRule> riskEventRules = riskEventRuleService.findAll(conditions);
        for (RiskEventRule riskEventRule : riskEventRules) {
            list.add(riskEventRule.getRuleCode());
        }
        String[] codeArray=list.toArray(new String[list.size()]);
        return codeArray;
    }



    /**
     * 鍛婅鏄庣粏鍒楄〃锛屾坊鍔犻澶栧睘鎬�
     * @param analysisVO
     * @param request
     * @return
     */
    @Override
    public PageRes_ES<WarnAnalysisVO> getAttackAlarmPager(AnalysisVO analysisVO, PageReq_ES request){
        PageRes_ES<WarnResultLogTmpVO> alarmPager = getAlarmPager(analysisVO, request);
        List<WarnResultLogTmpVO> list = alarmPager.getList();
        List<WarnAnalysisVO> mapList = mapper.mapList(list, WarnAnalysisVO.class);
        for (WarnAnalysisVO warnAnalysisVO : mapList) {
            setWarnUrl(warnAnalysisVO);
            setAttackPhase(warnAnalysisVO);
        }
        PageRes_ES<WarnAnalysisVO> pages = new PageRes_ES<>();
        pages.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        pages.setList(mapList);
        pages.setMessage("success");
        pages.setTotal(alarmPager.getTotal());
        return pages;
    }


    /**
     * 璁剧疆鍛婅Url
     * @param warnAnalysisVO
     */
    public void setWarnUrl(WarnAnalysisVO warnAnalysisVO) {
        String logsInfo = warnAnalysisVO.getLogsInfo();
        List<String> strList = new ArrayList<>();
        List<Map> list2 = new LinkedList<>();
        try {
            list2 = JsonMapper.fromJsonString2List(logsInfo, Map.class);
        } catch (IOException e) {
            logger.error("寮傚父闂", e);
        }
        for (Map map : list2) {
            if(map.containsKey("referer")&&map.get("referer")!=null) {
                String refererStr = map.get("referer").toString();
                strList.add(refererStr);
            }
        }
        if(strList.size()!=0) {
            String[] strs1=strList.toArray(new String[strList.size()]);
            String url = ArrayUtil.join(strs1, ",");
            warnAnalysisVO.setUrl(url);
        }
    }


    public void setAttackPhase(WarnAnalysisVO warnAnalysisVO) {
        String eventtypelevel = warnAnalysisVO.getEventtypelevel();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("eventCode", eventtypelevel));
        List<ConfAttackMapping> list = confAttackMappingService.findAll(conditions);
        if(list.size()>0){
            ConfAttackMapping confAttackMapping = list.get(0);
            String typeName = confAttackMapping.getTypeName();
            warnAnalysisVO.setAttackPhase(typeName);
        }
    }


    /**
     * 鑾峰緱30澶╁唴宸茬粡澶勭疆鐨勫憡璀︽暟鍜屾�诲憡璀︽暟
     * @param riskEventName
     * @return
     */

    @Override
    public List<List<Map<String, Object>>> getSafeAlarmTrendBy30Days(String riskEventName) {
        List<List<Map<String, Object>>> listMaps = new ArrayList<List<Map<String, Object>>>();
        String nowDay = DateUtil.format(new Date()); //鑾峰緱褰撳墠鏃堕棿
        String beforeMouthDay = DateUtil.addNMouth(-1); //鑾峰緱涓�涓湀浠ュ墠鐨勬椂闂�
        List<Map<String,Object>> allList = getAllAnalysisInfo(riskEventName, nowDay, beforeMouthDay);
        List<Map<String,Object>> dealList = getAlarmDealsInfo(riskEventName, nowDay, beforeMouthDay);
        listMaps.add(allList);
        listMaps.add(dealList);
        return listMaps;
    }

    /**
     * 鑾峰緱riskEventName鐨勬�诲憡璀︽暟
     * @param riskEventName
     * @param nowDay
     * @param previousDay
     * @return
     */
    private List<Map<String,Object>> getAllAnalysisInfo(String riskEventName,String nowDay,String previousDay){
        List<QueryCondition_ES> createAlarmConditions = new ArrayList<>();
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                createAlarmConditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        createAlarmConditions.add(QueryCondition_ES.eq("riskEventName", riskEventName));
        createAlarmConditions.add(QueryCondition_ES.between("triggerTime", previousDay, nowDay));
        String groupBy = "triggerTime";
        String timeFormat = "yyyy-MM-dd";
        long timeSpan = (long)24*3600*1000;
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeSpan, null,0,30);
        List<Map<String,Object>> list = queryStatistics(createAlarmConditions, searchField);
        nowDay = nowDay.substring(0, 10);
        previousDay = previousDay.substring(0, 10);
        List<String> dayLists = DateUtil.getDatesBetweenDays(previousDay, nowDay,DateUtil.Year_Mouth_Day);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");
        return list;
    }

    private List<Map<String,Object>> getAlarmDealsInfo(String riskEventName,String nowDay,String previousDay){
        User user = SessionUtil.getCurrentUser();
        int id = user.getId();
        List<Map<String,Object>> list = new ArrayList<>();
        if(id==ADMIN_ID) {
            list = alarmDealServer.getDealedAlarm(riskEventName, nowDay, previousDay);
        }else {
            String userId = String.valueOf(id);
            list = alarmDealServer.getDealedAlarmByUser(userId, riskEventName, nowDay, previousDay);
        }
        nowDay = nowDay.substring(0, 10);
        previousDay = previousDay.substring(0, 10);
        List<String> dayLists = DateUtil.getDatesBetweenDays(previousDay, nowDay,DateUtil.Year_Mouth_Day);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");
        return list;
    }


    /**
     * 鎸夌収鍛婅浜嬩欢绫诲瀷杩涜鍒嗙粍鑾峰緱title
     * @return
     */

    @Override
    public List<String> getSafeAlarmTitle() {
        List<String> strList = new ArrayList<>();
        List<QueryCondition_ES> conditions = new ArrayList<>();
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                conditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        SearchField	riskEventNameField= new SearchField("riskEventName", FieldType.String,0,50,null);
        List<Map<String,Object>> list = queryStatistics(conditions, riskEventNameField);
        for (Map<String, Object> map : list) {
            for(Map.Entry<String, Object> entry : map.entrySet()){
                String key = entry.getKey();
                if("riskEventName".equals(key)){
                    String name = entry.getValue().toString();
                    strList.add(name);
                }
            }
        }
        return strList;
    }

    /**
     * 鑾峰緱涓�骞寸殑鍛婅澶勭疆缁熻
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmDealCountByOneYear(){
        String previousYear = DateUtil.addNYear(-1).substring(0, 7);
        String nowYear = DateUtil.format(new Date()).substring(0, 7);
        List<QueryCondition_ES> cons = new ArrayList<QueryCondition_ES>();
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain:"+set);
            if(set!=null&&set.size()!=0){
                cons.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        cons.add(QueryCondition_ES.between("triggerTime", previousYear + "-01 00:00:00", DateUtil.format(new Date())));
        String groupBy = "triggerTime";
        String stackBy = "statusEnum";

        DateHistogramInterval timeInterval = DateHistogramInterval.MONTH;
        String timeFormat = "yyyy-MM";
        SearchField childField = new SearchField(stackBy, FieldType.String, 0,50,null);
        SearchField searchField = new SearchField(groupBy,FieldType.Date,timeFormat,timeInterval,childField,0,50);
        List<Map<String,Object>> list = queryStatistics(cons, searchField);
        SocUtil.dealAlarmInfo(list,stackBy);

        List<String> mouthList = DateUtil.getBetweenMounths(previousYear, nowYear,DateUtil.Year_Mouth);
        SocUtil.dealAnalysisResult(list, mouthList,stackBy,"triggerTime");
        return list;
    }
    /**
     * 鑾峰緱鍛婅鍚嶇О杩涜鍒嗙粍
     * @param analysisVO
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmNames(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        //logger.info("鏍慐S鏌ヨ鏉′欢:"+gson.toJson(conditions));
        if(SessionUtil.getCurrentUser()!=null&&SessionUtil.getauthorityType()){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                conditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        SearchField childrenSearchField = new SearchField("eventtypelevel",FieldType.String,null);
        String groupBy = "ruleName";
        SearchField searchField = new SearchField(groupBy,FieldType.String,childrenSearchField);
        searchField.setFieldName("ruleId");
        List<Map<String, Object>> queryStatistics = queryStatistics(conditions, searchField);
        List<Map<String, Object>> list = adjustAlarmNameConstructor(queryStatistics);
        list = getAlarmTypeTreeQuery(list);
        return list;
    }

    /**
     * 璋冩暣鎸夊悕绉板垎绫荤殑缁撴瀯
     * @param queryStatistics
     */
    private  List<Map<String,Object>> adjustAlarmNameConstructor(List<Map<String, Object>> queryStatistics){
        List<Map<String,Object>> list = new ArrayList<>();
        for (Map<String, Object> map : queryStatistics) {
            Map<String,Object> new_map = new HashMap<>();
            String ruleId = (String)map.get("ruleId");
            RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
            String ruleCode = null;
            if (riskEventRule != null)
            {
                String ruleName = riskEventRule.getName();
                ruleCode = riskEventRule.getRuleCode();
                new_map.put("ruleName", ruleName);
                new_map.put("ruleCode", ruleCode);
            }
            List<Map<String,Object>> eventtypelevel_list = (List<Map<String,Object>>)map.get("eventtypelevel");
            for (Map<String, Object> map2 : eventtypelevel_list) {
                Long count = (Long)map2.get("doc_count");
                String eventtypelevel = (String)map2.get("eventtypelevel");
                new_map.put("eventtypelevel", eventtypelevel);
                new_map.put("doc_count", count);
            }
            list.add(new_map);
        }
        return list;
    }





    /**
     * 鑾峰緱鏌ヨ鏉′欢
     * @param analysisVO
     * @return
     */
    @Override
    public List<QueryCondition_ES> getCondition(AnalysisVO analysisVO) {
        String id = analysisVO.getId();
        String statusEnum = analysisVO.getStatusEnum();
        String weight = analysisVO.getWeight();
        String eventtypelevel = analysisVO.getEventtypelevel();
        String riskEventName = analysisVO.getRiskEventName();
        String stime = analysisVO.getStime();
        String eTime = analysisVO.getEtime();
        String start_time=analysisVO.getStart_time();
        String end_time=analysisVO.getEnd_time();
        String ruleId = analysisVO.getRuleId();
        String ruleName = analysisVO.getRuleName();
        String stimebar = analysisVO.getStimebar();
        String relatedIps = analysisVO.getRelatedIps();
        String dstIps = analysisVO.getDstIps();
        String analysisIds = analysisVO.getAnalysisIds(); //鍛婅ID闆嗗悎
        String areaCode = analysisVO.getSrcAreaCode();
        Boolean linkAsset = analysisVO.getLinkAsset();
        Boolean linkApp = analysisVO.getLinkApp();  //鏄惁鍏宠仈App
        String srcIp_eq = analysisVO.getSrcIp_eq();
        String attackFlag = analysisVO.getAttackFlag();
        String dstAreaName = analysisVO.getDstAreaName();
        String dstDomainGuid = analysisVO.getDstAreaCode();
        String[] riskEventCodeArr = analysisVO.getRiskEventCodeArr();
        String systemId = analysisVO.getSystemId();
        String self_guid = analysisVO.getSelf_guid(); //鏈韩IP
        String complexSearch = analysisVO.getComplexSearch();
        String analysisType = analysisVO.getAnalysisType();  //鍛婅绫诲瀷
        String src_ips = analysisVO.getSrc_ips();
        String etimebar = "";
        Date date = null;
        String timeType = analysisVO.getTimeType();
        QueryCondition_ES esQuery = analysisVO.getEsQuery();
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String ruleCode = analysisVO.getRuleCode();
        String[] ruleCodeArr = analysisVO.getRuleCodeArr();
        String ruleCodeGroup = analysisVO.getRuleCodeGroup();
        String dataSource=analysisVO.getDataSource();
        String assetGuids=analysisVO.getAssetguids();

        /**
         * 涓栫晫鍩熺瓫閫�
         */
        getWorldDomainCondition(analysisVO, conditions);


        if(esQuery!=null) {
            conditions.add(esQuery);
        }

        if(!StringUtils.isEmpty(complexSearch)){
            conditions.add(QueryCondition_ES.or(QueryCondition_ES.like("src_ips", complexSearch),
                    QueryCondition_ES.like("dstIps", complexSearch),
                    QueryCondition_ES.like("ruleName", complexSearch)
            ));
        }
        if (!StringUtils.isEmpty(ruleCode)) {
            conditions.add(QueryCondition_ES.eq("ruleCode", ruleCode));
        }

        if (!StringUtils.isEmpty(ruleCodeGroup)&& "APT7".equalsIgnoreCase(ruleCodeGroup)){
            String[] ruleCodeArr2 = getRuleCodeArr();
            conditions.add(QueryCondition_ES.in("ruleCode", ruleCodeArr2));
        }

        if(!StringUtils.isEmpty(self_guid)){
            conditions.add(QueryCondition_ES.notEq("id", self_guid));
        }
        if(!StringUtils.isEmpty(id)){
            conditions.add(QueryCondition_ES.eq("id", id));
        }
        if(!StringUtils.isEmpty(relatedIps)){
            conditions.add(QueryCondition_ES.like("src_ips",relatedIps));
        }
        if(!StringUtils.isEmpty(src_ips)){
            conditions.add(QueryCondition_ES.like("src_ips",src_ips));
        }
        if(!StringUtils.isEmpty(riskEventName)){
            conditions.add(QueryCondition_ES.like("riskEventName",riskEventName));
        }
        if(!StringUtils.isEmpty(srcIp_eq)){
            conditions.add(QueryCondition_ES.eq("src_ips",srcIp_eq));
        }

        if(!StringUtils.isEmpty(areaCode)){
            conditions.add(QueryCondition_ES.eq("areaCode",areaCode));
        }
        if(!StringUtils.isEmpty(dstAreaName)){
            conditions.add(QueryCondition_ES.eq("dstAreaName",dstAreaName));
        }
        if(!StringUtils.isEmpty(dataSource)){
            if(dataSource.contains(",")) {
                String[] split = dataSource.split(",");
                List<String> dataSourceList = Arrays.asList(split);
                conditions.add(QueryCondition_ES.in("dataSource",dataSourceList));
            }else {
                conditions.add(QueryCondition_ES.eq("dataSource",dataSource));
            }

        }
        if(linkAsset!=null&&linkAsset) {
            conditions.add(QueryCondition_ES.gt("assetInfo.count", 0));
        }
        /**
         * 鏄惁鍏宠仈搴旂敤绯荤粺
         */
        if(linkApp!=null&&linkApp){
            conditions.add(QueryCondition_ES.gt("appSystemInfo.count", 0));
        }

        if(StringUtils.isNotEmpty(systemId)&&!"*".equals(systemId)) {
            conditions.add(QueryCondition_ES.or(
                    QueryCondition_ES.like("appSystemInfo.appIds", systemId),
                    QueryCondition_ES.like("appSystemInfo.appIds", ","+systemId),
                    QueryCondition_ES.like("appSystemInfo.appIds", systemId+",")
            ));
        }
        if(!StringUtils.isEmpty(dstDomainGuid)){
            if(dstDomainGuid.contains(",")) {
                String[] split = dstDomainGuid.split(",");
                List<String> codes = Arrays.asList(split);
                conditions.add(QueryCondition_ES.in("dstAreaCode",codes));
            }else {
                conditions.add(QueryCondition_ES.eq("dstAreaCode",dstDomainGuid));
            }
        }
        if(!StringUtils.isEmpty(attackFlag)){
            conditions.add(QueryCondition_ES.eq("attackFlag", attackFlag));
        }

        /**
         * 鍛婅绫诲瀷
         */
        logger.info("analysisType:"+analysisType);
        if(!StringUtils.isEmpty(analysisType)){
            Map<String, List<String> > eventConfigs= EventConfig.eventConfigList; //<"鐖櫕"锛宎rray>
            Map<String, String > eventNames=EventConfig.eventNames;//<"鐖櫕", "web_crawler">
            for (Map.Entry<String,String> entry : eventNames.entrySet()){
                String value = entry.getValue(); //瀵瑰簲鍊�
                if(value.equals(analysisType)){
                    String key = entry.getKey(); //瀵瑰簲鐨勯敭鍊�
                    List<String> riskEventCodeList = eventConfigs.get(key);
                    if(riskEventCodeList!=null&&riskEventCodeList.size()>0){
                        String[] riskEventCodeArrs=riskEventCodeList.toArray(new String[riskEventCodeList.size()]);
                        conditions.add(QueryCondition_ES.in("riskEventCode",riskEventCodeArrs));
                    }
                    break;
                }
            }
        }


        if(riskEventCodeArr!=null&&riskEventCodeArr.length>0){
            if(riskEventCodeArr.length==1) {
                conditions.add(QueryCondition_ES.or(
                        QueryCondition_ES.eq("riskEventCode",riskEventCodeArr[0]),
                        QueryCondition_ES.likeBegin("riskEventCode", riskEventCodeArr[0])
                ));
            }else {
                conditions.add(QueryCondition_ES.in("riskEventCode",riskEventCodeArr));
            }
        }

        if(ruleCodeArr!=null&&ruleCodeArr.length>0){
            if(ruleCodeArr.length==1) {
                conditions.add(QueryCondition_ES.eq("ruleCode",ruleCodeArr[0]));
            }else {
                conditions.add(QueryCondition_ES.in("ruleCode",ruleCodeArr));
            }
        }

        if(!StringUtils.isEmpty(dstIps)){
            conditions.add(QueryCondition_ES.like("dstIps", dstIps));
        }
        if(!StringUtils.isEmpty(analysisIds)){ //鍛婅闆嗗悎鏌ヨ
            String[] split = analysisIds.split(",");
            conditions.add(QueryCondition_ES.in("id", split));
        }
        if(!StringUtils.isEmpty(statusEnum)){
            String[] statusEnums = statusEnum.split(",");
            conditions.add(QueryCondition_ES.in("statusEnum", statusEnums));
        }
        if(!StringUtils.isEmpty(weight)){
            String[] weightArray = weight.split(",");
            conditions.add(QueryCondition_ES.in("weight", weightArray));
        }
        if(!StringUtils.isEmpty(eventtypelevel)){
            conditions.add(QueryCondition_ES.like("eventtypelevel",eventtypelevel));
        }
        if (!StringUtils.isEmpty(ruleName)) {
            conditions.add(QueryCondition_ES.like("ruleName", ruleName));
        }
        if (ruleId != null && !ruleId.isEmpty()) {
            conditions.add(QueryCondition_ES.eq("ruleId", ruleId));
        }

        if(StringUtils.isEmpty(stimebar)){
            if (!StringUtils.isEmpty(stime) && !StringUtils.isEmpty(eTime)) {
                if(stime.length()==10) {
                    stime = stime + " 00:00:00";
                }
                if(eTime.length()==10) {
                    eTime = eTime + " 23:59:59";
                }
                logger.info("时间间隔："+stime+ " "+eTime);
                conditions.add(QueryCondition_ES.between("triggerTime", stime, eTime));
            }else if (!StringUtils.isEmpty(start_time) && !StringUtils.isEmpty(end_time)) {
                if(start_time.length()==10) {
                    start_time = start_time + " 00:00:00";
                }
                if(end_time.length()==10) {
                    end_time = end_time + " 23:59:59";
                }
                logger.info("时间间隔："+start_time+ " "+end_time);
                conditions.add(QueryCondition_ES.between("triggerTime", start_time, end_time));
            }
        }
        if(!StringUtils.isEmpty(assetGuids)){
            conditions.add(QueryCondition_ES.eq("assetInfo.assetguids",assetGuids));
        }
        try {
            if (!StringUtils.isEmpty(timeType)){
                if ("day".equals(timeType)) {
                    date = DateUtil.parseDate(stimebar, "yyyy-MM-dd HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE, 30);
                    date = calendar.getTime();
                    etimebar = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");

                } else if ("week".equals(timeType) || "month".equals(timeType)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = sdf.parse(stimebar);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DATE, 1);
                    date = calendar.getTime();
                    etimebar = sdf.format(date);
                } else{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = sdf.parse(stimebar);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MONTH, 1);
                    date = calendar.getTime();
                    etimebar = sdf.format(date);
                }
                conditions.add(QueryCondition_ES.between("triggerTime", stimebar, etimebar));

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return conditions;
    }






    /**
     * 涓栫晫鍩熺瓫閫�
     * @param analysisVO
     * @param conditions
     */
    private void getWorldDomainCondition(AnalysisVO analysisVO, List<QueryCondition_ES> conditions) {
        //绫诲瀷鍙傛暟
        String mapLevel = analysisVO.getMapLevel(); //绫诲瀷
        String fromAreaName = analysisVO.getFromAreaName(); //涓栫晫鍩熸簮鍦板潃
        String toAreaName = analysisVO.getToAreaName(); //涓栫晫鍩熺洰鐨勫湴鍧�
        if(StringUtils.isNotEmpty(mapLevel)){
            String nameStr = "";
            switch (mapLevel) {
                case "1":
                    nameStr=".continent";
                    break;
                case "2":
                    nameStr=".country";
                    break;
                case "3":
                    nameStr=".province";
                    break;
                case "4":
                    nameStr=".city";
                    break;
                default:
                    throw new RuntimeException("鍦板浘鏄剧ず鑼冨洿浼犲弬寮傚父");
            }

            if(StringUtils.isNotEmpty(fromAreaName)){
                try{
                    fromAreaName = URLDecoder.decode(fromAreaName, "UTF-8");
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(!"*".equals(fromAreaName)){
                    conditions.add(QueryCondition_ES.eq("srcWorldMapName"+nameStr, fromAreaName));
                }
            }

            if(StringUtils.isNotEmpty(toAreaName)){
                try{
                    toAreaName = URLDecoder.decode(toAreaName, "UTF-8");
                }catch(Exception e){
                    e.printStackTrace();
                }
                if(!"*".equals(toAreaName)){
                    conditions.add(QueryCondition_ES.eq("dstWorldMapName"+nameStr, toAreaName));
                }
            }


        }
    }


    /**
     * 鑾峰緱鍛婅鏃ュ織鐨勫瓧娈典腑鏂囧悕
     * @param ruleId
     * @return
     * @throws ClassNotFoundException
     */
    public  List<String> getAnaslyasisLogColumnNameCn(String ruleId, LogTableNameVO logTableNameVO) throws ClassNotFoundException{
        List<String> column_list = new LinkedList<>();
        RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
        if(riskEventRule!=null){
            String logPath = riskEventRule.getLogPath();
            if(StringUtils.isNotEmpty(logPath)){
                Class<?> class1 = Class.forName(logPath);
                Field[] declaredFields = class1.getDeclaredFields();
                for (Field field : declaredFields) {
                    FieldDesc fieldDesc = field.getAnnotation(FieldDesc.class);
                    if(fieldDesc!=null){
                        String fieldDesc_value = fieldDesc.value();
                        try {
                            fieldDesc_value = new String(fieldDesc_value.getBytes("UTF-8"),"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.error("瀛楃瑙ｆ瀽澶辫触", e);
                        }
                        column_list.add(fieldDesc_value);
                    }
                }
            }else{
                String type = logTableNameVO.getType();
                EventTable eventTable = new EventTable();
                if("jdbc".equals(type)){
                    eventTable=eventTabelService.getEventTableByName(riskEventRule.getTableName());
                }else {
                    String id = logTableNameVO.getId();
                    eventTable = eventTabelService.getOne(id);
                }
                List<EventColumn> columnList=eventColumService.getEventColumnCurr(eventTable.getId());
                for(EventColumn eventColumn :columnList ){
                    String label = eventColumn.getLabel();
                    if(eventColumn.getIsShow()){
                        column_list.add(label);
                    }
                }
            }
        }
        return column_list;
    }

    /**
     * 鑾峰緱鍘熷鏃ュ織瀛楁鐨勮嫳鏂囧悕
     * @param ruleId
     * @return
     * @throws ClassNotFoundException
     */
    public List<String> getAnaslyasisLogColumnNameEn(String ruleId,LogTableNameVO logTableNameVO) throws ClassNotFoundException{
        RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
        List<String> column_list = new LinkedList<>();
        if(riskEventRule!=null){
            String logPath = riskEventRule.getLogPath();
            if(StringUtils.isNotEmpty(logPath)){
                Class<?> class1 = Class.forName(logPath);
                Field[] declaredFields = class1.getDeclaredFields();
                for (Field field : declaredFields) {
                    String name = field.getName();
                    logger.info("鏃ュ織鑻辨枃瀛楁锛�"+name);
                    column_list.add(name);
                }
            }else{
                String type = logTableNameVO.getType();
                EventTable eventTable = new EventTable();
                if("jdbc".equals(type)){
                    eventTable=eventTabelService.getEventTableByName(riskEventRule.getTableName());
                }else {
                    String id = logTableNameVO.getId();
                    eventTable = eventTabelService.getOne(id);
                }
                List<EventColumn> columnList=eventColumService.getEventColumnCurr(eventTable.getId());
                for(EventColumn eventColumn :columnList ){
                    String name = eventColumn.getName();
                    if(eventColumn.getIsShow()){
                        column_list.add(name);
                    }
                }
            }
        }
        return column_list;
    }


    /*
     *鑾峰緱鍘熷鏃ュ織琛ㄦ槑
     */
    @Override
    public List<LogTableNameVO> getLogTableNamVOs(String guid){
        List<LogTableNameVO> list = new ArrayList<>();
        WarnResultLogTmpVO warnResult = getAlarmById(guid);
        String ruleId = warnResult.getRuleId();
        RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
        if(riskEventRule!=null) {
            String flag = riskEventRule.getFlag();
            if(!"editor".equals(flag)) {
                LogTableNameVO logTableNameVO2 = filterLogTable(riskEventRule);
                logTableNameVO2.setAnalysisId(guid);
                list.add(logTableNameVO2);
            }else {
                FilterOperator filterOperatorByCode = getFilterOperatorByCode(riskEventRule);
                String roomType = filterOperatorByCode.getRoomType();
                if("idRoom".equals(roomType)){
                    list = getEditorLogTableNameByIdRoom(warnResult);
                }else {
                    list = getEditorLogTableNameByTimeRoom(warnResult);
                }
            }
            return list;
        }else {
            throw new RuntimeException("娌℃湁瀵瑰簲鐨勮鍒欎俊鎭紝璇锋鏌ワ紒");
        }
    }


    /**
     * 鑾峰緱瀵瑰簲鐨勫垎鏋愬櫒
     * @param riskEventRule
     * @return
     */
    private FilterOperator getFilterOperatorByCode(RiskEventRule riskEventRule) {
        String analysisId = riskEventRule.getAnalysisId();
        List<FilterOperator> filterList = filterOperatorService.getFilterOperators(analysisId);
        if(filterList.size()==1) {
            FilterOperator filterOperator = filterList.get(0);
            return filterOperator;
        }else {
            throw new RuntimeException("鍛婅瑙勫垯鏃犳硶鍏宠仈鍒板垎鏋愬櫒锛岃妫�鏌ワ紒");
        }
    }


    /**
     * 閫氳繃timeRoom鑾峰緱瀵瑰簲鐨勫師濮嬫棩蹇椾俊鎭�
     * @param warnResult
     * @return
     */
    private List<LogTableNameVO> getEditorLogTableNameByTimeRoom(WarnResultLogTmpVO warnResult){
        List<LogTableNameVO> list = new ArrayList<>();
        String timeRoom = warnResult.getTimeRoom();
        Map<String,Object> timeMap = gson.fromJson(timeRoom, Map.class);
        for (Map.Entry<String, Object> map: timeMap.entrySet()) {
            String key = map.getKey();
            constructEventTableInfo(warnResult, list, key);
        }
        return list;

    }


    /**
     * 鏋勯�爀venttableInfo
     * @param warnResult
     * @param list
     * @param key
     */
    private void constructEventTableInfo(WarnResultLogTmpVO warnResult, List<LogTableNameVO> list,
                                         String key) {
        EventTable eventTable=eventTabelService.getEventTableByName(key);
        LogTableNameVO logTableNameVO2 = new LogTableNameVO();
        logTableNameVO2.setId(eventTable.getId());
        logTableNameVO2.setLabel(eventTable.getLabel());
        logTableNameVO2.setName(key);
        logTableNameVO2.setType("editor");
        logTableNameVO2.setAnalysisId(warnResult.getId());
        list.add(logTableNameVO2);
    }


    /**
     * 鑾峰緱缂栬緫绫诲瀷鐨刲ogtables
     * @param warnResult
     * @return
     */
    private List<LogTableNameVO> getEditorLogTableNameByIdRoom(WarnResultLogTmpVO warnResult) {
        List<LogTableNameVO> list = new ArrayList<>();
        Map<String, String[]> idRoom = warnResult.getIdRoom();
        for(Map.Entry<String, String[]> entry : idRoom.entrySet()) {
            String key = entry.getKey();
            constructEventTableInfo(warnResult, list, key);
        }
        return list;
    }


    /**
     * 澧炲姞杩囨护鐨勬棩蹇�
     * @param riskEventRule
     */
    private LogTableNameVO filterLogTable(RiskEventRule riskEventRule) {
        LogTableNameVO logTableNameVO = new LogTableNameVO();
        String tableName = riskEventRule.getTableName();
        EventTable eventTable=eventTabelService.getEventTableByName(riskEventRule.getTableName());
        String id = eventTable.getId();
        logTableNameVO.setId(id);
        logTableNameVO.setName(tableName);
        logTableNameVO.setLabel(eventTable.getLabel());
        String logPath = riskEventRule.getLogPath();
        if(StringUtils.isNotEmpty(logPath)){
            logTableNameVO.setType("logvo");
        }else {
            logTableNameVO.setType("jdbc");
        }
        return logTableNameVO;
    }

    /**
     * 鏍规嵁guid鑾峰緱棰勮琛�
     * @param logTableNameVO
     * @return
     */

    @Override
    public Map<String, Object> getAnalysisTable(LogTableNameVO logTableNameVO) {
        Map<String, Object> map = new HashMap<String, Object>();
        WarnResultLogTmpVO warnResult = getAlarmById(logTableNameVO.getAnalysisId());
        String ruleId = warnResult.getRuleId();
        String name = logTableNameVO.getName();
        String logsInfo = warnResult.getLogsInfo();
        List<Map> list2 = new LinkedList<>();

        Map<String, List<LinkedHashMap<String, Object>>> tableRows = new HashMap<>();
        Map<String, PageRes_ES> tablePage = new HashMap<>();
        //鑾峰緱瀵瑰簲鐨勬棩蹇楄嫳鏂囧垪
        list2 = getEventTableData(logTableNameVO,warnResult, logsInfo, list2);
        List<String> english_list = new ArrayList<>();
        List<String> chinese_list = new ArrayList<>();
        try {
            english_list = getAnaslyasisLogColumnNameEn(ruleId, logTableNameVO);
            chinese_list = getAnaslyasisLogColumnNameCn(ruleId,logTableNameVO);
        } catch (ClassNotFoundException e) {
            logger.info("鏈彂鐜癱lassPath", e);
        }
        List<LinkedHashMap<String, Object>> newList = new ArrayList<LinkedHashMap<String, Object>>();
        if(chinese_list.size()==english_list.size()) { //涓枃鍜岃嫳鏂囨棩蹇楀瓧娈电浉瀵瑰簲
            for (Map<String,Object> maps : list2){
                LinkedHashMap<String, Object> rowJson = new LinkedHashMap<String, Object>();
                for (int i = 0; i < english_list.size(); i++) {
                    String column_cn = chinese_list.get(i); //涓枃鍘熷鏃ュ織瀛楁
                    String column_en = english_list.get(i); //鑻辨枃鍘熷鏃ュ織瀛楁
                    Object obj=dataPatternTranfer(column_en,maps.get(column_en));
                    rowJson.put(column_cn, obj);
                }
                newList.add(rowJson);
            }
        }
        tableRows.put(name, newList);
        for (String key : tableRows.keySet()) {
            PageRes_ES result = new PageRes_ES();
            result.setTotal((long)tableRows.get(key).size());
            result.setList(tableRows.get(key));
            tablePage.put(key, result);
        }
        map.put("tableColumns", chinese_list);
        map.put("tablePages", tablePage);
        return map;
    }

    private Object dataPatternTranfer(String key,Object data){

        switch (key){
            case "event_time" :
            case "indate" :
            case "time" :
            case "ActiveTime" :
            case "EndTime" :
            case "DisableTime" :
            case "topActiveTime" :
            case "start_time" :
            case "end_time" :
                String time=(String)data;
                String result=DateUtil.parseUTC(time);
                if(StringUtils.isNotEmpty(result)){
                    data=result;
                }
                break;
            case "log_level":
            case "event_level":
            case "security_level":
                String level=data.toString();
                if(StringUtils.isNotEmpty(level)){
                    logger.info("level:"+level);
                    data = WeightEnum.getWeightName(level);
                }
            default:
                break;
        }
        return data;
    }


    private List<Map> getEventTableData(LogTableNameVO logTableNameVO,WarnResultLogTmpVO warnResult, String logsInfo, List<Map> list2) {
        Map<String, String[]> idRoom = warnResult.getIdRoom();
        String timeRoom = warnResult.getTimeRoom();
        if(idRoom!=null&&idRoom.size()!=0) {
            getLogInfoByIdRoom(logTableNameVO, list2, idRoom);
        }else if(StringUtils.isNotEmpty(timeRoom)) {
            getLogInfoByTimeRoom(logTableNameVO, list2, timeRoom);
        }else {
            try {
                list2 = JsonMapper.fromJsonString2List(logsInfo, Map.class);
            } catch (IOException e) {
                logger.error("寮傚父闂", e);
            }
        }
        return list2;
    }


    private void getLogInfoByIdRoom(LogTableNameVO logTableNameVO, List<Map> list2, Map<String, String[]> idRoom) {
        String name = logTableNameVO.getName();
        String id = logTableNameVO.getId();
        String[] logIds = idRoom.get(name);
        if(logIds!=null){
            EventTable eventTable = eventTabelService.getOne(id);
            String indexName = eventTable.getIndexName();
            List<Map<String, Object>> all = getLogMapsByIdRoom(logIds, eventTable);
            list2.addAll(all);
        }
    }


    private void getLogInfoByTimeRoom(LogTableNameVO logTableNameVO, List<Map> list2, String timeRoom) {
        String name = logTableNameVO.getName();
        String id = logTableNameVO.getId();
        Map<String,Object> timeMap = gson.fromJson(timeRoom, Map.class);
        Object object = timeMap.get(name);
        String json = gson.toJson(object);
        if(StringUtils.isNotEmpty(json)) {
            EventTable eventTable = eventTabelService.getOne(id);
            String indexName = eventTable.getIndexName();
            TimeRoomVO timeRoomVO = gson.fromJson(json,TimeRoomVO.class);
            try {
                List<Map<String, Object>> all = getLogMapsByTimeRoom(timeRoomVO, eventTable);
                list2.addAll(all);
            } catch (ParseException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public List<Map<String, Object>> getLogMapsByIdRoom(String[] logIds, EventTable eventTable) {
        String indexName = eventTable.getIndexName();
        String indexType = eventTable.getIndexType();
        if(StringUtils.isEmpty(indexType)) {
            indexType = "logs";
        }
        String[] baseIndexNames = getIndexListByBaseIndexName(indexName);
        IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
        indexsInfoVO.setIndex(baseIndexNames);
        indexsInfoVO.setType(new String[] {indexType});
        List<QueryCondition_ES> indexCondition = new ArrayList<>();
        List<String> asList = Arrays.asList(logIds);
        indexCondition.add(QueryCondition_ES.in("guid", asList));
        return elasticSearchMapManage.findAll(indexsInfoVO, indexCondition);
    }



    public List<Map<String, Object>> getLogMapsByTimeRoom(TimeRoomVO timeRoomVO, EventTable eventTable) throws ParseException {
        String indexName = eventTable.getIndexName();
        String indexType = eventTable.getIndexType();
        String[] baseIndexNames = getIndexListByBaseIndexName(indexName);
        IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
        indexsInfoVO.setIndex(baseIndexNames);
        if(StringUtils.isEmpty(indexType)) {
            indexType = "logs";
        }
        indexsInfoVO.setType(new String[] {indexType});
        List<QueryCondition_ES> indexCondition = new ArrayList<>();
        String minTime = timeRoomVO.getMinTime();
        String maxTime = timeRoomVO.getMaxTime();
        Date minDate = DateUtil.parseDate(minTime, DateUtil.DEFAULT_DATE_PATTERN);
        Date maxDate = DateUtil.parseDate(maxTime, DateUtil.DEFAULT_DATE_PATTERN);
        minDate = DateUtil.addHours(minDate, -8);
        maxDate = DateUtil.addHours(maxDate, -8);
        String minUTC = DateUtil.format(minDate, DateUtil.UTC_TIME);
        String maxUTC = DateUtil.format(maxDate, DateUtil.UTC_TIME);
        indexCondition.add(QueryCondition_ES.between("event_time", minUTC, maxUTC));
        List<Map<String,Object>> list = elasticSearchMapManage.findAll(indexsInfoVO, indexCondition);
        return list;
    }




    /**
     * 鍛婅鏄庣粏bar缁戝畾
     * @param analysisVO
     * @return
     */

    @Override
    public List<Map<String, Object>> analysisBarList(AnalysisVO analysisVO){
        long begin = System.currentTimeMillis();
        String sTime = analysisVO.getStime();
        String eTime = analysisVO.getEtime();
        String type = analysisVO.getType();
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        //logger.info("鏌辩姸鍥綞S鏌ヨ鏉′欢:"+gson.toJson(conditions));
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                conditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if(StringUtils.isNotEmpty(sTime)&&StringUtils.isNotEmpty(eTime)){
            if(!"3month".equals(type)){
                try {
                    result = getAlarmThreadThendBarChart(conditions, type, sTime, eTime);
                } catch (ParseException e) {
                    logger.info("鏃堕棿瑙ｆ瀽澶辫触",e);
                }
            }else {
                result = getMouthAlarmInfo(sTime, eTime, conditions);
            }
            Map<String, Object> map = SocUtil.getFeedBackTime(begin);
            result.add(map);
        }
        return result;
    }

    private List<Map<String, Object>> getAlarmThreadThendBarChart(List<QueryCondition_ES> conditions, String flag,
                                                                  String stime, String etime) throws ParseException {
        if(stime.length()==10) {
            stime = stime + " 00:00:00";
        }
        if(etime.length()==10) {
            etime = etime + " 23:59:59";
        }
        Date startTime = DateUtil.parseDate(stime, DateUtil.DEFAULT_DATE_PATTERN);
        Date endTime = DateUtil.parseDate(etime, DateUtil.DEFAULT_DATE_PATTERN);
        long interval = 0; //绮掑害
        String format = ""; //鏃堕棿鏍煎紡
        String groupBy = "triggerTime";
        switch (flag) {
            //鍘讳綑鐨勫師鍥犳槸锛歟s鏌ヨ鐨勯兘鏄暣鏁板紑濮�
            case "hour":
                endTime = DateUtil.addMinutes(endTime, -(endTime.getMinutes()%5));
                interval = (long)5*60*1000;
                format = "HH:mm";
                break;
            case "day":
                endTime = DateUtil.addMinutes(endTime, -(endTime.getMinutes()%30));
                interval = (long)30*60*1000;
                format = "HH:mm";
                break;
            case "week":
                interval = (long)24*60*60*1000;
                format = "yyyy-MM-dd";
                break;
            case "month":
                interval = (long)24*60*60*1000;
                format = "yyyy-MM-dd";
                break;
            default:
                break;
        }
        SearchField searchField = 	new SearchField(groupBy, FieldType.Date, format, interval, null);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        SocUtil.getTimeFullMap(startTime, endTime, interval, format, list);
        return list;
    }


    /**
     * 鑾峰緱鏈堝憡璀︽暟鎹�
     * @param sTime
     * @param eTime
     * @param conditions
     * @return
     */
    private List<Map<String,Object>> getMouthAlarmInfo(String sTime, String eTime, List<QueryCondition_ES> conditions) {
        SearchField searchField = new SearchField("triggerTime", FieldType.Date, DateUtil.Year_Mouth_Day, DateHistogramInterval.DAY, null);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        sTime = sTime.substring(0, 7);
        eTime = eTime.substring(0, 7);
        List<String> mouthList = DateUtil.getBetweenMounths(sTime, eTime,DateUtil.Year_Mouth);
        SocUtil.dealAnalysisResult(list, mouthList, null,"triggerTime");
        return list;
    }

    /**
     * 鏍规嵁Id鑾峰緱棰勮淇℃伅
     * @param guid
     * @return
     */
    @Override
    public WarnResultLogTmpVO getAlarmById(String guid) {
        List<QueryCondition_ES> condition_ESs = new ArrayList<>();
        condition_ESs.add(QueryCondition_ES.eq("id", guid));
        List<WarnResultLogTmpVO> list = findAll(condition_ESs,"triggerTime","desc");
        if(list.size()==1 || list.size()>1) {
            WarnResultLogTmpVO warnResultLogTmpVO = list.get(0);
            return warnResultLogTmpVO;
        }else {
            return null;
        }
    }

    /**
     * 鏀瑰彉棰勮鐨勭姸鎬�
     * @param analysisStatusVO
     * @return
     */

    @Override
    public boolean changeAnalysisResultStatus(AnalysisStatusVO analysisStatusVO) {
        WarnResultLogTmpVO WarnResultLogTmpVO = getAlarmById(analysisStatusVO.getId());
        if(WarnResultLogTmpVO!=null){
            WarnResultLogTmpVO.setStatusEnum(Integer.valueOf(analysisStatusVO.getStatus()));
            WarnResultLogTmpVO.setDeal_person(analysisStatusVO.getDealPerson());
            responseStrategyService.set(selectRelateStrategy.getService(StrategyEnum.STATUSSTRATEGY));
            responseStrategyService.get().restartStrategy(WarnResultLogTmpVO);
            Date triggerTime = WarnResultLogTmpVO.getTriggerTime();
            String date = DateUtil.format(triggerTime, DateUtil.Year_Mouth_Day);
            String indexName = WARN_RESULT_TMP+"-"+date;
            save(indexName,WarnResultLogTmpVO);

            return true;
        }else {
            return false;
        }
    }
    /**
     * 鏍规嵁棰勮绫诲瀷鑾峰緱涓暟
     * @return
     */

    @Override
    public List<Map<String, Object>> getCountByAlarmType(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        SearchField searchField = new SearchField("riskEventName", FieldType.String, null);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        return list;
    }

    /**
     * 鍛婅杞笓瀹�
     * @param experVO
     * @return
     */

    @Override
    public Result<BusinessIntance> transferExpert(ExpertVO experVO) {
        try {
            //鍏堟敼鐘舵��
            String analysisId = experVO.getAnalysisId(); //澶氫釜鍛婅Id
            DealInfoVO dealInfo = new DealInfoVO();
            dealInfo.setId(analysisId);
            dealInfo.setType(TypeClass.professor);
            dealInfo.setUserId(experVO.getUserId());
            dealInfo.setContent("涓撳澶勭疆");
            dealInfo.setUserName(experVO.getUserName());
            dealInfo.setDeal_line(experVO.getDeadline());
            dealInfo.setDealPerson(experVO.getExpertNames());
            alarmDealServer.alarmDeal(dealInfo);  //鍙湁192.168.120.86鐘舵�佸彲浠ヨ浆涓撳
            //鍐嶅鐞�
            String[] alarm_Arr = analysisId.split(",");
            if(alarm_Arr.length>0){
                String alarm_guid = alarm_Arr[0]; //涓诲悎骞跺憡璀�
                WarnResultLogTmpVO warnResult =getAlarmById(alarm_guid);
                Map<String,Object> map = mapper.map(warnResult, Map.class);
                map.put("users", experVO.getUsers());
                map.put("analysisId", analysisId); //鍛婅ID闆嗗悎
                WorkDataVOByName workDataVOByName = new WorkDataVOByName();
                workDataVOByName.setForms(map);
                workDataVOByName.setCode(experVO.getCode());
                workDataVOByName.setName(warnResult.getRiskEventName());
                workDataVOByName.setUserId(experVO.getUserId());
                workDataVOByName.setUserName(experVO.getUserName());
                workDataVOByName.setProcessName(PROCESS_NAME);
                Result<BusinessIntance> result = flowService.createTicket(workDataVOByName);
                return result;
            }else{
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "鏈�夋嫨鍛婅锛岃妫�鏌�");
            }
        }catch(Exception e) {
            logger.error("鍒涘缓涓撳娴佺▼澶辫触", e);
            throw new RuntimeException("鍒涘缓涓撳娴佺▼澶辫触", e);
        }
    }

    /**
     * 鍛婅杞缃�
     * @param experVO
     * @return
     */

    @Override
    public Result<BusinessIntance> transferAlarm(ExpertVO experVO) {
        try {
            //鍏堟敼鐘舵��
            String analysisId = experVO.getAnalysisId(); //澶氫釜鍛婅Id
            DealInfoVO dealInfo = new DealInfoVO();
            dealInfo.setId(analysisId);
            dealInfo.setType(TypeClass.eventDeal);
            dealInfo.setUserId(experVO.getUserId());
            dealInfo.setContent("鍛婅澶勭疆");
            dealInfo.setUserName(experVO.getUserName());
            dealInfo.setDeal_line(experVO.getDeadline());
            dealInfo.setDealPerson(experVO.getExpertNames());
            alarmDealServer.alarmDeal(dealInfo);  //鍙湁192.168.120.86鐘舵�佸彲浠ヨ浆涓撳
            //鍐嶅鐞�
            String[] alarm_Arr = analysisId.split(",");
            if(alarm_Arr.length>0){
                String alarm_guid = alarm_Arr[0]; //涓诲悎骞跺憡璀�
                WarnResultLogTmpVO warnResult =getAlarmById(alarm_guid);
                Map<String,Object> map = mapper.map(warnResult, Map.class);
                String expertStr = gson.toJson(experVO);
                expertStr= JsonSanitizer.sanitize(expertStr);
                Map<String,Object> fromJson = gson.fromJson(expertStr, Map.class);
                map.putAll(fromJson);
                //浼氱浜哄憳
                List<String> list = new ArrayList<>();
                list.add(experVO.getUserId());
                map.put("signList", list);
                WorkDataVOByName workDataVOByName = new WorkDataVOByName();
                workDataVOByName.setForms(map);
                workDataVOByName.setCode(experVO.getCode());
                workDataVOByName.setName(experVO.getTicketName());
                workDataVOByName.setUserId(experVO.getUserId());
                workDataVOByName.setUserName(experVO.getUserName());
                workDataVOByName.setProcessName("浜嬩欢澶勭疆娴佺▼");
                Result<BusinessIntance> result = flowService.createTicket(workDataVOByName);
                return result;
            }else{
                return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "鏈�夋嫨鍛婅锛岃妫�鏌�");
            }
        }catch(Exception e) {
            logger.error("鍒涘缓涓撳娴佺▼澶辫触", e);
            throw new RuntimeException("鍒涘缓涓撳娴佺▼澶辫触", e);
        }

    }



    /**
     * 鑾峰緱鏁版嵁绫诲瀷
     * @return
     */
    private List<Map<String, Object>> getAlarmTypeList() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] ruleNameArr = {"铚滅綈绫�","瀹¤绫�","瑙勫垯绫�","妯″瀷绫�","濞佽儊绫�"};
        String[] ruleFlagArr = {"honeypot","audit","safer","model","threat"};
        String[] ruleIdArr = {HONEY_ID,AUDIT_ID,RULE_ID,MODEL_ID,THREAT_ID};
        for (int i = 0; i < ruleIdArr.length; i++) {
            Map<String, Object> initAlarmTypeMap = getInitAlarmTypeMap(ruleFlagArr[i], ruleNameArr[i]);
            getlevelTwoCategory(ruleIdArr[i], initAlarmTypeMap);
            list.add(initAlarmTypeMap);
        }
        return list;
    }

    /**
     * 鍔犺浇鎬婚噺
     * @param typeList
     */
    private void getSumsize(List<Map<String, Object>> typeList) {
        // 浜岀骇鏁版嵁鍔犺浇
        for (Map<String, Object> map : typeList) {
            List<Map<String, Object>> childrenList = (List<Map<String, Object>>) map.get("children");
            for (Map<String, Object> childMap : childrenList) {
                List<Map<String, Object>> childList = (List<Map<String, Object>>) childMap.get("children");
                long sum = 0;
                for (Map<String, Object> map2 : childList) {
                    sum += (long) map2.get("doc_count");
                }
                childMap.put("doc_count", sum);
            }
            map.put("children", childrenList);
        }
        // 涓�绾ф暟鎹姞杞�
        for (Map<String, Object> map : typeList) {
            List<Map<String, Object>> childrenList = (List<Map<String, Object>>) map.get("children");
            long sum = 0;
            for (Map<String, Object> map2 : childrenList) {
                sum += (long) map2.get("doc_count");
            }
            map.put("doc_count", sum);
        }
    }

    /**
     * 绛涢�変簩绾ф暟鎹�
     *
     * @param typeList
     */
    private void selectEmptyData(List<Map<String, Object>> typeList) {
        // 绛涢�変簩绾ф暟鎹�
        for (Map<String, Object> map : typeList) {
            List<Map<String, Object>> emptyMap = new ArrayList<>();
            List<Map<String, Object>> childrenList = (List<Map<String, Object>>) map.get("children");
            for (Map<String, Object> childMap : childrenList) {
                List<Map<String, Object>> childList = (List<Map<String, Object>>) childMap.get("children");
                if (childList.size() == 0) {
                    emptyMap.add(childMap);
                }
            }
            childrenList.removeAll(emptyMap);
            map.put("children", childrenList);
        }
        // 绛涢�変竴绾х骇鏁版嵁
        List<Map<String, Object>> emptyMap = new ArrayList<>();
        for (Map<String, Object> map : typeList) {
            List<Map<String, Object>> childrenList = (List<Map<String, Object>>) map.get("children");
            if (childrenList.size() == 0) {
                emptyMap.add(map);
            }
        }
        typeList.removeAll(emptyMap);
    }

    /**
     * 杩涜瀵瑰簲鐨勮祴鍊�
     *
     * @param typeList
     * @param map
     */
    private void getRelateLevel(List<Map<String, Object>> typeList, Map<String, Object> map){
        map.put("key", "three");
        Object typeLevel = map.get("eventtypelevel");
        if (typeLevel != null) {
            String typeStr = typeLevel.toString();
            for (Map<String, Object> typemap : typeList) {
                List<Map<String, Object>> childrenList = (List<Map<String, Object>>) typemap.get("children");
                for (Map<String, Object> child : childrenList) {
                    String levelTwoEventTypeLevel = child.get("eventtypelevel").toString();
                    if (typeStr.contains(levelTwoEventTypeLevel)) {
                        List<Map<String, Object>> children = (List<Map<String, Object>>) child.get("children");
                        children.add(map);
                        child.put("children", children);
                    }
                }
                typemap.put("children", childrenList);
            }
        }
    }

    /**
     * 鑾峰緱鍒濆鍖栫殑map
     * @param type
     * @return
     */
    private Map<String, Object> getInitAlarmTypeMap(String type, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("eventtypelevel", type);
        map.put("ruleName", name);
        map.put("key", "one");
        return map;
    }

    /**
     * 鑾峰緱浜岀骇浜嬪垎绫荤洰褰�
     *
     * @param parentId
     * @param map
     * @return
     */
    private void getlevelTwoCategory(String parentId, Map<String, Object> map) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("parentId", parentId));
        List<EventCategory> eventCategoryList = eventCategoryService.findAll(conditions);
        for (EventCategory eventCategory : eventCategoryList) {
            Map<String, Object> eventMap = new HashMap<>();
            List<Map<String, Object>> childrenList = new ArrayList<>();
            String codeLevel = eventCategory.getCodeLevel();
            String title = eventCategory.getTitle();
            eventMap.put("eventtypelevel", codeLevel);
            eventMap.put("ruleName", title);
            eventMap.put("children", childrenList);
            eventMap.put("key", "two");
            list.add(eventMap);
        }
        map.put("children", list);
    }

    /**
     * 鑾峰緱鍛婅绫诲瀷鏍戠殑鍒嗙被鏌ヨ
     * @param list
     * @return
     */

    public List<Map<String, Object>> getAlarmTypeTreeQuery(List<Map<String, Object>> list) {
        List<Map<String, Object>> typeList = getAlarmTypeList();
        for (Map<String, Object> map : list) {
            getRelateLevel(typeList, map);
        }
        // 绛涢�夋暟鎹�
        selectEmptyData(typeList);
        getSumsize(typeList);
        return typeList;
    }
    /**
     * 鑾峰緱鍘熷鏃ュ織瀛楁淇℃伅锛岃幏寰楁墍鏈夌殑
     * @return
     */

    @Override
    public List<Map<String, Object>> getOrignalLogInfo(){
        List<Map<String,Object>> map_list =  new ArrayList<>();
        try {
            List<Class<?>> list = PackageUtil.getClassName("com.vrv.logVO");
            for (Class<?> class1 : list) {
                //鑾峰緱绫绘敞閲�
                Map<String,Object> map = new HashMap<>();
                LogDesc logDesc = class1.getAnnotation(LogDesc.class);
                if(logDesc!=null){
                    String value = logDesc.value();
                    map.put("鏃ュ織鍚嶇О", value);
                }
                //鍙嶅皠鑾峰緱瀵瑰簲鐨勫瓧娈靛��
                Field[] declaredFields = class1.getDeclaredFields();
                for (Field field : declaredFields) {
                    String name = field.getName();
                    FieldDesc fieldDesc = field.getAnnotation(FieldDesc.class);
                    if(fieldDesc!=null){
                        String value = fieldDesc.value();
                        map.put(value, name);
                    }
                }
                map_list.add(map);
            }
        } catch (ClassNotFoundException | IOException e) {
            logger.error("鑾峰緱鍘熷鏃ュ織閿欒", e);
        }

        return map_list;
    }


    /**
     * 鑾峰緱涓冨ぉ鍐呯殑鍛婅瓒嬪娍鍥�
     * @return
     */

    @Override
    public List<Map<String, Object>> getSafeAlarmTrendBy7Days() {
        Date preDay = DateUtil.addDay(new Date(), -7);
        String nowDay = DateUtil.format(new Date());
        String preDayStr = DateUtil.format(preDay);
        List<QueryCondition_ES> createAlarmConditions = new ArrayList<>();
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                createAlarmConditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        createAlarmConditions.add(QueryCondition_ES.between("triggerTime", preDayStr, nowDay));
        String groupBy = "triggerTime";
        String timeFormat = "MM-dd";
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeInterval, null,0,7);
        List<Map<String,Object>> list = queryStatistics(createAlarmConditions, searchField);
        nowDay = nowDay.substring(5, 10);
        preDayStr = preDayStr.substring(5, 10);
        List<String> dayLists = DateUtil.getDatesBetweenDays(preDayStr, nowDay,DateUtil.Date_Format);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");
        return list;
    }


    /**
     * 鑾峰緱鍛婅濞佽儊鍒嗗竷(瀹氬埗)
     * @param map
     * @return
     */

    @Override
    public List<Map<String, Object>> getALarmRiskEvent(Map<String, Object> map) {
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String filter = map.get("filter").toString();
        Object ip = map.get("ip");
        Object type = map.get("type");
        if(ip!=null){
            if(type!=null) {
                String ip_str = ip.toString();
                String type_str = type.toString();
                if(StringUtils.isNotEmpty(type_str)&&StringUtils.isNotEmpty(ip_str)){
                    conditions.add(QueryCondition_ES.like(type_str, ip_str));
                }
            }else {
                String ip_str = ip.toString();
                if(StringUtils.isNotEmpty(ip_str)){
                    conditions.add(QueryCondition_ES.like("src_ips", ip_str));
                }
            }
        }

        String startTime = getStartTime(filter);  //鑾峰緱寮�濮嬫椂闂�
        String endTime  = DateUtil.format(new Date()); //鑾峰緱缁撴潫鏃堕棿

        if(startTime!=null&&endTime!=null) {
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }
        SearchField	riskEventNameField= new SearchField("riskEventName", FieldType.String,0,50,null);
        List<Map<String, Object>> queryStatistics = queryStatistics(conditions, riskEventNameField);
        return queryStatistics;
    }



    /**
     * 鑾峰緱寮�濮嬫椂闂�
     * @param filter
     * @return
     */
    private String getStartTime(String filter) {
        switch (filter) {
            case "day":
                Date addDay = DateUtil.addDay(new Date(), -1);
                String format = DateUtil.format(addDay);
                return format;
            case "month":
                Date addMonth = DateUtil.addMouth(new Date(), -1);
                String addMonth_Str = DateUtil.format(addMonth);
                return addMonth_Str;
            case "week":
                Date addWeek = DateUtil.addDay(new Date(), -7);
                String addWeek_str = DateUtil.format(addWeek);
                return addWeek_str;
            case "year":
                String beforeYear = DateUtil.addNYear(-1);
                return beforeYear;
            default:
                return null;
        }
    }


    /**
     * 鑾峰緱鍛婅绛夌骇鍒嗗竷缁熻
     * @param analysisVO
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmByWeight(AnalysisVO analysisVO){
        String startTime = analysisVO.getStime();
        String endTime = analysisVO.getEtime();
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        if(StringUtils.isNotEmpty(startTime)&&StringUtils.isNotEmpty(endTime)) {
            if(startTime.length()==10){
                startTime = startTime + " 00:00:00";
            }
            if(endTime.length()==10) {
                endTime = endTime + " 23:59:59";
            }
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }else{
            Date start_day = DateUtil.addDay(new Date(), -7);
            startTime = DateUtil.format(start_day);
            endTime = DateUtil.format(new Date());
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }
        //鏉冮檺娣诲姞
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                conditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        List<Map<String, Object>> list = getWeightMaps(conditions);
        return list;
    }

    @Override
    public List<Map<String, Object>> getWeightMaps(List<QueryCondition_ES> conditions) {
        String groupBy = "weight";
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,10,null);
        return queryStatistics(conditions,searchField);
    }


    /**
     * 鑾峰緱鍛婅绛夌骇鍜屾椂闂寸浉鍏冲垎甯冨浘
     * @param map
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmdByWeightAndTime(Map<String, Object> map) {
        Object obj_ip = map.get("ip");
        Object obj_startTime = map.get("start_time");
        Object obj_endTime = map.get("end_time");
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = null;
        String endTime = null;
        if(obj_ip!=null) {
            String ip = obj_ip.toString();
            conditions.add(QueryCondition_ES.like("src_ips", ip));
        }

        if(obj_startTime!=null&&obj_endTime!=null) {
            startTime = obj_startTime.toString();
            endTime = obj_endTime.toString();
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }else{
            Date start_day = DateUtil.addDay(new Date(), -7);
            startTime = DateUtil.format(start_day);
            endTime = DateUtil.format(new Date());
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }
        String groupBy = "triggerTime";
        String stackBy = "weight";
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        String timeFormat = "MM-dd";
        SearchField childField = new SearchField(stackBy, FieldType.String, 0,50,null);
        SearchField searchField = new SearchField(groupBy,FieldType.Date,timeFormat,timeInterval,childField,0,50);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        SocUtil.dealAlarmInfo(list,stackBy);
        startTime = startTime.substring(5, 10);
        endTime = endTime.substring(5, 10);
        List<String> dayLists = DateUtil.getDatesBetweenDays(startTime, endTime,DateUtil.Date_Format);
        SocUtil.dealAnalysisResult(list, dayLists,"weight","triggerTime");
        return list;
    }


    /**
     * 鍛婅绫诲瀷鍖哄煙缁熻
     * @param map
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmTypeStaticsByRegion(Map<String, Object> map) {
        Object obj_type = map.get("type");
        Object date = map.get("date"); //yyyy-mm-dd
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = null;
        String endTime = null;
        if(obj_type!=null) {
            String type = obj_type.toString();
            conditions.add(QueryCondition_ES.likeBegin("eventtypelevel", type));
        }
        if(date!=null) {
            String datetime = date.toString();
            startTime = datetime+" 00:00:00";
            endTime = datetime+" 23:59:59";
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }else{
            String datetime = DateUtil.format(new Date(), DateUtil.Year_Mouth_Day);
            startTime = datetime+" 00:00:00";
            endTime = datetime+" 23:59:59";
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }
        String groupBy = "orgCode";
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,50,null);
        List<Map<String,Object>> list = queryStatistics(conditions,searchField);
        return list;
    }

    @Override
    public List<Map<String,Object>> getAlarmStaticsByRegionAndType(AnalysisVO analysisVO){
        List<QueryCondition_ES> conditions =getCondition(analysisVO);
        List<QueryCondition_ES> condition_es =getCondition(analysisVO);
        //鏉冮檺娣诲姞
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                condition_es.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        String groupBy = "dstAreaName";
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,50,null);
        conditions.addAll(condition_es);
        List<Map<String,Object>> list = queryStatistics(conditions,searchField);
        for(Map<String,Object> map :list){
            if(StringUtils.isEmpty(analysisVO.getDstAreaName())&&StringUtils.isEmpty(analysisVO.getDstAreaCode())){
                analysisVO.setDstAreaName(map.get("dstAreaName").toString());
            }
            conditions=getCondition(analysisVO);
            conditions.addAll(condition_es);
            groupBy = "riskEventName";
            searchField = new SearchField(groupBy, FieldType.String,0,50,null);
            List<Map<String,Object>> typeList = queryStatistics(conditions,searchField);
            map.put("eventMap",typeList);
        }

        return  list;
    }



    @Override
    public List<Map<String, Object>> getAlarmTypeStaticsCount(Map<String, Object> map) {
        Object obj_type = map.get("type");
        Object obj_startTime = map.get("start_time");
        Object obj_endTime = map.get("end_time");
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = null;
        String endTime = null;
        if(obj_type!=null) {
            String type = obj_type.toString();
            conditions.add(QueryCondition_ES.likeBegin("eventtypelevel", type));
        }

        getTimeCondition(startTime,endTime,obj_startTime, obj_endTime, conditions);
        String groupBy = "triggerTime";
        String timeFormat = "MM-dd";
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeInterval, null,0,7);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        List<String> dayLists = getDayBetweenList(startTime, endTime);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");
        return list;


    }

    /**
     * 鑾峰緱寮�濮嬫埅姝㈡棩鏈熷ぉ鏁�
     * @param startTime
     * @param endTime
     * @return
     */
    private List<String> getDayBetweenList(String startTime, String endTime) {
        if(endTime!=null&&startTime!=null){
            endTime = endTime.substring(5, 10);
            startTime = startTime.substring(5, 10);
        }
        List<String> dayLists = DateUtil.getDatesBetweenDays(startTime, endTime,DateUtil.Date_Format);
        return dayLists;
    }


    /**
     * 鍛婅绫诲瀷绔釜鏁板崟浣嶆帓琛岋細
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmTypeStaticsByRegionTop20(Map<String,Object> map) {
        Object obj_type = map.get("type");
        Object obj_startTime = map.get("start_time");
        Object obj_endTime = map.get("end_time");
        Object obj_statEnum = map.get("statEnum");
        List<QueryCondition_ES> conditions = new ArrayList<>();
        String startTime = null;
        String endTime = null;
        getTimeCondition(startTime,endTime,obj_startTime, obj_endTime, conditions);
        String groupBy = "areaName"; //缁勭粐缁撴瀯
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,20,null);
        List<Map<String,Object>> queryStatistics = queryStatistics(conditions, searchField);
        return queryStatistics;
    }


    /**
     * 鏃堕棿閫夋嫨澶勭悊
     * @param obj_startTime
     * @param obj_endTime
     * @param conditions
     */
    private void getTimeCondition(String startTime,String endTime,Object obj_startTime, Object obj_endTime, List<QueryCondition_ES> conditions) {
        if(obj_startTime!=null&&obj_endTime!=null) {
            startTime = obj_startTime.toString();
            endTime = obj_endTime.toString();
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }else{
            Date start_day = DateUtil.addDay(new Date(), -7);
            startTime = DateUtil.format(start_day);
            endTime = DateUtil.format(new Date());
            conditions.add(QueryCondition_ES.between("triggerTime", startTime,endTime));
        }
    }


    /**
     * 鍛婅鎬绘暟鎺掑簭
     * @param map
     * @return
     */

    @Override
    public List<Map<String, Object>> getAlarmTypeOrderByCount(Map<String, Object> map) {
        Object obj_type = map.get("type");
        List<QueryCondition_ES> conditions = new ArrayList<>();
        if(obj_type!=null) {
            String type = obj_type.toString();
            conditions.add(QueryCondition_ES.likeBegin("eventtypelevel", type));
        }
        String groupBy = "eventtypelevel"; //缁勭粐缁撴瀯
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,20,null);
        List<Map<String,Object>> queryStatistics = queryStatistics(conditions, searchField);
        return queryStatistics;
    }


    @Override
    public List<WarnResultLogTmpVO> getAlarmByIds(String guids) {
        List<WarnResultLogTmpVO> list = new ArrayList<>();
        String[] guids_asrray = guids.split(",");
        for (String guid : guids_asrray) {
            WarnResultLogTmpVO WarnResultLogTmpVO = getAlarmById(guid);
            list.add(WarnResultLogTmpVO);
        }
        return list;
    }



    @Override
    public List<AssetIpVO> getDstIpTop5(String riskEventCode) {
        List<AssetIpVO> list = new ArrayList<>();
        String groupBy = "dstIps";
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,5,null);
        List<QueryCondition_ES> condition = new ArrayList<>();
        if(StringUtils.isNotEmpty(riskEventCode)){
            condition.add(QueryCondition_ES.eq("eventtypelevel", riskEventCode));
        }
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        for (Map<String, Object> map : queryStatistics) {
            Object dstIps_obj = map.get(groupBy);
            Object doc_count_obj = map.get("doc_count");
            if(dstIps_obj!=null&&doc_count_obj!=null){
                AssetIpVO assetIpVO = new AssetIpVO();
                String dstIps = dstIps_obj.toString();
                Long doc_count = (Long)doc_count_obj;
                Map<String,Object> asset_map = new HashMap<>();
                asset_map.put("asset_ip", dstIps);
                AssetDetailVO resultObjVO = assetService.getOneAssetDetailByIp(dstIps);
                Asset asset = resultObjVO.getAsset();
                assetIpVO.setAsset(asset);
                assetIpVO.setIp(dstIps);
                assetIpVO.setCount(doc_count);
                list.add(assetIpVO);
            }
        }
        return list;
    }



    @Override
    public List<Map<String, Object>> getSrcIpTop5(AnalysisVO analysisVO) {
        String groupBy = "src_ips";
        int size=50;
        if(analysisVO.getSize()!=null){
            size= analysisVO.getSize();
        }
        SearchField searchField = new SearchField(groupBy, FieldType.String,0,size,null);
        List<QueryCondition_ES> condition =  getCondition(analysisVO);
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        return queryStatistics;
    }


    /**
     * 鑾峰緱30澶╁唴鏍规嵁浜嬩欢鍒嗙被鏌ヨ鍛婅瓒嬪娍
     * @param riskEventCode
     * @return
     */

    @Override
    public List<Map<String, Object>> getSafeAlarmTrendBy30DaysByEventCategory(String riskEventCode) {
        String nowDay = DateUtil.format(new Date()); //鑾峰緱褰撳墠鏃堕棿
        String beforeMouthDay = DateUtil.addNMouth(-1); //鑾峰緱涓�涓湀浠ュ墠鐨勬椂闂�
        List<QueryCondition_ES> createAlarmConditions = new ArrayList<>();
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                createAlarmConditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        createAlarmConditions.add(QueryCondition_ES.eq("eventtypelevel", riskEventCode));
        createAlarmConditions.add(QueryCondition_ES.between("triggerTime", beforeMouthDay, nowDay));
        String groupBy = "triggerTime";
        String timeFormat = "MM-dd";
        long timeSpan = (long)24*3600*1000;
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeSpan, null,0,30);
        List<Map<String,Object>> list = queryStatistics(createAlarmConditions, searchField);
        nowDay = nowDay.substring(5, 10);
        beforeMouthDay = beforeMouthDay.substring(5, 10);
        List<String> dayLists = DateUtil.getDatesBetweenDays(beforeMouthDay, nowDay,DateUtil.Date_Format);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");
        return list;
    }



    @Override
    public AlarmAttackPath getAlarmAttackPath(String riskEventCode) {
        AlarmAttackPath alarmAttackPath = new AlarmAttackPath();
        List<AssetIpVO> dstAssetList = this.getAssetCollections(riskEventCode, "dstIps");  //鐩殑璧勪骇闆嗗悎
        List<AssetIpVO> srcAssetList = this.getAssetCollections(riskEventCode, "src_ips"); //婧愯祫浜ч泦鍚�
        List<AssetPathVO> assetAttackPath = this.getAssetAttackPath(riskEventCode);
        alarmAttackPath.setSrcIps(srcAssetList);
        alarmAttackPath.setDstIps(dstAssetList);
        alarmAttackPath.setAttackPath(assetAttackPath);
        return alarmAttackPath;
    }

    @Override
    public AttackVO getAttackPath(String riskEventCode) {
        AttackVO alarmAttackPath = new AttackVO();
        List<AttackNodeVO> dstAssetList=getAttackNodeVOCollections(riskEventCode, "dstIps");  //鐩殑璧勪骇闆嗗悎
        List<AttackNodeVO> srcAssetList = getAttackNodeVOCollections(riskEventCode, "src_ips"); //婧愯祫浜ч泦鍚�
        List<AttackPathVO> assetAttackPath = this.getAttackPathVOs(riskEventCode);
        alarmAttackPath.setSrcIps(srcAssetList);
        alarmAttackPath.setDstIps(dstAssetList);
        alarmAttackPath.setAttackPath(assetAttackPath);
        return alarmAttackPath;
    }






    /**
     * 鑾峰緱瀵硅祫浜ф敾鍑昏矾寰勯泦鍚�
     * @param riskEventCode
     * @return
     */
    private List<AttackPathVO> getAttackPathVOs(String riskEventCode){
        SearchField child_search_field = new SearchField("dstIps", FieldType.String,0,500,null);
        SearchField searchField = new SearchField("src_ips",FieldType.String, 0, 500,child_search_field);
        List<QueryCondition_ES> condition = new ArrayList<>();
        if(StringUtils.isNotBlank(riskEventCode)){
            condition.add(QueryCondition_ES.eq("eventtypelevel", riskEventCode));
        }
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        List<AttackPathVO> list = new ArrayList<>();
        for (Map<String, Object> map : queryStatistics) {
            AttackPathVO attackPathVO = new AttackPathVO();
            Object src_ips_objects = map.get("src_ips");
            Object dstIps_objects = map.get("dstIps");
            if(src_ips_objects!=null && dstIps_objects!=null){
                //婧愯祫浜у疄浣�
                AssetIpVO src_asset = new AssetIpVO();
                String src_ip = src_ips_objects.toString();

                AssetDetailVO src_asset_obj = assetService.getOneAssetDetailByIp(src_ip);
                Asset srcAsset=src_asset_obj.getAsset();
                if(srcAsset!=null){
                    AttackNodeVO attackNodeVO=mapper.map(src_asset_obj.getAsset(),AttackNodeVO.class);
                    attackPathVO.setSrcAsset(attackNodeVO);
                }
                //鐩殑璧勪骇瀹炰綋
                AssetIpVO dst_asset = new AssetIpVO();
                List<Map<String,Object>> dst_ip_colletions = (List<Map<String,Object>>)dstIps_objects;
                if(dst_ip_colletions.size()>0){
                    Map<String, Object> dst_map = dst_ip_colletions.get(0);
                    Object doc_count_obj=dst_map.get("doc_count");
                    Object dstIps_obj=dst_map.get("dstIps");
                    if(doc_count_obj!=null && dstIps_obj!=null){
                        String dstIps = dstIps_obj.toString();
                        AssetDetailVO dst_asset_obj = assetService.getOneAssetDetailByIp(dstIps);
                        AttackNodeVO dstNodeVO=mapper.map(dst_asset_obj.getAsset(),AttackNodeVO.class);
                        attackPathVO.setDstAsset(dstNodeVO);
                    }
                }
            }
            list.add(attackPathVO);
        }

        return list;
    }


    /**
     * 鑾峰緱瀵硅祫浜ф敾鍑昏矾寰勯泦鍚�
     * @param riskEventCode
     * @return
     */
    private List<AssetPathVO> getAssetAttackPath(String riskEventCode){
        SearchField child_search_field = new SearchField("dstIps", FieldType.String,0,500,null);
        SearchField searchField = new SearchField("src_ips",FieldType.String, 0, 500,child_search_field);
        List<QueryCondition_ES> condition = new ArrayList<>();
        if(StringUtils.isNotBlank(riskEventCode)){
            condition.add(QueryCondition_ES.eq("eventtypelevel", riskEventCode));
        }
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        List<AssetPathVO> list = new ArrayList<>();
        for (Map<String, Object> map : queryStatistics) {
            AssetPathVO assetPathVO = new AssetPathVO();
            Object src_ips_objects = map.get("src_ips");
            Object dstIps_objects = map.get("dstIps");
            if(src_ips_objects!=null && dstIps_objects!=null){
                //婧愯祫浜у疄浣�
                AssetIpVO src_asset = new AssetIpVO();
                String src_ip = src_ips_objects.toString();
                AssetDetailVO src_asset_obj = assetService.getOneAssetDetailByIp(src_ip);
                src_asset.setAsset(src_asset_obj.getAsset());
                src_asset.setIp(src_ip);
                assetPathVO.setSrcAsset(src_asset);
                //鐩殑璧勪骇瀹炰綋
                AssetIpVO dst_asset = new AssetIpVO();
                List<Map<String,Object>> dst_ip_colletions = (List<Map<String,Object>>)dstIps_objects;
                if(dst_ip_colletions.size()>0){
                    Map<String, Object> dst_map = dst_ip_colletions.get(0);
                    Object doc_count_obj=dst_map.get("doc_count");
                    Object dstIps_obj=dst_map.get("dstIps");
                    if(doc_count_obj!=null && dstIps_obj!=null){
                        Long doc_count = (Long)doc_count_obj;
                        String dstIps = dstIps_obj.toString();
                        AssetDetailVO dst_asset_obj = assetService.getOneAssetDetailByIp(dstIps);
                        dst_asset.setAsset(dst_asset_obj.getAsset());
                        dst_asset.setIp(dstIps);
                        assetPathVO.setDstAsset(dst_asset);
                        assetPathVO.setCount(doc_count);
                    }
                }
            }
            list.add(assetPathVO);
        }

        return list;
    }


    /**
     * 鑾峰緱璧勪骇闆嗗悎
     * @param riskEventCode
     * @return
     */
    private List<AssetIpVO> getAssetCollections(String riskEventCode,String field){
        List<AssetIpVO> list = new ArrayList<>();
        SearchField searchField = new SearchField(field, FieldType.String, 0, 500,null);
        List<QueryCondition_ES> condition = new ArrayList<>();
        if(StringUtils.isNotBlank(riskEventCode)){
            condition.add(QueryCondition_ES.eq("eventtypelevel", riskEventCode));
        }
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        for (Map<String, Object> map : queryStatistics) {
            Object dstIps_obj = map.get(field);
            Object doc_count_obj = map.get("doc_count");
            if(dstIps_obj!=null&&doc_count_obj!=null){
                AssetIpVO assetIpVO = new AssetIpVO();
                String dstIps = dstIps_obj.toString();
                Long doc_count = (Long)doc_count_obj;
                Map<String,Object> asset_map = new HashMap<>();
                asset_map.put("asset_ip", dstIps);
                AssetDetailVO resultObjVO = assetService.getOneAssetDetailByIp(dstIps);
                Asset asset = resultObjVO.getAsset();
                assetIpVO.setAsset(asset);
                assetIpVO.setIp(dstIps);
                assetIpVO.setCount(doc_count);
                list.add(assetIpVO);
            }
        }
        return list;
    }

    private List<AttackNodeVO> getAttackNodeVOCollections(String riskEventCode,String field){
        List<AttackNodeVO> list = new ArrayList<>();
        SearchField searchField = new SearchField(field, FieldType.String, 0, 500,null);
        List<QueryCondition_ES> condition = new ArrayList<>();
        if(StringUtils.isNotBlank(riskEventCode)){
            condition.add(QueryCondition_ES.eq("eventtypelevel", riskEventCode));
        }
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        for (Map<String, Object> map : queryStatistics) {
            Object dstIps_obj = map.get(field);
            if(dstIps_obj!=null){
                AttackNodeVO attackNodeVO = new AttackNodeVO();
                String dstIps = dstIps_obj.toString();
                Map<String,Object> asset_map = new HashMap<>();
                asset_map.put("asset_ip", dstIps);
                AssetDetailVO resultObjVO = assetService.getOneAssetDetailByIp(dstIps);
                Asset asset = resultObjVO.getAsset();
                if(asset!=null){
                    mapper.copy(asset,attackNodeVO);
                    list.add(attackNodeVO);
                }
            }
        }
        return list;
    }



    @Override
    public List<Map<String,Object>> attackTypeDistribute(String field) {
        String preDate = DateUtil.addNMouth(-1);
        String nowDate = DateUtil.format(new Date());
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.between("triggerTime", preDate, nowDate));
        String groupBy = "triggerTime";
        String timeFormat = "MM-dd";
        long timeSpan = (long)24*3600*1000;
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeSpan, null,0,30);
        SearchField	riskEventNameField= new SearchField(field, FieldType.String,0,50,searchField);
        List<Map<String,Object>> list = queryStatistics(conditions, riskEventNameField);
        for (Map<String, Object> map : list) {
            Object trigger_time_obj = map.get("triggerTime");
            if(trigger_time_obj!=null) {
                List<Map<String,Object>> trigger_time_list = (List<Map<String,Object>>)trigger_time_obj;
                nowDate = nowDate.substring(5, 10);
                preDate = preDate.substring(5, 10);
                List<String> dayLists = DateUtil.getDatesBetweenDays(preDate, nowDate,DateUtil.Date_Format);
                SocUtil.dealAnalysisResult(trigger_time_list, dayLists,null,"triggerTime");
                map.put("triggerTime", trigger_time_list);
            }
        }
        return list;
    }



    @Override
    public List<Map<String, Object>> attackTypeHotPoint() {
        String preDate = DateUtil.format(DateUtil.addDay(new Date(), -5));
        String nowDate = DateUtil.format(new Date());
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.between("triggerTime", preDate, nowDate));
        String groupBy = "triggerTime";
        String timeFormat = "HH";
        long timeSpan = (long)3600*1000;
        SearchField	riskEventNameField= new SearchField("weight", FieldType.String,0,50,null);
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeSpan, riskEventNameField,0,30);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        return list;

    }



    @Override
    public List<Map<String, Object>> getAnalysisInfoByArea(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);

        SearchField searchField = new SearchField("area_name", FieldType.String,0,10000,null);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        return list;
    }


    @Override
    public List<Map<String, Object>> getAnalysisInfoBySrcArea(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);

        SearchField searchField = new SearchField("srcAreaName", FieldType.String,0,10000,null);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        return list;
    }



    @Override
    public List<Map<String, Object>> alarmAttackByTimeStatics() {
        List<QueryCondition_ES> conditions = new ArrayList<>();
        conditions.add(QueryCondition_ES.between("triggerTime", DateUtil.format(new Date(), "yyyy-MM-dd")+" 00:00:00",DateUtil.format(new Date(), "yyyy-MM-dd")+" 23:59:59"));
        String groupBy = "triggerTime";
        String timeFormat = "HH";
        long timeSpan = (long)3600*1000;
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeSpan, null,0,30);
        List<Map<String,Object>> list = queryStatistics(conditions, searchField);
        for (Map<String, Object> map2 : list) {
            map2.put("name", map2.get("triggerTime"));
            map2.put("value", map2.get("doc_count"));
            map2.remove("triggerTime");
            map2.remove("doc_count");
        }
        return list;
    }



    @Override
    public List<Map<String, Object>> getSrcIpList(String start_time, String end_time) {
        if(StringUtils.isNotEmpty(start_time)&&StringUtils.isNotEmpty(end_time)){
            List<QueryCondition_ES> conditions = new ArrayList<>();
            conditions.add(QueryCondition_ES.between("triggerTime", start_time, end_time));
            String groupBy = "triggerTime";
            String timeFormat = "HH";
            long timeSpan = (long)3600*1000;
            SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeSpan, null,0,30);
            List<Map<String,Object>> queryStatistics = queryStatistics(conditions, searchField);
            return queryStatistics;
        }
        return new ArrayList<Map<String,Object>>();
    }



    @Override
    public List<Map<String, Object>> getAlarmInfoBySrcIpAndRuleId(Map<String, Object> map) {
        Object start_time_obj = map.get("start_time");
        Object end_time_obj = map.get("end_time");
        List<Map<String,Object>> list = new ArrayList<>();
        if(start_time_obj!=null&&end_time_obj!=null){
            String start_time = start_time_obj.toString();
            String end_time = end_time_obj.toString();
            List<QueryCondition_ES> conditions = new ArrayList<>();
            conditions.add(QueryCondition_ES.between("triggerTime", start_time, end_time));
            SearchField childField = new SearchField("ruleId", FieldType.String, 0,50,null);
            SearchField searchField = new SearchField("src_ips", FieldType.String, 0,50,childField);
            list = queryStatistics(conditions, searchField);
            return list;
        }
        return list;
    }



    @Override
    public List<Map<String, Object>> getAlarmScoreBySrcIp(Map<String, Object> map) {
        Object start_time_obj = map.get("start_time");
        Object end_time_obj = map.get("end_time");
        Object src_ip_obj = map.get("src_ip");
        List<Map<String,Object>> list = new ArrayList<>();
        if(start_time_obj!=null&&end_time_obj!=null&&src_ip_obj!=null){
            String start_time = start_time_obj.toString();
            String end_time = end_time_obj.toString();
            String src_ip = src_ip_obj.toString();
            List<QueryCondition_ES> conditions = new ArrayList<>();
            conditions.add(QueryCondition_ES.between("triggerTime", start_time, end_time));
            conditions.add(QueryCondition_ES.eq("src_ips", src_ip));
            SearchField field = new SearchField("ruleId", FieldType.String, 0,50,null);
            list = queryStatistics(conditions, field);
        }
        return list;
    }


    @Override
    public List<Map<String, Object>> eventAlarmStatistics(AnalysisVO analysisVO, SearchField searchField) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        Date eTime=null;
        Date stime=null;

        if (!StringUtils.isEmpty(analysisVO.getStime()) && !StringUtils.isEmpty(analysisVO.getEtime())) {
            try {
                stime= DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
                eTime=DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
            }catch (Exception e) {
                eTime=null;
                stime=null;
            }
        }
        IndexsInfoVO indexsInfoVO = getIndexsInfoVO(stime,eTime);
        SearchFieldIsNotNull(searchField,conditions);

        logger.debug("缁熻鏌ヨ绱㈠紩锛�"+gson.toJson(indexsInfoVO));
        //logger.debug("缁熻鏌ヨ杩囨护鏉′欢锛�"+gson.toJson(conditions));
        logger.debug("缁熻鏌ヨ鍒嗙粍瀛楁锛�"+gson.toJson(searchField));

        List<Map<String, Object>> queryStatistics = super.queryStatistics(indexsInfoVO,conditions, searchField);
        if(queryStatistics.size()>10) {
            logger.debug("缁熻鏌ヨ缁撴灉锛�"+gson.toJson(queryStatistics.subList(0, 10)));
        }else {
            logger.debug("缁熻鏌ヨ缁撴灉锛�"+gson.toJson(queryStatistics));
        }
        return queryStatistics;
    }

    private void SearchFieldIsNotNull(SearchField searchField,List<QueryCondition_ES> conditions) {
        conditions.add(QueryCondition_ES.notNull(searchField.getFieldName()));
        if(searchField.getChildrenField()!=null&&searchField.getChildrenField().size()>0) {
            for(SearchField  child : searchField.getChildrenField()) {
                SearchFieldIsNotNull(child,conditions);
            }
        }
    }



    @Override
    public Integer getAffectedAssetCount(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        conditions.add(QueryCondition_ES.gt("assetInfo.count", 0));

        SearchField child=new SearchField("assetInfo.count",FieldType.NumberMax,null);
        SearchField searchField=new SearchField("dstIps",FieldType.String,child);

        List<Map<String, Object>> queryStatistics = super.queryStatistics(getIndexsInfoVO(analysisVO),conditions, searchField);
        //logger.info(queryStatistics);
        return queryStatistics.size();
    }


    @Override
    public List<Map<String, Object>> getAffectedAssetDetail(AnalysisVO analysisVO, Integer top) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        conditions.add(QueryCondition_ES.gt("assetInfo.count", 0));

        //	SearchField child=new SearchField("assetInfo.count",FieldType.NumberMax,null);
        SearchField searchField=new SearchField("dstIps",FieldType.String,0,top,null);

        List<Map<String, Object>> queryStatistics = super.queryStatistics(getIndexsInfoVO(analysisVO),conditions, searchField);

        return queryStatistics ;
    }


    /**********************************鍥界綉鍛婅鏌ヨ鎺ュ彛*************************************/


    @Override
    public Result<Long> eventAlarmTotal(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                conditions.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                Result<Long> result = ResultUtil.success(0L);
                return result;
            }
        }
        long count = count(conditions);
        Result<Long> result = ResultUtil.success(count);
        return result;
    }



    @Override
    public Result<List<Map<String, Object>>> getAlarmTrendBy7Days(AnalysisVO analysisVO) {
        String stime = analysisVO.getStime(); //寮�濮嬫椂闂�
        String etime = analysisVO.getEtime(); //缁撴潫浜嬩欢
        List<String> dayLists =new ArrayList<>();
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        String flag = analysisVO.getFlag();
        String groupBy = "triggerTime";
        String format = null;
        DateHistogramInterval timeInterval = null;
        switch (flag) {
            //鍘讳綑鐨勫師鍥犳槸锛歟s鏌ヨ鐨勯兘鏄暣鏁板紑濮�
            case "day":
                timeInterval = DateHistogramInterval.HOUR;
                format = "HH";
                dayLists = GwParamsUtil.getAllElement(0,24);
                break;
            case "week":
                timeInterval = DateHistogramInterval.DAY;
                format = "MM-dd";
                stime = stime.substring(5, 10);
                etime = etime.substring(5, 10);
                dayLists = DateUtil.getDatesBetweenDays(stime, etime,format);
                break;
            case "month":
                timeInterval = DateHistogramInterval.DAY;
                format = "MM-dd";
                stime = stime.substring(5, 10);
                etime = etime.substring(5, 10);
                dayLists = DateUtil.getDatesBetweenDays(stime, etime,format);
                break;
            case "year":
                timeInterval = DateHistogramInterval.MONTH;
                format = "MM";
                dayLists = GwParamsUtil.getAllElement(1,13);
                break;
            default:
                break;
        }
        SearchField searchField = new SearchField(groupBy, FieldType.Date, format, timeInterval, null,0,50);
        SearchFieldIsNotNull(searchField,condition);
        List<Map<String,Object>> list = queryStatistics(condition, searchField);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");
        Result<List<Map<String,Object>>> result = ResultUtil.success(list);
        return result;
    }



    @Override
    public Result<Map<String, Object>> getAlarmEventLevel(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        String groupBy = "weight";

        SearchField searchField = new SearchField(groupBy, FieldType.String, null);
        List<Map<String,Object>> list = queryStatistics(getIndexsInfoVO(analysisVO),conditions,searchField);
        List<String> weightList = getWeightEnum();  //weight闆嗗悎
        SocUtil.completionUtil(list, weightList, "weight", "doc_count");
        Map<String, Object> safeLevel = getSafeLevel(list);
        return ResultUtil.success(safeLevel);
    }


    public Result<List<Map<String,Object>>> getAlarmEventLevel1_5(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        String groupBy = "weight";
        SearchField searchField = new SearchField(groupBy, FieldType.String, null);
        List<Map<String,Object>> list = queryStatistics(getIndexsInfoVO(analysisVO),conditions,searchField);
        List<String> weightList = getWeightEnum();  //weight闆嗗悎
        SocUtil.completionUtil(list, weightList, "weight", "doc_count");

        return ResultUtil.success(list);
    }

    /**
     * 鑾峰緱瀹夊叏绛夌骇
     * @param list
     * @return
     */
    private static Map<String,Object> getSafeLevel(List<Map<String,Object>> list){
        Long weight  = 0L;
        Long mid = 0L;
        Long high = 0L;
        Map<String,Object> map_level = new HashMap<>();
        for (Map<String, Object> map : list) {
            Object weight_obj = map.get("weight");
            if(weight_obj instanceof String) {
                String weightStr = weight_obj.toString();
                weight =Long.valueOf(weightStr);
            }else {
                weight= (Long)weight_obj;
            }
            if(weight==1) {
                map_level.put("veryLow", map.get("doc_count"));
            }else if(weight==2) {
                String doccountStr = map.get("doc_count").toString();
                mid+=Long.valueOf(doccountStr);
                map_level.put("low", mid);
            }else if(weight==3) {
                String doccountStr = map.get("doc_count").toString();
                mid+=Long.valueOf(doccountStr);
                map_level.put("mid", mid);
            }else if(weight==4) {
                String doccountStr = map.get("doc_count").toString();
                mid+=Long.valueOf(doccountStr);
                map_level.put("high", mid);
            }else if(weight==5)  {
                String doc_count_str  = map.get("doc_count").toString();
                high+=Integer.valueOf(doc_count_str);
                map_level.put("veryHigh", high);
            }
        }
        return map_level;


    }


    /**
     * 鑾峰緱Weight鏋氫妇
     * @return
     */
    private  List<String> getWeightEnum(){
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }



    @Override
    public Result<List<Map<String, Object>>> getLevelEventCateoryByTriggerTime(AnalysisVO analysisVO) {
        String stime = analysisVO.getStime(); //寮�濮嬫椂闂�
        String etime = analysisVO.getEtime(); //缁撴潫浜嬩欢
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        String groupBy = "triggerTime";
        String timeFormat = "MM-dd";
        DateHistogramInterval timeInterval = DateHistogramInterval.DAY;
        SearchField childSearchField = new SearchField("weight", FieldType.String, null);
        SearchField searchField = new SearchField(groupBy, FieldType.Date, timeFormat, timeInterval, childSearchField,0,7);
        List<Map<String,Object>> list = queryStatistics(condition, searchField);
        for (Map<String, Object> map : list) {
            Object weightObject = map.get("weight");
            if(weightObject!=null){
                List<Map<String,Object>> weighList = (List<Map<String,Object>>)weightObject;
                List<String> weightEnum = getWeightEnum();  //weight闆嗗悎
                SocUtil.completionUtil(weighList, weightEnum, "weight", "doc_count");
                Map<String, Object> safeLevel = getSafeLevel(weighList);
                map.put("weight", safeLevel);
            }
        }
        stime = stime.substring(5, 10);
        etime = etime.substring(5, 10);
        List<String> dayLists = DateUtil.getDatesBetweenDays(stime, etime,DateUtil.Date_Format);
        SocUtil.dealAnalysisResult(list, dayLists,null,"triggerTime");

        Result<List<Map<String,Object>>> result = ResultUtil.success(list);
        return result;

    }



    @Override
    public Result<List<Map<String, Object>>> getLevelEventCateoryByRegion(AnalysisVO analysisVO) {
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        String groupBy = "areaName";
        SearchField childSearchField = new SearchField("weight", FieldType.String, null);
        SearchField searchField = new SearchField(groupBy, FieldType.String, childSearchField);
        List<Map<String,Object>> list = queryStatistics(condition, searchField);
        for (Map<String, Object> map : list) {
            Object weightObject = map.get("weight");
            if(weightObject!=null){
                List<Map<String,Object>> weighList = (List<Map<String,Object>>)weightObject;
                List<String> weightEnum = getWeightEnum();  //weight闆嗗悎
                SocUtil.completionUtil(weighList, weightEnum, "weight", "doc_count");
                Map<String, Object> safeLevel = getSafeLevel(weighList);
                map.put("weight", safeLevel);
            }
        }
        Result<List<Map<String,Object>>> result = ResultUtil.success(list);
        return result;
    }

    @Override
    public Result<List<Map<String, Object>>> getWeightTrend(AnalysisVO analysisVO){
        List<Map<String,Object>> resultList=new ArrayList<>();
        String stime=analysisVO.getStime();
        String etime=analysisVO.getEtime();
        String type=analysisVO.getType();
        List<QueryCondition_ES> condition = getCondition(analysisVO);

        if(stime.length()==10) {
            stime = stime + " 00:00:00";
        }
        if(etime.length()==10) {
            etime = etime + " 23:59:59";
        }
        Date startTime=null;
        Date endTime = null;
        try {
            startTime = DateUtil.parseDate(stime, DateUtil.DEFAULT_DATE_PATTERN);
            endTime = DateUtil.parseDate(etime, DateUtil.DEFAULT_DATE_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long interval = 0; //绮掑害
        String format = ""; //鏃堕棿鏍煎紡
        String groupBy = "triggerTime";
        DateHistogramInterval timeInterval = null;
        List<String> dayLists =new ArrayList<>();
        switch (type) {
            //鍘讳綑鐨勫師鍥犳槸锛歟s鏌ヨ鐨勯兘鏄暣鏁板紑濮�
            case "day":
                timeInterval = DateHistogramInterval.HOUR;
                format = "HH";
                dayLists = GwParamsUtil.getAllElement(0,24);
                break;
            case "week":
                timeInterval = DateHistogramInterval.DAY;
                format = "MM-dd";
                stime = stime.substring(5, 10);
                etime = etime.substring(5, 10);
                dayLists = DateUtil.getDatesBetweenDays(stime, etime,format);
                break;
            case "month":
                timeInterval = DateHistogramInterval.DAY;
                format = "MM-dd";
                stime = stime.substring(5, 10);
                etime = etime.substring(5, 10);
                dayLists = DateUtil.getDatesBetweenDays(stime, etime,format);
                break;
            case "year":
                timeInterval = DateHistogramInterval.MONTH;
                format = "MM";
                dayLists = GwParamsUtil.getAllElement(1,13);
                break;
            default:
                break;
        }
        SearchField childSearchField = 	new SearchField(groupBy, FieldType.Date, format, timeInterval, null);

        SearchField searchField = new SearchField("weight", FieldType.String, childSearchField);
        List<Map<String,Object>> list = queryStatistics(condition, searchField);
        Map<Integer,List<Map<String,Object>>> integerListMap=list.stream().collect(Collectors.toMap(item->Integer.parseInt(item.get("weight").toString()), item->(List<Map<String, Object>>) item.get("triggerTime")));
        for(int i=1;i<=5;i++){
            Map<String, Object> map=new HashMap<>();
            List<Map<String,Object>> timeList=new ArrayList<>();
            if(integerListMap.containsKey(i)){
                timeList=integerListMap.get(i);
            }
            if(i==1) {
                map.put("weight","veryLow");
            }else if(i==2) {
                map.put("weight","low");
            }else if(i==3) {
                map.put("weight","mid");
            }else if(i==4) {
                map.put("weight","high");
            }else if(i==5)  {
                map.put("weight","veryHigh");
            }
            List<String> stringList = timeList.stream().map(item->item.get("triggerTime").toString()).collect(Collectors.toList());
            for(String day : dayLists){
                if(!stringList.contains(day)){
                    Map<String,Object>  elementMap=new HashMap<>();
                    elementMap.put("triggerTime",day);
                    elementMap.put("doc_count",0L);
                    timeList.add(elementMap);
                }
            }
            //鎺掑簭
            AnalysisSort alarmSort = new AnalysisSort("triggerTime");
            Collections.sort(list, alarmSort);
            map.put("triggerTime",timeList);
            resultList.add(map);
        }
        Result<List<Map<String,Object>>> result = ResultUtil.success(resultList);
        return result;
    }
    @Override
    public  Result<List<Map<String,Object>>> getStatusBar(AnalysisVO analysisVO){

        List<Map<String,Object>> resultList=new ArrayList<>();
        String stime=analysisVO.getStime();
        String etime=analysisVO.getEtime();
        String type=analysisVO.getType();
        List<QueryCondition_ES> condition = getCondition(analysisVO);

        if(stime.length()==10) {
            stime = stime + " 00:00:00";
        }
        if(etime.length()==10) {
            etime = etime + " 23:59:59";
        }
        Date startTime=null;
        Date endTime = null;
        try {
            startTime = DateUtil.parseDate(stime, DateUtil.DEFAULT_DATE_PATTERN);
            endTime = DateUtil.parseDate(etime, DateUtil.DEFAULT_DATE_PATTERN);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long interval = 0; //绮掑害
        String format = ""; //鏃堕棿鏍煎紡
        String groupBy = "triggerTime";
        DateHistogramInterval timeInterval = null;
        List<String> dayLists =new ArrayList<>();
        switch (type) {
            //鍘讳綑鐨勫師鍥犳槸锛歟s鏌ヨ鐨勯兘鏄暣鏁板紑濮�
            case "day":
                timeInterval = DateHistogramInterval.HOUR;
                format = "HH";
                dayLists = GwParamsUtil.getAllElement(0,24);
                break;
            case "week":
                timeInterval = DateHistogramInterval.DAY;
                format = "MM-dd";
                stime = stime.substring(5, 10);
                etime = etime.substring(5, 10);
                dayLists = DateUtil.getDatesBetweenDays(stime, etime,format);
                break;
            case "month":
                timeInterval = DateHistogramInterval.DAY;
                format = "MM-dd";
                stime = stime.substring(5, 10);
                etime = etime.substring(5, 10);
                dayLists = DateUtil.getDatesBetweenDays(stime, etime,format);
                break;
            case "year":
                timeInterval = DateHistogramInterval.MONTH;
                format = "MM";
                dayLists = GwParamsUtil.getAllElement(1,13);
                break;
            default:
                break;
        }
        SearchField childSearchField = new SearchField("statusEnum", FieldType.String,null );
        SearchField searchField = 	new SearchField(groupBy, FieldType.Date, format, timeInterval, childSearchField);
        List<Map<String,Object>> list = queryStatistics(condition, searchField);
        Map<Integer,List<Map<String,Object>>> integerListMap=list.stream().collect(Collectors.toMap(item->Integer.parseInt(item.get("triggerTime").toString()),item->(List<Map<String, Object>>) item.get("triggerTime")));
        for(Map.Entry<Integer,List<Map<String,Object>>> entry : integerListMap.entrySet()){
            Map<String, Object> map=new HashMap<>();
            List<Map<String,Object>> timeList=new ArrayList<>();
            timeList=entry.getValue();
            List<String> stringList = timeList.stream().map(item->item.get("statusEnum").toString()).collect(Collectors.toList());
            for(String day : dayLists){
                if(!stringList.contains(day)){
                    Map<String,Object>  elementMap=new HashMap<>();
                    elementMap.put("triggerTime",day);
                    elementMap.put("doc_count",0L);
                    timeList.add(elementMap);
                }
            }
            //鎺掑簭
            AnalysisSort alarmSort = new AnalysisSort("triggerTime");
            Collections.sort(list, alarmSort);
            map.put("triggerTime",timeList);
            resultList.add(map);
        }
        Result<List<Map<String,Object>>> result = ResultUtil.success(resultList);
        return result;

    }






    @Override
    public Result<Map<String,Object>> getMultiAlarmTrendBy7Days(List<AnalysisVO> analysisVOs) {
        Map<String,Object> map  = new HashMap<>();
        for (AnalysisVO analysisVO : analysisVOs) {
            Result<List<Map<String,Object>>> result = getAlarmTrendBy7Days(analysisVO);
            List<Map<String,Object>> data = result.getData();
            map.put(analysisVO.getRiskEventName(), data);
        }
        Result<Map<String,Object>> result = ResultUtil.success(map);
        return result;
    }



    @Override
    public Result<Map<String, Object>> getMultiEventAlarmTotal(List<AnalysisVO> analysisVOs) {
        Map<String,Object> map  = new HashMap<>();
        for (AnalysisVO analysisVO : analysisVOs) {
            Result<Long> eventAlarmTotal = eventAlarmTotal(analysisVO);
            Long data = eventAlarmTotal.getData();
            map.put(analysisVO.getRiskEventName(), data);
        }
        Result<Map<String,Object>> result = ResultUtil.success(map);
        return result;
    }



    @Override
    public Result<Map<String,Object>> getCountByRegion(List<AnalysisVO> analysisVOs) {
        Map<String,Object> map  = new HashMap<>();
        for (AnalysisVO analysisVO : analysisVOs) {
            List<QueryCondition_ES> condition = getCondition(analysisVO);
            String groupBy = "areaName";
            SearchField searchField = new SearchField(groupBy, FieldType.String, null);
            List<Map<String,Object>> list = queryStatistics(condition, searchField);
            map.put(analysisVO.getExtraField(), list);
        }
        Result<Map<String,Object>> result = ResultUtil.success(map);
        return result;
    }


    public List<Map<String,Object>>  getDstIpsData(AnalysisVO analysisVO){
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        SearchField dstIpsField = new SearchField("dstIps", FieldType.String,null);
        List<Map<String,Object>> list = queryStatistics(condition, dstIpsField);
        return list;
    }


    public List<Map<String,Object>>  getDstIpsData(AnalysisVO analysisVO,List<String> dstIps){

        if(dstIps.size()==0) {
            logger.info("dstIps闆嗗悎涓虹┖锛屽鑷存棤娉曟煡寰椾换浣曟暟鎹�");
            return new ArrayList<Map<String,Object>>();
        }
        List<QueryCondition_ES> condition = getCondition(analysisVO);

        if(dstIps.size()<=10) {
            condition.add(QueryCondition_ES.in("dstIps", dstIps));
        }


        SearchField dstIpsField = new SearchField("dstIps", FieldType.String,null);
        List<Map<String,Object>> list = queryStatistics(condition, dstIpsField);

        List<Map<String,Object>> result=new ArrayList<>();
        for(Map<String,Object> item: list) {
            Object dstIp = item.get("dstIps");
            if(dstIp!=null&&dstIps.contains(dstIp.toString())) {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * 鎸夌収dstip鍒嗙粍缁熻  缁撴灉鏈�2灞�
     * @param analysisVO 鏉′欢
     * @param type  绗簩涓垎缁勫瓧娈�
     * @return
     */
    public List<Map<String,Object>>  getDstIpsData(AnalysisVO analysisVO,String type){


        List<QueryCondition_ES> condition = getCondition(analysisVO);
        SearchField dstIpsField = new SearchField("dstIps", FieldType.String,null);

        switch (type) {
            case "weight":
                SearchField weight	=new SearchField("weight", FieldType.String, null);
                dstIpsField.setChildField(weight);
                break;
            case "ruleName":
                SearchField ruleName	=new SearchField("ruleName", FieldType.String, null);
                dstIpsField.setChildField(ruleName);
                break;
            case "areaCode":
                SearchField areaCode	=new SearchField("areaCode", FieldType.String, null);
                dstIpsField.setChildField(areaCode);
                break;
            case "srcAreaName":
                SearchField srcAreaName	=new SearchField("srcAreaName", FieldType.String, null);
                dstIpsField.setChildField(srcAreaName);
                break;
            case "dstAreaName":
                SearchField dstAreaName	=new SearchField("dstAreaName", FieldType.String, null);
                dstIpsField.setChildField(dstAreaName);
                break;
            case "triggerTime_day":
                String groupBy = "triggerTime";
                String format = "HH";
                DateHistogramInterval timeInterval =   DateHistogramInterval.HOUR;

                SearchField triggerTime_day = new SearchField(groupBy, FieldType.Date, format, timeInterval, null,0,50);
                dstIpsField.setChildField(triggerTime_day);
                break;
            case "src_ips":
                SearchField src_ips	=new SearchField("src_ips", FieldType.String, null);
                dstIpsField.setChildField(src_ips);
                break;
            default:
                break;
        }
        List<Map<String,Object>> list = queryStatistics(condition, dstIpsField);
        return list;
    }


    public List<Map<String,Object>>  getDstIpsData(AnalysisVO analysisVO,List<String> dstIps,String type){

        if(dstIps.size()==0) {
            logger.info("dstIps闆嗗悎涓虹┖锛屽鑷存棤娉曟煡寰椾换浣曟暟鎹�");
            return new ArrayList<Map<String,Object>>();
        }

        List<Map<String,Object>> list = getDstIpsData(analysisVO,type);

        logger.info("鍘熷鏁版嵁鏌ヨ鎴愬姛锛�"+(list==null?0:list.size())+"鏉�");
        //logger.info(gson.toJson(list));

        logger.info("dstIps:\r\n"+gson.toJson(dstIps));
        List<Map<String,Object>> result =  new ArrayList< >();

        for(Map<String,Object>  item : list) {
            Object dstIp = item.get("dstIps");
            if(dstIp!=null&&dstIps.contains(dstIp.toString())) {
                Object child = item.get(type);
                if(child!=null) {
                    try {
                        List<Map<String,Object>>  aggs=(List<Map<String,Object>>)child;
                        if(aggs!=null&&aggs.size()>0) {
                            for(Map<String,Object> agg:  aggs) {
                                String key = agg.get(type).toString();
                                Long value = Long.parseLong(agg.get("doc_count").toString());
                                if(key!=null&&value!=null) {
                                    Map<String,Object>  temp=null;
                                    for(Map<String,Object>  r : result) {

                                        if(r.get(type).toString().equals(key)) {
                                            temp=r;
                                            break;
                                        }
                                    }

                                    if(temp==null) {
                                        result.add(agg);
                                    }else {
                                        value+=Long.parseLong(temp.get("doc_count").toString());

                                        temp.remove("doc_count");
                                        temp.put("doc_count", value);
                                    }
                                }
                            }
                        }
                    }catch (Exception e) {
                        logger.error("鎵ц寮傚父",e);
                    }

                }
            }
        }
        return  result;
    }



    @Override
    public ResultObjVO<Map<String, Object>> getKnowledgeByTag(String ruleId) {
        Map<String,Object> map = new HashMap<>();
        RiskEventRule riskEventRule = riskEventRuleService.getOne(ruleId);
        String knowledgeTag = riskEventRule.getKnowledgeTag();
        map.put("count_", 15);
        map.put("esType", "knowledge_knowledge");
        map.put("typeId", "f269ffb221754638ac2aaca101d31c68");
        map.put("tags_or", knowledgeTag);
        ResultObjVO<Map<String,Object>> resultObjVO = knowledgeFeign.getKnowledgeByTag(map);
        return resultObjVO;
    }

    private	IndexsInfoVO getIndexsInfoVO(AnalysisVO analysisVO) {
        Date dtStart=null;
        Date dtEnd=null;

        if (!StringUtils.isEmpty(analysisVO.getStime()) && !StringUtils.isEmpty(analysisVO.getEtime())) {
            try {
                dtStart=DateUtils.parseDate(analysisVO.getStime(), "yyyy-MM-dd HH:mm:ss");
                dtEnd=DateUtils.parseDate(analysisVO.getEtime(), "yyyy-MM-dd HH:mm:ss");
            }catch (Exception e) {
                dtStart=null;
                dtEnd=null;
            }
        }

        List<String> indexs=new ArrayList<>();
        String[] allIndex = getIndexListByBaseIndexName(getIndexName());
        if (dtStart != null && dtEnd != null) {
            Calendar cale = Calendar.getInstance();
            cale.setTime(dtEnd);
            cale.set(Calendar.DAY_OF_MONTH, 1);
            cale.set(Calendar.HOUR, 0);
            cale.set(Calendar.MILLISECOND, 0);
            cale.set(Calendar.SECOND, 0);
            cale.set(Calendar.MINUTE, 0);
            Date lastMonthFirstDay = cale.getTime();
            Date nextMonthLastDay = DateUtils.addMonths(lastMonthFirstDay, 1);


            for (Date temp = dtStart; temp.before(nextMonthLastDay)
                    || temp.getTime() == dtEnd.getTime(); temp = DateUtils.addDays(temp, 1)) {
                String index = getIndexName() + DateUtil.format(temp, "-yyyy-MM-dd");
                for (String indexKey : allIndex) {
                    if (indexKey.equals(index)) {
                        indexs.add(index);
                    }
                }
            }
        }else {
            indexs.add(getIndexName()+"*");
        }

        logger.debug("鎴愬姛鍖归厤鐨勭储寮曟湁锛�"+gson.toJson(indexs));

        IndexsInfoVO indexsInfoVO=new IndexsInfoVO();

        indexsInfoVO.setIndex(indexs.toArray(new String[indexs.size()]));
        return indexsInfoVO;
    }


    private IndexsInfoVO getIndexsInfoVO(Date dtStart, Date dtEnd) {
        List<String> indexs=new ArrayList<>();
        String[] allIndex = getIndexListByBaseIndexName(getIndexName());
        if (dtStart != null && dtEnd != null) {
            Calendar cale = Calendar.getInstance();
            cale.setTime(dtEnd);
            cale.set(Calendar.DAY_OF_MONTH, 1);
            cale.set(Calendar.HOUR, 0);
            cale.set(Calendar.MILLISECOND, 0);
            cale.set(Calendar.SECOND, 0);
            cale.set(Calendar.MINUTE, 0);
            Date lastMonthFirstDay = cale.getTime();
            Date nextMonthLastDay = DateUtils.addMonths(lastMonthFirstDay, 1);


            for (Date temp = dtStart; temp.before(nextMonthLastDay)
                    || temp.getTime() == dtEnd.getTime(); temp = DateUtils.addDays(temp, 1)) {
                String index = getIndexName() + DateUtil.format(temp, "-yyyy-MM-dd");
                for (String indexKey : allIndex) {
                    if (indexKey.equals(index)) {
                        indexs.add(index);
                    }
                }
            }
        }else {
            indexs.add(getIndexName()+"*");
        }

        logger.debug("鎴愬姛鍖归厤鐨勭储寮曟湁锛�"+gson.toJson(indexs));

        IndexsInfoVO indexsInfoVO=new IndexsInfoVO();


        indexsInfoVO.setIndex(indexs.toArray(new String[indexs.size()]));
        return indexsInfoVO;
    }


    @Override
    public Result<List<Map<String, Object>>> getAttackTypeTop20(AnalysisVO analysisVO) {
        String groupBy = "riskEventName";
        SearchField searchField = new SearchField(groupBy, FieldType.String,0, 20,null);
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        return ResultUtil.success(queryStatistics);
    }



    @Override
    public Result<List<Map<String, Object>>> getAttackTrendByTimeRange(AnalysisVO analysisVO){
        String stime = analysisVO.getStime();
        String etime = analysisVO.getEtime();
        if(StringUtils.isNotEmpty(stime)&&StringUtils.isNotEmpty(etime)) {
            List<QueryCondition_ES> condition = getCondition(analysisVO);

            Date startTime = null;
            Date endTime = null;
            try {
                startTime = DateUtil.parseDate(stime, DateUtil.DEFAULT_DATE_PATTERN);
                endTime = DateUtil.parseDate(etime, DateUtil.DEFAULT_DATE_PATTERN);
            } catch (ParseException e) {
                throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"时间转换异常");
            }
            long interval = (long)24*60*60*1000;
            String format = "yyyy-MM-dd";
            String groupBy = "triggerTime";
            SearchField searchField = 	new SearchField(groupBy, FieldType.Date, format, interval, null);
            List<Map<String,Object>> list = queryStatistics(condition, searchField);
            SocUtil.getTimeFullMap(startTime, endTime, interval, format, list);
            return ResultUtil.success(list);
        }else {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"查询失败");
        }
    }




    public List<Map<String, Object>> getAlarmStaticsTop20(AnalysisVO analysisVO) {
        String systemId = analysisVO.getSystemId();
        List<String> ipList = baseSysinfoService.getIpList(systemId);
        logger.info("鏍规嵁搴旂敤绯荤粺id鑾峰彇鍒扮殑IP闆嗗悎锛�" + ArrayUtil.join(ipList.toArray(), ","));
        List<QueryCondition_ES> conditions = getCondition(analysisVO);
        if(ipList.size()!=0){
            conditions.add(QueryCondition_ES.in("dstIps", ipList));
        }
        long count = count(conditions);
        Map<String,Object> map = new HashMap<>();
        map.put("total", count);
        SearchField	riskEventNameField= new SearchField("riskEventName", FieldType.String,0,20,null);
        List<Map<String,Object>> list = queryStatistics(conditions, riskEventNameField);
        list.add(map);
        return list;
    }



    public Result<List<Map<String, Object>>> getAttackInterruptTrendByTimeRange(AnalysisVO analysisVO) {
        String stime = analysisVO.getStime();
        String etime = analysisVO.getEtime();
        if(StringUtils.isNotEmpty(stime)&&StringUtils.isNotEmpty(etime)) {
            List<QueryCondition_ES> condition = getCondition(analysisVO);
            List<InterruptKey> findAll = interruptKeyService.findAll();
            List<QueryCondition_ES> orConditions = new ArrayList<>();
            for (InterruptKey interruptKey : findAll) {
                orConditions.add(QueryCondition_ES.like("logsInfo", interruptKey.getKeyword()));
            }
            condition.add(QueryCondition_ES.or(orConditions));
            Date startTime = null;
            Date endTime = null;
            try{
                startTime = DateUtil.parseDate(stime, DateUtil.DEFAULT_DATE_PATTERN);
                endTime = DateUtil.parseDate(etime, DateUtil.DEFAULT_DATE_PATTERN);
            }catch(ParseException e) {
                throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"鏃堕棿瑙ｆ瀽鍑虹幇閿欒锛岃妫�鏌ワ紒");
            }
            long interval = (long)24*60*60*1000;
            String format = "yyyy-MM-dd";
            String groupBy = "triggerTime";
            SearchField searchField = 	new SearchField(groupBy, FieldType.Date, format, interval, null);
            List<Map<String,Object>> list = queryStatistics(condition, searchField);
            SocUtil.getTimeFullMap(startTime, endTime, interval, format, list);
            return ResultUtil.success(list);
        }else {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"璇蜂紶瀵瑰簲鐨勬椂闂村弬鏁�!");
        }

    }


    @Override
    public List<Map<String, Object>> getStasticsByRelateField(String fieldName, Integer count) {
        SearchField searchField = new SearchField(fieldName, FieldType.String,0,count,null);
        List<QueryCondition_ES> condition = new ArrayList<>();
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        return queryStatistics;
    }

    public List<Map<String,Object>> getRealStasticsByRelateField(List<QueryCondition_ES> condition,SearchField searchField){
        if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
            List<String> set = SessionUtil.getUserDomainCodes();
            logger.info("domain鍊间负锛�"+set);
            if(set!=null&&set.size()!=0){
                condition.add(QueryCondition_ES.in("dstAreaCode", set));
            }else {
                return new ArrayList<>();
            }
        }
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        return queryStatistics;
    }


    @Override
    public List<Map<String, Object>> getStasticsByRelateField(AnalysisVO analysisVO,String fieldName, Integer count) {
        List<QueryCondition_ES> condition=getCondition(analysisVO);
        SearchField searchField = new SearchField(fieldName, FieldType.String,0,count,null);
        List<Map<String,Object>> queryStatistics = queryStatistics(condition, searchField);
        return queryStatistics;
    }



    /**
     * 宸ヤ綔鍙板憡璀﹀缃缃�
     * @param analysisVO
     * @return
     */
    @Override
    public Result<Map<String,Object>> eventAlarmStatusTotalByWorkBench(AnalysisVO analysisVO) {
        Map<String,Object> map = new HashMap<>();
        analysisVO.setStatusEnum(AlarmStatusConstant.WAIT_STATUS);
        List<QueryCondition_ES> waitConditions = getCondition(analysisVO);
        long waitCount = count(waitConditions);
        analysisVO.setStatusEnum(AlarmStatusConstant.COMPLETE_STATUS);
        List<QueryCondition_ES> completeConditions = getCondition(analysisVO);
        long completeCount = count(completeConditions);
        analysisVO.setStatusEnum(AlarmStatusConstant.DEALING_STATUS);
        List<QueryCondition_ES> dealingConditions = getCondition(analysisVO);
        long dealingCount = count(dealingConditions);
        map.put("waitCount", waitCount);
        map.put("completeCount", completeCount);
        map.put("dealingCount", dealingCount);

        Result<Map<String,Object>> result = ResultUtil.success(map);
        return result;
    }


    //TODO 涓存椂鏂规硶
    public void setRiskEventRuleService(RiskEventRuleService riskEventRuleService){
        this.riskEventRuleService = riskEventRuleService;
    }

    /**
     * 杈撳嚭鍛婅excel
     */
    @Override
    public  Result<String> exportAlarmDeal(Map<String,Object> map){
        List<WarnResultLogTmpVO> warnResultLogTmpVOList=getListForExcel(map);
        if(warnResultLogTmpVOList==null){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"鍛婅鎬绘潯鏁拌秴杩�60000鏉�");
        }
        try {
            DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSSSSSSS");
            String now=format.format(new Date());
            String fileName="alarmdeal_"+now+".xlsx";
            String filePath=fileConfiguration.getFilePath()+ File.separator +fileName;
            ExcelUtils.getInstance().exportObjects2Excel(warnResultLogTmpVOList, WarnResultLogExcelVO.class,true,filePath);
            return ResultUtil.success(fileName);
        } catch (IOException e) {
            logger.error("瀵煎嚭鍛婅excel闂:"+e.getMessage());
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"e.getMessage()");
        }
    }

    private  List<WarnResultLogTmpVO> getListForExcel(Map<String,Object> map){
        List<QueryCondition_ES> conditionEs=new ArrayList<>();
        Boolean isAll=(Boolean)map.get("isAll");
        List<WarnResultLogTmpVO> warnResultLogTmpVOList=new ArrayList<>();
        if(isAll==null||!isAll){
            List<String> guids=(ArrayList) map.get("guids");
            for(String guid : guids){
                WarnResultLogTmpVO warnResultLogTmpVO=getAlarmById(guid);
                warnResultLogTmpVOList.add(warnResultLogTmpVO);
            }
        }else{
            //鏉冮檺娣诲姞
            if(SessionUtil.getauthorityType()&&SessionUtil.getCurrentUser()!=null){
                List<String> set = SessionUtil.getUserDomainCodes();
                logger.info("domain鍊间负锛�"+set);
                if(set!=null&&set.size()!=0){
                    conditionEs.add(QueryCondition_ES.in("dstAreaCode", set));
                }
            }
            long count=count(conditionEs);
            if(count>=60000L){
                return null;
            }else{
                warnResultLogTmpVOList=findAll(conditionEs);
            }
        }
        return  warnResultLogTmpVOList;
    }

    /**
     * 鍘熷鏃ュ織鍒嗙粍缁熻
     * @param guid
     * @return
     */
    @Override
    public  List<Map<String,Object>> getAlarmLogByTime(String guid){
        List<Map<String,Object>> resultList=new ArrayList<>();
        WarnResultLogTmpVO warnResultLogTmpVO=getAlarmById(guid);
        Map<String,String[]> idRoom=warnResultLogTmpVO.getIdRoom();
        String timeRoom=warnResultLogTmpVO.getTimeRoom();
        if(idRoom==null){
            getAlarmLogGroupByTimeOneVersion(resultList, warnResultLogTmpVO);
            Integer repeatCount=warnResultLogTmpVO.getRepeatCount();
            resultList.forEach(item->{
                List<Map<String, Object>> list=(List)item.get("timeMap");
                list.forEach(obj->{
                    obj.put("doc_count",repeatCount+1);
                });
            });
        }else{
            for(Map.Entry<String, String[]> entry :idRoom.entrySet()){
                getAlarmLogGroupByTimeTwoVersion(resultList,entry);
            }
        }
        if(StringUtils.isNotEmpty(timeRoom)){

        }
        return resultList;
    }

    /**
     * 鍛婅2.0鍘熷鏃ュ織鍒嗙粍
     * @param resultList
     * @param entry
     */
    private void getAlarmLogGroupByTimeTwoVersion(List<Map<String, Object>> resultList, Map.Entry<String, String[]> entry) {
        String eventTableName=entry.getKey();
        EventTable eventTable=eventTabelService.getEventTableByName(eventTableName);
        String baseIndex=eventTable.getIndexName();
        String[] ids=entry.getValue();
        String[] baseIndexNames = getIndexListByBaseIndexName(baseIndex);
        if(baseIndexNames.length>0){
            IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
            indexsInfoVO.setIndex(baseIndexNames);
            indexsInfoVO.setType(new String[] {"logs"});
            List<QueryCondition_ES> indexCondition = new ArrayList<>();
            List<String> asList = Arrays.asList(ids);
            indexCondition.add(QueryCondition_ES.in("guid", asList));
            List<Map<String,Object>> list= elasticSearchMapManage.findAll(indexsInfoVO,indexCondition);
            Map<String,List<Map<String,Object>>> dataMap=list.stream().collect(Collectors.groupingBy(obj -> obj.get("event_time").toString().substring(0,10)));
            constructAlarmLogGroupByTime(resultList,eventTable, dataMap);
        }
    }

    /**
     * 鍛婅2.0鍘熷鏃ュ織鍒嗙粍
     * @param resultList
     * @param warnResultLogTmpVO
     */
    private void getAlarmLogGroupByTimeOneVersion(List<Map<String, Object>> resultList, WarnResultLogTmpVO warnResultLogTmpVO) {
        RiskEventRule riskEventRule=riskEventRuleService.getOne(warnResultLogTmpVO.getRuleId());
        EventTable eventTable=eventTabelService.getEventTableByName(riskEventRule.getTableName());
        Gson gson = new Gson();
        String logsInfo = warnResultLogTmpVO.getLogsInfo();
        List<Map<String,Object>> list =gson.fromJson(logsInfo, new TypeToken<List<Map<String,Object>>>() {}.getType());
        Map<String,List<Map<String,Object>>> dataMap=list.stream().collect(Collectors.groupingBy(obj -> obj.get("triggerTime").toString().split(" ")[0]));
        constructAlarmLogGroupByTime(resultList,eventTable, dataMap);
    }

    private void constructAlarmLogGroupByTime(List<Map<String, Object>> resultList,EventTable eventTable, Map<String, List<Map<String, Object>>> dataMap) {
        List<Map<String,Object>> mapList=new ArrayList<>();
        Map<String, Object> resultMap=new HashMap<>();
        for(Map.Entry<String,List<Map<String,Object>>> entry : dataMap.entrySet()){
            Map<String,Object> map=new HashMap<>();
            map.put("event_time",entry.getKey());
            map.put("doc_count",entry.getValue().size());
            mapList.add(map);
        }
        resultMap.put("type",eventTable.getLabel());
        resultMap.put("timeMap",mapList);
        resultList.add(resultMap);
    }

    @Override
    public ThreatIntelligenceVO getThreatIntelligenceVO(String guid) {
        WarnResultLogTmpVO warnResultLogTmpVO=getAlarmById(guid);
        Map<String,String[]> idRoom=warnResultLogTmpVO.getIdRoom();
        String timeRoom = warnResultLogTmpVO.getTimeRoom();
        Pattern ipPattern = Pattern.compile(IP_PATTERN);
        Pattern emailPattern= Pattern.compile(EMAIL_PATTERN);
        Pattern webSitPattern=Pattern.compile(WEB_SIT_PATTERN);
        String[] ipArr=new String[]{};
        String[] emailArr=new String[]{};
        String[] netArr=new String[]{};
        ThreatIntelligenceVO threatIntelligenceVO=new ThreatIntelligenceVO();
        String logs=warnResultLogTmpVO.getLogsInfo();
        List<Map<String,Object>> all=new ArrayList<>();
        if(idRoom!=null&&!idRoom.isEmpty()){
            for(Map.Entry<String, String[]> entry :idRoom.entrySet()){
                String key=entry.getKey();
                EventTable eventTable=eventTabelService.getEventTableByName(key);
                String[] ids=entry.getValue();
                List<Map<String,Object>> mapList=getLogMapsByIdRoom(ids,eventTable);
                all.addAll(mapList);
            }
            logs= JSON.toJSONString(all);
        }else if(StringUtils.isNotEmpty(timeRoom)){
            Map<String,Object> timeMap = gson.fromJson(timeRoom, Map.class);
            for(Map.Entry<String, Object> entry :timeMap.entrySet()){
                String key=entry.getKey();
                EventTable eventTable=eventTabelService.getEventTableByName(key);
                Object value = entry.getValue();
                String json = gson.toJson(value);
                TimeRoomVO timeRoomVO = gson.fromJson(json,TimeRoomVO.class);
                List<Map<String, Object>> mapList;
                try {
                    mapList = getLogMapsByTimeRoom(timeRoomVO, eventTable);
                    all.addAll(mapList);
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
            logs= JSON.toJSONString(all);
        }
        if(StringUtils.isNotEmpty(logs)){
            ipArr= RegUtil.matchStr(ipPattern,logs);
            emailArr= RegUtil.matchStr(emailPattern,logs);
            netArr=RegUtil.matchStr(webSitPattern,logs);
        }
        threatIntelligenceVO.setEmail(emailArr);
        threatIntelligenceVO.setIp(ipArr);
        threatIntelligenceVO.setNetSite(netArr);
        return threatIntelligenceVO;
    }

    /**
     * 杈撳嚭鍛婅excel
     */
    @Override
    public  Result<String> exportAlarmLogs(Map<String,Object> map){
        String guid=map.get("guid").toString();
        Map<String,List<Map<String,Object>>> logMap=getAlarmLogList(guid);
        if(logMap==null || logMap.size()==0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"鍘熷鏃ュ織涓虹┖");
        }
        OutputStream out = null;
        HSSFWorkbook workbook = null;
        try {
            DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSSSSSSSS");
            String now=format.format(new Date());
            String fileName="alarmLog_"+now+".xls";
            String filePath=fileConfiguration.getFilePath()+ File.separator +fileName;
            File file= new File(filePath) ;
            out = new FileOutputStream(file);//瀵煎嚭鏈湴
            workbook = new HSSFWorkbook();
            int index=0;
            for(Map.Entry<String,List<Map<String,Object>>> entry : logMap.entrySet()){
                List<EventColumn> columnList=eventColumService.getEventColumnCurr(entry.getKey());
                List<String> headers=new ArrayList<>();
                List<String> headersCh=new ArrayList<>();
                List<Map<String,Object>> value=entry.getValue();
                Map<String,Object> baseMap=value.get(0);
                for(String key :baseMap.keySet()){
                    List<EventColumn> columns=columnList.stream().filter(column -> column.getName().equals(key)).collect(Collectors.toList());
                    if(columns!=null&& columns.size()>0){
                        headers.add(columns.get(0).getLabel());
                        headersCh.add(columns.get(0).getName());
                    }
                }
                List<List<Object>> allValue=new ArrayList<>();
                int size=headers.size();
                for(Map<String, Object> map1 :entry.getValue()){
                    List<Object> list=Arrays.asList(new Object[size]);
                    for(Map.Entry<String,Object> entry1: map1.entrySet()){
                        Integer i=headersCh.indexOf(entry1.getKey());
                        if(i!=-1){
                            list.set(i,entry1.getValue());
                        }
                    }
                    allValue.add(list);
                }
                EventTable eventTable=eventTabelService.getOne(entry.getKey());
                ExcelUtil.exportExcel(workbook, index, eventTable.getLabel(), headers, allValue, out);
                index++;
            }
            workbook.write(out);
            return ResultUtil.success(fileName);
        } catch (Exception e) {
            logger.error("瀵煎嚭鍛婅excel闂:"+e.getMessage());
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),e.getMessage());
        }finally {
            try {
                if(out!=null) {
                    out.close();
                }
                if(workbook!=null) {
                    workbook.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Map<String, Object>> getAlarmLogMaps(String[] logIds, String indexName) {

        String[] baseIndexNames = getIndexListByBaseIndexName(indexName);
        IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
        indexsInfoVO.setIndex(baseIndexNames);
        indexsInfoVO.setType(new String[] {"logs"});
        List<QueryCondition_ES> indexCondition = new ArrayList<>();
        List<String> asList = Arrays.asList(logIds);
        indexCondition.add(QueryCondition_ES.in("guid", asList));
        return elasticSearchMapManage.findAll(indexsInfoVO, indexCondition);
    }


    private Map<String,List<Map<String,Object>>> getAlarmLogList(String guid){
        WarnResultLogTmpVO warnResultLogTmpVO=getAlarmById(guid);
        Map<String,String[]> idRoom=warnResultLogTmpVO.getIdRoom();
        Map<String,List<Map<String,Object>>> resultMap=new HashMap<>();
        List<Map<String,Object>> list=new ArrayList<>();
        String ruleId=warnResultLogTmpVO.getRuleId();
        RiskEventRule riskEventRule=riskEventRuleService.getOne(ruleId);
        if(idRoom!=null){
            for(Map.Entry<String,String[]> entry :idRoom.entrySet()){
                String key=entry.getKey();
                EventTable eventTable=eventTabelService.getEventTableByName(key);
                String[] guids=entry.getValue();
                List<Map<String, Object>> logs=getAlarmLogMaps(guids,eventTable.getIndexName());
                if(logs!=null){
                    resultMap.put(eventTable.getId(),logs);
                }
            }
        }else{
            String logsInfo=warnResultLogTmpVO.getLogsInfo();
            list = gson.fromJson(logsInfo, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            if(list!=null&&list.size()>0){
                String tableName = riskEventRule.getTableName();
                EventTable eventTable=eventTabelService.getEventTableByName(tableName);
                resultMap.put(eventTable.getId(),list);
            }
        }
        return resultMap;
    }


    @Override
    public int getSrcIpSum(AnalysisVO analysisVO){
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        SearchField dstIpsField = new SearchField("src_ips", FieldType.String,null);
        List<Map<String,Object>> list = queryStatistics(condition, dstIpsField);

        return list.size();
    }


    @Override
    public int getDstIpSum(AnalysisVO analysisVO){
        List<QueryCondition_ES> condition = getCondition(analysisVO);
        SearchField dstIpsField = new SearchField("dstIps", FieldType.String,null);
        List<Map<String,Object>> list = queryStatistics(condition, dstIpsField);

        return list.size();
    }

}
