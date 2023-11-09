package com.vrv.vap.admin.service;

import com.vrv.flume.cmd.FlumeTools;
import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.vo.CollectorDataAccessVO;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.vo.Result;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 *@author lilang
 *@date 2022/1/5
 *@description
 */
public interface CollectorDataAccessService extends BaseService<CollectorDataAccess> {

    List<CollectorDataAccessVO> transformDataAccess(List<CollectorDataAccess> accessList);

    FlumeTools getFlumeTool();

    String generateFlumeConf(CollectorDataAccess collectorDataAccess, String ruleJson, String filePath, String jsContent);

    String getRuleContent(CollectorDataAccess collectorDataAccess);

    void restartFlume();

    /**
     * 获取运行的非内置流程数
     * @return
     */
    Long getRunningCount();

    /**
     * 下载日志
     * @param response
     * @param cid
     * @return
     */
    Result downloadLog(HttpServletResponse response, String cid);

    /**
     * 开启、关闭采集器时发送消息通知
     * @param cid
     * @param openStatus
     */
    void changeStatus(String cid, Boolean openStatus);

    /**
     * 已接产品上报数据
     * @return
     */
    List<Map<String,Object>> getAccessReport();

}
