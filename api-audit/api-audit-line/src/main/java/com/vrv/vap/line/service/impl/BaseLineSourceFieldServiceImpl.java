package com.vrv.vap.line.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.mapper.BaseLineSourceFieldMapper;
import com.vrv.vap.line.model.BaseLineSourceField;
import com.vrv.vap.line.service.BaseLineSourceFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class BaseLineSourceFieldServiceImpl implements BaseLineSourceFieldService {

    @Autowired
    private BaseLineSourceFieldMapper baseLineSourceFieldMapper;

    @Override
    public List<BaseLineSourceField> findBySource(String sourceId) {
        QueryWrapper<BaseLineSourceField> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("source_id",sourceId);
        return baseLineSourceFieldMapper.selectList(queryWrapper);
    }

    @Override
    public int batchSave(List<BaseLineSourceField> list) {
        return baseLineSourceFieldMapper.saveBatch4List(list);
    }

    @Override
    public BaseLineSourceField add(BaseLineSourceField field) {
        this.baseLineSourceFieldMapper.insert(field);
        return field;
    }

    @Override
    public BaseLineSourceField update(BaseLineSourceField field) {
        this.baseLineSourceFieldMapper.updateById(field);
        return field;
    }

    @Override
    public int delete(String ids) {
        return this.baseLineSourceFieldMapper.deleteBatchIds(Arrays.asList(ids.split(",")));
    }

    @Override
    public List<BaseLineSourceField> findBySourceIds(Collection sourceIds) {
        QueryWrapper<BaseLineSourceField> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("source_id",sourceIds);
        return this.baseLineSourceFieldMapper.selectList(queryWrapper);
    }

}
