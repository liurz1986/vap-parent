package com.vrv.vap.admin.common.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.google.gson.Gson;
import com.vrv.vap.admin.common.manager.ESClient;
import com.vrv.vap.admin.mapper.OfflineTimeStatisticsMapper;
import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.model.OfflineTimeStatistics;
import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.model.TerminalLoginLog;
import com.vrv.vap.admin.service.BasePersonZjgService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.vo.OfflineTimeStatisticsInfo;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 同步主审终端登录日志任务
 */
@Component
public class SyncTerminalLoginLogTask {

    private static final Gson gson = new Gson();

    private static final Logger logger = LoggerFactory.getLogger(SyncTerminalLoginLogTask.class);

    // 离线登录计算最大天数
    private static final String OFFLINE_LOGIN_DEFAULT_DAYS = "offline_login_default_days";

    // 离线登录同步时间
    private static final String OFFLINE_LOGIN_SYNC_TIME = "offline_login_sync_time";

    @Autowired
    private BasePersonZjgService basePersonZjgService;

    @Resource
    private OfflineTimeStatisticsMapper offlineTimeStatisticsMapper;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取间隔天数
     *
     * @return 日期列表
     */
    public List<LocalDateTime> getIntervalDays() {
        List<LocalDateTime> result = new ArrayList<>();
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(OFFLINE_LOGIN_SYNC_TIME);
        // 最后一次任务执行的最大日期
        systemConfig = systemConfigService.findOne(systemConfig);
        if (systemConfig == null) {
            Integer defaultDays = getConfigValue();
            LocalDateTime now = LocalDateTime.now();
            for (int i = 1; i <= defaultDays; i++) {
                LocalDateTime localDate = now.minusDays(i);
                result.add(localDate);
            }
        } else {
            LocalDateTime lastTaskDay = DateUtil.toLocalDateTime(systemConfig.getConfTime());
            // 当前任务执行日期
            LocalDateTime lastDay = LocalDateTime.now().minusDays(1);
            // 获取间隔天数
            long intervalDays = ChronoUnit.DAYS.between(lastTaskDay, lastDay);
            if (intervalDays > 1) {
                for (long i = 0; i <= intervalDays; i++) {
                    LocalDateTime localDate = lastTaskDay.plusDays(i);
                    result.add(localDate);
                }
            } else result.add(lastDay);
        }
        return result;
    }

    /**
     * 获取配置天数
     *
     * @return
     */
    public Integer getConfigValue(){
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(OFFLINE_LOGIN_DEFAULT_DAYS);
        // 获取配置天数
        systemConfig = systemConfigService.findOne(systemConfig);
        if (systemConfig == null) {
            SystemConfig entry = new SystemConfig();
            entry.setConfId(OFFLINE_LOGIN_DEFAULT_DAYS);
            entry.setConfValue("30");
            systemConfigService.save(entry);
            return Integer.parseInt(entry.getConfValue());
        } else {
            return Integer.parseInt(systemConfig.getConfValue());
        }
    }

    /**
     * 更新同步任务最后执行时间
     */
    public void updateSyncTime() {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(OFFLINE_LOGIN_SYNC_TIME);
        systemConfig = systemConfigService.findOne(systemConfig);
        if (systemConfig == null) {
            SystemConfig entry = new SystemConfig();
            entry.setConfId(OFFLINE_LOGIN_SYNC_TIME);
            entry.setConfTime(new Date());
            systemConfigService.save(entry);
        } else {
            systemConfig.setConfTime(new Date());
            systemConfigService.updateSelective(systemConfig);
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void sync() {
        long begin = System.currentTimeMillis();
        DateTime beginTime = DateUtil.date(begin);
        logger.info("starting sync terminal login log task :" + beginTime);
        List<OfflineTimeStatisticsInfo> infos = initializeEsQueryCondition();
        for (OfflineTimeStatisticsInfo info : infos) {
            String currentTime = DateUtil.format(info.getCurrentTime(), "yyyy-MM-dd");
            String esQueryTime = DateUtil.format(info.getCurrentTime(), "yyyy.MM.dd");
            String userNo = info.getUserNo();
            List<TerminalLoginLog> morningTerminalLoginLogList = queryTerminalLoginLogByTime(currentTime + " 00:00:00",
                    currentTime + " 12:00:00", esQueryTime, currentTime, userNo, "AM");
            logger.info("terminal login task query morning data :" + gson.toJson(morningTerminalLoginLogList));
            if (IterUtil.isNotEmpty(morningTerminalLoginLogList)) {
                handlerTerminalLoginLog(morningTerminalLoginLogList);
            }
            List<TerminalLoginLog> afternoonTerminalLoginLogList = queryTerminalLoginLogByTime(currentTime + " 14:00:00",
                    currentTime + " 23:59:59", esQueryTime, currentTime, userNo, "PM");
            logger.info("terminal login task query afternoon data :" + gson.toJson(afternoonTerminalLoginLogList));
            if (IterUtil.isNotEmpty(afternoonTerminalLoginLogList)) {
                handlerTerminalLoginLog(afternoonTerminalLoginLogList);
            }
        }
        updateSyncTime();
        long end = System.currentTimeMillis();
        DateTime endTime = DateUtil.date(end);
        logger.info("ending sync terminal login log task :" + endTime);
        logger.info("sync terminal login use time :" + (end - begin) / 1000 / 60 + "分钟");
    }

    /**
     * 组装间隔天数和员工列表
     *
     * @return 间隔天数和员工列表
     */
    public List<OfflineTimeStatisticsInfo> initializeEsQueryCondition() {
        // 获取间隔天数列表
        List<LocalDateTime> intervalDays = getIntervalDays();
        // 获取人员信息列表
        List<BasePersonZjg> allPerson = getAllPerson();
        List<OfflineTimeStatisticsInfo> infos = new ArrayList<>();
        List<String> userNoList = allPerson.stream().map(BasePersonZjg::getUserNo).collect(Collectors.toList());
        for (String userNo : userNoList) {
            for (LocalDateTime intervalDay : intervalDays) {
                OfflineTimeStatisticsInfo info = new OfflineTimeStatisticsInfo();
                info.setCurrentTime(intervalDay);
                info.setUserNo(userNo);
                infos.add(info);
            }
        }
        return infos;
    }

    /**
     * 获取所有员工信息
     *
     * @return 员工信息列表
     */
    public List<BasePersonZjg> getAllPerson() {
        return basePersonZjgService.findAll();
    }

    /**
     * 根据员工编号获取员工信息
     *
     * @param userNo 员工编号
     * @return 员工信息
     */
    public BasePersonZjg getUserInfoByNo(String userNo) {
        BasePersonZjg basePersonZjg = new BasePersonZjg();
        basePersonZjg.setUserNo(userNo);
        return basePersonZjgService.findOne(basePersonZjg);
    }

    /**
     * 根据时间和员工编号查询员工终端登录日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param yesterday 前一天
     * @param userNo    用户编号
     * @param timeFlag  时间判断标识
     * @return 终端登录日志列表
     */
    public List<TerminalLoginLog> queryTerminalLoginLogByTime(String startTime, String endTime, String yesterday,
                                                              String currentTime, String userNo, String timeFlag) {
        Date start = null;
        Date end = null;
        try {
            start = com.vrv.vap.admin.common.util.DateUtil.parseDate(startTime, DatePattern.NORM_DATETIME_PATTERN);
            end = com.vrv.vap.admin.common.util.DateUtil.parseDate(endTime, DatePattern.NORM_DATETIME_PATTERN);
        } catch (ParseException e) {
            logger.error("Error parsing date: " + startTime, e);
        }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.termQuery("std_user_no", userNo));
        boolQueryBuilder.must(QueryBuilders.termQuery("op_result", "0"));
        boolQueryBuilder.must(QueryBuilders.rangeQuery("event_time").gte(start).lte(end));
        searchSourceBuilder.query(boolQueryBuilder);
        SearchRequest searchRequest = new SearchRequest(new String[]{"terminal-login-" + yesterday}, searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = ESClient.getInstance().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error getting search response", e);
        }
        assert searchResponse != null;
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        List<TerminalLoginLog> loginHits = new ArrayList<>();
        if (ArrayUtil.isEmpty(searchHits)) return null;
        for (SearchHit sh : hits) {
            String source = sh.getSourceAsString();
            TerminalLoginLog terminalLoginLog = gson.fromJson(source, TerminalLoginLog.class);
            loginHits.add(terminalLoginLog);
        }
        return fillTerminalLoginLog(loginHits, currentTime, timeFlag);
    }

    /**
     * 补全终端登录日志
     *
     * @param loginHits   终端登录日志
     * @param currentTime 当前时间
     * @param timeFlag    时间标识
     * @return 补全后的登录日志
     */
    public List<TerminalLoginLog> fillTerminalLoginLog(List<TerminalLoginLog> loginHits, String currentTime, String timeFlag) {
        Map<String, List<TerminalLoginLog>> collect = loginHits.stream().sorted(Comparator.comparing(TerminalLoginLog::getEventTime))
                .collect(Collectors.groupingBy(TerminalLoginLog::getStdUserNo));
        List<TerminalLoginLog> loginLogList = new LinkedList<>();
        if (collect.size() > 0) {
            for (Map.Entry<String, List<TerminalLoginLog>> login : collect.entrySet()) {
                LinkedList<TerminalLoginLog> linkedList = CollUtil.newLinkedList();
                linkedList.addAll(login.getValue());
                Date eventTime = null;
                for (int i = 0; i < linkedList.size(); i++) {
                    Integer opType = linkedList.get(i).getOpType();
                    TerminalLoginLog log = new TerminalLoginLog();
                    log.setStdUserNo(linkedList.get(i).getStdUserNo());
                    log.setDevIp(linkedList.get(i).getDevIp());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(linkedList.get(i).getEventTime());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    // 0~12 第一条数据不为登录，补一个登录时间为8点
                    // 14~24 第一条数据不为登录，补一个登录时间为14点
                    if (i == 0 && opType != 1) {
                        try {
                            if ("AM".equals(timeFlag)) {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 08:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            } else {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 14:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            }
                        } catch (ParseException e) {
                            logger.error("Error parsing date: " + currentTime, e);
                        }
                        log.setEventTime(eventTime);
                        log.setOpType(1);
                        linkedList.addFirst(log);
                    } else if (i == 0 && opType == 1 && (hour >= 8 || hour >= 14)) {
                        try {
                            if ("AM".equals(timeFlag) ) {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 08:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            } else {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 14:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            }
                        } catch (ParseException e) {
                            logger.error("Error parsing date: " + currentTime, e);
                        }
                        log.setEventTime(eventTime);
                        log.setOpType(2);
                        linkedList.addFirst(log);
                    } else if (opType != 2 && i == linkedList.size() - 1) {
                        try {
                            if ("AM".equals(timeFlag)) {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 12:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            } else {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 18:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            }
                        } catch (ParseException e) {
                            logger.error("Error parsing date: " + currentTime, e);
                        }
                        log.setEventTime(eventTime);
                        log.setOpType(2);
                        linkedList.addLast(log);
                        break;
                    } else if (opType == 2 && i == linkedList.size() - 1 && (hour <= 12 || hour <= 18)) {
                        try {
                            if ("AM".equals(timeFlag) ) {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 12:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            } else {
                                eventTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 18:00:00", DatePattern.NORM_DATETIME_PATTERN);
                            }
                        } catch (ParseException e) {
                            logger.error("Error parsing date: " + currentTime, e);
                        }
                        log.setEventTime(eventTime);
                        log.setOpType(2);
                        linkedList.addLast(log);
                        break;
                    }
                }
                loginLogList = linkedList;
            }
        }
        return loginLogList.stream().sorted(Comparator.comparing(TerminalLoginLog::getEventTime)).collect(Collectors.toList());
    }

    /**
     * 统计用户终端在线离线时长
     *
     * @param terminalLoginLogList 终端登录日志列表
     */
    public void handlerTerminalLoginLog(List<TerminalLoginLog> terminalLoginLogList) {
        LinkedList<OfflineTimeStatistics> offlineTimeStatistics = new LinkedList<>();
        if (terminalLoginLogList.size() > 0) {
            for (int i = 0; i < terminalLoginLogList.size(); i++) {
                TerminalLoginLog current = terminalLoginLogList.get(i);
                int index = i + 1;
                if (index == terminalLoginLogList.size()) break;
                TerminalLoginLog next = terminalLoginLogList.get(index);
                BasePersonZjg userInfo = getUserInfoByNo(String.valueOf(current.getStdUserNo()));
                // 第一条为登录，第二条为注销，统计为在线时长
                // 第一条为登录，第二条为登录，统计为在线时长
                boolean onlineResult = (Objects.equals(current.getOpType(), 1) && Objects.equals(next.getOpType(), 2))
                        || (Objects.equals(current.getOpType(), 1) && Objects.equals(next.getOpType(), 1));
                // 第一条为注销，第二条为登录，统计为离线时长
                // 第一条为注销，第二条为注销，统计为离线时长
                boolean offlineResult = (Objects.equals(current.getOpType(), 2) && Objects.equals(next.getOpType(), 1))
                        || (Objects.equals(current.getOpType(), 2) && Objects.equals(next.getOpType(), 2));
                OfflineTimeStatistics statistics = new OfflineTimeStatistics();
                statistics.setLoginTime(current.getEventTime());
                statistics.setLogoutTime(next.getEventTime());
                statistics.setDepartmentName(userInfo.getOrgName());
                statistics.setUserName(userInfo.getUserName());
                statistics.setUserNo(userInfo.getUserNo());
                statistics.setIp(current.getDevIp());
                statistics.setCurrentDay(current.getEventTime());
                int loginMinute = getMinute(statistics.getLoginTime());
                int logoutMinute = getMinute(statistics.getLogoutTime());
                if (loginMinute < 480 && logoutMinute <= 480) {
                    continue;
                } else if ((loginMinute < 480 && logoutMinute > 480) || (loginMinute < 480 && logoutMinute >= 720)) {
                    Date loginTime = null;
                    String currentTime = DateUtil.format(statistics.getLoginTime(), "yyyy-MM-dd");
                    try {
                        loginTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 08:00:00", DatePattern.NORM_DATETIME_PATTERN);
                    } catch (ParseException e) {
                        logger.error("Error parsing date: " + currentTime, e);
                    }
                    statistics.setLoginTime(loginTime);
                } else if (loginMinute < 1080 && logoutMinute > 1080) {
                    Date logoutTime = null;
                    String currentTime = DateUtil.format(statistics.getLoginTime(), "yyyy-MM-dd");
                    try {
                        logoutTime = com.vrv.vap.admin.common.util.DateUtil.parseDate(currentTime + " 18:00:00", DatePattern.NORM_DATETIME_PATTERN);
                    } catch (ParseException e) {
                        logger.error("Error parsing date: " + currentTime, e);
                    }
                    statistics.setLogoutTime(logoutTime);
                } else if (loginMinute >= 1080 && logoutMinute > 1080) {
                    continue;
                }
                long loginTime = statistics.getLoginTime().getTime() / 60000L;
                long logoutTime = statistics.getLogoutTime().getTime() / 60000L;
                long countTime = logoutTime - loginTime;
                if (countTime > 0) {
                    if (onlineResult) {
                        statistics.setCountTime((int) countTime);
                        statistics.setLoginType(1);
                        offlineTimeStatistics.add(statistics);
                    } else if (offlineResult) {
                        statistics.setCountTime((int) countTime);
                        statistics.setLoginType(2);
                        offlineTimeStatistics.add(statistics);
                    }
                }
            }
            offlineTimeStatisticsMapper.insertList(offlineTimeStatistics);
        }
    }

    public int getMinute(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
    }
}
