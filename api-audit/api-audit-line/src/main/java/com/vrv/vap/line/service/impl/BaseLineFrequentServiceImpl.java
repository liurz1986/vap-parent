package com.vrv.vap.line.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.mapper.BaseLineFrequentMapper;
import com.vrv.vap.line.model.BaseLineFrequent;
import com.vrv.vap.line.service.BaseLineFrequentService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseLineFrequentServiceImpl implements BaseLineFrequentService {
    @Autowired
    private BaseLineFrequentMapper baseLineFrequentMapper;

    @Override
    public List<BaseLineFrequent> findUserFrequent(String key) {
        QueryWrapper<BaseLineFrequent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",key);
        queryWrapper.eq("is_continue", LineConstants.CONTINUE.YES);
        queryWrapper.eq("type", "1");
        return baseLineFrequentMapper.selectList(queryWrapper);
    }

    @Override
    public BaseLineFrequent findByUser(String userKey) {
        QueryWrapper<BaseLineFrequent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userKey);
        queryWrapper.eq("type","1");
        //queryWrapper.isNull("sys_id");
        List<BaseLineFrequent> frequents = baseLineFrequentMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(frequents)){
            return frequents.get(0);
        }else{
            return null;
        }
    }

    @Override
    public BaseLineFrequent findByUserAndSysid(String user, String sysId) {
        QueryWrapper<BaseLineFrequent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",user);
        queryWrapper.eq("sys_id",sysId);
        List<BaseLineFrequent> frequents = baseLineFrequentMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(frequents)){
            return frequents.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void updateFrequent(BaseLineFrequent frequent) {
        if(frequent.getId() != null){
            this.baseLineFrequentMapper.updateById(frequent);
        }else{
            this.baseLineFrequentMapper.insert(frequent);
        }
    }

    @Override
    public List<BaseLineFrequent> queryByCondition(String column ,String org) {
        QueryWrapper<BaseLineFrequent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(column,org);
        return this.baseLineFrequentMapper.selectList(queryWrapper);
    }
}
