package com.vrv.vap.base;

import com.github.pagehelper.PageInfo;
import com.vrv.vap.common.vo.Query;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Created by ${huipei.x} on 2018-3-15.
 */
public interface BaseService<T> {

    public Integer save(T record);

    public Integer save(List<T> record);

    public T findById(Integer id);

    public List<T> findAll();

    public T findOne(T record);

    public List<T> findByids(String ids);


    public List<T> findByExample(Example example);

    public List<T> findByProperty(Class<T> entityClass,String property, Object value);

    public PageInfo<T> findPageExample(Integer page, Integer rows, Example example) ;

    public Integer saveSelective(T record);

    public Integer update(T record);

    public Integer updateSelective(T record);

    public Integer updateSelectiveByExample(T record, Example example);

    public Integer deleteById(Integer id);


    public Integer deleteByIds(String ids);

    public Integer count(T record);

    public Integer count(Example example);


    public Example orderQuery(Class<?> c,Query query);

}
