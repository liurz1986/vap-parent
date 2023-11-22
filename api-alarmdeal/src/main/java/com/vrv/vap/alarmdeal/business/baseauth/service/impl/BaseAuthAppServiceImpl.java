package com.vrv.vap.alarmdeal.business.baseauth.service.impl;



import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthApp;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthAppRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthAppService;
import com.vrv.vap.alarmdeal.business.baseauth.util.PValidUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthAppVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthAppQueryVo;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;



@Service
@Transactional
public class BaseAuthAppServiceImpl extends BaseServiceImpl<BaseAuthApp, Integer> implements BaseAuthAppService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthAppServiceImpl.class);

    @Autowired
    private BaseAuthAppRepository baseAuthAppRepository;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Override
    public BaseRepository<BaseAuthApp, Integer> getRepository() {
        return this.baseAuthAppRepository;
    }


    @Override
    public PageRes<BaseAuthAppVo> getPager(BaseAuthAppQueryVo baseAuthAppQueryVo) {
        PageRes<BaseAuthAppVo> baseAuthAppVoPageRes=new PageRes<>();
        List<QueryCondition> queryConditionsApp=new ArrayList<>();
        if (StringUtils.isNotBlank(baseAuthAppQueryVo.getIp())){
          List<QueryCondition> queryConditions=new ArrayList<>();
            queryConditions.add(QueryCondition.like("ip",baseAuthAppQueryVo.getIp()));
            List<BaseAuthApp> all = findAll(queryConditions);
            if (all.size()>0){
                List<Integer> collect = all.stream().map(a -> a.getAppId()).collect(Collectors.toList());
                queryConditionsApp.add(QueryCondition.in("id",collect));
            }else {
                queryConditionsApp.add(QueryCondition.isNull("id"));
            }
        }
        List<BaseAuthApp> all1 = findAll();
        if (all1.size()>0){
            List<Integer> collect = all1.stream().map(a -> a.getAppId()).collect(Collectors.toList());
            queryConditionsApp.add(QueryCondition.in("id",collect));
        }else {
            queryConditionsApp.add(QueryCondition.isNull("id"));
        }
        Page<AppSysManager> all = appSysManagerService.findAll(queryConditionsApp, baseAuthAppQueryVo.getPageable());
        PageRes<AppSysManager> res = PageRes.toRes(all);
        List<AppSysManager> list = res.getList();
        baseAuthAppVoPageRes.setTotal(res.getTotal());
        baseAuthAppVoPageRes.setCode(res.getCode());
        baseAuthAppVoPageRes.setMessage(res.getMessage());
        List<BaseAuthAppVo> baseAuthAppVos=new ArrayList<>();
        if (list.size()>0){
            for (AppSysManager appSysManager:list){
                BaseAuthAppVo baseAuthAppVo=new BaseAuthAppVo();
                baseAuthAppVo.setAppName(appSysManager.getAppName());
                String in=getIPs(appSysManager.getId(),0);
                String out=getIPs(appSysManager.getId(),1);
                baseAuthAppVo.setOutIp(out);
                baseAuthAppVo.setInsideIp(in);
                baseAuthAppVo.setId(appSysManager.getId());
                baseAuthAppVo.setAppId(appSysManager.getId());
                baseAuthAppVos.add(baseAuthAppVo);
            }
        }
        baseAuthAppVoPageRes.setList(baseAuthAppVos);
        return baseAuthAppVoPageRes;
    }

    private String getIPs(Integer id, int i) {
        List<QueryCondition> queryConditionList=new ArrayList<>();
        queryConditionList.add(QueryCondition.eq("appId",id));
        queryConditionList.add(QueryCondition.eq("type",i));
        List<BaseAuthApp> baseAuthAppList = findAll(queryConditionList);
        List<String> strings = baseAuthAppList.stream().map(a -> a.getIp()).collect(Collectors.toList());
        if (strings.size()>0){
            return String.join(",",strings);
        }
        return "";
    }

    @Override
    public Result<BaseAuthAppVo> addAuthApp(BaseAuthAppVo baseAuthAppVo) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("id",baseAuthAppVo.getAppId()));
        List<AppSysManager> all = appSysManagerService.findAll(queryConditions);
        if (all.size()==0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "应用系统不存在");
        }
        List<QueryCondition> queryConditionAppAuth=new ArrayList<>();
        queryConditionAppAuth.add(QueryCondition.eq("appId",baseAuthAppVo.getAppId()));
        long count = count(queryConditionAppAuth);
        if (count>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该应用系统审批信息已存在");
        }
        if (!PValidUtil.isIPValid(baseAuthAppVo.getOutIp())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "外部授权访问ip格式异常");
        }
        if (!PValidUtil.isIPValid(baseAuthAppVo.getInsideIp())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "内部授权访问ip格式异常");
        }
        if (PValidUtil.hasDuplicate(baseAuthAppVo.getInsideIp())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "外部授权访问ip存在重复ip");
        }
        if (PValidUtil.hasDuplicate(baseAuthAppVo.getOutIp())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "内部授权访问ip存在重复ip");
        }
        List<BaseAuthApp> baseAuthAppList=new ArrayList<>();
        if (StringUtils.isNotBlank(baseAuthAppVo.getOutIp())){
            String[] split = baseAuthAppVo.getOutIp().split(",");
            for (String s:split){
                BaseAuthApp baseAuthApp=new BaseAuthApp();
                baseAuthApp.setCreateTime(new Date());
                baseAuthApp.setIp(s);
                baseAuthApp.setAppId(baseAuthAppVo.getAppId());
                baseAuthApp.setType(1);
                baseAuthAppList.add(baseAuthApp);
            }
        }
        if (StringUtils.isNotBlank(baseAuthAppVo.getInsideIp())){
            String[] split = baseAuthAppVo.getInsideIp().split(",");
            for (String s:split){
                BaseAuthApp baseAuthApp=new BaseAuthApp();
                baseAuthApp.setCreateTime(new Date());
                baseAuthApp.setIp(s);
                baseAuthApp.setAppId(baseAuthAppVo.getAppId());
                baseAuthApp.setType(0);
                baseAuthAppList.add(baseAuthApp);
            }
         save(baseAuthAppList);
        }
        try {
            QueueUtil.putAuth(3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        baseAuthAppVo.setAppName(all.get(0).getAppName());
        return ResultUtil.success(baseAuthAppVo);
    }

    @Override
    public Result<BaseAuthAppVo> updateAuthApp(BaseAuthAppVo baseAuthAppVo) {
        if (!PValidUtil.isIPValid(baseAuthAppVo.getOutIp())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "外部授权访问ip格式异常");
        }
        if (!PValidUtil.isIPValid(baseAuthAppVo.getInsideIp())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "内部授权访问ip格式异常");
        }
        //删除老数据
        deleteOldData(baseAuthAppVo);
        List<BaseAuthApp> baseAuthAppList=new ArrayList<>();
        if (StringUtils.isNotBlank(baseAuthAppVo.getOutIp())){
            String[] split = baseAuthAppVo.getOutIp().split(",");
            for (String s:split){
                BaseAuthApp byIp=getByIp(s,baseAuthAppVo.getAppId(),1);
                BaseAuthApp baseAuthApp=new BaseAuthApp();
                if (byIp!=null){
                    baseAuthApp.setId(byIp.getId());
                    baseAuthApp.setCreateTime(byIp.getCreateTime());
                }else {
                    baseAuthApp.setCreateTime(new Date());
                }
                baseAuthApp.setIp(s);
                baseAuthApp.setAppId(baseAuthAppVo.getAppId());
                baseAuthApp.setType(1);
                baseAuthAppList.add(baseAuthApp);
            }
        }
        if (StringUtils.isNotBlank(baseAuthAppVo.getInsideIp())){
            String[] split = baseAuthAppVo.getInsideIp().split(",");
            for (String s:split){
                BaseAuthApp byIp=getByIp(s,baseAuthAppVo.getAppId(),0);
                BaseAuthApp baseAuthApp=new BaseAuthApp();
                if (byIp!=null){
                    baseAuthApp.setId(byIp.getId());
                    baseAuthApp.setCreateTime(byIp.getCreateTime());
                }else {
                    baseAuthApp.setCreateTime(new Date());
                }
                baseAuthApp.setIp(s);
                baseAuthApp.setAppId(baseAuthAppVo.getAppId());
                baseAuthApp.setType(0);
                baseAuthAppList.add(baseAuthApp);
            }
            save(baseAuthAppList);
        }
        List<QueryCondition> queryConditionsApp=new ArrayList<>();
        queryConditionsApp.add(QueryCondition.eq("id",baseAuthAppVo.getAppId()));
        List<AppSysManager> appSysManagers = appSysManagerService.findAll(queryConditionsApp);
        baseAuthAppVo.setAppName(appSysManagers.get(0).getAppName());
        try {
            QueueUtil.putAuth(3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(baseAuthAppVo);
    }

    private BaseAuthApp getByIp(String s, Integer appId, int i) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("appId",appId));
        queryConditions.add(QueryCondition.eq("type",i));
        queryConditions.add(QueryCondition.eq("ip",s));
        List<BaseAuthApp> all = findAll(queryConditions);
        if (all.size()>0){
            return  all.get(0);
        }
        return null;
    }


    private void deleteOldData(BaseAuthAppVo baseAuthAppVo) {
        List<BaseAuthApp> authApps=new ArrayList<>();
        if (StringUtils.isNotBlank(baseAuthAppVo.getInsideIp())){
            String[] split = baseAuthAppVo.getInsideIp().split(",");
            List<QueryCondition> queryConditions1=new ArrayList<>();
            queryConditions1.add(QueryCondition.eq("appId",baseAuthAppVo.getAppId()));
            queryConditions1.add(QueryCondition.not(QueryCondition.in("ip",Arrays.asList(split))));
            queryConditions1.add(QueryCondition.eq("type",0));
            List<BaseAuthApp> all = findAll(queryConditions1);
            authApps.addAll(all);
        }
        if (StringUtils.isNotBlank(baseAuthAppVo.getOutIp())){
            String[] split = baseAuthAppVo.getOutIp().split(",");
            List<QueryCondition> queryConditions1=new ArrayList<>();
            queryConditions1.add(QueryCondition.eq("appId",baseAuthAppVo.getAppId()));
            queryConditions1.add(QueryCondition.not(QueryCondition.in("ip",Arrays.asList(split))));
            queryConditions1.add(QueryCondition.eq("type",1));
            List<BaseAuthApp> all = findAll(queryConditions1);
            authApps.addAll(all);
        }
        if (authApps.size()>0){
            deleteInBatch(authApps);
        }
    }

    @Override
    public Result<String> delAuthApp(BaseAuthAppVo baseAuthAppVo) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("appId",baseAuthAppVo.getId()));
        List<BaseAuthApp> all = findAll(queryConditions);
        if (all.size()>0){
            deleteInBatch(all);
        }
        try {
            QueueUtil.putAuth(3);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success("success");
    }

    @Override
    public void addAuthAppByName(BaseAuthAppVo baseAuthAppVo) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("appName",baseAuthAppVo.getAppName()));
        List<AppSysManager> all = appSysManagerService.findAll(queryConditions);
        if (all.size()>0){
            baseAuthAppVo.setAppId(all.get(0).getId());
            List<QueryCondition> queryConditionAppAuth=new ArrayList<>();
            queryConditionAppAuth.add(QueryCondition.eq("appId",baseAuthAppVo.getAppId()));
            long count = count(queryConditionAppAuth);
            if (count>0){
                return ;
            }
            List<BaseAuthApp> baseAuthAppList=new ArrayList<>();
            if (StringUtils.isNotBlank(baseAuthAppVo.getOutIp())){
                String[] split = baseAuthAppVo.getOutIp().split(",");
                for (String s:split){
                    BaseAuthApp baseAuthApp=new BaseAuthApp();
                    baseAuthApp.setCreateTime(new Date());
                    baseAuthApp.setIp(s);
                    baseAuthApp.setAppId(baseAuthAppVo.getAppId());
                    baseAuthApp.setType(1);
                    baseAuthAppList.add(baseAuthApp);
                }
            }
            if (StringUtils.isNotBlank(baseAuthAppVo.getInsideIp())){
                String[] split = baseAuthAppVo.getInsideIp().split(",");
                for (String s:split){
                    BaseAuthApp baseAuthApp=new BaseAuthApp();
                    baseAuthApp.setCreateTime(new Date());
                    baseAuthApp.setIp(s);
                    baseAuthApp.setAppId(baseAuthAppVo.getAppId());
                    baseAuthApp.setType(0);
                    baseAuthAppList.add(baseAuthApp);
                }
                save(baseAuthAppList);
            }
        }


    }
}
