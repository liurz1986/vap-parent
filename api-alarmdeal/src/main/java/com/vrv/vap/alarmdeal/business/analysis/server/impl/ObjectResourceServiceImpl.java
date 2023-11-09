package com.vrv.vap.alarmdeal.business.analysis.server.impl;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.analysis.enums.*;
import com.vrv.vap.alarmdeal.business.analysis.model.ObjectResource;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.MultiVersion;
import com.vrv.vap.alarmdeal.business.analysis.repository.ObjectResourceRespository;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.server.ObjectResourceService;
import com.vrv.vap.alarmdeal.business.analysis.vo.ObjectResourceVO;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ObjectResourceServiceImpl extends BaseServiceImpl<ObjectResource,String> implements ObjectResourceService {

    @Autowired
    private ObjectResourceRespository objectResourceRespository;


    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private FilterOperatorService filterOperatorService;
    


    @Override
    public ObjectResourceRespository getRepository() {
        return objectResourceRespository;
    }

    /**
     * 新增资源对象
     */
    @Override
    public Result<ObjectResource> addResource(ObjectResourceVO objectResourceVO){
        ObjectResource objectResource=mapperUtil.map(objectResourceVO,ObjectResource.class);
        objectResource.setGuid(UUID.randomUUID().toString());
        String code = UUID.randomUUID().toString();
        String name = objectResource.getName();
        objectResource.setCode(code);
        objectResource.setDeleteFlag(ObjectResourceConst.DELETE_FLAG_NORMAL);
        objectResource.setVersion(0);
        objectResource.setSource(ObjectResourceConst.SOURCE_CUSTOMIZE);
        objectResource.setCreateTime(new Date());
        String resourceMultiVersion = getResourceMultiVersion(0, code, name);
        objectResource.setMultiVersion(resourceMultiVersion);
        save(objectResource);
        return ResultUtil.success(objectResource);
    }
     
    
    private String getResourceMultiVersion(Integer version,String code,String name){
    	Gson gson  = new Gson();
    	List<MultiVersion> lists = new ArrayList<>();
    	MultiVersion multiVersion = new MultiVersion();
    	multiVersion.setCode(code);
    	multiVersion.setName(name);
    	multiVersion.setType(VersionConstant.SELF);
    	lists.add(multiVersion);
    	String json = gson.toJson(lists);
    	return json;
    }
    
    
    /**
     * 编辑资源对象
     */
    @Override
    public Result<ObjectResource> editResource(ObjectResourceVO objectResourceVO){
       /*--------------------更新资源结构------------------------*/
    	String guid=objectResourceVO.getGuid();
        ObjectResource objectResource=getOne(guid);
        String multiVersion = objectResource.getMultiVersion();
        String code = objectResource.getCode();
        Integer version=objectResource.getVersion();
        mapperUtil.copy(objectResourceVO, objectResource);
        objectResource.setVersion(version);
        objectResource.setMultiVersion(multiVersion);
        objectResource.setCode(code);
        objectResource.setUpdateTime(new Date());
        ObjectResource editObjectResource = new ObjectResource();
        mapperUtil.copy(objectResource, editObjectResource);
        editObjectResource.setVersion(version+1);
        editObjectResource.setGuid(UUID.randomUUID().toString());
        objectResource.setDeleteFlag(ObjectResourceConst.DELETE_FLAG_DELETE);
        save(objectResource);   //删除原先的
        save(editObjectResource); //更新保存
        /*-------------------修改过滤器和分析器当中的值------------------------*/
        filterOperatorService.changeFilterAndAnalysisRelateResource(code,version+1);
        return ResultUtil.success(editObjectResource);
    }

    /**
     * 删除资源对象
     */
    @Override
    public Result<Boolean> deleteResource(String guid){
        ObjectResource objectResource=getOne(guid);
        if(objectResource!=null){
            objectResource.setDeleteFlag(ObjectResourceConst.DELETE_FLAG_DELETE); //标记删除
            save(objectResource);
        }
        return ResultUtil.success(true);
    }

    /**
     * 资源对象分页查询
     */
    @Override
    public PageRes<ObjectResource> getObjectResourcePager(ObjectResourceVO objectResourceVO, Pageable pageable){
        String title=objectResourceVO.getTitle();
        String type=objectResourceVO.getObjectResourceType(); //类型
        List<QueryCondition> conditions = new ArrayList<QueryCondition>();
        if (StringUtils.isNotEmpty(title)) {
            conditions.add(QueryCondition.like("name", title));
        }
        if (StringUtils.isNotEmpty(type)) {
            conditions.add(QueryCondition.like("objectResourceType", type));
        }
        conditions.add(QueryCondition.notEq("deleteFlag",ObjectResourceConst.DELETE_FLAG_DELETE));
        Page<ObjectResource> page=findAll(conditions,pageable);
        PageRes<ObjectResource> res = PageRes.toRes(page);
        return res;
    }



}

