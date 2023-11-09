package com.vrv.vap.alarmdeal.business.model.job;

import com.vrv.vap.alarmdeal.business.model.constant.ModelManageConstant;
import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import com.vrv.vap.alarmdeal.business.model.service.ModelManageService;
import com.vrv.vap.jpa.quartz.QuartzFactory;
import com.vrv.vap.jpa.spring.SpringUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 定时执行模型分析接口
 */
public class ModelRunJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(ModelRunJob.class);
    private ModelManageService modelManageService= SpringUtil.getBean(ModelManageService.class);
    private QuartzFactory quartzFactory= SpringUtil.getBean(QuartzFactory.class);
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //获取任务的id
        String guid=context.getJobDetail().getJobDataMap().get("CUSTOM_DATA_KEY").toString();
        logger.info("定时执行运行模型接口，guid:"+guid);
        // 判断模型配置现有状态
        ModelManage model = modelManageService.getOne(guid);
        // 配置已经删除，配置处于下架装态，任务执行删除操作
        if(-1 == model.getIsDelete() ||model.getStatus()==ModelManageConstant.ModelStatus.OFFSHELF){
            logger.info("模型已处于已经删除，配置处于下架装态,删除任务，guid:"+guid);
            quartzFactory.removeJob(ModelManageConstant.modelJobName+model.getGuid(), ModelRunJob.class);
            return;
        }
        // 模型处于停止状态，不执行任务中方法
        if(model.getStatus()==ModelManageConstant.ModelStatus.STOP){
            logger.info("模型已模型处于停止状态，不执行方法，guid:"+guid);
            return;
        }
        model.setUpdateTime(new Date());
        try{
            Result<String> result= modelManageService.modelRun(guid);
            if(result.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
                model.setRemark("调用模型分析接口异常！"+result.getMsg());
            }
            model.setRemark("调用模型分析接口成功！");
        }catch (Exception e){
            model.setRemark("调用模型分析接口异常！");
           logger.error("调用模型分析接口异常：",e);
        }
        modelManageService.save(model);
    }
}
