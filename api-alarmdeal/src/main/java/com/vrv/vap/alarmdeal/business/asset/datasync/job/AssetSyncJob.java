package com.vrv.vap.alarmdeal.business.asset.datasync.job;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppSynchService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.DataInfoManageSyncService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;
import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;
import com.vrv.vap.alarmdeal.business.asset.datasync.constant.AssetDataSyncConstant;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetHandSyncService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetSyncService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.HandStrategyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;
import com.vrv.vap.alarmdeal.business.asset.util.QueUtil;
import com.vrv.vap.jpa.web.Result;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 资产关联处理
 * 1. 数据同步kafka资产数据
 * 2. 数据同步kafka数据信息数据
 * 3. 数据同步kafka应用系统数据
 *
 * 2023-4-11
 */
@Component
@Order(value = 18)
public class AssetSyncJob implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(AssetSyncJob.class);
    private List<Map<String,Object>> assets = new ArrayList<>();
    private List<AppSysManagerSynchVo> apps = new ArrayList<>();
    private List<AssetSyncVO> assetSyncVOs = new ArrayList<>();
    private List<DataInfoManage> dataInfos = new ArrayList<>();

    @Autowired
    private AppSynchService appVerifyService;
    @Autowired
    private DataInfoManageSyncService dataInfoManageSyncService;
    @Autowired
    private AssetSyncService assetSyncService;
    @Autowired
    private AssetHandSyncService assetHandSyncService;
    @Autowired
    private HandStrategyService handStrategyService;



    /**
     * 开始时间
     */
    private Date startTime = new Date();
    @Override
    public void run(String... args) throws Exception {
       new Thread(new Runnable() {
           @Override
           public void run() {
               excDataInfoManageSynch();
           }
       }).start();
    }

   private void excDataInfoManageSynch() {
      //  死循环，一直处理
       while (true) {
           try {
               Map<String,Object> data = QueUtil.assetRefQuePoll(); //获取队列数据
               if (null != data) {
                   assets.add(data);
                   dataStructure(data);
               }
               // 每500条处理一次或者10s处理一次
               boolean isFiveMin = getTimeResult();
               if (isFiveMin || assets.size() >= 500) {
                   if (CollectionUtils.isNotEmpty(assets)) {
                       excDataHandle(apps,assetSyncVOs,dataInfos,assets);
                       startTime = new Date();
                   }
               }
           } catch (Exception e) {
               logger.error("应用系统数据处理异常", e);
           }
       }
   }

    private void dataStructure(Map<String, Object> queData) {
        String key = String.valueOf(queData.get("key"));
        Object data = queData.get("data");
        switch (key){
            case  QueUtil.ASSET:
                AssetSyncVO assetSyncVO = JSONObject.parseObject(data.toString(),AssetSyncVO.class);
                assetSyncVOs.add(assetSyncVO);
                break;
            case  QueUtil.DATAINFO:
                DataInfoManage dataInfoManage =JSONObject.parseObject(data.toString(),DataInfoManage.class);
                dataInfos.add(dataInfoManage);
                break;
            case  QueUtil.APP:
                AppSysManagerSynchVo appSysdata =JSONObject.parseObject(data.toString(), AppSysManagerSynchVo.class);
                apps.add(appSysdata);
                break;
        }
    }

    private void excDataHandle(List<AppSysManagerSynchVo> apps, List<AssetSyncVO> assetSyncVOs, List<DataInfoManage> dataInfos,List<Map<String,Object>> assets) {
        assets.clear();
        if(apps.size() > 0){
            appVerifyService.excDataSync(apps);
        }
        if(dataInfos.size() > 0){
            dataInfoManageSyncService.excDataSync(dataInfos);
        }
        if(assetSyncVOs.size() > 0){
            Result<String> result =  handStrategyService.queryImportType();
            // 自动入库
            if(AssetDataSyncConstant.AUTO_IMPORT.equals(result.getData())){
                assetSyncService.excAssetDataSync(assetSyncVOs);
            }
            // 手动入库
            if(AssetDataSyncConstant.HAND_IMPORT.equals(result.getData())){
                assetHandSyncService.excAssetDataSync(assetSyncVOs);
            }
        }
    }

    /**
     * 获得时间
     *
     * @return
     */
    private boolean getTimeResult() {
        Date endTime = new Date();
        long timeSpan = (endTime.getTime() - startTime.getTime()) / 1000;
        if (timeSpan > 10) {
            startTime = new Date();
            return true;
        } else {
            return false;
        }
    }
}
