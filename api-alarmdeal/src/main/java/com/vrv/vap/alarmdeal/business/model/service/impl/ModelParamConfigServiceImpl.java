package com.vrv.vap.alarmdeal.business.model.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.model.model.ModelManage;
import com.vrv.vap.alarmdeal.business.model.model.ModelParamConfig;
import com.vrv.vap.alarmdeal.business.model.repository.ModelParamConfigRepository;
import com.vrv.vap.alarmdeal.business.model.service.ModelManageService;
import com.vrv.vap.alarmdeal.business.model.service.ModelParamConfigService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelParamConfigServiceImpl extends BaseServiceImpl<ModelParamConfig,String> implements ModelParamConfigService {

    @Autowired
    private ModelParamConfigRepository moodelParamConfigRepository;

    @Autowired
    private ModelManageService modelManageService;

    @Override
    public BaseRepository<ModelParamConfig, String> getRepository() {
        return moodelParamConfigRepository;
    }

    /**
     * 获取模型参数
     * @param guid
     * @return
     */
    @Override
    public Result<List<ModelParamConfig>> queryModelParamList(String guid) {
        ModelManage model =this.modelManageService.getOne(guid);
        if(null == model){
            ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有查到模型配置信息！");
        }
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("modelManageId",guid));
        List<ModelParamConfig> paramConfigs = this.findAll(conditions);
        return ResultUtil.successList(paramConfigs);
    }

    /**
     * 模型发布：以默认值加载全部参数
     * @param guid
     * @return
     */
    @Override
    public Result<Map<String, Object>> queryParamConfigByGuid(String guid) {
        List<ModelParamConfig> paramConfigs = this.queryModelParamList(guid).getList();
        // 组装成json字符
        Map<String, Object> params = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        if(null == paramConfigs || paramConfigs.size() ==0 ){
            return ResultUtil.success(null);
        }
        for(ModelParamConfig param : paramConfigs){
            // 当前值没有，取默认值
            params.put(param.getName(),param.getParamValue()==null?param.getParamDefaultValue():param.getParamDefaultValue());
        }
        data.put("params", JSONObject.toJSONString(params));
        return ResultUtil.success(data);
    }

    /**
     * 模型发布：模型配置的参数名称下拉列表
     * @param guid
     * @return
     */
    @Override
    public Result<List<String>> queryParamNamesByGuid(String guid) {
        List<ModelParamConfig> paramConfigs = this.queryModelParamList(guid).getList();
        // 组装成json字符
        List<String> names = new ArrayList<>();
        if(null == paramConfigs || paramConfigs.size() ==0 ){
            return ResultUtil.successList(null);
        }
        for(ModelParamConfig param : paramConfigs){
            if(!names.contains(param.getName())){
                names.add(param.getName());
            }
        }
        return ResultUtil.successList(names);
    }
}
