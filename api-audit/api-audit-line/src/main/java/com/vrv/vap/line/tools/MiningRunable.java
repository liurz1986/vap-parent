package com.vrv.vap.line.tools;

import com.vrv.amt.utils.alg.FrequentSequenceDM;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.mapper.BaseLineSequenceMapper;
import com.vrv.vap.line.model.BaseLineFrequent;
import com.vrv.vap.line.model.BaseLineSequence;
import com.vrv.vap.line.schedule.task.BaseLineTask;
import com.vrv.vap.line.schedule.task.BaseTask;
import com.vrv.vap.line.service.BaseLineFrequentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class MiningRunable implements Runnable{

    private BaseLineSequence sequence;
    private String time;
    private Date now;
    private BaseLineSequenceMapper baseLineSequenceMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSequenceMapper.class);
    private BaseLineFrequentService baseLineFrequentService = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentService.class);

    public MiningRunable(BaseLineSequence sequence, String time, Date now) {
        this.sequence = sequence;
        this.time = time;
        this.now = now;
    }


    private static final Log log = LogFactory.getLog(BaseLineTask.class);

    @Override
    public void run() {
        log.info("频繁序列挖掘用户："+sequence.getUserIp()+" 系统："+sequence.getSysId());
        sequence.setStartTime(time);
        sequence.setEndTime(time);
        List<String> urls = baseLineSequenceMapper.queryByUserSys(sequence);
        if(CollectionUtils.isNotEmpty(urls)){
            Map<String, FrequentSequenceDM.Sequence> sq = FrequentSequenceDM.fromCollection(urls);
            BaseLineFrequent freq = this.baseLineFrequentService.findByUserAndSysid(sequence.getUserIp(),sequence.getSysId());
            List<String> frees = new ArrayList<>();
            if(freq != null){
                frees.add(freq.getFrequents());
            }else{
                freq = new BaseLineFrequent();
                freq.setUserId(sequence.getUserIp());
                freq.setCount(1);
                freq.setIsContinue(LineConstants.CONTINUE.YES);
                freq.setRole(sequence.getRole());
                freq.setOrg(sequence.getOrg());
                freq.setSysId(sequence.getSysId());
            }
            freq.setTime(now);
            if(sq != null && sq.size() > 0){
                sq.entrySet().forEach(r ->{
                    String item = r.getValue().getItem();
                    if(StringUtils.isNotEmpty(item)){
                        item = item.substring(0,item.length()-1);
                        frees.add(item);
                    }
                });
            }
            Collections.sort(frees, Comparator.comparingInt(i -> i.toString().length()).reversed());
            StringBuffer temStr = new StringBuffer();
            for(String str : frees){
                if(temStr.toString().indexOf(str) == -1){
                    if(StringUtils.isNotEmpty(temStr)){
                        temStr.append(LineConstants.SQ.itemSeparator);
                    }
                    temStr.append(str);
                }
            }
            freq.setFrequents(temStr.toString());
            this.baseLineFrequentService.updateFrequent(freq);
        }
    }
}
