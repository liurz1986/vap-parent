package com.vrv.vap.line.schedule.task;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.line.model.BaseLine;
import com.vrv.vap.line.model.BaseLineSpecial;
import com.vrv.vap.line.tools.BaseLineUtil;
import com.vrv.vap.line.tools.FlinkTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;

import java.util.HashMap;
import java.util.Map;

public class FrequentScoreTask extends BaseTask{

    private static final Log log = LogFactory.getLog(FrequentScoreTask.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private BaseLineUtil lineUtil = new BaseLineUtil();
    @Override
    void run(String jobName) {

    }

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        log.info("序列实时得分监控开始");
        int id = jobDataMap.getInt("id");
        BaseLine baseLine = baseLineMapper.selectById(id);
        if(StringUtils.isEmpty(baseLine.getSpecialId())){
            return;
        }
        BaseLineSpecial baseLineSpecial = baseLineSpecialMapper.selectById(baseLine.getSpecialId());
        if(StringUtils.isEmpty(baseLine.getMonitorId())){
            String respones = lineUtil.runSpecialActualTaskBySsh(baseLineSpecial,JSONObject.parseObject(baseLine.getSpecialParam(),HashMap.class));
            if(StringUtils.isNotEmpty(respones)){
                String jobid = FlinkTools.parseJobId(respones);
                baseLine.setMonitorId(jobid);
                baseLineMapper.updateById(baseLine);
            }
            return;
        }
        boolean state = lineUtil.queryJobState(baseLine.getMonitorId());
        if(!state){
            //重新提交任务
            String respones = lineUtil.runSpecialActualTaskBySsh(baseLineSpecial,JSONObject.parseObject(baseLine.getSpecialParam(),HashMap.class));
            if(StringUtils.isNotEmpty(respones)){
                String jobid = FlinkTools.parseJobId(respones);
                baseLine.setMonitorId(jobid);
                baseLineMapper.updateById(baseLine);
            }
            /*
            if(StringUtils.isNotEmpty(respones)){
                Map<String,String> hashMap = JSONObject.parseObject(respones, HashMap.class);
                if(hashMap.containsKey("jobid")){
                    baseLineSpecial.setMonitorId(hashMap.get("jobid"));
                    baseLineSpecialMapper.updateById(baseLineSpecial);
                }
            }*/
        }
    }
}
