package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.Label;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealOverdueEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.SuperviseTaskService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.OperationLog;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementAggService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.AlarmDealUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.PageReqESUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.GuidNameVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmDealAggregationRow;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.kafka.model.EventAlarmTaskNodeMsg;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerVo;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.Role;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.FeignCache;
import com.vrv.vap.alarmdeal.frameworks.util.ExcelUtil;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.IndexsInfoVO;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.common.IpUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.lucene.search.TotalHits;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月23日 9:30
 */
@Service
public class AlarmEventManagementForESServiceImpl implements AlarmEventManagementForESService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(AlarmEventManagementForESServiceImpl.class);

    // gson格式化
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    public static final String WARN_RESULT_TMP = "alarmeventmanagement";

//    @Autowired
//    FeignCache feignCache;

    @Autowired
    SelfConcernAssetService selfConcernAssetService;

    @Autowired
    ElasticSearchRestClientService elasticSearchRestService;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ElasticSearchRestClient elasticSearchRestClient;

    @Autowired
    private SuperviseTaskService superviseTaskService;

    @Autowired
    AlarmEventManagementAggService alarmEventManagementAggService;
    @Autowired
    AppSysManagerService appSysManagerService;
    @Autowired
    AssetService assetService;

    /**
     * 功能描述
     *
     * @param query
     * @param pageReq
     * @param auth
     * @return
     */
    @Override
    public PageRes_ES<AlarmEventAttribute> getPageQueryResult(EventDetailQueryVO query, PageReq pageReq, boolean auth) {
        logger.debug("getPageQueryResult start");
        // 处理请求参数
        List<QueryCondition_ES> querys = getQueryParam(query, auth);

        // 处理查询分页参数
        PageReq_ES pageQuery = getQueryPageEsParam(pageReq);
        try {
//            logger.info("查询条件格式化" + gson.toJson(querys));
            PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, querys);
            List<AlarmEventAttribute> pageList = findByPage.getList();
            pageList.forEach(event -> {
                try {
                    autoAppendOverdueLabel(new Date(), event);
                } catch (Exception e) {
                    logger.error("执行异常", e);
                }
            });
            return findByPage;
        } catch (Exception ex) {
            logger.error("分页查询异常", ex);
            PageRes_ES<AlarmEventAttribute> findByPage = new PageRes_ES<>();
            findByPage.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            findByPage.setList(new ArrayList<>());
            findByPage.setTotal(0L);
            findByPage.setMessage(ex.getMessage());
            return findByPage;
        }
    }
    @Override
    public AlarmEventAttribute findOne(List<QueryCondition_ES> baseQueryParam) {
        PageReq_ES pageQuery = new PageReq_ES();
        pageQuery.setStart_(0);
        pageQuery.setCount_(1);
        PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, baseQueryParam);
        List<AlarmEventAttribute> list = findByPage.getList();
        if (list.size()>0){
            return list.get(0);
        }
        return null;
    }
    // 处理拼接查询入参
    private List<QueryCondition_ES> getQueryParam(EventDetailQueryVO query, boolean auth) {
        List<QueryCondition_ES> querys = getAlarmDealQuerys(query);
        if ("8888888".equals(query.getOrgTreeCode())) {
            querys.add(QueryCondition_ES.isNull("unitList.unitDepartSubCode"));
        }
        if (auth) {
            querys.addAll(this.getDataPermissions());
        }
//        logger.info("查询条件：" + gson.toJson(querys));
        return querys;
    }

    /**
     * 处理分页参数
     *
     * @param pageReq
     * @return
     */
    private PageReq_ES getQueryPageEsParam(PageReq pageReq) {
        if (Arrays.asList("eventKind", "eventName", "eventType", "alarmRiskLevel").contains(pageReq.getOrder_())) {
            pageReq.setOrder(pageReq.getOrder_());
        }
        PageReq_ES pageQuery = PageReqESUtil.getPageReq_ES(pageReq);
        if (StringUtils.isEmpty(pageQuery.getOrder_())) {
            pageQuery.setOrder_("eventCreattime");
            pageQuery.setBy_("desc");
        }
        return pageQuery;
    }

    /**
     * 设置请求参数
     *
     * @return
     */
    @Override
    public List<QueryCondition_ES> getDataPermissions() {
        List<QueryCondition_ES> querys = new ArrayList<>();
        User currentUser = SessionUtil.getCurrentUser();
//         User currentUser = new User();
//         currentUser.setId(33);
//         currentUser.setRoleCode(Arrays.asList("admin"));
        if (currentUser == null) {
            querys.add(QueryCondition_ES.eq("eventId", "@#$%&^*(%"));
            logger.debug("用户未登录");
            return querys;
        } else {
            List<String> roleCode = currentUser.getRoleCode();

            if (roleCode == null || roleCode.isEmpty()) {
                logger.debug("用户角色信息为空");
                querys.add(QueryCondition_ES.eq("eventId", "@#$%&^*(%"));
                return querys;
            }

            // 3 系统管理员 syscontroller 三权之系统管理员，对系统进行运行和维护
            // 4 安全审计员 audit 三权之安全审计员，对管理员的操作日志进行管理
            // 5 安全管理员 safeter 三权之安全管理员，对系统进行安全配置、用户管理

            // 业务主管：只能看到本部分，自己处置事件相关数据
            // 运维主管：可看到单位+事件权限数据
            // 保密主管：可看全部
            if (roleCode.contains("admin")) {
                return querys;
            }

            // sysAdmin 三权之系统管理员，具有用户管理、升级维护、系统运行状态日志管理功能。
            // audAdmin 三权之安全审计员，具有系统管理员和安全保密管理员日志管理等功能。
            // secAdmin 三权之安全保密管理员，具有用户权限管理、分析模型停用授权、用户行为及安全审计员日志管理等功能。

            // 查询本单位的数据
            List<BaseKoalOrg> orgByUserId = FeignCache.orgByUserId(Integer.toString(currentUser.getId()));
            if (orgByUserId != null && !orgByUserId.isEmpty()) {
                List<BaseKoalOrg> data = orgByUserId;
                List<String> orgCodes = new ArrayList<>();
                for (BaseKoalOrg org : data) {
                    orgCodes.add(org.getCode());
                }
                if (CollectionUtils.isNotEmpty(orgCodes)) {
                    querys.add(QueryCondition_ES.or(QueryCondition_ES.in("unitList.unitIdent", orgCodes),
                            QueryCondition_ES.in("unitList.unitGeoIdent", orgCodes), QueryCondition_ES.isNull("unitList.unitDepartSubCode")));
                }
            } else {
                logger.debug("该账户找不到机构信息：" + gson.toJson(orgByUserId));
                // querys.add(QueryCondition_ES.isNull("unitInfo.unitDepartSubCode"));
            }


            // secretMgr 保密主管 查看单位保密态势，督促单位内各部门监管事件处置，处置上级监管平台下发的预警和督办任务、生成涉密网络保密监管情况报告。
            // businessMgr 业务主管 查看单位内所属部门保密态势，管理所属部门用户行为监测策略，处置所属部门用户行为异常事件，填报处置结果。
            // operationMgr 运维主管
            // 管理网络基础信息，查看单位保密态势，处置配置合规性、网络安全异常、应用服务异常、运维行为异常、跨单位互联异常等监管事件，填报处置结果。
            List<QueryCondition_ES> ors = new ArrayList<>();

            // 处置人是自己 或者自己处置过
            ors.add(QueryCondition_ES.eq("authorization.canOperateUser.guid", currentUser.getId()));
            ors.add(QueryCondition_ES.in("authorization.canOperateRole.guid", roleCode));
            ors.add(QueryCondition_ES.eq("authorization.operatorRecord.userId", currentUser.getId()));
            ors.add(QueryCondition_ES.eq("urgeInfos.toUser.guid",currentUser.getId()));
            ors.add(QueryCondition_ES.eq("urgeInfos.toRole.guid",roleCode));
//            ors.add(QueryCondition_ES.eq("urgeInfos.isAuto",true));

            if (!roleCode.contains("secretMgr")) {
                if (ors.size() == 1) {
                    querys.add(ors.get(0));
                } else if (ors.size() > 1) {
                    // 大于1
                    querys.add(QueryCondition_ES.or(ors));
                }
            }
        }
        return querys;
    }

    /**
     * 处理参数（根据入参）
     *
     * @param query
     * @return
     */
    @Override
    public List<QueryCondition_ES> getAlarmDealQuerys(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = new ArrayList<>();
        if (query != null) {
            if (query.getIsRead() != null) {
                querys.add(QueryCondition_ES.eq("isRead", query.getIsRead()));
            }
            if (query.getIsSupervise() != null) {
                querys.add(QueryCondition_ES.eq("isSupervise", query.getIsSupervise()));
            }
            //0715增加是否协办筛选  全部为空 是 true 否 false
            if (query.getIsAssist() != null) {
                querys.add(QueryCondition_ES.eq("isAssist", query.getIsAssist()));
            }
            if (query.getBeginSuperviseTime() != null) {
                querys.add(QueryCondition_ES.ge("superviseTime", DateUtil.format(DateUtils.addHours(query.getBeginSuperviseTime(), +0))));
            }
            if (query.getEndSuperviseTime() != null) {
                querys.add(QueryCondition_ES.le("superviseTime", DateUtil.format(DateUtils.addHours(query.getEndSuperviseTime(), +0))));
            }

            if (query.getIsUrge() != null) {
                querys.add(QueryCondition_ES.eq("isUrge", query.getIsUrge()));
            }

            if (query.getBeginUrgeTime() != null) {
                querys.add(QueryCondition_ES.ge("urgeInfos.urgeTime", DateUtil.format(DateUtils.addHours(query.getBeginUrgeTime(), +0))));
            }
            if (query.getEndUrgeTime() != null) {
                querys.add(QueryCondition_ES.le("urgeInfos.urgeTime", DateUtil.format(DateUtils.addHours(query.getEndUrgeTime(), +0))));
            }

            if (query.getAlarmDealState() != null) {
                querys.add(QueryCondition_ES.eq("alarmDealState", query.getAlarmDealState()));
            }
            if (!StringUtils.isEmpty(query.getDeviceIp())) {
                //deviceCount
                querys.add(QueryCondition_ES.gt("deviceCount", 0));
                querys.add(QueryCondition_ES.like("deviceInfos.deviceIp", query.getDeviceIp().trim().replaceAll("\t", "")));
            }

            if (!StringUtils.isEmpty(query.getPrincipalIp())) {
                querys.add(QueryCondition_ES.like("principalIp", query.getPrincipalIp().trim().replaceAll("\t", "")));
            }
            if (query.getAppId() != null) {
                List<String> strings=new ArrayList<>();
                AppSysManagerVo appSysManagerVo = appSysManagerService.queryOne(query.getAppId());
                if (appSysManagerVo != null && StringUtils.isNotBlank(appSysManagerVo.getServiceId())) {
                    List<QueryCondition> queryConditions = new ArrayList<>();
                    queryConditions.add(QueryCondition.in("guid", Arrays.asList(appSysManagerVo.getServiceId().split(","))));
                    List<Asset> assetServiceAll = assetService.findAll(queryConditions);
                    strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                }
                querys.add(QueryCondition_ES.in("principalIp", strings));
            }
            if (StringUtils.isNotBlank(query.getRangIp())){
                String[] split = query.getRangIp().split(",");
                List<QueryCondition_ES> queryConditions = new ArrayList<>();
                if (split.length>0){
                    for (String s:split){
                        String[] split1 = s.split("-");
                        Long starNum  = IpUtil.ip2int(split1[0]);
                        Long endNum  = IpUtil.ip2int(split1[1]);
                        queryConditions.add(QueryCondition_ES.between("principalIpNum",starNum,endNum));
                    }
                }
                querys.add(QueryCondition_ES.or(queryConditions));
            }
            if (query.getEventLevel() != null) {
                querys.add(QueryCondition_ES.eq("alarmRiskLevel", query.getEventLevel()));
            }

            if (query.getTimeLimitNum() != null) {
                if (query.getTimeLimitNum() == -1) {
                    querys.add(QueryCondition_ES.notEq("alarmDealState", 3));
                }
                querys.add(QueryCondition_ES.le("validityDate", DateUtil.format(DateUtils.addSeconds(DateUtils.addHours(new Date(), +0), query.getTimeLimitNum()))));
            }

            if (!StringUtils.isEmpty(query.getUserName())) {
                querys.add(QueryCondition_ES.gt("staffNum", 0));
                querys.add(QueryCondition_ES.like("relatedStaffInfos.staffName", query.getUserName().trim().replaceAll("\t", "")));
            }

            if (!StringUtils.isEmpty(query.getUserCode())) {
                querys.add(QueryCondition_ES.gt("staffNum", 0));
                querys.add(QueryCondition_ES.like("relatedStaffInfos.staffNo", query.getUserCode()));
            }

            if (!StringUtils.isEmpty(query.getApplicationId())) {
                querys.add(QueryCondition_ES.gt("deviceAppCount", 0));
                querys.add(QueryCondition_ES.like("applicationInfos.applicationId", query.getApplicationId()));
            }
            //label
            if (query.getLabels() != null && !query.getLabels().isEmpty()) {
                List<QueryCondition_ES> ors = new ArrayList<>();
                for (String label : query.getLabels()) {
                    AlarmDealOverdueEnum alarmDealOverdueEnum = AlarmDealOverdueEnum.getAlarmDealOverdueEnum(label);
                    if (alarmDealOverdueEnum != null) {
                        ors.add(QueryCondition_ES.and(QueryCondition_ES.ge("validityDate", DateUtil.format(DateUtils.addSeconds(new Date(), alarmDealOverdueEnum.getDifferenceStart()))), QueryCondition_ES.le("validityDate", DateUtil.format(DateUtils.addSeconds(new Date(), alarmDealOverdueEnum.getDifferenceEnd())))));
                    }
                }
                ors.add(QueryCondition_ES.in("labels.title", query.getLabels()));
                querys.add(QueryCondition_ES.or(ors));

            }

            if (!StringUtils.isEmpty(query.getOrgCode())) {
                querys.add(QueryCondition_ES.like("unitList.unitIdent", query.getOrgCode()));
            }
            if (!StringUtils.isEmpty(query.getDeptCode())) {
                querys.add(QueryCondition_ES.like("unitList.unitGeoIdent", query.getDeptCode()));
            }

            if (!StringUtils.isEmpty(query.getDeptName())) {
                querys.add(QueryCondition_ES.like("unitList.unitDepartName", query.getDeptName().trim().replaceAll("\t", "")));
            }

            if (query.getBeginTime() != null) {
                querys.add(QueryCondition_ES.ge("eventCreattime", DateUtil.format(DateUtils.addHours(query.getBeginTime(), +0))));
            }
            if (query.getEndTime() != null) {
                querys.add(QueryCondition_ES.le("eventCreattime", DateUtil.format(DateUtils.addHours(query.getEndTime(), +0))));
            }

            if (StringUtils.isNotEmpty(query.getEventName())) {
                querys.add(QueryCondition_ES.like("eventName", query.getEventName()));
            }

            if (StringUtils.isNotEmpty(query.getDeviceType())) {
                querys.add(QueryCondition_ES.eq("deviceInfos.deviceType", query.getDeviceType()));
            }

            if (query.getEventType() != null) {

                querys.add(QueryCondition_ES.eq("eventType", query.getEventType()));
            }

            if (query.getIsJustAssetOfConcern() != null && Boolean.TRUE.equals(query.getIsJustAssetOfConcern())) {
                List<String> ipsOfConcern = getIpsOfConcern();
                querys.add(QueryCondition_ES.in("principalIp", ipsOfConcern));
            }

            if (StringUtils.isNotEmpty(query.getKeyWordAgg())) {
                querys.add(QueryCondition_ES.or(QueryCondition_ES.like("eventName", query.getKeyWordAgg())
                        , QueryCondition_ES.like("deviceInfos.deviceIp", query.getKeyWordAgg())
                        , QueryCondition_ES.like("labels.title", query.getKeyWordAgg())));
            }
            //事件名称、责任人、部门
            if (StringUtils.isNotEmpty(query.getKeyWordDetail())) {
                querys.add(QueryCondition_ES.or(QueryCondition_ES.like("eventName", query.getKeyWordDetail())
                        , QueryCondition_ES.like("relatedStaffInfos.staffName", query.getKeyWordDetail())
                        , QueryCondition_ES.like("unitList.unitDepartName", query.getKeyWordDetail())));
            }

            if (StringUtils.isNotEmpty(query.getDstIp())) {
                querys.add(QueryCondition_ES.eq("dstIps", query.getDstIp()));
            }
            if (StringUtils.isNotEmpty(query.getSrcIp())) {
                querys.add(QueryCondition_ES.eq("srcIps", query.getSrcIp()));
            }
            if (StringUtils.isNotEmpty(query.getOrgTreeCode()) && !"8888888".equals(query.getOrgTreeCode())) {
                querys.add(QueryCondition_ES.likeBegin("unitList.unitDepartSubCode", query.getOrgTreeCode()));
            }

            if (query.getIsDealt() != null) {
                if (Boolean.TRUE.equals(query.getIsDealt())) {
                    querys.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode().intValue()));
                } else {
                    querys.add(QueryCondition_ES.notEq("srcIps", AlarmDealStateEnum.PROCESSED.getCode().intValue()));
                }
            }
            if (StringUtils.isNotEmpty(query.getEventCodeBeginLike())) {
                querys.add(QueryCondition_ES.likeBegin("eventCode", query.getEventCodeBeginLike()));
            }
            // 自查自评关联事件详情的情况 2023-09
            if (CollectionUtils.isNotEmpty(query.getEventIds())) {
                querys.add(QueryCondition_ES.in("eventId", query.getEventIds()));
            }
        }
        return querys;
    }

    /**
     * 逾期/小于30分钟/小于4小时/小于1天
     *
     * @param now   现在时间
     * @param event 告警事件
     */
    @Override
    public void autoAppendOverdueLabel(Date now, AlarmEventAttribute event) {
        if (event.getValidityDate() != null && !AlarmDealStateEnum.PROCESSED.getCode().equals(event.getAlarmDealState())) {
            List<Label> labels = event.getLabels();
            if (labels == null) {
                labels = new ArrayList<>();
            }
            if (now.after(event.getValidityDate())) {
                // 增加逾期标签
                labels.add(new Label(AlarmDealOverdueEnum.OVERDUE.getTitle(), "#ccc",
                        "已在" + DateUtil.format(event.getValidityDate(), "yyyy-MM-dd HH:mm:ss") + "时逾期"));
            } else if (DateUtils.addMinutes(now, 30).after(event.getValidityDate())) {
                // 增加 小于30分钟 标签
                labels.add(new Label(AlarmDealOverdueEnum.MIN30.getTitle(), "#ccc",
                        "将在" + DateUtil.format(event.getValidityDate(), "yyyy-MM-dd HH:mm:ss") + "时逾期"));
            } else if (DateUtils.addHours(now, 4).after(event.getValidityDate())) {
                // 增加 小于30分钟 标签
                labels.add(new Label(AlarmDealOverdueEnum.H4.getTitle(), "#ccc",
                        "将在" + DateUtil.format(event.getValidityDate(), "yyyy-MM-dd HH:mm:ss") + "时逾期"));
            } else if (DateUtils.addDays(now, 1).after(event.getValidityDate())) {
                // 增加 小于30分钟 标签
                labels.add(new Label(AlarmDealOverdueEnum.DAY1.getTitle(), "#ccc",
                        "将在" + DateUtil.format(event.getValidityDate(), "yyyy-MM-dd HH:mm:ss") + "时逾期"));
            }
            event.setLabels(labels);
        }
    }

    // 获取我关注的
    @Override
    public List<String> getIpsOfConcern() {
        // 获取session登录信息
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }

        // 查询 我关注资产
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("userId", currentUser.getId()));
        conditions.add(QueryCondition.eq("type",0));
        List<SelfConcernAsset> findAll = selfConcernAssetService.findAll(conditions);
        List<String> concerns = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(findAll)) {
            concerns = findAll.stream().map(SelfConcernAsset::getIp).collect(Collectors.toList());
        }
        return concerns;
    }

    @Override
    public List<AlarmEventAttribute> findAll(List<QueryCondition_ES> request) {
        return elasticSearchRestService.findAll(request);
    }

    @Override
    public List<NameValue> getStatisticsByStringField(List<QueryCondition_ES> querys, int top, String groupByName) {
        if (top == 0) {
            top = 1000;
        }
        List<NameValue> result = new ArrayList<>();
        SearchField searchField = new SearchField(groupByName, FieldType.String, 0, top, null);
        List<Map<String, Object>> queryStatistics = elasticSearchRestService.queryStatistics(querys, searchField);
        logger.info("getStatisticsByStringField" + gson.toJson(queryStatistics));
        queryStatistics.forEach(map -> {
            result.add(new NameValue(map.get("doc_count").toString(), map.get(groupByName).toString()));
        });
        logger.info("getStatisticsByStringField queryStatistics" + gson.toJson(result));
        return result;
    }

    @Override
    public long count(List<QueryCondition_ES> requests) {
        return elasticSearchRestService.count(requests);
    }

    @Override
    public List<QueryCondition_ES> getQuerys(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = new ArrayList<>();
        if (query != null) {
            if (query.getIsRead() != null) {
                querys.add(QueryCondition_ES.eq("isRead", query.getIsRead()));
            }

            if (query.getIsSupervise() != null) {
                querys.add(QueryCondition_ES.eq("isSupervise", query.getIsSupervise()));
            }

            if (query.getBeginSuperviseTime() != null) {
                querys.add(QueryCondition_ES.ge("superviseTime", DateUtil.format(DateUtils.addHours(query.getBeginSuperviseTime(), +0))));
            }
            if (query.getEndSuperviseTime() != null) {
                querys.add(QueryCondition_ES.le("superviseTime", DateUtil.format(DateUtils.addHours(query.getEndSuperviseTime(), +0))));
            }

            if (query.getIsUrge() != null) {
                querys.add(QueryCondition_ES.eq("isUrge", query.getIsUrge()));
            }

            if (query.getBeginUrgeTime() != null) {
                querys.add(QueryCondition_ES.ge("urgeInfos.urgeTime", DateUtil.format(DateUtils.addHours(query.getBeginUrgeTime(), +0))));
            }
            if (query.getEndUrgeTime() != null) {
                querys.add(QueryCondition_ES.le("urgeInfos.urgeTime", DateUtil.format(DateUtils.addHours(query.getEndUrgeTime(), +0))));
            }

            if (query.getAlarmDealState() != null) {
                querys.add(QueryCondition_ES.eq("alarmDealState", query.getAlarmDealState()));
            }
            if (!StringUtils.isEmpty(query.getDeviceIp())) {

                //deviceCount
                querys.add(QueryCondition_ES.gt("deviceCount", 0));

                querys.add(QueryCondition_ES.like("deviceInfos.deviceIp", query.getDeviceIp().trim().replaceAll("\t", "")));
            }

            if (!StringUtils.isEmpty(query.getPrincipalIp())) {
                querys.add(QueryCondition_ES.like("principalIp", query.getPrincipalIp().trim().replaceAll("\t", "")));
            }
            if (query.getAppId() != null) {
                List<String> strings=new ArrayList<>();
                AppSysManagerVo appSysManagerVo = appSysManagerService.queryOne(query.getAppId());
                if (appSysManagerVo != null && StringUtils.isNotBlank(appSysManagerVo.getServiceId())) {
                    List<QueryCondition> queryConditions = new ArrayList<>();
                    queryConditions.add(QueryCondition.in("guid", Arrays.asList(appSysManagerVo.getServiceId().split(","))));
                    List<Asset> assetServiceAll = assetService.findAll(queryConditions);
                    strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                }
                querys.add(QueryCondition_ES.in("principalIp", strings));
            }
            if (StringUtils.isNotBlank(query.getRangIp())){
                String[] split = query.getRangIp().split(",");
                List<QueryCondition_ES> queryConditions = new ArrayList<>();
                if (split.length>0){
                    for (String s:split){
                        String[] split1 = s.split("-");
                        Long starNum  = IpUtil.ip2int(split1[0]);
                        Long endNum  = IpUtil.ip2int(split1[1]);
                        queryConditions.add(QueryCondition_ES.between("principalIpNum",starNum,endNum));
                    }
                }
                querys.add(QueryCondition_ES.or(queryConditions));
            }
            if (query.getEventLevel() != null) {
                querys.add(QueryCondition_ES.eq("alarmRiskLevel", query.getEventLevel()));
            }

            if (query.getTimeLimitNum() != null) {
                querys.add(QueryCondition_ES.le("validityDate", DateUtil.format(DateUtils.addSeconds(DateUtils.addHours(new Date(), +0), query.getTimeLimitNum()))));
            }

            if (!StringUtils.isEmpty(query.getUserName())) {
                querys.add(QueryCondition_ES.gt("staffNum", 0));

                querys.add(QueryCondition_ES.like("staffInfos.staffName", query.getUserName().trim().replaceAll("\t", "")));
            }

            if (!StringUtils.isEmpty(query.getUserCode())) {
                querys.add(QueryCondition_ES.gt("staffNum", 0));

                querys.add(QueryCondition_ES.like("staffInfos.staffNo", query.getUserCode()));
            }

            if (!StringUtils.isEmpty(query.getApplicationId())) {
                querys.add(QueryCondition_ES.gt("deviceAppCount", 0));

                querys.add(QueryCondition_ES.like("applicationInfos.applicationId", query.getApplicationId()));
            }

            //label

            if (query.getLabels() != null && !query.getLabels().isEmpty()) {

                List<QueryCondition_ES> ors = new ArrayList<>();
                for (String label : query.getLabels()) {
                    AlarmDealOverdueEnum alarmDealOverdueEnum = AlarmDealOverdueEnum.getAlarmDealOverdueEnum(label);
                    if (alarmDealOverdueEnum != null) {
                        ors.add(QueryCondition_ES.and(QueryCondition_ES.ge("validityDate", DateUtil.format(DateUtils.addSeconds(new Date(), alarmDealOverdueEnum.getDifferenceStart()))), QueryCondition_ES.le("validityDate", DateUtil.format(DateUtils.addSeconds(new Date(), alarmDealOverdueEnum.getDifferenceEnd())))));
                    }
                }

                ors.add(QueryCondition_ES.in("labels.title", query.getLabels()));

                querys.add(QueryCondition_ES.or(ors));

            }

            if (!StringUtils.isEmpty(query.getOrgCode())) {

                querys.add(QueryCondition_ES.like("unitList.unitIdent", query.getOrgCode()));

            }
            if (!StringUtils.isEmpty(query.getDeptCode())) {
                querys.add(QueryCondition_ES.like("unitList.unitGeoIdent", query.getDeptCode()));
            }

            if (!StringUtils.isEmpty(query.getDeptName())) {
                querys.add(QueryCondition_ES.like("unitList.unitDepartName", query.getDeptName().trim().replaceAll("\t", "")));
            }

            if (query.getBeginTime() != null) {
/*				querys.add(
                        QueryCondition_ES.or(QueryCondition_ES.ge("eventCreattime", DateUtils.addHours(query.getBeginTime(), +8))
                        , QueryCondition_ES.ge("triggerTime", DateUtils.addHours(query.getBeginTime(), +8))));*/
                querys.add(QueryCondition_ES.ge("eventCreattime", DateUtil.format(DateUtils.addHours(query.getBeginTime(), +0))));
            }
            if (query.getEndTime() != null) {
/*				querys.add(
                        QueryCondition_ES.or(
                        QueryCondition_ES.lt("eventCreattime", DateUtils.addHours(query.getEndTime(),+8)),
                        QueryCondition_ES.lt("triggerTime", DateUtils.addHours(query.getEndTime(),+8))
                                ));*/

                querys.add(QueryCondition_ES.le("eventCreattime", DateUtil.format(DateUtils.addHours(query.getEndTime(), +0))));
            }

            if (StringUtils.isNotEmpty(query.getEventName())) {
                querys.add(QueryCondition_ES.like("eventName", query.getEventName()));
            }

            if (StringUtils.isNotEmpty(query.getDeviceType())) {
                querys.add(QueryCondition_ES.eq("deviceInfos.deviceType", query.getDeviceType()));
            }

            if (query.getEventType() != null) {

                querys.add(QueryCondition_ES.eq("eventType", query.getEventType()));
            }

            if (query.getIsJustAssetOfConcern() != null && Boolean.TRUE.equals(query.getIsJustAssetOfConcern())) {
                List<String> ipsOfConcern = getIpsOfConcern();
                // querys.add(QueryCondition_ES.in("deviceInfos.deviceIp", ipsOfConcern));
                querys.add(QueryCondition_ES.in("principalIp", ipsOfConcern));
            }

            if (StringUtils.isNotEmpty(query.getKeyWordAgg())) {
                querys.add(QueryCondition_ES.or(QueryCondition_ES.like("eventName", query.getKeyWordAgg())
                        , QueryCondition_ES.like("deviceInfos.deviceIp", query.getKeyWordAgg())
                        , QueryCondition_ES.like("labels.title", query.getKeyWordAgg())));
            }
            //事件名称、责任人、部门
            if (StringUtils.isNotEmpty(query.getKeyWordDetail())) {
                querys.add(QueryCondition_ES.or(QueryCondition_ES.like("eventName", query.getKeyWordDetail())
                        , QueryCondition_ES.like("staffInfos.staffName", query.getKeyWordDetail())
                        , QueryCondition_ES.like("unitList.unitDepartName", query.getKeyWordDetail())));
            }

            if (StringUtils.isNotEmpty(query.getDstIp())) {
                querys.add(QueryCondition_ES.eq("dstIps", query.getDstIp()));
            }
            if (StringUtils.isNotEmpty(query.getSrcIp())) {
                querys.add(QueryCondition_ES.eq("srcIps", query.getSrcIp()));
            }
            if (StringUtils.isNotEmpty(query.getOrgTreeCode()) && !"8888888".equals(query.getOrgTreeCode())) {

                querys.add(QueryCondition_ES.likeBegin("unitList.unitDepartSubCode", query.getOrgTreeCode()));
            }

            if (query.getIsDealt() != null) {
                if (Boolean.TRUE.equals(query.getIsDealt())) {
                    querys.add(QueryCondition_ES.eq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode().intValue()));
                } else {
                    querys.add(QueryCondition_ES.notEq("srcIps", AlarmDealStateEnum.PROCESSED.getCode().intValue()));
                }
            }

            if (StringUtils.isNotEmpty(query.getEventCodeBeginLike())) {
                querys.add(QueryCondition_ES.likeBegin("eventCode", query.getEventCodeBeginLike()));
            }

        }
        return querys;
    }

    @Override
    public List<Map<String, Object>> queryStatistics(List<QueryCondition_ES> conditions, SearchField field) {
        return elasticSearchRestService.queryStatistics(conditions, field);
    }

    @Override
    public AlarmEventAttribute getDocByEventId(String eventId) {
        return (AlarmEventAttribute) elasticSearchRestService.getDoc(eventId);
    }

    @Override
    public void saveAlarmEventData(AlarmEventAttribute doc) {
        elasticSearchRestService.save(doc);
    }

    @Override
    public void saveAlarmEventDatas(List<AlarmEventAttribute> doc) {
        doc.forEach(item -> {
            elasticSearchRestService.save(item);
        });
    }

    @Override
    public List<Map<String, Object>> getLogByEventIdAndIndexName(String indexName, List<String> eventIds) {
        // 设置查询索引
        IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
        indexsInfoVO.setIndex(new String[]{indexName});

        // 设置查询guid list
        List<QueryCondition_ES> reqs = new ArrayList<>();
        reqs.add(QueryCondition_ES.in("guid", eventIds));
        List<Map<String, Object>> results = elasticSearchRestService.findAll(indexsInfoVO, reqs);
        return results;
    }

    @Override
    public String createReportFile(EventDetailQueryVO query, String token) {
        String filePath = getFilePath(token);
        // filePath ="D:\\file.xls";
        File targetFile = new File(fileConfiguration.getFilePath());
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }

        List<QueryCondition_ES> querys = getQuerys(query);

        querys.addAll(this.getDataPermissions());
        PageReq_ES pageQuery = new PageReq_ES();
        pageQuery.setCount_(10000);
        pageQuery.setStart_(0);
        pageQuery.setOrder_(query.getOrder_());
        pageQuery.setBy_(pageQuery.getBy_());

        PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, querys);

        List<AlarmEventAttribute> pageList = findByPage.getList();

        OutputStream out = null;
        HSSFWorkbook workbook = null;
        try {

            try {
                out = new FileOutputStream(filePath);
            } catch (FileNotFoundException e) {
                logger.error("FileNotFoundException: ", e);
            }

            List<List<Object>> result = new ArrayList<>();
            List<String> headers = new ArrayList<>();
            headers.add("发生时间");
            headers.add("事件名称");
            headers.add("事件种类");
            headers.add("事件类型");
            headers.add("事件等级");
            //补充的
            headers.add("标签");
            headers.add("设备IP");
            headers.add("部门");
            headers.add("责任人");

            headers.add("处置时限");

            headers.add("处理状态");

            headers.add("是否督促");
            headers.add("是否督办");
            headers.add("是否已读");
            //补充
            headers.add("是否协办");
            String[] alarmRiskLevels = new String[]{"", "较低", "一般", "重要", "严重", "紧急"};

            String[] alarmDealStates = new String[]{"未处置", "处置中", "已挂起", "已处置"};

            String[] alarmEventKinds = new String[]{"", "标准事件", "自定义事件"};
            String[] alarmEventTypes = new String[]{"", "配置合规性事件", "网络安全异常事件", "用户行为异常事件", "运维行为异常事件", "应用服务异常事件",
                    "跨单位互联异常事件"};

            pageList.forEach(item -> {
                // AlarmEventAttribute item = event.getExtendEntityAttr();
                List<Object> row = new ArrayList<>();
                //发生时间
                row.add(DateUtil.format(item.getEventCreattime(), "yyyy-MM-dd HH:mm:ss"));
                //事件名称
                row.add(item.getEventName());
                //事件种类
                row.add(alarmEventKinds[(item.getEventType() != null && item.getEventType() <= 6 ? 1 : 2)]);
                //事件类型
                row.add(alarmEventTypes[item.getEventType()]);
                //事件等级
                row.add(alarmRiskLevels[item.getAlarmRiskLevel() == null ? 1 : item.getAlarmRiskLevel()]);
                String labels = "";
                if (item.getLabels() != null) {
                    List<String> labelTitleList = new ArrayList<>();
                    for (Label label : item.getLabels()) {
                        labelTitleList.add(label.getTitle());
                    }
                    labels = String.join("、", labelTitleList);
                }
                //标签
                row.add(labels);
                //设备ip
                row.add(item.getSrcIps());
                //部门 todo 20230714
//                row.add(item.getUnitInfo() != null ? item.getUnitInfo().getUnitDepartName() : "");
                String staffNames = "";
                if (item.getStaffInfos() != null) {
                    List<String> staffInfoNameList = new ArrayList<>();
                    for (StaffInfo staffInfo : item.getStaffInfos()) {
                        staffInfoNameList.add(staffInfo.getStaffName());
                    }
                    staffNames = String.join("、", staffInfoNameList);
                }
                //责任人
                row.add(staffNames);
                //处置时限
                row.add(item.getValidityDate() == null ? ""
                        : DateUtil.format(item.getValidityDate(), "yyyy-MM-dd HH:mm:ss"));
                //是否已经督办
                row.add(alarmDealStates[item.getAlarmDealState()]);
                row.add(Boolean.TRUE.equals(item.getIsUrge()) ? "是" : "否");
                row.add(Boolean.TRUE.equals(item.getIsSupervise()) ? "是" : "否");
                row.add(Boolean.TRUE.equals(item.getIsRead()) ? "是" : "否");
                //协办
                row.add(Boolean.TRUE.equals(item.getIsAssist()) ? "是" : "否");
                result.add(row);
            });
            // 生成Excel
            workbook = new HSSFWorkbook(); // POI生成对象
            ExcelUtil.exportExcel(workbook, 0, "事件详情", headers, result, out);

            workbook.write(out);
            // 输入文件流
        } catch (Exception e) {
            logger.error("IOException:", e);
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "文件构造异常");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger.error("IOException:", e);
                }
            }
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    logger.error("workbook.close() Exception:", e);
                }
            }
        }

        return token;
    }

    @Override
    public String getIndexName() {
        return WARN_RESULT_TMP;
    }

    @Override
    public List<AlarmDealAggregationRow> getAggDataRows(EventDetailQueryVO query) {
        PageRes<AlarmDealAggregationRow> pageRes = alarmEventManagementAggService.getAlarmDealAggetStatisticsCountgregationPager(query);
        return pageRes.getList();
    }

    @Override
    public Map<String, Object> getDoc(String indexName, String id) {
        return elasticSearchRestClient.getDoc(indexName, id);
    }

    @Override
    public Map<String, Long> getCountGroupByField(String indexName, String fieldName, List<QueryCondition_ES> conditions) {
        Map<String, Long> result = new HashMap<>();
        // 设置分组字段
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.notNull(fieldName));
        querys.addAll(conditions);
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        result.putAll(queryEsForAgg(indexName, fieldName, queryBuilder));
        querys.clear();
        querys.add(QueryCondition_ES.isNull(fieldName));
        querys.addAll(conditions);
        QueryBuilder queryBuilder1 = ElasticSearchUtil.toQueryBuilder(querys);
        Long nums = queryEsForNull(indexName, queryBuilder1);
        if (nums != 0) {
            result.put("", nums);
        }
        return result;
    }

    public Long queryEsForNull(String indexName, QueryBuilder queryBuilder) {
        try {
            String[] indexNameArr = elasticSearchRestService.getIndexListByBaseIndexName(indexName);
            if (indexNameArr != null && indexNameArr.length != 0) {
                SearchResponse searchResponse = this.elasticSearchRestClient.getDocs(indexNameArr, queryBuilder, (SortBuilder) null, (SearchField) null, 0, 1);
                TotalHits totalHits = searchResponse.getHits().getTotalHits();
                long value = totalHits.value;
                return value;
            } else {
                return 0L;
            }
        } catch (ElasticsearchStatusException var9) {
            RestStatus status = var9.status();
            if (status.getStatus() != 404) {
                logger.error("查询出现异常", var9);
            }

            return 0L;
        } catch (Exception var10) {
            logger.error("查询出现异常", var10);
            return 0L;
        }
    }

    /**
     * @param indexName
     * @param fieldName
     * @param queryBuilder
     */

    private Map<String, Long> queryEsForAgg(String indexName, String fieldName, QueryBuilder queryBuilder) {
        Map<String, Long> result = new HashMap<>();
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field(fieldName);
        Script queryScript = AlarmDealUtil.initScript(new String[]{fieldName});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(10);
        groupByFieldAgg.subAggregation(fieldCounts);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        boolean isIndex = elasticSearchRestService.isEsIndexExist(indexName);
        if (!isIndex) {
            return result;
        }

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    String id = terms.getBuckets().get(i).getKey().toString();
                    Long sum = terms.getBuckets().get(i).getDocCount();
                    result.put(id, sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Long> getCountGroupByFields(String indexName, String fieldName, List<QueryCondition_ES> conditions) {
        Map<String, Long> result = new HashMap<>();
        // 设置分组字段
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field(fieldName);

        Script queryScript = AlarmDealUtil.initScript(new String[]{fieldName});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript)
                .size(10);
        groupByFieldAgg.subAggregation(fieldCounts);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);

        // 增加查询条件
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(10000).build();
            RequestOptions options = RequestOptions.DEFAULT.toBuilder().setRequestConfig(requestConfig).build();
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, options);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    String id = terms.getBuckets().get(i).getKey().toString();
                    Long sum = terms.getBuckets().get(i).getDocCount();
                    result.put(id, sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Map<String, Long> queryStatisticsByTime(String indexName, List<QueryCondition_ES> conditions, String fieldName, DateHistogramInterval timeInterval, String timeFormat) {
        Map<String, Long> result = new HashMap<>();
        // 设置分组字段
        String tarmsName = "GroupBy" + fieldName;
        AggregationBuilder fieldCounts = AggregationBuilders.dateHistogram(tarmsName)
                .field(fieldName).dateHistogramInterval(timeInterval).format(timeFormat);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(fieldCounts);

        // 增加查询条件
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(conditions);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        try {
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(10000).build();
            RequestOptions options = RequestOptions.DEFAULT.toBuilder().setRequestConfig(requestConfig).build();
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, options);
            Histogram terms = searchResponse.getAggregations().get(tarmsName);
            if (terms.getBuckets().size() != 0) {
                List<Histogram.Bucket> buckets = (List<Histogram.Bucket>) terms.getBuckets();
                for (Histogram.Bucket bucket : buckets) {
                    result.put(bucket.getKeyAsString(), bucket.getDocCount());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void addList(String indexName, List<AlarmEventAttribute> entities) {
        elasticSearchRestService.addList(indexName, entities);
    }

    public String getFilePath(String token) {
        String fileName = token + ".xls";// 文件名称
        String filePath = Paths.get(fileConfiguration.getFilePath(), fileName).toString();
        return filePath;
    }

    @Override
    public void saveEventAlarmDealChange(EventAlarmTaskNodeMsg nodeMsg) {
        try {
            logger.debug("收到事件处理消息：" + gson.toJson(nodeMsg));
            AlarmDealStateEnum alarmDealStateEnumByCode = AlarmDealStateEnum
                    .getAlarmDealStateEnumByCode(nodeMsg.getEventAlarmStatus().intValue());
            //核心点：根据事件id来查询2022/09/06
            AlarmEventAttribute extendEntityAttr = this.getDoc(nodeMsg.getEventId());

            if (extendEntityAttr == null) {
                return;
            }

            extendEntityAttr.setAlarmDealState(nodeMsg.getEventAlarmStatus());

            AuthorizationControl authorization = extendEntityAttr.getAuthorization();
            if (authorization == null) {
                authorization = new AuthorizationControl();
            }

            List<OperationLog> operatorRecord = authorization.getOperatorRecord();
            if (operatorRecord == null) {
                operatorRecord = new ArrayList();

            }
            OperationLog log = new OperationLog();
            log.setTime(new Date());
            log.setUserId(nodeMsg.getDealedPersonId());

            log.setUserName(nodeMsg.getDealedPersonName());
            log.setRoleIds(nodeMsg.getDealedRole());

            if (alarmDealStateEnumByCode != null) {

                log.setLog("完成[" + nodeMsg.getTaskName() + "]操作，当前处于" + alarmDealStateEnumByCode.getTitle() + "状态");
            }

            List<String> canOperateUser = nodeMsg.getCanOperateUser();
            List<String> canOperateRole = nodeMsg.getCanOperateRole();

            if (canOperateUser == null || canOperateUser.isEmpty()) {
                authorization.setCanOperateUser(new ArrayList<>());
            } else {
                List<GuidNameVO> users = new ArrayList<>();
                canOperateUser.forEach(userId -> {
                    GuidNameVO vo = new GuidNameVO(userId, "");

                    try {
                        com.vrv.vap.alarmdeal.frameworks.contract.user.User userResult = FeignCache.getUserById(userId);
                        if (userResult != null) {
                            com.vrv.vap.alarmdeal.frameworks.contract.user.User data = userResult;
                            vo.setName(data.getName());
                        }
                    } catch (Exception e) {
                        logger.error("getUserById查询异常", e);
                    }
                    users.add(vo);
                });

                authorization.setCanOperateUser(users);
            }

            if (canOperateRole == null || canOperateRole.isEmpty()) {
                authorization.setCanOperateRole(new ArrayList<>());
            } else {
                List<GuidNameVO> roles = new ArrayList<>();

                canOperateRole.forEach(roleCode -> {
                    GuidNameVO vo = new GuidNameVO(roleCode, "");
                    try {
                        Map<String, Object> param = new HashMap<>();
                        param.put("code", roleCode);
                        List<Role> roleResult = FeignCache.getRoleById(param);
                        if (roleResult != null) {
                            List<Role> data = roleResult;
                            if (data != null && !data.isEmpty()) {
                                vo.setName(data.get(0).getName());
                            }
                        }
                    } catch (Exception e) {
                        logger.error("getUserById查询异常", e);
                    }

                    roles.add(vo);
                });

                authorization.setCanOperateRole(roles);
            }

            operatorRecord.add(log);
            authorization.setOperatorRecord(operatorRecord);
            extendEntityAttr.setAuthorization(authorization);
            if (StringUtils.isNotBlank(nodeMsg.getCauseAnalysis())) {
                extendEntityAttr.setCauseAnalysis(nodeMsg.getCauseAnalysis());
            }
            logger.debug(gson.toJson(extendEntityAttr));
            this.save(extendEntityAttr);

//            if (Boolean.TRUE.equals(extendEntityAttr.getIsSupervise())) {
//                if (AlarmDealStateEnum.PROCESSED.getCode().intValue() == extendEntityAttr.getAlarmDealState()
//                        .intValue()) {
//                    superviseTaskService.dealSuperviseTask(extendEntityAttr.getEventId());
//                }
//            }
        } catch (Exception e) {
            logger.error("事件处理失败：", e);
            throw e;
        }
    }

    @Override
    public void saveEventAlarmDealChange(String eventId, String analysis) {
        AlarmEventAttribute alarmEventAttribute = getDoc(eventId);
        alarmEventAttribute.setCauseAnalysis(analysis);
        save(alarmEventAttribute);
    }

    @Override
    public void save(AlarmEventAttribute doc) {
        elasticSearchRestService.save(doc);
    }

    @Override
    public AlarmEventAttribute getDoc(String eventId) {
        return (AlarmEventAttribute) elasticSearchRestService.getDoc(eventId);
    }

    @Override
    public String getBaseField() {

        return "";
    }

    @Override
    public PageRes_ES<AlarmEventAttribute> getPageQueryAbnormalResult(EventDetailQueryVO query, EventDetailQueryVO pageReq, boolean auth) {
        logger.debug("getPageQueryResult start");
        // 处理请求参数
        List<QueryCondition_ES> querys = getQueryParam(query, auth);
        querys.add(QueryCondition_ES.in("eventType", Arrays.asList(2, 3, 4, 5, 6)));
        // 处理查询分页参数
        PageReq_ES pageQuery = getQueryPageEsParam(pageReq);
        try {
            logger.info("查询条件格式化" + gson.toJson(querys));
            PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, querys);
            List<AlarmEventAttribute> pageList = findByPage.getList();
            pageList.forEach(event -> {
                try {
                    autoAppendOverdueLabel(new Date(), event);
                } catch (Exception e) {
                    logger.error("执行异常", e);
                }
            });
            return findByPage;
        } catch (Exception ex) {
            logger.error("分页查询异常", ex);
            PageRes_ES<AlarmEventAttribute> findByPage = new PageRes_ES<>();
            findByPage.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            findByPage.setList(new ArrayList<>());
            findByPage.setTotal(0L);
            findByPage.setMessage(ex.getMessage());
            return findByPage;
        }
    }

    @Override
    public PageRes_ES<AlarmEventAttribute> getPageQueryAppAbnormalResult(EventDetailQueryVO query, EventDetailQueryVO pageReq, boolean auth, List<Integer> integers) {
        logger.debug("getPageQueryResult start");
        // 处理请求参数
        List<QueryCondition_ES> querys = getQueryParam(query, auth);
        if (integers.size() > 0) {
            querys.add(QueryCondition_ES.in("eventType", integers));
        }
        // 处理查询分页参数
        PageReq_ES pageQuery = getQueryPageEsParam(pageReq);
        try {
            logger.info("查询条件格式化" + gson.toJson(querys));
            PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, querys);
            List<AlarmEventAttribute> pageList = findByPage.getList();
            pageList.forEach(event -> {
                try {
                    autoAppendOverdueLabel(new Date(), event);
                } catch (Exception e) {
                    logger.error("执行异常", e);
                }
            });
            return findByPage;
        } catch (Exception ex) {
            logger.error("分页查询异常", ex);
            PageRes_ES<AlarmEventAttribute> findByPage = new PageRes_ES<>();
            findByPage.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            findByPage.setList(new ArrayList<>());
            findByPage.setTotal(0L);
            findByPage.setMessage(ex.getMessage());
            return findByPage;
        }
    }

    @Override
    public Map<String, Long> getCountGroupNumByFieldSize(String indexName, String fieldName, List<QueryCondition_ES> conditions, int top) {
        Map<String, Long> result = new HashMap<>();
        // 设置分组字段
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.notNull(fieldName));
        querys.addAll(conditions);
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        result.putAll(queryEsForAggNumSize(indexName, fieldName, queryBuilder, top));
        return result;
    }


    private Map<String, Long> queryEsForAggNumSize(String indexName, String fieldName, QueryBuilder queryBuilder, int top) {
        Map<String, Long> result = new HashMap<>();
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field(fieldName);
        Script queryScript = AlarmDealUtil.initScript(new String[]{fieldName});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(top);
        groupByFieldAgg.subAggregation(fieldCounts);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        boolean isIndex = elasticSearchRestService.isEsIndexExist(indexName);
        if (!isIndex) {
            return result;
        }

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    String id = terms.getBuckets().get(i).getKey().toString();
                    Long sum = terms.getBuckets().get(i).getDocCount();
                    result.put(id, sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Long> getCountGroupNumByField(String indexName, String fieldName, List<QueryCondition_ES> conditions) {
        Map<String, Long> result = new HashMap<>();
        // 设置分组字段
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.notNull(fieldName));
        querys.addAll(conditions);
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        result.putAll(queryEsForAggNum(indexName, fieldName, queryBuilder));
        return result;
    }


    private Map<String, Long> queryEsForAggNum(String indexName, String fieldName, QueryBuilder queryBuilder) {
        Map<String, Long> result = new HashMap<>();
        AggregationBuilder fieldCounts = AggregationBuilders.count("count")
                .field(fieldName);
        Script queryScript = AlarmDealUtil.initScript(new String[]{fieldName});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(10000);
        groupByFieldAgg.subAggregation(fieldCounts);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);

        boolean isIndex = elasticSearchRestService.isEsIndexExist(indexName);
        if (!isIndex) {
            return result;
        }

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                for (int i = 0; i < terms.getBuckets().size(); i++) {
                    String id = terms.getBuckets().get(i).getKey().toString();
                    Long sum = terms.getBuckets().get(i).getDocCount();
                    result.put(id, sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Map<String, Map<String, Long>>> getAggIpLevelData(String indexName, String principalIp, String alarmRiskLevel, List<QueryCondition_ES> querys) {
        List<Map<String, Map<String, Long>>> list = new ArrayList<>();
        Script queryScript = AlarmDealUtil.initScript(new String[]{principalIp});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(10000);
        AggregationBuilder RiskLevel = AggregationBuilders.terms("risk_level_field")
                .field(alarmRiskLevel);
        groupByFieldAgg.subAggregation(RiskLevel);
        querys.add(QueryCondition_ES.notNull(principalIp));
        querys.add(QueryCondition_ES.notNull(alarmRiskLevel));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(indexName);
        searchRequest.source(searchSourceBuilder);
        boolean isIndex = elasticSearchRestService.isEsIndexExist(indexName);
        if (!isIndex) {
            return list;
        }
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Terms terms = searchResponse.getAggregations().get("group_by_field");
            if (terms.getBuckets().size() != 0) {
                List<? extends Terms.Bucket> buckets = terms.getBuckets();
                for (int i = 0; i < buckets.size(); i++) {
                    Map<String, Map<String, Long>> map = new HashMap<>();
                    String id = buckets.get(i).getKey().toString();
                    Aggregations aggregations = buckets.get(i).getAggregations();
                    Map<String, Aggregation> asMap = aggregations.getAsMap();
                    ParsedLongTerms risk_level_field1 = (ParsedLongTerms) asMap.get("risk_level_field");
                    List<? extends Terms.Bucket> buckets1 = risk_level_field1.getBuckets();
                    Map<String, Long> map1 = new HashMap<>();
                    for (Terms.Bucket bucket : buckets1) {
                        String key = bucket.getKey().toString();
                        long docCount = bucket.getDocCount();
                        map1.put(key, docCount);
                    }
                    map.put(id, map1);
                    list.add(map);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


}
