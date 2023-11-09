package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.job;

import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年06月27日 16:40
 */
@Configuration
@EnableScheduling
public class DeleteFlinkJob {
    private static Logger logger = LoggerFactory.getLogger(DeleteFlinkJob.class);

    @Scheduled(cron = "${delete.flink.process.time}")
    public void deleteFlinkJob() {
        logger.warn("定时deleteFlinkJob start");
        kill();
    }

    /**
     * 删除进程
     */
    public void kill() {
        List<String> pids = getFlinkMainPids();
        if (CollectionUtils.isNotEmpty(pids) && pids.size() > 1) {
            killPid(pids);
        }
    }

    /**
     * 查询 flink启动 main函数 pid
     *
     * @return
     */
    public List<String> getFlinkMainPids() {
        String[] shell = {"bash", "-c", "ps -x | grep com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction | perl -nle 'print $1 if /^ *([0-9]+)/'"};
        List<String> contents = ShellExecuteScript.executeShellArrayByResult(shell);
        return contents;
    }

    /**
     * kill 调服务
     *
     * @param pids
     */
    public void killPid(List<String> pids) {
        String pidStr = String.join(" ", pids);
        logger.info("deleteFlinkJob kill pids={}",pidStr);
        String[] shell = {"bash", "-c", "kill -9 " + pidStr};
        ShellExecuteScript.executeShellByResultArray(shell);
    }
}
