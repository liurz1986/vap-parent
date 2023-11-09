package com.vrv.vap.alarmdeal.business.asset.analysis.service.impl;

import com.vrv.vap.alarmdeal.business.asset.analysis.Repository.AssetAnalysisOnLineStatisticRepository;
import com.vrv.vap.alarmdeal.business.asset.analysis.Repository.AssetAnalysisTotalStatisticRepository;
import com.vrv.vap.alarmdeal.business.asset.analysis.Repository.AssetAnalysisTypeStatisticRepository;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisService;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetOnLineQueryService;
import com.vrv.vap.alarmdeal.business.asset.analysis.vo.*;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetChangeService;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  资产分析
 *
 *  2022-09-08
 */
@Service
public class AssetOnLineQueryServiceImpl implements AssetOnLineQueryService {
    private static Logger logger = LoggerFactory.getLogger(AssetOnLineQueryServiceImpl.class);
    @Autowired
    private AssetOnLineService assetOnLineService ;
    @Autowired
    private AssetAnalysisService assetAnalysisService;
    @Autowired
    private AssetChangeService assetChangeService;
    @Autowired
    private AssetAnalysisOnLineStatisticRepository assetAnalysisOnLineStatisticRepository;
    @Autowired
    private AssetAnalysisTotalStatisticRepository assetAnalysisTotalStatisticRepository;
    @Autowired
    private AssetAnalysisTypeStatisticRepository assetAnalysisTypeStatisticRepository;
    @Autowired
    private MapperUtil mapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final String WEEK="week";  // 周

    private final String MONTH="month"; // 月

    private final String QUARTER="quarter"; //季度

    /**
     * 资产分析中资产相关统计
     *
     * 发现资产、在线资产、资产在线比例、台账资产、未处理告警数
     *
     * @return
     */
    @Override
    public QueryAssetQuantityVO quantity(){
        QueryAssetQuantityVO data = new QueryAssetQuantityVO();
        // 发现资产总数:在线资产表所有资产总数
        List<QueryCondition> conditions = new ArrayList<QueryCondition>();
        conditions.add(QueryCondition.eq("isDelete","0"));
        Long totalNum = assetOnLineService.count(conditions);
        data.setAssetDiscoveryTotal(totalNum==null?"0":String.valueOf(totalNum));
        // 在线资产:在线资产中状态为在线的总数
        conditions = new ArrayList<QueryCondition>();
        conditions.add(QueryCondition.eq("isDelete","0"));
        conditions.add(QueryCondition.eq("status","0"));  // 在线
        Long assetOnLineTotal = assetOnLineService.count(conditions);
        data.setAssetOnLineTotal(assetOnLineTotal==null?"0":String.valueOf(assetOnLineTotal));
        // 台账资产:台账总数(只统计资产探针资产总数)
        Long assetTotal = assetAnalysisService.getAssetTotal();
        data.setAssetTotal(assetTotal==null?"0":String.valueOf(assetTotal));
        // 资产在线比例: 在线状态数据/在线资产总数
        String percent = getPerCent(totalNum,assetOnLineTotal);
        data.setAssetOnLinePercent(percent);
        // 未处理告警数据
        Long waram = assetChangeService.getWarmCount();
        data.setWarmNum(waram==null?"0":String.valueOf(waram));
        return  data;
    }
    // 计算百分比：保留两位小数
    private String getPerCent(Long assetOnLineTotal, Long count) {
        if(assetOnLineTotal == 0) {
            return "0";
        }
        float num = ((float) count / assetOnLineTotal) * 100;
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        return df.format(num);
    }

    /**
     * 台账表按资产大类分类统计：只统计探针资产，data_source_type为4表示探针资产 2023-1-4
     * @return
     */
    @Override
    public List<QueryAssetLineTypeVO> getCountByAssetTypeGroup() {
        String sql="select a.dataName,count(*) as num from( " +
                "select asset.guid as guid,asset_type_group.Name as dataName from asset inner join asset_type on asset.Type_Guid=asset_type.Guid " +
                "inner join asset_type_group on asset_type.TreeCode LIKE CONCAT(`asset_type_group`.`TreeCode`,'-%') where asset.data_source_type=4)a group by a.dataName";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVOMapper());
        return details;
    }
    /**
     * 组装数据
     */
    private class QueryAssetLineTypeVOMapper implements RowMapper<QueryAssetLineTypeVO> {
        @Override
        public QueryAssetLineTypeVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            QueryAssetLineTypeVO asset = new QueryAssetLineTypeVO();
            String dataName = rs.getString("dataName");
            if(org.apache.commons.lang3.StringUtils.isEmpty(dataName)){
                dataName = "未知";
            }
            asset.setName(dataName);
            asset.setNum(rs.getInt("num"));
            return asset;
        }
    }

    /**
     * 台账表按资产小类分类统计：只统计探针资产，data_source_type为4表示探针资产 2023-1-4
     * @return
     */
    @Override
    public List<QueryAssetLineTypeVO> getCountByAssetType() {
        String sql="select a.dataName,count(*) as num from " +
                "(select asset_type.name as dataName ,asset.guid from asset " +
                "inner join asset_type on asset.Type_Guid=asset_type.Guid  where asset.data_source_type=4 )a group by a.dataName ";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVOMapper());
        return details;
    }


    /**
     * 资产总数变化趋势：
     * type:表示周、月、季  week、month、quarter
     *
     * @return
     */
    @Override
    public List<QueryAssetLineTypeVO> getTotalChangeTrend(String type) throws ParseException {
        switch(type){
            case WEEK:
                return getWeekAssetTotalChange();
            case MONTH:
                return getMonthAssetTotalChange();
            case QUARTER:
                return getQuarterAssetTotalChange();
            default:
                return getWeekAssetTotalChange(); // 默认按周
        }
    }

    /**
     * 周：按周统计资产总数变化趋势：近七天：从当前时间向后推7天
     * @return
     * @throws ParseException
     */
    private List<QueryAssetLineTypeVO> getWeekAssetTotalChange() throws ParseException {
        String sql="select date_format(create_time,'%Y-%m-%d %H') as dataName,num from asset_analysis_total_statistic   where date_format( DATE_SUB(SYSDATE(), INTERVAL 7 DAY),'%Y-%m-%d %H') <= create_time ORDER BY  date_format(create_time,'%Y-%m-%d %H') desc";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVO2Mapper());
        // 获取x轴数据
        List<String> dataXs = getWeekDataX(); // 在这没有当X轴
        return dataSupplement(details,dataXs);
    }
    /**
     * 月：按月统计资产总数变化趋势：近一个月
     * @return
     * @throws ParseException
     */
    private List<QueryAssetLineTypeVO> getMonthAssetTotalChange() throws ParseException {
        String sql="select date_format(create_time,'%Y-%m-%d') as dataName,num from asset_analysis_total_statistic where  create_time in (" +
                " select max(create_time) from asset_analysis_total_statistic  where DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(create_time)" +
                " group by date_format(create_time,'%Y-%m-%d'))";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVO2Mapper());
        // 获取x轴数据
        List<String> dataXs = getMonthDataX(); // 在这没有当X轴
        return dataSupplement(details,dataXs);
    }

    /**
     * 按季度统计资产总数变化趋势：进一个季度
     * @return
     * @throws ParseException
     */
    private List<QueryAssetLineTypeVO> getQuarterAssetTotalChange() throws ParseException {
        String sql="select date_format(create_time,'%Y-%m-%d') as dataName,num from asset_analysis_total_statistic where  create_time in (" +
                " select max(create_time) from asset_analysis_total_statistic  where DATE_SUB(CURDATE(), INTERVAL 3 MONTH) <= date(create_time)" +
                " group by date_format(create_time,'%Y-%m-%d'))";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVO2Mapper());
        // 获取x轴数据
        List<String> dataXs = getQuarterDataX(); // 在这没有当X轴
        return dataSupplement(details,dataXs);
    }




    /**
     * 在线资产数据
     * type:表示周、月、季  week、month、quarter
     * @return
     */
    @Override
    public List<QueryAssetLineTypeVO> getCountChange(String type) throws ParseException {
        switch(type){
            case WEEK:
                return getWeekAssetOnlineChange();
            case MONTH:
                return getMonthAssetOnlineChange();
            case QUARTER:
                return getQuarterAssetOnlineChange();
            default:
                return getWeekAssetOnlineChange(); // 默认按周
        }
    }


    /**
     *在线资产数据 :周
     * @return
     */
    private List<QueryAssetLineTypeVO> getWeekAssetOnlineChange() throws ParseException {
        String sql="select date_format(create_time,'%Y-%m-%d %H') as dataName,num from asset_analysis_online_statistic   where date_format( DATE_SUB(SYSDATE(), INTERVAL 7 DAY),'%Y-%m-%d %H') <= create_time ORDER BY  date_format(create_time,'%Y-%m-%d %H') desc";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVO2Mapper());
        List<String> dataXs = getWeekDataX(); // 在这没有当X轴
        return dataSupplement(details,dataXs);
    }

    /**
     *在线资产数据 :月
     * @return
     */
    private List<QueryAssetLineTypeVO> getMonthAssetOnlineChange() throws ParseException {
        String sql="select date_format(create_time,'%Y-%m-%d') as dataName,num  from asset_analysis_online_statistic where  create_time in (" +
                " select max(create_time) from asset_analysis_online_statistic  where DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(create_time)" +
                " group by date_format(create_time,'%Y-%m-%d'))";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVO2Mapper());
        List<String> dataXs = getMonthDataX(); // 在这没有当X轴
        return dataSupplement(details,dataXs);
    }


    /**
     *在线资产数据 :季
     * @return
     */
    private List<QueryAssetLineTypeVO> getQuarterAssetOnlineChange() throws ParseException {
        String sql="select date_format(create_time,'%Y-%m-%d') as dataName,num  from asset_analysis_online_statistic where  create_time in (" +
                "select max(create_time) from asset_analysis_online_statistic  where DATE_SUB(CURDATE(), INTERVAL 3 MONTH) <= date(create_time)" +
                "group by date_format(create_time,'%Y-%m-%d'))";
        List<QueryAssetLineTypeVO> details = jdbcTemplate.query(sql, new QueryAssetLineTypeVO2Mapper());
        List<String> dataXs = getQuarterDataX(); // 在这没有当X轴
        return dataSupplement(details,dataXs);
    }


    /**
     * 台账资产分类变化趋势：资产小类分类，
     * type:表示周、月、季  week、month、quarter
     * @return
     */
    @Override
    public QueryAssetCountChangeTrendVO getCountChangeTrend(String type) throws ParseException {
        switch(type){
            case WEEK:
                return getWeekCountChange();
            case MONTH:
                return getMonthCountChange();
            case QUARTER:
                return getQuarterCountChange();
            default:
                return getWeekCountChange(); // 默认按周
        }
    }


    /**
     * 资产数量变化趋势：周 <二级资产类型>
     * @return
     */
    private QueryAssetCountChangeTrendVO getWeekCountChange() throws ParseException {
        QueryAssetCountChangeTrendVO resultData = new QueryAssetCountChangeTrendVO();
        // 获取所有数据
        String sql ="select date_format(create_time,'%Y-%m-%d %H') as createTime,num, name from asset_analysis_type_statistic   where date_format( DATE_SUB(SYSDATE(), INTERVAL 7 DAY),'%Y-%m-%d %H') <= create_time";
        List<AssetAnalysisTypeStatisticVO> details = jdbcTemplate.query(sql, new BeanPropertyRowMapper<AssetAnalysisTypeStatisticVO>(AssetAnalysisTypeStatisticVO.class));
        // 获取x轴数据
        List<String> dataXs = getWeekDataX();
        resultData.setDataX(dataXs);
        List<QueryAssetCountChangeTrendExtendVO> dataY = new ArrayList<>();
        if(CollectionUtils.isEmpty(details)){
            return resultData;
        }
        // 获取数据中资产类型名称
        Map<String, List<AssetAnalysisTypeStatisticVO>>  datas = details.parallelStream().collect(Collectors.groupingBy(AssetAnalysisTypeStatisticVO::getName));
        QueryAssetCountChangeTrendExtendVO  data= null;
        for(Map.Entry<String, List<AssetAnalysisTypeStatisticVO>> entry : datas.entrySet()){
            data = new QueryAssetCountChangeTrendExtendVO();
            String typeName = entry.getKey();
            List<AssetAnalysisTypeStatisticVO> list = entry.getValue();
            data.setTypeName(typeName == null?"未知":typeName);
            data.setDatas(ChangeTrendHourSupplement(list,dataXs));
            dataY.add(data);
        }
        resultData.setDataY(dataY);
        return resultData;
    }
    /**
     * 资产数量变化趋势：月 <二级资产类型>
     * @return
     */
    private QueryAssetCountChangeTrendVO getMonthCountChange() throws ParseException {
        QueryAssetCountChangeTrendVO resultData = new QueryAssetCountChangeTrendVO();
        // 获取所有数据
        String sql ="select date_format(create_time,'%Y-%m-%d') as createTime,num ,name from asset_analysis_type_statistic where  create_time in (" +
                " select max(create_time) from asset_analysis_type_statistic  where DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(create_time) group by date_format(create_time,'%Y-%m-%d'))";
        List<AssetAnalysisTypeStatisticVO> details = jdbcTemplate.query(sql, new BeanPropertyRowMapper<AssetAnalysisTypeStatisticVO>(AssetAnalysisTypeStatisticVO.class));
        // 获取x轴数据
        List<String> dataXs = getMonthDataX();
        resultData.setDataX(dataXs);
        if(CollectionUtils.isEmpty(details)){
            return resultData;
        }
        List<QueryAssetCountChangeTrendExtendVO> dataY = new ArrayList<>();
        // 获取数据中资产类型名称
        Map<String, List<AssetAnalysisTypeStatisticVO>>  datas = details.parallelStream().collect(Collectors.groupingBy(AssetAnalysisTypeStatisticVO::getName));
        QueryAssetCountChangeTrendExtendVO  data= null;
        for(Map.Entry<String, List<AssetAnalysisTypeStatisticVO>> entry : datas.entrySet()){
            data = new QueryAssetCountChangeTrendExtendVO();
            String typeName = entry.getKey();
            List<AssetAnalysisTypeStatisticVO> list = entry.getValue();
            data.setTypeName(typeName == null?"未知":typeName);
            data.setDatas(ChangeTrendHourSupplement(list,dataXs));
            dataY.add(data);
        }
        resultData.setDataY(dataY);
        return resultData;
    }
    /**
     * 资产数量变化趋势：季 <二级资产类型>
     * @return
     */
    private QueryAssetCountChangeTrendVO getQuarterCountChange() throws ParseException {
        QueryAssetCountChangeTrendVO resultData = new QueryAssetCountChangeTrendVO();
        // 获取所有数据
        String sql ="select date_format(create_time,'%Y-%m-%d') as createTime,num ,name from asset_analysis_type_statistic where  create_time in (" +
                " select max(create_time) from asset_analysis_type_statistic  where DATE_SUB(CURDATE(), INTERVAL 3 MONTH) <= date(create_time) group by date_format(create_time,'%Y-%m-%d'))";
        List<AssetAnalysisTypeStatisticVO> details = jdbcTemplate.query(sql, new BeanPropertyRowMapper<AssetAnalysisTypeStatisticVO>(AssetAnalysisTypeStatisticVO.class));
        // 获取x轴数据
        List<String> dataXs = getQuarterDataX();
        resultData.setDataX(dataXs);
        if(CollectionUtils.isEmpty(details)){
            return resultData;
        }
        List<QueryAssetCountChangeTrendExtendVO> dataY = new ArrayList<>();
        // 获取数据中资产类型名称
        Map<String, List<AssetAnalysisTypeStatisticVO>>  datas = details.parallelStream().collect(Collectors.groupingBy(AssetAnalysisTypeStatisticVO::getName));
        QueryAssetCountChangeTrendExtendVO  data= null;
        for(Map.Entry<String, List<AssetAnalysisTypeStatisticVO>> entry : datas.entrySet()){
            data = new QueryAssetCountChangeTrendExtendVO();
            String typeName = entry.getKey();
            List<AssetAnalysisTypeStatisticVO> list = entry.getValue();
            data.setTypeName(typeName == null?"未知":typeName);
            data.setDatas(ChangeTrendHourSupplement(list,dataXs));
            dataY.add(data);
        }
        resultData.setDataY(dataY);
        return resultData;
    }


    private List<Integer> ChangeTrendHourSupplement(List<AssetAnalysisTypeStatisticVO> details, List<String> dataXs) {
        List<Integer> data = new ArrayList<>();
        for(int i= 0;i < dataXs.size(); i++){
            data.add(getNumAssetCountChangeTrend(details,dataXs.get(i)));
        }
        return data;
    }

    /**
     * 资产总数变化趋势、在线资产数量、资产数量变化趋势
     */
    public class QueryAssetLineTypeVO2Mapper implements RowMapper<QueryAssetLineTypeVO>{
        @Override
        public QueryAssetLineTypeVO mapRow(ResultSet rs, int rowNum) throws SQLException {
            QueryAssetLineTypeVO asset = new QueryAssetLineTypeVO();
            asset.setName(rs.getString("dataName"));
            asset.setNum(rs.getInt("num"));
            return asset;
        }
    }

    /**
     * 周：
     * X轴数据：近七天按小时处理
     * @return
     * @throws ParseException
     */
    private  List<String> getWeekDataX() throws ParseException {
        List<String> dataXS= new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");  // 具体小时
        String endTimeStr = sdf.format(new Date());
        Date endTime = sdf.parse(endTimeStr);
        long endTimes = endTime.getTime();
        Date startTime = DateUtils.addDays(endTime,-7);  // 7天的第一天
        long startTimes = startTime.getTime();
        while(startTimes <= endTimes){
            Date date = new Date(startTimes);
            dataXS.add(sdf.format(date));
            startTimes = startTimes+1000 * 60 * 60;
        }
        return dataXS;
    }

    /**
     * 月：
     * X轴数据：近一个月按天处理
     * @return
     * @throws ParseException
     */
    private List<String> getMonthDataX() throws ParseException {
        List<String> dataXS= new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // 具体天
        String endTimeStr = sdf.format(new Date());
        Date endTime = sdf.parse(endTimeStr);
        long endTimes = endTime.getTime();
        Date startTime =  DateUtils.addMonths(endTime, -1);
        long startTimes = startTime.getTime();
        while(startTimes <= endTimes){
            Date date = new Date(startTimes);
            dataXS.add(sdf.format(date));
            startTimes = startTimes+1000 * 60 * 60 * 24;
        }
        return dataXS;
    }
    /**
     * 一个季度：
     * X轴数据：近一个季度按天处理
     * @return
     * @throws ParseException
     */
    private List<String> getQuarterDataX() throws ParseException {
        List<String> dataXS= new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // 具体天
        String endTimeStr = sdf.format(new Date());
        Date endTime = sdf.parse(endTimeStr);
        long endTimes = endTime.getTime();
        Date startTime = DateUtils.addMonths(endTime,-3);  // 一个季度的第一天
        long startTimes = startTime.getTime();
        while(startTimes <= endTimes){
            Date date = new Date(startTimes);
            dataXS.add(sdf.format(date));
            startTimes = startTimes+1000 * 60 * 60 * 24;
        }
        return dataXS;
    }

    /**
     * 数据补全
     * @param datas
     * @param dataXs
     * @return
     * @throws ParseException
     */
    private  List<QueryAssetLineTypeVO> dataSupplement(List<QueryAssetLineTypeVO> datas ,List<String> dataXs) throws ParseException {
        List<QueryAssetLineTypeVO> allDatas = new ArrayList<QueryAssetLineTypeVO>();
        QueryAssetLineTypeVO assetQuery = null;
        for(int i= 0;i < dataXs.size(); i++){
            assetQuery = new QueryAssetLineTypeVO();
            setAssetTypeNum(datas,assetQuery,dataXs.get(i));
            allDatas.add(assetQuery);
        }
        return allDatas;
    }

    private void setAssetTypeNum(List<QueryAssetLineTypeVO> datas, QueryAssetLineTypeVO assetQuery,String hour) {
         int num = getNum(datas,hour);
         assetQuery.setNum(num);
         assetQuery.setName(hour);
    }

    private int getNum(List<QueryAssetLineTypeVO> datas, String hour) {
        if(CollectionUtils.isEmpty(datas)){
            return 0;
        }
        List<QueryAssetLineTypeVO> filters = datas.stream().filter(item -> item.getName().equals(hour)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(filters)){
            return 0;
        }
        return  filters.get(0).getNum();
    }

    private int getNumAssetCountChangeTrend(List<AssetAnalysisTypeStatisticVO> datas, String name) {
        if(CollectionUtils.isEmpty(datas)){
            return 0;
        }
        List<AssetAnalysisTypeStatisticVO> filters = datas.stream().filter(item -> item.getCreateTime().equals(name)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(filters)){
            return 0;
        }
        return  filters.get(0).getNum();
    }

}
