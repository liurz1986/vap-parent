package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.SelfConcernAssetMapper;
import com.vrv.vap.xc.pojo.SelfConcernAsset;
import com.vrv.vap.xc.service.ISelfConcernAssetService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SelfConcernAssetServiceImpl extends ServiceImpl<SelfConcernAssetMapper, SelfConcernAsset> implements ISelfConcernAssetService {

    public List<SelfConcernAsset> findSelfConcernAsset(Integer userId) {
        QueryWrapper<SelfConcernAsset> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("type", 0);
        return this.list(queryWrapper);
    }
}
