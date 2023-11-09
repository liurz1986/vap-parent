package com.vrv.vap.alarmdeal.business.appsys.datasync.controller;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.appsys.datasync.service.AppVerifyService;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppVerifySearchVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetSyncVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.BaseSynchKafkaVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用系统待审
 *
 * 2022-07
 */
@RestController
@RequestMapping("/appVerify")
public class AppVerifyController {
    private static Logger logger = LoggerFactory.getLogger(AppVerifyController.class);

    @Autowired
    private AppVerifyService appVerifyService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 待申库数据编辑
     * @param appSysManagerVerify
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="待申库数据编辑",notes="")
    @SysRequestLog(description="待申库数据编辑", actionType = ActionType.UPDATE,manually=false)
    public Result<String> saveEditdData(@RequestBody AppSysManagerVerify appSysManagerVerify){
        try{
            return appVerifyService.saveEditdData(appSysManagerVerify);
        }catch (Exception e){
            logger.error("编辑异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"编辑异常");
        }
    }

    /**
     * 执行忽略
     * @param appSysManagerVerify
     * @return
     */
    @PostMapping(value="/neglect")
    @ApiOperation(value="执行忽略",notes="")
    @SysRequestLog(description="执行忽略", actionType = ActionType.UPDATE,manually=false)
    public Result<String> neglect(@RequestBody AppSysManagerVerify appSysManagerVerify){
        try{
            return appVerifyService.neglect(appSysManagerVerify.getId());
        }catch (Exception e){
            logger.error("忽略异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"忽略异常");
        }
    }

    /**
     * 待申表查询
     * @param searchVO
     * @return
     */
    @PostMapping(value="/getPage")
    @ApiOperation(value="待申表查询",notes="")
    @SysRequestLog(description="待申表查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<AppSysManagerVerify> query(@RequestBody AppVerifySearchVO searchVO){
        return appVerifyService.query(searchVO);
    }

    /**
     * 单条记录入库
     * @param appSysManagerVerify
     * @return
     */
    @PostMapping(value="/saveApp")
    @ApiOperation(value="应用系统待审单条记录入库",notes="")
    @SysRequestLog(description="应用系统待审单条记录入库", actionType = ActionType.UPDATE,manually=false)
    public Result<String> saveApp(@RequestBody AppSysManagerVerify appSysManagerVerify){
        try{
            return appVerifyService.saveApp(appSysManagerVerify.getId());
        }catch (Exception e){
            logger.error("单条记录入库异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"单条记录入库异常");
        }
    }

    /**
     * 批量入库
     * @return
     */
    @PostMapping(value="/batchSaveApp")
    @ApiOperation(value="应用系统待审批量入库",notes="")
    @SysRequestLog(description="应用系统待审批量入库", actionType = ActionType.ADD,manually=false)
    public Result<String> batchSaveApp (){
        try{
            return appVerifyService.batchSaveApp();
        }catch (Exception e){
            logger.error("批量入库异常",e);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"批量入库异常");
        }
    }



    /**
     * 模拟发kafka消息--app
     * @param appKafkaVO
     * @return
     */
    @PostMapping(value="/testKafka")
    public Result<String> testKafka(@RequestBody BaseSynchKafkaVO appKafkaVO){
        kafkaTemplate.send("sync-base-data-app", JSON.toJSONString(appKafkaVO));
        return ResultUtil.success("success");
    }

    /**
     * 模拟发kafka消息--app
     * 批量200条
     * @param
     * @return
     */
    @PostMapping(value="/batchTestKafka")
    public Result<String> batchTestKafka( ){
        BaseSynchKafkaVO baseData = new BaseSynchKafkaVO();
        for(int i= 1000 ;i < 1200;i++){
            kafkaTemplate.send("sync-base-data-app", JSON.toJSONString(getData(i)));
        }
        return ResultUtil.success("success");
    }

    private BaseSynchKafkaVO getData(int appNo){
        BaseSynchKafkaVO baseData = new BaseSynchKafkaVO();
        AppSysManagerSynchVo data = new AppSysManagerSynchVo();
        data.setAppName("北信源综合审计监管系统");
        data.setAppNo(appNo+"");
        data.setDepartmentName("武汉市保密办");
        data.setDepartmentGuid("000000000000");
        data.setSecretCompany("北信源");
        data.setSecretLevel("4");
        data.setDomainName("https://192.168.120.171");
        data.setSyncUid(appNo+"");
        data.setDataSourceType(2);
        data.setSyncSource("bxy-zs");
        baseData.setData(data);
        baseData.setDataType("app");
        return baseData;
    }
}
