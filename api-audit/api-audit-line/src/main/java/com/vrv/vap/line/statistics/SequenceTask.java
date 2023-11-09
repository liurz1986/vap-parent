package com.vrv.vap.line.statistics;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.line.model.BaseLine;
import com.vrv.vap.line.model.BaseLineSpecial;
import com.vrv.vap.line.tools.BaseLineUtil;
import com.vrv.vap.line.tools.FlinkTools;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class SequenceTask extends BaseStatisticsTask {
    private static BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private BaseLineUtil lineUtil = new BaseLineUtil();
    @Override
    public void execute(Map<String, Object> params) {
        if(!params.containsKey("id")){
            return;
        }
        BaseLine baseLine = baseLineMapper.selectById(Integer.parseInt(params.get("id").toString()));
        if(StringUtils.isEmpty(baseLine.getSpecialId())){
            return;
        }
        BaseLineSpecial baseLineSpecial = baseLineSpecialMapper.selectById(baseLine.getSpecialId());
        if(StringUtils.isEmpty(baseLine.getMonitorId())){
            String respones = lineUtil.runSpecialTaskBySsh(baseLineSpecial, JSONObject.parseObject(baseLine.getSpecialParam(), HashMap.class));
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
            String respones = lineUtil.runSpecialTaskBySsh(baseLineSpecial,JSONObject.parseObject(baseLine.getSpecialParam(),HashMap.class));
            if(StringUtils.isNotEmpty(respones)){
                String jobid = FlinkTools.parseJobId(respones);
                baseLine.setMonitorId(jobid);
                baseLineMapper.updateById(baseLine);
            }
        }
    }
}
