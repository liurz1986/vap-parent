package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.xc.mapper.BaseLineResultMapper;
import com.vrv.vap.xc.mapper.BaseLineSpecialMapper;
import com.vrv.vap.xc.pojo.BaseLineSpecial;
import com.vrv.vap.xc.service.BaseLineSpecialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseLineSpecialServiceImpl implements BaseLineSpecialService {
    @Autowired
    private BaseLineSpecialMapper baseLineSpecialMapper;

    @Override
    public List<BaseLineSpecial> findAll() {
        return baseLineSpecialMapper.selectList(new QueryWrapper<>());
    }
}
