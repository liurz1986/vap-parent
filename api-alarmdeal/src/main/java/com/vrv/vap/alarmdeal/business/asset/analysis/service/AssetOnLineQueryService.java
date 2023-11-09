package com.vrv.vap.alarmdeal.business.asset.analysis.service;

import com.vrv.vap.alarmdeal.business.asset.analysis.vo.QueryAssetCountChangeTrendVO;
import com.vrv.vap.alarmdeal.business.asset.analysis.vo.QueryAssetLineTypeVO;
import com.vrv.vap.alarmdeal.business.asset.analysis.vo.QueryAssetQuantityVO;

import java.text.ParseException;
import java.util.List;

public interface AssetOnLineQueryService {

    /**
     * 资产分析中资产相关统计
     *
     * 在线资产数量、台账资产数量、资产在线比例、未处理告警数据
     *
     * @return
     */
    public QueryAssetQuantityVO quantity();

    /**
     * 按资产大类分类统计
     * @return
     */
    public List<QueryAssetLineTypeVO> getCountByAssetTypeGroup();

    /**
     * 按资产小类分类统计
     * @return
     */
    public List<QueryAssetLineTypeVO> getCountByAssetType();

    /**
     * 资产总数变化趋势：近七天的，根据首次发现时间判断时间
     * @return
     */
    public List<QueryAssetLineTypeVO> getTotalChangeTrend(String type) throws ParseException;

    /**
     * 在线资产数据：近七天的，根据首次发现时间判断时间
     *
     * @return
     */
    public List<QueryAssetLineTypeVO> getCountChange(String type) throws ParseException;
    /**
     * 资产数量变化趋势：近七天的，根据首次发现时间判断时间
     *
     * @return
     */
    public QueryAssetCountChangeTrendVO getCountChangeTrend(String type) throws ParseException;
}
