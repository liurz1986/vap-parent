package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.asset.contract.SystemConfig;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.HandStrategyService;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HandStrategyServiceImpl implements HandStrategyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandStrategyServiceImpl.class);

    @Autowired
    private TbConfService tbConfService;
    @Autowired
    private AdminFeign adminFeign;
    // 手动入库策略配置信息
    private String[] configs={"sync_asset_data_source_type","sync_asset_data_key_type","sync_asset_data_diff_json"};


    /**
     * 更新规则时：
     * @param tbConfS
     */
    @Override
    public void updateAsset(List<TbConf> tbConfS) {
        LOGGER.info("更新手动入库策略");
        // 更新策略数据
        tbConfService.save(tbConfS);

    }

    @Override
    public List<TbConf> queyConfigAssets() {
        LOGGER.info("查询手动入库策略");
        // 缓存中没有查询
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("key",configs));
        return tbConfService.findAll(conditions);
    }

    @Override
    public Result<String> queryImportType() {
        VData<SystemConfig> vdata = adminFeign.getConfigById("sync_asset_data_import_type");
        SystemConfig systemConfig =  vdata.getData();
        if(null == systemConfig){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有配置入库方式");
        }
        String importType = systemConfig.getConfValue();
        return ResultUtil.success(importType);
    }

    @Override
    public Result<Map<String,Object>> queyConfigAssets(String key) {
        // 缓存中没有查询数据库
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("key",key));
        List<TbConf> list = tbConfService.findAll(conditions);
        if(CollectionUtils.isEmpty(list)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有配置对比列");
        }
        String value =list.get(0).getValue();
        Map<String,Object> data = JSON.parseObject(value,Map.class);
        return ResultUtil.success(data);
    }
    
}
