package com.vrv.vap.xc.schedule.task;


import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.service.DataSourceManagerService;

/**
 * 读取json写入数据源管理
 */
public class DatasourceReadTask extends BaseTask {

    private DataSourceManagerService client = VapXcApplication.getApplicationContext().getBean(DataSourceManagerService.class);

    @Override
    void run(String jobName) {
        client.readJsonDataAndStore();
    }
}
