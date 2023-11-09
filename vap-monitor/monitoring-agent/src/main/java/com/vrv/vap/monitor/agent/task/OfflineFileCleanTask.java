package com.vrv.vap.monitor.agent.task;

import com.vrv.vap.monitor.agent.AgentApplication;
import com.vrv.vap.monitor.agent.config.BaseProperties;
import com.vrv.vap.monitor.agent.config.BatchQueueProperties;
import com.vrv.vap.monitor.agent.manager.ServerManager;
import com.vrv.vap.monitor.agent.task.base.MonitorBaseTask;
import com.vrv.vap.monitor.agent.utils.FileUtils;
import com.vrv.vap.monitor.agent.utils.JsonUtil;
import com.vrv.vap.monitor.agent.utils.ServiceCheckUtil;
import com.vrv.vap.monitor.common.model.AlarmInfo;
import com.vrv.vap.monitor.common.model.MonitorConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobDataMap;
import org.springframework.context.ApplicationContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//离线告警数据文件数据处理与清理
@Slf4j
public class OfflineFileCleanTask extends MonitorBaseTask {
    BatchQueueProperties batchQueueProperties;
    ServerManager serverManager;

    @Override
    public void run(String jobName, JobDataMap jobDataMap) {
        log.info("离线数据读取开始=========");
        ApplicationContext applicationContext = AgentApplication.getApplicationContext();
        serverManager=applicationContext.getBean(ServerManager.class);
        batchQueueProperties=applicationContext.getBean(BatchQueueProperties.class);
        setServerManager(serverManager);
        //查询kafka状态
        if (serverManager.getServerStateInfo().getStatus()&&ServiceCheckUtil.checkServiceStatus("monitoring-server")){
            //查询所有离线json文件
            File f = new File(batchQueueProperties.getFileFolder());
            File fa[] = f.listFiles();
            if (fa.length>0){
                for (File file:fa) {
                    //读取文件内容
                    if (file.getName().contains(batchQueueProperties.getFileSuffix())){
                        List<AlarmInfo>  fileInfo =readFileInfo(file.getPath());
                        if (fileInfo!=null){
                            Boolean offonline = serverManager.offonline(fileInfo);
                            if (offonline){
                                log.info("deleteFile",file.getPath());
                                FileUtils.deleteFile(file.getPath());
                            }
                        }else {
                            FileUtils.deleteFile(file.getPath());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(String jobName) {
      run(jobName,null);
    }
    public static  List<AlarmInfo> readFileInfo(String path) {
        List<AlarmInfo> alarmInfos  = new ArrayList<>();
        //读取文件数据、
        String tempString = null;
        String laststr = "";
        BufferedReader reader = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (StringUtils.isNotBlank(laststr)) {
            //转
            String[] split = laststr.split("}");
            for (String s:split) {
                if (StringUtils.isNotBlank(s)) {
                    s=s+"}";
                    AlarmInfo alarmInfo = JsonUtil.jsonToEntity(s, AlarmInfo.class);
                    if (alarmInfo!=null){
                        alarmInfos.add(alarmInfo);
                    }
                }
            }
        }

        return alarmInfos;
    }




    @Override
    public Boolean restart(MonitorConfig config) {

        return true;

    }

    @Override
    public Boolean checkRestartStatus(MonitorConfig config) {

        return true;
    }
}
