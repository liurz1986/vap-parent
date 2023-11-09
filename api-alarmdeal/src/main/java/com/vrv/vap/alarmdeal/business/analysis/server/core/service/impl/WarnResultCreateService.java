package com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl;

import com.vrv.vap.alarmModel.AlarmPluiginListener;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.EventCategory;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmNoticeService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.bean.WarnResultLogVo;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.AlarmInfoMergerHandler;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventCategoryService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.kafkadeal.disruptor.alarmsave.AlarmSaveEventProducer;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.IndexsInfoVO;
import com.vrv.vap.es.vo.ScrollVO;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//import com.vrv.vap.AlarmAsset.AssetAlarmService;

/**
 *  
 *
 * @author wudi 
 *  E‐mail:wudi@vrvmail.com.cn
 *  @version 创建时间：2019年7月5日 下午4:46:53
 *  类说明     告警制造制造数据（告警按时间进行分索引操作）
 */
@Service
public class WarnResultCreateService extends ElasticSearchRestClientService<WarnResultLogTmpVO> {

    private static Logger logger = LoggerFactory.getLogger(WarnResultCreateService.class);

    private static final String warnResultType = "warnResultType"; //类型名称

    private static final String warnresulttmp = "warnresulttmp";  //索引名称

    @Autowired
    private AlarmInfoMergerHandler alarmInfoMergerHandler;
    @Autowired
    private EventCategoryService eventCategoryService;

    @Autowired
    private AlarmPluiginListener alarmPluiginListener;

    @Autowired
    private WarnResultForESService warnResultForESService;

    @Autowired
    private AlarmNoticeService alarmNoticeService;

    @Autowired
    private AlarmSaveEventProducer alarmSaveEventProducer;

    @Autowired
    private MapperUtil mapperUtil;

    @Override
    public String getIndexName() {
        return warnresulttmp;
    }

    /**
     * 告警产生的入口方法
     * 消费kafka的告警队列数据，进行数据处理，写入es，做响应
     *
     * @param warnResultLogVO
     */
    public void constructAlarmInfoData(WarnResultLogTmpVO warnResultLogVO) {
        warnResultLogVO = alarmInfoMergerHandler.handler(warnResultLogVO);//数据合并
        if (warnResultLogVO != null) {
            logger.debug("规则code:" + warnResultLogVO.getRuleCode());
            WarnResultLogVo vo = mapperUtil.map(warnResultLogVO,WarnResultLogVo.class);
//            alarmSaveEventProducer.send(vo);
            QueueUtil.put(warnResultLogVO);
        }
    }

    /**
     * 告警响应处理
     *
     * @param warnResultLogVO
     */
    private void sendResponse(WarnResultLogTmpVO warnResultLogVO) {
        alarmNoticeService.sendNotice(warnResultLogVO); //告警响应
    }

    /**
     * 转移就数据到新的索引数据当中
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public Result<Boolean> transformOldIndexDataToNewIndex(String startTime, String endTime) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            list = getIndexs(startTime, endTime);
        } else {  //修复所有的索引信息
            String[] indexNames = getIndexListByBaseIndexName(WarnResultForESService.WARN_RESULT_TMP); //获得告警对应所有的对应信息
            list = Arrays.asList(indexNames);
        }
        Result<Boolean> result = exetuteTransformDataThread(list);
        return result;
    }

    private Result<Boolean> exetuteTransformDataThread(List<String> list) {
        if (list.size() != 0) {
            createNewIndexAndTransformData(list);
            return ResultUtil.success(true);
        } else {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "所选时间范围没有对应的时间索引请检查！");
        }
    }

    /**
     * 创建新索引并转移数据
     *
     * @param sourcesIndexs
     */
    public void createNewIndexAndTransformData(List<String> sourcesIndexs) {
        long beginTime = System.currentTimeMillis();
        for (String sourcesIndex : sourcesIndexs) {
            executeReIndexProdure(sourcesIndex);
        }
        long endTime = System.currentTimeMillis();
        logger.info("转移es数据一共花费：" + (endTime - beginTime) / 1000 + "s");
    }

    /**
     * 执行ReIndex的过程
     *
     * @param sourceIndexName
     */
    private void executeReIndexProdure(String sourceIndexName) {
        String destIndexName = getWarnResultDstIndexNameBySourceIndex(sourceIndexName); //目的索引
        String warnResultAlias = getWarnResultAlias(sourceIndexName);
        createWarnResultNewIndex(destIndexName, warnResultType);
        delWarnResultIndexName(sourceIndexName);
        addWarnResultIndexAlias(destIndexName, warnResultAlias);
    }

    /**
     * 根据告警索引获得对应的别名
     *
     * @param indexName
     * @return
     */
    private String getWarnResultAlias(String indexName) {
        String[] indexArray = indexName.split("-");
        if (indexArray.length == 4) {
            return indexName;
        }
        if (indexArray.length == 5) {
            indexName = indexName.substring(0, indexName.lastIndexOf("-"));
            return indexName;
        } else {
            throw new RuntimeException("索引长度不符合要求请检查！");
        }
    }

    /**
     * 根据源告警索引获得目的告警索引
     *
     * @param indexName
     * @return
     */
    private String getWarnResultDstIndexNameBySourceIndex(String indexName) {
        String[] indexArray = indexName.split("-");
        if (indexArray.length == 4) {
            indexName = indexName + "-" + UUIDUtils.get32UUID();
            return indexName;
        }
        if (indexArray.length == 5) {
            indexName = indexName.substring(0, indexName.lastIndexOf("-"));
            indexName = indexName + "-" + UUIDUtils.get32UUID();
            return indexName;
        } else {
            throw new RuntimeException("索引长度不符合要求,请检查！");
        }
    }

    /**
     * 1.新增一个新的索引
     * 2.把老索引的数据移过去
     * 3.删除老索引的数据
     * 4.给新索引赋值上老索引的别名
     *
     * @param indexName
     * @param type
     */
    private void createWarnResultNewIndex(String indexName, String type) {
        WarnResultLogTmpVO warnResultLogVO = new WarnResultLogTmpVO();
        alarmPluiginListener.update(warnResultLogVO);
    }

    /**
     * 删除告警索引
     *
     * @param indexName
     */
    private boolean delWarnResultIndexName(String indexName) {
        boolean result = delIndexByIndexName(indexName);
        logger.info("删除索引" + indexName + "结果：" + result);
        return result;
    }

    /**
     * 添加告警索引别名
     *
     * @param indexName
     * @return
     */
    private void addWarnResultIndexAlias(String indexName, String oldIndexNameAlias) {
        boolean addAlias = addAlias(indexName, oldIndexNameAlias);
        logger.info("添加别名" + oldIndexNameAlias + "结果：{}", addAlias);
    }

    /**
     * 开始时间
     * 结束时间
     *
     * @param startTime
     * @param endTime
     */
    public Result<Boolean> fixOldEsDataByDate(String startTime, String endTime) {
        Result<Boolean> result = null;
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            list = getIndexs(startTime, endTime);
        } else {  //修复所有的索引信息
            String[] indexNames = getIndexListByBaseIndexName(WarnResultForESService.WARN_RESULT_TMP); //获得告警对应所有的对应信息
            list = Arrays.asList(indexNames);
        }
        result = executeFixOldEsData(list);
        return result;

    }

    /**
     * 开始时间
     * 结束时间
     *
     * @param startTime
     * @param endTime
     */
    public Result<Boolean> deleteOldEsDataByDate(String startTime, String endTime) {
        Result<Boolean> result = null;
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            list = getIndexs(startTime, endTime);
        } else {  //修复所有的索引信息
            String[] indexNames = getIndexListByBaseIndexName(WarnResultForESService.WARN_RESULT_TMP); //获得告警对应所有的对应信息
            list = Arrays.asList(indexNames);
        }
        result = executeDeleteOldEsData(list);
        return result;

    }

    /**
     * 执行修复es数据
     *
     * @param list
     */
    private Result<Boolean> executeDeleteOldEsData(List<String> list) {
        if (list.size() != 0) {
            deleteBadData(list, 10);
            return ResultUtil.success(true);
        } else {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "所选时间范围没有对应的时间索引请检查！");
        }
    }

    /**
     * es脏数据清理
     */
    public void deleteBadData(List<String> indexNames, Integer number) {

        for (String index : indexNames) {
            IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
            indexsInfoVO.setIndex(new String[]{index});
            indexsInfoVO.setType(new String[]{warnResultType});
            List<QueryCondition_ES> conditions = new ArrayList<>();
            ScrollVO<WarnResultLogTmpVO> scrollVO = findAll(indexsInfoVO, conditions, null, null, 1000); //按1000次进行分页
            List<WarnResultLogTmpVO> list = scrollVO.getList();
            batchDeleteESData(list, index, indexsInfoVO, conditions); //第一次处理数据
            batchDeleteEsDataByScrollId(scrollVO, index, indexsInfoVO, conditions);

        }
    }

    private void batchDeleteESData(List<WarnResultLogTmpVO> list, String index, IndexsInfoVO indexsInfoVO, List<QueryCondition_ES> conditions) {
        List<WarnResultLogTmpVO> warnResultLogTmpVOlist = new ArrayList<>();
        for (WarnResultLogTmpVO warnResultLogVO : list) {
            if (StringUtils.isBlank(warnResultLogVO.getRiskEventName())) {
                warnResultLogTmpVOlist.add(warnResultLogVO);
            }
        }
        int size = warnResultLogTmpVOlist.size();
        logger.info("size: " + size);
        if (size > 0) {
            logger.info("WarnResultLogTmpVO: " + warnResultLogTmpVOlist.get(0));
        }
    }

    private void batchDeleteEsDataByScrollId(ScrollVO<WarnResultLogTmpVO> scrollVO, String index, IndexsInfoVO indexsInfoVO, List<QueryCondition_ES> conditions) {
        String scrollId = scrollVO.getScrollId(); //获得对应的游标Id
        boolean flag = true;
        while (flag) {
            ScrollVO<WarnResultLogTmpVO> searchByScrollId = new ScrollVO<>();
            List<WarnResultLogTmpVO> list = searchByScrollId.getList();
            if (list.size() > 0) {
                batchDeleteESData(list, index, indexsInfoVO, conditions); //第一次并进行数据处理
            } else {
                logger.info("数据分页已到结尾结束。");
                //	cleanScrollId(scrollId);
                flag = false;
            }
        }
    }

    /**
     * 执行修复es数据
     *
     * @param list
     */
    private Result<Boolean> executeFixOldEsData(List<String> list) {
        if (list.size() != 0) {
            fixOldEsData(list);
            return ResultUtil.success(true);
        } else {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "所选时间范围没有对应的时间索引请检查！");
        }
    }

    /**
     * 获得对应的索引
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private List<String> getIndexs(String startTime, String endTime) {
        List<String> list = new ArrayList<>();
        List<String> betweenDays = DateUtil.getDatesBetweenDays(startTime, endTime, DateUtil.Year_Mouth_Day);
        String[] indexNames = getIndexListByBaseIndexName(WarnResultForESService.WARN_RESULT_TMP); //获得告警对应所有的对应信息
        for (String day : betweenDays) {
            for (String indexName : indexNames) {
                if (indexName.contains(day)) {
                    list.add(indexName);
                    break;
                }
            }
        }
        return list;
    }

    /**
     * 开始修复旧数据
     */
    public void fixOldEsData(List<String> indexNames) {
        long beginTime = System.currentTimeMillis();
        for (String index : indexNames) {
            logger.info("修复缩影数据索引：" + index);
            IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
            indexsInfoVO.setIndex(new String[]{index});
            indexsInfoVO.setType(new String[]{warnResultType});
            List<QueryCondition_ES> conditions = new ArrayList<>();
            ScrollVO<WarnResultLogTmpVO> scrollVO = findAll(indexsInfoVO, conditions, null, null, 1000); //按1000次进行分页
            List<WarnResultLogTmpVO> list = scrollVO.getList();
            logger.info("告警数据集个数：" + list.size());
            batchSaveESData(list, index, indexsInfoVO, conditions); //第一次处理数据
            batchSaveEsDataByScrollId(scrollVO, index, indexsInfoVO, conditions);
        }
        long endTime = System.currentTimeMillis();
        logger.info("修复数据完成,共花费时间：" + (endTime - beginTime) / 1000 + "s");
    }

    /**
     * 根据游标进行数据的过滤操作
     *
     * @param scrollVO
     * @param index
     * @param indexsInfoVO
     * @param conditions
     */
    private void batchSaveEsDataByScrollId(ScrollVO<WarnResultLogTmpVO> scrollVO, String index, IndexsInfoVO indexsInfoVO, List<QueryCondition_ES> conditions) {
        String scrollId = scrollVO.getScrollId(); //获得对应的游标Id
        boolean flag = true;
        while (flag) {
            ScrollVO<WarnResultLogTmpVO> searchByScrollId = searchByScrollId(scrollId);
            List<WarnResultLogTmpVO> list = searchByScrollId.getList();
            if (list.size() > 0) {
                batchSaveESData(list, index, indexsInfoVO, conditions); //第一次并进行数据处理
            } else {
                logger.info("数据分页已到结尾结束。");
                cleanScrollId(scrollId);
                flag = false;
            }
        }
    }

    /**
     * 告警数据填充对应的攻击标识
     */
    private void addAttackFlagByWarnResultLogVO(WarnResultLogTmpVO warnResultLogVO) {
        String riskEventId = warnResultLogVO.getRiskEventId();
        if (StringUtils.isNotEmpty(riskEventId)) {
            EventCategory eventCategory = eventCategoryService.getOne(riskEventId);
            if (eventCategory != null) {
                String attackFlag = eventCategory.getAttackFlag();
                warnResultLogVO.setAttackFlag(attackFlag);
            }
        }

    }

    /**
     * 批量保存es数据
     *
     * @param index
     * @param indexsInfoVO
     * @param conditions
     */
    private void batchSaveESData(List<WarnResultLogTmpVO> list, String index, IndexsInfoVO indexsInfoVO, List<QueryCondition_ES> conditions) {
        for (WarnResultLogTmpVO warnResultLogVO : list) {
            alarmPluiginListener.update(warnResultLogVO);
            this.addAttackFlagByWarnResultLogVO(warnResultLogVO);//攻击标识
        }
        if (list.size() > 0) {
            logger.info(index + "个数：" + list.size());
            try {
                addList(index, list);
            } catch (Exception e) {
                logger.error("批量修复数据执行失败", e);
            }
        }
    }

    /**
     * 删除es重复数据
     *
     * @param stime
     * @param etime
     * @return
     */
    public Result<Boolean> deleteRepeatESData(String stime, String etime) {
        List<String> datesBetweenDays = DateUtil.getDatesBetweenDays(stime, etime, DateUtil.Year_Mouth_Day);
        for (String date : datesBetweenDays) {
            String indexName = "warnresulttmp" + "-" + date;
            Date strConvertDate = null;
            try {
                strConvertDate = DateUtil.parseDate(date, DateUtil.Year_Mouth_Day);
            } catch (ParseException e) {
                logger.error("异常解析:{}", e);
            }
            Date preDay = DateUtil.addDay(strConvertDate, -1);
            String format = DateUtil.format(preDay, DateUtil.Year_Mouth_Day);
            String fstime = format + " 00:00:00";
            String fetime = format + " 23:59:59";
            List<QueryCondition_ES> conditions = new ArrayList<>();
            conditions.add(QueryCondition_ES.between("triggerTime", fstime, fetime));
            IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
            indexsInfoVO.setIndex(new String[]{indexName});
            indexsInfoVO.setType(new String[]{warnResultType});
            Boolean esIndexExist = isEsIndexExist(indexName);
            if (esIndexExist) {
                List<WarnResultLogTmpVO> list = findAll(indexsInfoVO, conditions);
                deleteList(indexName, list);
                logger.info("删除多余数据个数：" + list.size() + " " + indexName + "删除完成");
            }
        }
        Result<Boolean> result = ResultUtil.success(true);
        return result;
    }

    /**
     * 删除相关的数据
     *
     * @param analysisVO
     * @return
     */
    public Boolean deleteRelateEsData(AnalysisVO analysisVO) {
        List<QueryCondition_ES> conditions = warnResultForESService.getCondition(analysisVO);
        List<WarnResultLogTmpVO> list = findAll(conditions);
        deleteList(list);
        return true;
    }

}
