package com.vrv.vap.alarmdeal.business.baseauth.service.impl;

import com.vrv.vap.alarmdeal.business.baseauth.dao.BaseAuthOverviewDao;
import com.vrv.vap.alarmdeal.business.baseauth.enums.OptEnum;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthOverviewService;
import com.vrv.vap.alarmdeal.business.baseauth.util.BaseAuthUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.CoordinateVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendExtendVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendResultVO;
import com.vrv.vap.alarmdeal.business.baseauth.vo.TrendVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 审批信息概览
 *
 * @Date 2023-09
 * @author liurz
 */
@Service
public class BaseAuthOverviewServiceImpl implements BaseAuthOverviewService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthOverviewServiceImpl.class);
    @Autowired
    private BaseAuthOverviewDao baseAuthOverviewDao;
    @Autowired
    private MapperUtil mapper;
    /**
     * 总数统计及差异统计
     * 近一个月趋势统计
     * 审批信息总数：all
     * 打印权限总数：print
     * 刻录权限总数：burn
     * 系统访问权限总数：access
     * 网络互联权限总数：inter
     * 运维权限总数：maint
     * 打印	1
     * 刻录	2
     * 访问	3
     * 运维	4
     * 网络互联	5
     * @return
     */
    @Override
    public Result<Map<String, Object>> getTotalStatistics(String type) throws ParseException {
        Map<String, Object> mapRes = new HashMap<>();
        switch (type){
            case  BaseAuthUtil.TYPE_ALL:  // 所有
                return getTotalAndTrend(-1,true);
            case  BaseAuthUtil.TYPE_PRINT:  // 打印
                return getTotalAndTrend(OptEnum.PRINT.getCode(),false);
            case  BaseAuthUtil.TYPE_BURN: // e刻录
                return getTotalAndTrend(OptEnum.BURN.getCode(),false);
            case  BaseAuthUtil.TYPE_INTER: //网路互联
                return getTotalAndTrend(OptEnum.INTER.getCode(),false);
            case  BaseAuthUtil.TYPE_ACCESS: // 访问
                return getTotalAndTrend(OptEnum.ACCESS.getCode(),false);
            case  BaseAuthUtil.TYPE_MAINT: // 运维
                return getTotalAndTrend(OptEnum.MAINT.getCode(),false);
            default:
                return ResultUtil.success(mapRes);
        }
    }

    /**
     * 总数统计及差异统计
     * 近一个月趋势统计
     * @param opt
     * @param isAll
     * @return
     * @throws ParseException
     */
    private Result<Map<String, Object>> getTotalAndTrend(int opt,boolean isAll) throws ParseException {
        Map<String, Object> mapRes = new HashMap<>();
        // 总数统计及差异统计
        Map<String, Object> totalNum = new HashMap<>();
        int total = baseAuthOverviewDao.getTotal(true,isAll,opt);
        int lastTotal = baseAuthOverviewDao.getTotal(false,isAll,opt);
        totalNum.put("total",total);
        totalNum.put("diffCount",(total-lastTotal));
        // 近一个月趋势统计
        List<TrendVO> list = baseAuthOverviewDao.getTrendMonthByOpt(opt,isAll);
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        mapRes.put("count",totalNum);
        mapRes.put("trend",trendDatas);
        return ResultUtil.success(mapRes);
    }


    /**
     * 打印权限统计
     * 目的对象类型为文件、操作类型打印
     * @return
     */
    @Override
    public Result<Map<String, Object>> getPrintStatistics() throws ParseException {
        Map<String, Object> result = new HashMap<>();
        // 数量统计
        Map<String, Object> dataResult = new HashMap<>();
        long total = baseAuthOverviewDao.getFileTotalByOpt(1,true);
        long yesTotal = baseAuthOverviewDao.getFileTotalByOpt(1,false);
        dataResult.put("total",total);
        dataResult.put("diffCount",(total-yesTotal));
        // 月度变化趋势
        List<TrendVO> list = baseAuthOverviewDao.getFileTrend(1);
        // 获取x轴数据
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        result.put("count",dataResult);
        result.put("trend",trendDatas);
        return ResultUtil.success(result);
    }

    /**
     * 刻录权限统计
     * 目的对象类型为文件、操作类型刻录
     * @return
     */
    @Override
    public Result<Map<String, Object>> getBurnStatistics() throws ParseException {
        Map<String, Object> result = new HashMap<>();
        // 数量统计
        Map<String, Object> dataResult = new HashMap<>();
        long total = baseAuthOverviewDao.getFileTotalByOpt(2,true);
        long yesTotal = baseAuthOverviewDao.getFileTotalByOpt(2,false);
        dataResult.put("total",total);
        dataResult.put("diffCount",(total-yesTotal));
        // 月度变化趋势
        List<TrendVO> list = baseAuthOverviewDao.getFileTrend(2);
        // 获取x轴数据
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        result.put("count",dataResult);
        result.put("trend",trendDatas);
        return ResultUtil.success(result);
    }
    /**
     * 系统访问权限统计(内部用户终端)
     */
    @Override
    public Result<Map<String, Object>> getAccessHostStatistics() throws ParseException {
        Map<String, Object> result = new HashMap<>();
        // 数量统计
        Map<String, Object> dataResult = new HashMap<>();
        long total = baseAuthOverviewDao.getAccessHostTotal(true);
        long yesTotal = baseAuthOverviewDao.getAccessHostTotal(false);
        dataResult.put("total",total);
        dataResult.put("diffCount",(total-yesTotal));
        // 月度变化趋势
        List<TrendVO> list = baseAuthOverviewDao.getAccessHostTrend();
        // 获取x轴数据
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        result.put("count",dataResult);
        result.put("trend",trendDatas);
        return ResultUtil.success(result);
    }
    /**
     * 系统访问权限统计(外部Ip)
     */
    @Override
    public Result<Map<String, Object>> getExternalAssetStatistics() throws ParseException {
        Map<String, Object> result = new HashMap<>();
        // 数量统计
        Map<String, Object> dataResult = new HashMap<>();
        long total = baseAuthOverviewDao.getExternalAssetTotal(true);
        long yesTotal = baseAuthOverviewDao.getExternalAssetTotal(false);
        dataResult.put("total", total);
        dataResult.put("diffCount", (total - yesTotal));
        // 月度变化趋势
        List<TrendVO> list = baseAuthOverviewDao.getExternalAssetTrend();
        // 获取x轴数据
        List<String> dataXs = BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list, dataXs);
        result.put("count", dataResult);
        result.put("trend", trendDatas);
        return ResultUtil.success(result);
    }

    /**
     * 运维权限统计
     * ip数量统计
     *
     * 1. 源对象是运维终端
     * 2. 操作类型为运维
     * @return
     */
    @Override
    public Result<List<CoordinateVO>> getMaintenFlagCountStatistics() {
        return ResultUtil.successList(baseAuthOverviewDao.getMaintenFlagCountStatistics());
    }
    /**
     * 运维权限统计
     * 月度变化统计
     *
     * 1. 源对象是运维终端
     * 2. 操作类型为运维
     * @return
     */
    @Override
    public List<TrendResultVO> getMaintenFlagMonthStatistics() throws ParseException {
        List<TrendResultVO> result = new ArrayStack();
        // 一个月时间按天、ip分组统计结果
        List<TrendExtendVO> list = baseAuthOverviewDao.getMaintenFlagMonthStatistics();
        if(CollectionUtils.isEmpty(list)){
            return result;
        }
        // ip分组
        Map<String,List<TrendExtendVO>> groupDatas = list.stream().collect(Collectors.groupingBy(TrendExtendVO::getFlag));
        Set<String> ips = groupDatas.keySet();
        TrendResultVO trendVO = null;
        // 获取x轴数据
        List<String> dataXs = BaseAuthUtil.getMonthDataX();
        for(String ip : ips){
            if(StringUtils.isEmpty(ip)){
                continue;
            }
            trendVO = new TrendResultVO();
            trendVO.setName(ip);
            List<TrendExtendVO> datas = groupDatas.get(ip);
            List<TrendVO> trends = mapper.mapList(datas,TrendVO.class);
            List<CoordinateVO> resList = dataSupplementObj(trends,dataXs);
            trendVO.setCoords(resList);
            result.add(trendVO);
        }
        return result;
    }



    /**
     * 数据补全
     * @param datas
     * @param dataXs
     * @return
     * @throws ParseException
     */
    private  List<CoordinateVO>  dataSupplementObj(List<TrendVO> datas , List<String> dataXs) throws ParseException {
        List<CoordinateVO> allDatas = new ArrayList<CoordinateVO>();
        CoordinateVO data = null;
        for(int i= 0;i < dataXs.size(); i++){
            data = new CoordinateVO();
            dataHandleObj(datas,data,dataXs.get(i));
            allDatas.add(data);
        }
        return allDatas;
    }

    private void dataHandleObj(List<TrendVO> datas, CoordinateVO data, String name) {
        int number =  getNumObj(datas,name);
        data.setDataY(number+"");
        data.setDataX(name);
    }

    private int getNumObj(List<TrendVO> datas, String name) {
        if(CollectionUtils.isEmpty(datas)){
            return 0;
        }
        for(TrendVO param : datas){
            String dataName =param.getName();
            if(name.equals(dataName)){
                return param.getNumber();
            }
        }
        return  0;
    }
}
