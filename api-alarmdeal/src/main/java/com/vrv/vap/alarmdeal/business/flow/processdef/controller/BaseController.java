package com.vrv.vap.alarmdeal.business.flow.processdef.controller;

import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.FieldValidateException;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.Id;
import javax.validation.Valid;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public abstract class BaseController <T, ID extends Serializable>{
    private static final Map<Class<?>, String> idFields = new HashMap();

    public BaseController() {
    }

    protected abstract BaseService<T, ID> getService();

    @GetMapping
    @ApiIgnore
    public PageRes<T> page(T condition, PageReq pageReq) {
        Pageable pageable = PageReq.getPageable(pageReq);
        Page<T> findAll = this.getService().findAll(condition, pageable);
        return PageRes.toRes(findAll);
    }

    @GetMapping({"{guid}"})
    public Result<T> get(@PathVariable ID guid) {
        T one = this.getService().getOne(guid);
        return ResultUtil.success(one);
    }

    @PutMapping
    public Result<T> edit(@Valid T model, BindingResult bindingResult) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        this.validateModifyModel(model);
        T save = this.getService().save(model);
        return ResultUtil.success(save);
    }
    protected void validateModifyModel(T model) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        long count = 0L;
        Class<? extends Object> class1 = model.getClass();
        String idField = this.getEntityIdField(class1);
        Field declaredFieldGuid = class1.getDeclaredField(idField);
        Map<String, String> validatesFields = this.validatesDuplicationFields();
        Iterator var8 = validatesFields.keySet().iterator();

        String field;
        do {
            if (!var8.hasNext()) {
                return;
            }

            field = (String)var8.next();
            Field declaredFieldCode = class1.getDeclaredField(field);
            declaredFieldGuid.setAccessible(true);
            declaredFieldCode.setAccessible(true);
            Object guid = declaredFieldGuid.get(model);
            Object code = declaredFieldCode.get(model);
            List<QueryCondition> conditions = new ArrayList();
            conditions.add(QueryCondition.notEq(idField, guid));
            conditions.add(QueryCondition.eq(field, code));
            count = this.getService().count(conditions);
        } while(count <= 0L);

        throw new FieldValidateException((String)validatesFields.get(field));
    }

    private String getEntityIdField(Class<? extends Object> class1) {
        if (!idFields.containsKey(class1)) {
            Field[] declaredFields = class1.getDeclaredFields();
            String idField = "guid";
            Field[] var4 = declaredFields;
            int var5 = declaredFields.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Field field = var4[var6];
                Id annotation = (Id)field.getAnnotation(Id.class);
                if (annotation != null) {
                    idField = field.getName();
                    break;
                }
            }

            idFields.put(class1, idField);
        }

        return (String)idFields.get(class1);
    }
    protected Map<String, String> validatesDuplicationFields() {
        Map<String, String> validates = new HashMap();
        return validates;
    }

    @DeleteMapping({"{guid}"})
    public Result<Boolean> delete(@PathVariable ID guid) {
        this.getService().delete(guid);
        return ResultUtil.success(true);
    }
}