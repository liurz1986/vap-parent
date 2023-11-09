package com.vrv.vap.alarmdeal.business.baseauth.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.service.DataInfoManageService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthCommonConfig;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthTypeConfig;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthCommonConfigRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthCommonConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthTypeConfigService;
import com.vrv.vap.alarmdeal.business.baseauth.util.BaseAuthUtil;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础数据
 *  2023-08
 * @Author liurz
 */
@Service
@Transactional
public class BaseAuthCommonConfigServiceImpl extends BaseServiceImpl<BaseAuthCommonConfig, String> implements BaseAuthCommonConfigService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthCommonConfigServiceImpl.class);
    @Autowired
    private BaseAuthCommonConfigRepository baseAuthCommonConfigRepository;

    @Autowired
    private AssetService assetService;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Autowired
    private DataInfoManageService dataInfoManageService;
    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private BaseAuthTypeConfigService baseAuthTypeConfigService;
    @Override
    public BaseRepository<BaseAuthCommonConfig, String> getRepository() {
        return this.baseAuthCommonConfigRepository;
    }

    /**
     * 获取配置信息
     * SRC_OBJ_TYPE:源对象类型
     * DST_OBJ_TYPE:目标对象类型
     * OPT_TYPE:动作
     * OPT_TYPE_REF：源与目的组合对应的操作类型
     * @return
     */
    @Override
    public List<BaseAuthCommonConfig> getBaseConfig() {
        List<QueryCondition> conditions = new ArrayList<>();
        List<String> confids = new ArrayList<>();
        confids.add("SRC_OBJ_TYPE");
        confids.add("DST_OBJ_TYPE");
        confids.add("OPT_TYPE");
        confids.add("OPT_TYPE_REF");
        conditions.add(QueryCondition.in("confId",confids));
        return this.findAll(conditions);
    }
    /**
     * 获取关联基础数据
     * 网络设备	NetworkDevice
     * 服务器	service
     * 安全保密设备	SafeDevice
     * 终端	assetHost
     * 运维终端	maintenHost
     * USB存储	USBMemory
     * USB外设设备	USBPeripheral
     * 用户 user
     * 文件	dataInfoManage
     * 应用系统	app
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> getRefBaseData(String code) {
        switch (code){
            case BaseAuthUtil.ASSETHOST:
            case BaseAuthUtil.SERVICE:
            case BaseAuthUtil.MAINTENTHOST:
            case BaseAuthUtil.NETWORKDEVICE:
            case BaseAuthUtil.SAFEDEVICE:
               return assetService.getAssetMsg(code);
            case BaseAuthUtil.USBMEMORG:
            case BaseAuthUtil.USBPERIPHERAL:
                return assetService.getUsb(code);
            case BaseAuthUtil.APP:
                return appSysManagerService.getAppsAuth();
            case BaseAuthUtil.DATAINFOMANAGE:
                return dataInfoManageService.getFilesAuth();
            case BaseAuthUtil.USER:
                return getUsers();
        }
        return null;
    }

    /**
     * 获取动态展示列
     * 其中操作类型为固定展示列
     *
     * assetHost：源对象或目的对象中的code的值
     * [{"name":"终端名称","code":"name"},{"name":"终端IP","code":"flag"}]：
     * name对应的值就是展示字段名称，code对应的name就是展示对应的名称子段，flag对应的标识字段
     * @param typeId
     * @return
     */
    @Override
    public Map<String, Object> getColumns(String typeId) {
        Map<String, Object> result = new HashMap<>();
        BaseAuthCommonConfig config = this.getOne("COLUMN_DISPLAY");
        if(null == config){
            logger.error("对象展示列名配置数据不存在！");
            throw new AlarmDealException(-1,"对象展示列名配置数据不存在！");
        }
        BaseAuthTypeConfig baseAuthTypeConfigfig = baseAuthTypeConfigService.getOne(Integer.parseInt(typeId));
        if(null == baseAuthTypeConfigfig){
            logger.error(typeId+"对应的审批类型配置数据不存在！");
            throw new AlarmDealException(-1,typeId+"对应的审批类型配置数据不存在！");
        }
        logger.info("列展示名称配置数据："+JSON.toJSONString(baseAuthTypeConfigfig));
        String srcFlag = baseAuthTypeConfigfig.getSrcObjtype();
        String dstFlag = baseAuthTypeConfigfig.getDstObjtype();
        String configValue = config.getConfValue();
        Map<String,Object> maps = JSON.parseObject(configValue,Map.class);
        Object srcObj = maps.get(srcFlag);
        if(null == srcObj){
            logger.error(srcFlag+":对应配置的列展示数据为空");
        }else{
            List<Map> lists = JSONArray.parseArray(srcObj.toString(),Map.class);
            for(Map map : lists){
               String name = String.valueOf(map.get("name"));
               String code = String.valueOf(map.get("code"));
               // 源对象名称
               if("name".equals(code)){
                   result.put("srcObjLabel",name);
               }
               // srcObj
               if("flag".equals(code)){
                   result.put("srcObj",name);
               }
            }
        }
        Object dstObj = maps.get(dstFlag);
        if(null == dstObj){
            logger.error(dstObj+":对应配置的列展示数据为空");
        }else{
            List<Map> lists = JSONArray.parseArray(dstObj.toString(),Map.class);
            for(Map map : lists){
                String name = String.valueOf(map.get("name"));
                String code = String.valueOf(map.get("code"));
                // 目的对象标识
                if("name".equals(code)){
                    result.put("dstObjLabel",name);
                }
                //  目的对象名称
                if("flag".equals(code)){
                    result.put("dstObj",name);
                }
            }
        }
        // 固定一个列：操作类型
        result.put("opt","操作类型");
        return result;
    }

    /**
     * 获取审批类型下的操作类型
     * @param typeId
     * @return
     */
    @Override
    public List<Map> getOptType(String typeId) {
        BaseAuthCommonConfig config = this.getOne("OPT_TYPE_REF");
        if(null == config){
            logger.error("源与目的组合对应的操作类型配置数据不存在！");
            throw new AlarmDealException(-1,"源与目的组合对应的操作类型配置数据不存在！");
        }
        BaseAuthTypeConfig baseAuthTypeConfigfig = baseAuthTypeConfigService.getOne(Integer.parseInt(typeId));
        String srcFlag = baseAuthTypeConfigfig.getSrcObjtype();
        String dstFlag = baseAuthTypeConfigfig.getDstObjtype();
        String code=srcFlag+"-"+dstFlag;
        String configValue = config.getConfValue();
        List<Map> lists = JSONArray.parseArray(configValue,Map.class);
        Object queryOpty = getOptTypeByCocde(lists,code);
        if(null == queryOpty){
            return null;
        }
        List<Map> valuesList = JSONArray.parseArray(queryOpty.toString(),Map.class);
        return valuesList;
    }

    private Object getOptTypeByCocde(List<Map> lists, String code) {
        for(Map map :lists){
            String name = String.valueOf(map.get("name"));
            if(code.equals(name)){
                return map.get("value");
            }
        }
        return null;
    }

    /**
     * 调用feign接口获取用户信息
     * @return
     */
    private Result<List<Map<String, Object>>> getUsers() {
        try{
            VData<List<Map<String,Object>>> result =  adminFeign.getUserAuth();
            List<Map<String,Object>> users = result.getData();
            return ResultUtil.successList(users);
        }catch (Exception e){
            logger.error("调用api-admin，获取用户信息异常：{}",e);
            throw new AlarmDealException(-1,"调用api-admin，获取用户信息接口异常");
        }
    }
}
