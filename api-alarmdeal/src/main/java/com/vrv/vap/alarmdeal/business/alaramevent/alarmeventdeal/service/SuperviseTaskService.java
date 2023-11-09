package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.SuperviseTask;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskQueryVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskReceiveVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskVo;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


/**
 * @author lps 2021/8/4
 */
public interface SuperviseTaskService extends BaseService<SuperviseTask,String> {

    /**
     * 分页查询
     * @param superviseTaskQueryVo
     * @return
     */
    PageRes<SuperviseTask> getSuperviseTaskPage(SuperviseTaskQueryVo superviseTaskQueryVo);


    /**
     * 更新督办任务状态为“已处置”
     * @param analysisId
     * @return
     */
    public Boolean dealSuperviseTask(String analysisId);


    /**
     * 下发任务入库
     *
     * @param superviseTaskReceiveVo
     */
    public void pullSuperviseTask(SuperviseTaskReceiveVo superviseTaskReceiveVo);

    /**
     * 更新告警事件为已经协办
     */
    public void updateEsAssist(String eventId);


    /**
     * 新增协办任务
     */
    public SuperviseTask addAssistingTask(SuperviseTaskVo superviseTaskVo);

    /**
     * 响应督办/预警/协办
     * @param superviseTaskVo
     * @return
     */
    public SuperviseTask responseSuperviseTask(@RequestBody SuperviseTaskVo superviseTaskVo);

    /**
     * 督办任务查询top
     */
    public  List<SuperviseTask> findSuperviseTaskTop(Integer count);

    /**
     *预警查询top
     */
    public  List<SuperviseTask> findWarningTop(Integer count);

    /**
     * 督办、预警统计
     */
    Map<String,Object> countSuperviseTask();

    /**
     * 今日下发任务统计
     * @param s
     * @return
     */
    public Integer getTodayDownTaskCount(String s);


    /**
     * 任务统计
     *
     * @param type   任务类型，
     * @param source 上报或下发，
     * @return
     */
    public Integer getTaskCount(String type, String source, String dealStatus);

    /**
     * 通过下发时间查询统计督办格式
     *
     * @param eventDetailQueryVO 入参  beginTime开始时间-endTime 结束时间
     * @return
     */
    public Integer getSuperviseCountForSendTime(EventDetailQueryVO eventDetailQueryVO);




}
