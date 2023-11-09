package com.vrv.vap.common.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.plugin.util.QueryUtils;
import com.vrv.vap.common.vo.Query;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.*;

public class ApiController {

    private Map<Class<?>, HashMap<String, String>> ColumnMap = new HashedMap();

    // 允许的 by 参数
    private static final Set<String> BY = new HashSet<>(Arrays.asList(new String[]{"asc", "desc"}));

   // order 参数转换
    private void _orderBy(Example example, Query query, Class<?> clazz)  {
        if (!StringUtils.isEmpty(query.getOrder_()) && !StringUtils.isEmpty(query.getBy_()) ) {

            if(!BY.contains(query.getBy_().toLowerCase())){
                throw new RuntimeException("未定义的排序类型"+query.getBy_());
            }

            String order = query.getOrder_();
            if (!this.ColumnMap.containsKey(clazz)) {
                this.ColumnMap.put(clazz, new HashMap<String, String>());
            }

            Map<String, String> columns = this.ColumnMap.get(clazz);
            if (columns.containsKey(order)) {
                order = columns.get(order);

            } else {
                try {
                    Field field = clazz.getDeclaredField(order);
                    if (field != null) {
                        Column column = field.getAnnotation(Column.class);
                        if (column != null && !StringUtils.isEmpty(column.name())) {
                            order = column.name();
                        }
                        columns.put(query.getOrder_(), order);
                    }
                } catch (NoSuchFieldException e) {
                }

            }

            if(columns.containsKey(query.getOrder_())) {
                example.setOrderByClause(order + "  " + query.getBy_());
            }else{
                throw new  RuntimeException("未定义的字段类型:"+query.getOrder_());
            }

        }
    }

    /**
     * 分页插件，调用之后，只允许出现一个 select 语句，否则分页会失败，语句可以是 service 自带的 select,find 等方法，也可以在 mapper 里面自己写 sql 语句
     */
    public Example pagination(Query query, Class<?> clazz) {
        PageHelper.offsetPage(query.getStart_(), query.getCount_());
        Example example = new Example(clazz);
        this._orderBy(example, query, clazz);
        return example;
    }

    /**
     * 分页插件
     */
    public Example pageQuery(Query query, Class<?> clazz) {
        PageHelper.offsetPage(query.getStart_(), query.getCount_());
        Example example = new Example(clazz);
        this._orderBy(example, query, clazz);
        QueryUtils.buildCondition(example, query);
        return example;
    }

    /**
     * 不分页查询列表
     * 说明：不用传start_和count_参数，Query用于排序以及构造查询条件类型封装
     * @param query
     * @param clazz
     * @return
     */
    public Example query(Query query, Class<?> clazz) {
        Example example = new Example(clazz);
        this._orderBy(example, query, clazz);
        QueryUtils.buildCondition(example, query);
        return example;
    }


    /**
     * @param list 查询列表
     *             特别说明：此方法调用时，必须确保已经调用了其中之一：
     *             1. 在 Controller 中 调用 `pagination(Query query,Class<?> clazz)` 方法
     *             2. 手动调用 `PageHelper` 的相关分页方法
     *             <p>
     *             否则分页无法实现，请自行使用方法查询到总数 `total` 后，调用 `vList(list,total)` 方法
     */
    public <T> VList<T> vList(List<T> list) {
        long total = ((Page<T>) list).getTotal();
        return this.vList(list, (int) total);
    }

    public <T> VList vList(List<T> list, int total) {
        VList<T> resp = new VList<T>();
        resp.setCode("0");
        resp.setTotal(total);
        resp.setList(list);
        return resp;
    }

    public <T> VList  vList(ResultAble resultAble) {
        VList rtn = new VList<>();
        Result rst = resultAble.getResult();
        rtn.setCode(rst.getCode());
        rtn.setMessage(rst.getMessage());
        return rtn;
    }

    public Result result(boolean isOk) {
        if (isOk) {
            return Global.OK;
        }
        return Global.ERROR;
    }

    public Result result(ResultAble resultAble) {
        return resultAble.getResult();
    }

    public <T> VData<T> vData(T t) {
        VData<T> resp = new VData<T>();
        if (null == t) {
            resp.setCode("999");
        } else {
            resp.setCode("0");
            resp.setData(t);
        }
        return resp;
    }


    public VData vData(ResultAble resultAble) {
        VData rtn = new VData<>();
        Result rst = resultAble.getResult();
        rtn.setCode(rst.getCode());
        rtn.setMessage(rst.getMessage());
        return rtn;
    }

    public VData vData(Result rst) {
        VData rtn = new VData<>();
        rtn.setCode(rst.getCode());
        rtn.setMessage(rst.getMessage());
        return rtn;
    }

    public VData vData(boolean isOk) {
        if (isOk) {
            return this.vData(Global.OK);
        }
        return this.vData(Global.ERROR);
    }

}
