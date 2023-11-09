package com.vrv.vap.line.statistics;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.amt.utils.alg.FrequentSequenceDM;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.mapper.BaseLineSequenceMapper;
import com.vrv.vap.line.model.BaseLine;
import com.vrv.vap.line.model.BaseLineFrequent;
import com.vrv.vap.line.model.BaseLineSequence;
import com.vrv.vap.line.service.BaseLineFrequentService;
import com.vrv.vap.line.tools.FrequentAttrClear;
import com.vrv.vap.line.tools.FrequentOrgAll;
//import com.vrv.vap.line.tools.FrequentRoleAll;
import com.vrv.vap.line.tools.MiningRunable;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 频繁序列挖掘任务
 */
public class FrequentMiningTask extends BaseStatisticsTask{
    private BaseLineSequenceMapper baseLineSequenceMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSequenceMapper.class);
    private BaseLineFrequentService baseLineFrequentService = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentService.class);
    private static ExecutorService exec = Executors.newFixedThreadPool(10);

    @Override
    public void execute(Map<String, Object> params) {
        int cycle = 1;
        log.info("频繁序列挖掘任务开始");
        if(params.containsKey("cycle")){
            cycle = Integer.parseInt(params.get("cycle").toString());
        }
        Date now = new Date();
        String startTime = TimeTools.format(TimeTools.getNowBeforeByDay(cycle),"yyyy-MM-dd");
        String endTime = TimeTools.format(TimeTools.getNowBeforeByDay2(1),"yyyy-MM-dd");
//        String startTime = "2022-12-22";
//        String endTime = "2022-12-22";
        BaseLineSequence record = new BaseLineSequence();
        record.setStartTime(startTime);
        record.setEndTime(endTime);
        List<BaseLineSequence> baseLineSequences = baseLineSequenceMapper.groupUserSys(record);
        if(CollectionUtils.isNotEmpty(baseLineSequences)){
            baseLineSequences.forEach(sequence ->{
                exec.execute(new MiningRunable(sequence,startTime,now));
            });
        }
        //new FrequentRoleAll().updateFrequent();
        new FrequentOrgAll().updateFrequent();
        new FrequentAttrClear().unionAndClear();
        log.info("频繁序列挖掘任务结束");
    }
}
