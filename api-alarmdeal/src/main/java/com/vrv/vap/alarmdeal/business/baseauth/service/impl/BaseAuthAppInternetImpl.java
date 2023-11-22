package com.vrv.vap.alarmdeal.business.baseauth.service.impl;



import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import com.vrv.vap.alarmdeal.business.appsys.model.InternetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.InternetInfoManageService;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthInternet;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthInternetRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthInternetService;
import com.vrv.vap.alarmdeal.business.baseauth.util.PValidUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthInternetVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class BaseAuthAppInternetImpl extends BaseServiceImpl<BaseAuthInternet, Integer> implements BaseAuthInternetService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthAppInternetImpl.class);

    @Autowired
    private BaseAuthInternetRepository baseAuthInternetRepository;
    @Autowired
    private InternetInfoManageService internetInfoManageService;
    @Autowired
    private MapperUtil mapper;
    @Override
    public BaseRepository<BaseAuthInternet, Integer> getRepository() {
        return this.baseAuthInternetRepository;
    }


    @Override
    public Result<BaseAuthInternetVo> addAuthInt(BaseAuthInternetQueryVo baseAuthInternet) {
        BaseAuthInternetVo baseAuthInternetVo=new BaseAuthInternetVo();
        InternetInfoManage one = internetInfoManageService.getOne(baseAuthInternet.getInternetId());
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("internetId",baseAuthInternet.getInternetId()));
        long count = count(queryConditions);
        if (count>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该互联单位审批信息已存在");
        }
        String ips = baseAuthInternet.getIps();
        if (!PValidUtil.isIPValid(ips)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "设备ip格式错误");
        }
        if (PValidUtil.hasDuplicate(ips)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "设备ip存在重复ip");
        }
        String[] split = ips.split(",");
        List<BaseAuthInternet> baseAuthInternets=new ArrayList<>();
        for (String ip:split){
            BaseAuthInternet baseAuthInternet1=new BaseAuthInternet();
            baseAuthInternet1.setInternetId(baseAuthInternet.getInternetId());
            baseAuthInternet1.setIp(ip);
            baseAuthInternet1.setCreateTime(new Date());
            baseAuthInternets.add(baseAuthInternet1);
        }
        save(baseAuthInternets);
        baseAuthInternetVo.setInternetName(one.getInternetName());
        baseAuthInternetVo.setId(one.getId());
        baseAuthInternetVo.setIps(baseAuthInternet.getIps());
        baseAuthInternetVo.setSecretLevel(one.getSecretLevel());
        baseAuthInternetVo.setName(one.getName());
        baseAuthInternetVo.setIp(one.getIp());
        try {
            QueueUtil.putAuth(4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(baseAuthInternetVo);
    }
    @Override
    public void addAuthInterneByName(BaseAuthInternetVo baseAuthInternet) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("internetName",baseAuthInternet.getInternetName()));
        List<InternetInfoManage> internetInfoManageServiceAll = internetInfoManageService.findAll(queryConditions);
        if (internetInfoManageServiceAll.size()>0){
            baseAuthInternet.setInternetId(internetInfoManageServiceAll.get(0).getId());
            List<QueryCondition> queryConditionAppAuth=new ArrayList<>();
            queryConditionAppAuth.add(QueryCondition.eq("internetId",baseAuthInternet.getInternetId()));
            long count = count(queryConditionAppAuth);
            if (count>0){
                return;
            }
            String ips = baseAuthInternet.getIps();
            String[] split = ips.split(",");
            List<BaseAuthInternet> baseAuthInternets=new ArrayList<>();
            for (String ip:split){
                BaseAuthInternet baseAuthInternet1=new BaseAuthInternet();
                baseAuthInternet1.setInternetId(baseAuthInternet.getInternetId());
                baseAuthInternet1.setIp(ip);
                baseAuthInternet1.setCreateTime(new Date());
                baseAuthInternets.add(baseAuthInternet1);
            }
            save(baseAuthInternets);
        }

    }
    @Override
    public Result<String> delAuthInt(BaseAuthInternetQueryVo baseAuthInternet) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("internetId",baseAuthInternet.getId()));
        List<BaseAuthInternet> all = findAll(queryConditions);
        deleteInBatch(all);
        if (all.size()>0){
            deleteInBatch(all);
        }
        try {
            QueueUtil.putAuth(4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success("success");
    }

    @Override
    public Result<BaseAuthInternetVo> updateAuthInt(BaseAuthInternetQueryVo baseAuthInternetQueryVo) {
        BaseAuthInternetVo baseAuthInternetVo=new BaseAuthInternetVo();
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("internetId",baseAuthInternetQueryVo.getId()));
        List<BaseAuthInternet> all = findAll(queryConditions);
        if (all.size()>0){
            deleteInBatch(all);
        }
        List<QueryCondition> queryConditions1=new ArrayList<>();
        queryConditions1.add(QueryCondition.eq("internetId",baseAuthInternetQueryVo.getInternetId()));
        List<BaseAuthInternet> all1 = findAll(queryConditions1);
        if (all1.size()>0){
           deleteInBatch(all1);
        }
        String[] split = baseAuthInternetQueryVo.getIps().split(",");
        List<BaseAuthInternet> baseAuthInternets=new ArrayList<>();
        for (String ip:split){
            BaseAuthInternet baseAuthInternet1=new BaseAuthInternet();
            baseAuthInternet1.setInternetId(baseAuthInternetQueryVo.getInternetId());
            baseAuthInternet1.setIp(ip);
            baseAuthInternet1.setCreateTime(new Date());
            baseAuthInternets.add(baseAuthInternet1);
        }
        save(baseAuthInternets);
        InternetInfoManage one = internetInfoManageService.getOne(baseAuthInternetQueryVo.getId());
        baseAuthInternetVo.setInternetName(one.getInternetName());
        baseAuthInternetVo.setId(one.getId());
        baseAuthInternetVo.setIps(baseAuthInternetQueryVo.getIps());
        baseAuthInternetVo.setSecretLevel(one.getSecretLevel());
        baseAuthInternetVo.setName(one.getName());
        baseAuthInternetVo.setIp(one.getIp());
        try {
            QueueUtil.putAuth(4);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(baseAuthInternetVo);
    }

    @Override
    public PageRes<BaseAuthInternetVo> intPage(BaseAuthInternetQueryVo baseAuthInternetQueryVo) {
        PageRes<BaseAuthInternetVo> pageRes=new PageRes<>();
        List<BaseAuthInternetVo> baseAuthInternetVos=new ArrayList<>();
        List<QueryCondition> queryConditionList=new ArrayList<>();
        if (StringUtils.isNotBlank(baseAuthInternetQueryVo.getIp())){
            List<QueryCondition> queryConditions=new ArrayList<>();
            queryConditions.add(QueryCondition.like("ip",baseAuthInternetQueryVo.getIp()));
            List<BaseAuthInternet> all = findAll(queryConditions);
            if (all.size()>0){
                List<Integer> collect = all.stream().map(a -> a.getInternetId()).collect(Collectors.toList());
                queryConditionList.add(QueryCondition.in("id",collect));
            }else {
                queryConditionList.add(QueryCondition.isNull("id"));
            }
        }
        List<BaseAuthInternet> all1 = findAll();
        if (all1.size()>0){
            List<Integer> collect = all1.stream().map(a -> a.getInternetId()).collect(Collectors.toList());
            queryConditionList.add(QueryCondition.in("id",collect));
        }else {
            queryConditionList.add(QueryCondition.isNull("id"));
        }
        Page<InternetInfoManage> all = internetInfoManageService.findAll(queryConditionList, baseAuthInternetQueryVo.getPageable());
        PageRes<InternetInfoManage> res = PageRes.toRes(all);
        pageRes.setCode(res.getCode());
        pageRes.setTotal(res.getTotal());
        pageRes.setMessage(res.getMessage());
        List<InternetInfoManage> list = res.getList();
        if (list.size()>0){
            for (InternetInfoManage internetInfoManage:list){
                BaseAuthInternetVo baseAuthInternetVo=new BaseAuthInternetVo();
                baseAuthInternetVo.setId(internetInfoManage.getId());
                baseAuthInternetVo.setInternetId(internetInfoManage.getId());
                baseAuthInternetVo.setSecretLevel(getSecretLevel(internetInfoManage.getSecretLevel()));
                baseAuthInternetVo.setName(internetInfoManage.getName());
                baseAuthInternetVo.setIp(internetInfoManage.getIp());
                baseAuthInternetVo.setIps(getIps(internetInfoManage.getId()));
                baseAuthInternetVo.setInternetName(internetInfoManage.getInternetName());
                baseAuthInternetVos.add(baseAuthInternetVo);
            }
        }
        pageRes.setList(baseAuthInternetVos);
        return pageRes;
    }

    private String getSecretLevel(String secretLevel) {
        switch (secretLevel) {
            case "0":
                return "绝密";
            case "1":
                return "机密";
            case "2":
                // 必填
                return "秘密";

            case "3":
                return "内部";
            case "4":
                return "非密";
            default:
                break;
        }
     return "";
    }


    private String getIps(Integer id) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("internetId",id));
        List<BaseAuthInternet> all = findAll(queryConditions);
        List<String> strings = all.stream().map(a -> a.getIp()).collect(Collectors.toList());
        if (strings.size()>0){
            return String.join(",",strings);
        }
        return "";
    }
}
