package com.vrv.vap.line.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.mapper.BaseLineFrequentAttrMapper;
import com.vrv.vap.line.model.BaseLineFrequentAttr;
import com.vrv.vap.line.service.BaseLineFrequentAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseLineFrequentAttrServiceImpl implements BaseLineFrequentAttrService {
    @Autowired
    private BaseLineFrequentAttrMapper baseLineFrequentAttrMapper;

    @Override
    public List<BaseLineFrequentAttr> findByFrequents(List<String> frequents,String userId) {
        QueryWrapper<BaseLineFrequentAttr> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("item",frequents);
        queryWrapper.eq("ukey",userId);
        //queryWrapper.isNull("sys_id");
        return baseLineFrequentAttrMapper.selectList(queryWrapper);
    }
}
