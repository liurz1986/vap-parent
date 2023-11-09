package com.vrv.vap.line.schedule.task;

import com.vrv.vap.line.tools.AbnormalLineAnalysis;

public class SequenceScoreTask extends BaseTask{
    @Override
    void run(String jobName) {
        AbnormalLineAnalysis task = new AbnormalLineAnalysis();
        task.renderData2();
    }
}
