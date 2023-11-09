package com.vrv.vap.alarmdeal.business.asset.analysis.service.impl;

import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisOnLineStatistic;
import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisTotalStatistic;
import com.vrv.vap.alarmdeal.business.asset.analysis.model.AssetAnalysisTypeStatistic;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisOnLineStatisticService;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisService;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisTotalStatisticService;
import com.vrv.vap.alarmdeal.business.asset.analysis.service.AssetAnalysisTypeStatisticService;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AssetAnalysisServiceImpl implements AssetAnalysisService {
    private static Logger logger = LoggerFactory.getLogger(AssetAnalysisServiceImpl.class);

    @Autowired
    private AssetOnLineService assetOnLineService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AssetAnalysisOnLineStatisticService assetAnalysisOnLineStatisticService;
    @Autowired
    private AssetAnalysisTotalStatisticService assetAnalysisTotalStatisticService;
    @Autowired
    private AssetAnalysisTypeStatisticService assetAnalysisTypeStatisticService;

    /**
     * 执行资产分布数据统计
     *  1. 台账二级资产分类统计 : 记录数据按小时记录的
     *  2. 台账资产总数总数统计: 记录数据按小时记录的
     *  3. 在线资产表资产总数统计: 记录数据按小时记录的
     */
    @Override
    public void excStatistic() {
        try{
            // 台账资产总数总数统计
            List<QueryCondition> conditions = new ArrayList<>();
            Long totalNum = getAssetTotal(); // 只统计台账中探针入库的资产总数  2023-1-4
            // 在线资产表资产总数统计
            conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("isDelete","0"));
            Long assetOnLineTotal = assetOnLineService.count(conditions);
            // 台账二级资产分类统计
            assetAnalysisByAssetType();
            //台账资产表总数统计
            queryAssetOnlineTotal(totalNum);
            // 在线资产表资产总数
            queryAssetOnlineStatusTotal(assetOnLineTotal);
        }catch (Exception e){
            logger.error("资产分布周期统计异常",e);
        }

    }

    /**
     * 只统计台账中探针入库的资产总数  2023-1-4
     * @return
     */
    public Long getAssetTotal() {
      String sql="select count(*) as total from asset where data_source_type=4";
      Map<String,Object> result = jdbcTemplate.queryForMap(sql);
      return Long.parseLong(String.valueOf(result.get("total")));
    }


    /**
     * 台账按资产小类分类统计
     * @return
     */
    public void assetAnalysisByAssetType() throws ParseException {
        String sql="select a.dataName,count(*) as num from " +
                "(select asset_type.name as dataName ,asset.guid from asset  " +
                "inner join asset_type on asset.Type_Guid=asset_type.Guid where asset.data_source_type=4)a group by a.dataName";
        List<AssetAnalysisTypeStatistic> details = jdbcTemplate.query(sql, new AssetAnalysisTypeStatisticMapper());
        String createTimeStr = DateUtil.format(new Date(),"yyyy-MM-dd HH");
        Date createTime = DateUtil.parseDate(createTimeStr,"yyyy-MM-dd HH");
        // 记录按一个小时进行记录，存在当前小时数据，删除当前数据进行更新
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("createTime",createTime));
        List<AssetAnalysisTypeStatistic> list = assetAnalysisTypeStatisticService.findAll(conditions);
        if(CollectionUtils.isNotEmpty(list)){
            assetAnalysisTypeStatisticService.deleteInBatch(list); // 删除当前小时数据
        }
        if(CollectionUtils.isNotEmpty(details)){
            details.stream().forEach(item -> item.setCreateTime(createTime));
            assetAnalysisTypeStatisticService.save(details); // 将最新的小时数据新增进去
        }
    }
    public class AssetAnalysisTypeStatisticMapper implements RowMapper<AssetAnalysisTypeStatistic> {
        @Override
        public AssetAnalysisTypeStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
            AssetAnalysisTypeStatistic query = new AssetAnalysisTypeStatistic();
            query.setName(rs.getString("dataName"));
            query.setNum(rs.getInt("num"));
            query.setGuid(UUIDUtils.get32UUID());
            return query;
        }
    }
    /**
     * 台账资产总数统计
     *
     * @return
     */
    public void queryAssetOnlineTotal(Long totalNum) throws ParseException {
        // 记录按一个小时进行记录，存在当前小时数据，删除当前数据进行更新
        String createTimeStr = DateUtil.format(new Date(),"yyyy-MM-dd HH");
        Date createTime = DateUtil.parseDate(createTimeStr,"yyyy-MM-dd HH");
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("createTime",createTime));
        List<AssetAnalysisTotalStatistic> list = assetAnalysisTotalStatisticService.findAll(conditions);
        if(CollectionUtils.isNotEmpty(list)){
            assetAnalysisTotalStatisticService.deleteInBatch(list); // 删除当前小时数据
        }
        AssetAnalysisTotalStatistic data = new AssetAnalysisTotalStatistic();
        data.setGuid(UUIDUtils.get32UUID());
        data.setCreateTime(createTime);
        data.setNum(Integer.parseInt(String.valueOf(totalNum)));
        assetAnalysisTotalStatisticService.save(data); // 将最新的小时数据新增进去
    }

    /**
     * 在线资产表资产总数统计
     *
     * @return
     */
    public void queryAssetOnlineStatusTotal(Long totalNum) throws ParseException {
        // 记录按一个小时进行记录，存在当前小时数据，删除当前数据进行更新
        String createTimeStr = DateUtil.format(new Date(),"yyyy-MM-dd HH");
        Date createTime = DateUtil.parseDate(createTimeStr,"yyyy-MM-dd HH");
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("createTime",createTime));
        List<AssetAnalysisOnLineStatistic> list = assetAnalysisOnLineStatisticService.findAll(conditions);
        if(CollectionUtils.isNotEmpty(list)){
            assetAnalysisOnLineStatisticService.deleteInBatch(list); // 删除当前小时数据
        }
        AssetAnalysisOnLineStatistic data = new AssetAnalysisOnLineStatistic();
        data.setGuid(UUIDUtils.get32UUID());
        data.setCreateTime(createTime);
        data.setNum(Integer.parseInt(String.valueOf(totalNum)));
        assetAnalysisOnLineStatisticService.save(data);
    }

}
