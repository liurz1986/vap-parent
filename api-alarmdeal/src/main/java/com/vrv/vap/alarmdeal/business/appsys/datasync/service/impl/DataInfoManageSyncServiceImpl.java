package com.vrv.vap.alarmdeal.business.appsys.datasync.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.datasync.service.DataInfoManageSyncService;
import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.DataInfoManageService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 信息管理数据同步
 *
 * 2022-06-01
 */
@Service
@Transactional
public class DataInfoManageSyncServiceImpl implements DataInfoManageSyncService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInfoManageSyncServiceImpl.class);
    @Autowired
    private DataInfoManageService dataInfoManageService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ClassifiedLevelService classifiedLevelService;

    private List<DataInfoManage> allDatas;

    private List<String> secretLevels;

    private List<DataInfoManage> saveLists;

    private int failNum;

    @Override
    public void excDataSync(List<DataInfoManage> dataInfoManages) {
        LOGGER.debug("信息管理数据总数量为："+dataInfoManages.size());
        try{
            // 初始化数据
            initData();
            // 循环校验处理
            for(DataInfoManage dataInfoManage : dataInfoManages){
                excDataHandle(dataInfoManage);
            }
            LOGGER.debug("信息管理数据校验失败数量为："+failNum);
            // 保存数据及发送消息
            LOGGER.debug("信息管理数据入库数量为："+saveLists.size());
            batchSaveDataInfos(saveLists);
            LOGGER.debug("信息管理数据同");
        }catch (Exception e){
            LOGGER.error("信息管理数据同步异常",e);
        }
        dataInfoManages.clear();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        allDatas = dataInfoManageService.findAll();
        secretLevels= classifiedLevelService.getDataInfoSecretLevelAllCodes();
        saveLists = new ArrayList<>();
        failNum = 0;
    }

    /**
     * 数据处理
     * @param dataInfoManage
     */
    private void excDataHandle(DataInfoManage dataInfoManage){
        Result<String>  validateResult =dataSynchandle(dataInfoManage);
        // 数据入库失败原因输出日志打印出来
        if (validateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            failNum ++;
            LOGGER.debug("数据入库失败原因输出："+validateResult.getMsg());
        }
    }
    /**
     * 批量保存数据及发送消息
     * @param saveLists
     */
    private void batchSaveDataInfos(List<DataInfoManage> saveLists) {
        if(saveLists.size() == 0){
            return;
        }
        dataInfoManageService.save(saveLists);
        // 数据变更消息推送
        messageService.sendKafkaMsg("file");
    }

    /**
     * 数据处理
     * @param dataInfoManage
     * @return
     */
    public Result<String> dataSynchandle(DataInfoManage dataInfoManage) {
        // 数据标识判断数据是不是存在
        String dataFlag = dataInfoManage.getDataFlag();
        if(StringUtils.isEmpty(dataFlag)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据标识为空，数据不处理");
        }
        DataInfoManage dataInfo =  getDataInfoManageByDataFlag(allDatas,dataFlag);
        dataInfoManage.setCreateTime(new Date());
        if(null != dataInfo){
            // 存在的话用以前的id，其他数据覆盖处理
            dataInfoManage.setId(dataInfo.getId());
        }
        // 数据必填，有效性校验
        Result<String>  validateResult =dataValidate(dataInfoManage);
        if (validateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return validateResult;
        }
        // 保存数据
        saveLists.add(dataInfoManage);
        return  ResultUtil.success("success");
    }

    private DataInfoManage getDataInfoManageByDataFlag(List<DataInfoManage> allDatas, String dataFlag) {
        for(DataInfoManage data : allDatas){
            if(dataFlag.equals(data.getDataFlag())){
                return  data;
            }
        }
        return  null;
    }

    /**
     * 数据标识、业务类型、涉密等级、数据来源类型、外部来源信息必填
     * 涉密等级有效性
     * 文件大小格式校验:不为空进行校验，数据类型
     * @param dataInfoManage
     * @return
     */
    private Result<String> dataValidate(DataInfoManage dataInfoManage) {
        // 数据校验：数据标识、业务类型、涉密等级必填校验，校验ok才能入库，其中涉密等级符合要求
        String secretLevel = dataInfoManage.getSecretLevel();
        if(StringUtils.isEmpty(secretLevel)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级为空，数据不处理");
        }
        String businessType = dataInfoManage.getBusinessType();
        if(StringUtils.isEmpty(businessType)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"业务类型为空，数据不处理");

        }
        // 验证涉密等级是不是符合现有子典配置
        if(!secretLevels.contains(secretLevel.trim())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级的值不合法，数据不处理，涉密等级的值为："+secretLevel);
        }
        dataInfoManage.setSecretLevel(secretLevel.trim()); // 涉密等级转换后的值
        // 数据来源类型
        int dataSourceType = dataInfoManage.getDataSourceType();
        // 判读是不是数据同步
        if(2 != dataSourceType){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前数据来源类型不符合要求，"+dataSourceType);
        }
        // 外部来源信息
        String syncSource = dataInfoManage.getSyncSource();
        if(StringUtils.isEmpty(syncSource)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"外部来源信息不能为空");
        }
        // 文件大小格式校验:不为空进行校验，数据类型
        String fileSize = dataInfoManage.getFileSize();
        if(!dataInfoManageService.validateFileSize(fileSize)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"文件大小格式校验失败");
        }
        return ResultUtil.success("success");
    }


}
