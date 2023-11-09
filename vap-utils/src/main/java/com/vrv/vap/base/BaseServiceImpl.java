package com.vrv.vap.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vrv.vap.common.vo.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by ${huipei.x} on 2018-3-15.
 */
public abstract class BaseServiceImpl<T> implements BaseService<T>{
    @Autowired
    protected BaseMapper<T> baseMapper;

    private Class<T> modelClass;

    public BaseServiceImpl() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        modelClass = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public T findById(Integer id) {
        return baseMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<T> findAll() {
        return baseMapper.selectAll();
    }


    @Override
    public T findOne(T record) {
        return baseMapper.selectOne(record);
    }


    @Override
    public List<T> findByids(String ids) {
        return baseMapper.selectByIds(ids);
    }


    @Override
    public List<T> findByExample(Example example) {
        return baseMapper.selectByExample(example);
    }

    @Override
    public List<T> findByProperty(Class<T> entityClass,String property, Object value){
        Example example = new Example(entityClass);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(property,value);
        return findByExample(example);
    }

    @Override
    public PageInfo<T> findPageExample(Integer page, Integer rows, Example example) {
        PageHelper.startPage(page, rows);
        List<T> list = this.findByExample(example);
        return new PageInfo<T>(list);
    }

    @Override
    public Integer save(T record) {
        return baseMapper.insert(record);
    }

    @Override
    public Integer save(List<T> record) {
        return baseMapper.insertList(record);
    }

    @Override
    public Integer saveSelective(T record) {
        return baseMapper.insertSelective(record);
    }

    @Override
    public Integer update(T record) {
        return baseMapper.updateByPrimaryKey(record);
    }

    @Override
    public Integer updateSelective(T record) {
        return baseMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Integer updateSelectiveByExample(T record, Example example) {
        return baseMapper.updateByExampleSelective(record, example);
    }

    @Override
    public Integer deleteById(Integer id) {
        return baseMapper.deleteByPrimaryKey(id);
    }

    public Integer deleteByIds(Class<T> clazz, String property, List<Object> values) {
        Example example = new Example(clazz);
        example.createCriteria().andIn(property, values);
        return baseMapper.deleteByExample(example);
    }

    @Override
    public Integer deleteByIds(String ids) {
        return baseMapper.deleteByIds(ids);
    }


    @Override
    public Integer count(T record) {
        return baseMapper.selectCount(record);
    }

    @Override
    public Integer count(Example example) {
        return baseMapper.selectCountByExample(example);
    }

    @Override
    public Example orderQuery (Class<?> c, Query query){
        Example example = new Example(c);
        if (StringUtils.isNotEmpty(query.getOrder_()) && StringUtils.isNotEmpty(query.getBy_())) {
            example.setOrderByClause(query.getOrder_() + "  " + query.getBy_());
        }
        return  example;
    }
}
